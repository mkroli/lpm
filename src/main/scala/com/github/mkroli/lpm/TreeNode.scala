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
}
