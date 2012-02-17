/*
 * Copyright 2012 Michael Krolikowski
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
package de.krolikowski.lpm

private[lpm] sealed class TreeNode[T: Manifest] {
  val subNodes: Array[Option[TreeNode[T]]] = Array.fill(10)(None)
  val values: Array[Option[T]] = Array.fill(10)(None)
}

class LongestPrefixMatch[T: Manifest] {
  private val root = new TreeNode[T]

  private def prefixFromString(prefix: String) =
    prefix.toCharArray.map(_.toString.toInt)

  def addValueForRange(rangeStart: String, rangeEnd: String, value: T) {
    val start = rangeStart.toLong
    val end = rangeEnd.toLong
    def lower(number: Long, digits: Int) =
      (number / Math.pow(10, digits).asInstanceOf[Long]) * Math.pow(10, digits).asInstanceOf[Long]
    def upper(number: Long, digits: Int) =
      lower(number, digits) + Math.pow(10, digits).asInstanceOf[Long] - 1
    for {
      o <- 1 to Math.max(rangeStart.length, rangeEnd.length)
      i <- start to end
      if lower(i, o) < start || upper(i, o) > end
      if lower(i, o - 1) >= start && upper(i, o - 1) <= end
      if lower(i, o - 1) != lower(i - 1, o - 1)
    } {
      val path = prefixFromString((lower(i, o - 1) / Math.pow(10, o - 1).asInstanceOf[Long]).toString)
      var tree = root
      for (i <- path.dropRight(1)) {
        if (tree.subNodes(i) == None)
          tree.subNodes(i) = Some(new TreeNode[T])
        tree = tree.subNodes(i).get
      }
      tree.values(path.last) = Some(value)
    }
  }

  private def getValueFromPrefix(tree: TreeNode[T], prefix: Seq[Int]): Option[T] = {
    tree.subNodes(prefix.head) match {
      case Some(subTree) if prefix.length > 1 =>
        getValueFromPrefix(subTree, prefix.drop(1)) match {
          case subValue: Some[T] => subValue
          case _ => tree.values(prefix.head)
        }
      case _ =>
        tree.values(prefix.head)
    }
  }

  def getValueFromPrefix(prefix: String): Option[T] =
    getValueFromPrefix(root, prefixFromString(prefix))
}
