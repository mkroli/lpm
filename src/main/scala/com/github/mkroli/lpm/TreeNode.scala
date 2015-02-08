/*
 * Copyright 2012-2015 Michael Krolikowski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.mkroli.lpm
import scala.annotation.tailrec

private class TreeNode[+A] private (
    val subNodes: Vector[Option[TreeNode[A]]],
    val values: Vector[Option[(Int, A)]]) {
  def this() = this(Vector.fill(10)(None), Vector.fill(10)(None))

  def update[B >: A](path: Seq[Int], value: Option[(Int, B)]): TreeNode[B] = {
    path match {
      case Nil => new TreeNode(Vector.fill(10)(None), Vector.fill(10)(value))
      case head :: Nil =>
        new TreeNode(subNodes, values.updated(head, value))
      case head :: tail =>
        val subTree = subNodes(head) match {
          case Some(subNode) => subNode
          case None => new TreeNode
        }
        new TreeNode(
          subNodes.updated(head, Some(subTree.update(tail, value))),
          values)
    }
  }

  @tailrec
  final def apply(path: Seq[Int]): Option[(Int, A)] = {
    path match {
      case Nil => None
      case head :: Nil => values(head)
      case head :: tail => subNodes(head) match {
        case Some(subNode) => subNode(tail)
        case None => None
      }
    }
  }

  private def dropValuesBelow(depth: Int) = new TreeNode(subNodes, values.map {
    case Some((d, _)) if d <= depth => None
    case e => e
  })

  def compact(): TreeNode[A] = {
    @tailrec
    def allTheSame[T](v: Vector[T]): Boolean = v match {
      case a +: b +: _ if a != b => false
      case v if v.size == 1 => true
      case _ +: tail => allTheSame(tail)
      case _ => false
    }

    val (compactSubNodes, compactValues) = (subNodes.map(_.map(_.compact())) zip values).map {
      case e @ (None, _) => e
      case (Some(subNode), v) =>
        if (allTheSame(subNode.values)) {
          val compactValue = (subNode.values.head, v) match {
            case (s, None) => s
            case (s @ Some((ad, _)), Some((bd, _))) if ad > bd => s
            case _ => v
          }
          val compactSubNode = new TreeNode(subNode.subNodes, Vector.fill(10)(None))
          val relevantCompactSubNode = if (compactSubNode.subNodes.exists(_.isDefined) || compactSubNode.values.exists(_.isDefined))
            Some(compactSubNode)
          else
            None
          (relevantCompactSubNode, compactValue)
        } else {
          (Some(subNode), v)
        }
    }.map {
      case (subNode, v @ Some((depth, _))) =>
        val compactSubNode = subNode.map(_.dropValuesBelow(depth)).filter { treeNode =>
          treeNode.subNodes.exists(_.isDefined) || treeNode.values.exists(_.isDefined)
        }
        (compactSubNode, v)
      case e => e
    }.unzip
    new TreeNode(compactSubNodes, compactValues)
  }
}
