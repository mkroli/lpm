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
import scala.language.postfixOps

private class TreeNode[+A] private (val subNodes: Vector[Option[TreeNode[A]]], val values: Vector[Option[(Int, A)]]) {
  def this() = this(Vector.fill(10)(None), Vector.fill(10)(None))

  def update[B >: A](path: Seq[Int], depth: Int, value: Option[B]): TreeNode[B] = {
    val v = value.map(v => (depth, v))
    path match {
      case Nil         => new TreeNode(Vector.fill(10)(None), Vector.fill(10)(v))
      case head :: Nil => new TreeNode(subNodes, values.updated(head, v))
      case head :: tail =>
        val subTree = subNodes(head).getOrElse(new TreeNode).update(tail, depth, value)
        if (subTree.values.forall(value.==)) {
          if (value != None && subTree.subNodes.forall(None.==)) {
            new TreeNode(subNodes.updated(head, None), values.updated(head, v))
          } else {
            new TreeNode(subNodes.updated(head, Some(subTree)), values.updated(head, v))
          }
        } else {
          new TreeNode(subNodes.updated(head, Some(subTree)), values)
        }
    }
  }

  @tailrec
  final def apply(path: Seq[Int]): Option[(Int, A)] = {
    path match {
      case Nil         => None
      case head :: Nil => values(head)
      case head :: tail =>
        subNodes(head) match {
          case Some(subNode) => subNode(tail)
          case None          => None
        }
    }
  }

  def size(): Int = values.map(_.size).sum + subNodes.map(_.map(_.size()).getOrElse(0)).sum

  private def toString(level: Int): String = {
    def s(str: List[String]): String = str match {
      case Nil => ""
      case e :: Nil =>
        " " * (level * 2) + e
      case e :: tail =>
        " " * (level * 2) + e + "\n" + s(tail)
    }

    "Node " + s((subNodes zip values zipWithIndex).collect {
      case ((subNode, value), i) if subNode.isDefined || value.isDefined =>
        val self = Seq(
          Some(i.toString),
          value.map { case (depth, value) =>
            s"$value ($depth)"
          }
        ).flatten.mkString(" ")
        val children = subNode.map("\n" + _.toString(level + 1)).getOrElse("")
        self + children
    }.toList)
  }

  override def toString() = toString(0)
}
