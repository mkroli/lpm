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
package com.github.mkroli.lpm

private[lpm] sealed class TreeNode[T: Manifest] {
  val subNodes: Array[Option[TreeNode[T]]] = Array.fill(10)(None)
  val values: Array[Option[(Int, T)]] = Array.fill(10)(None)
}

/**
 * This class stores data belonging to decimal number ranges. This can be used
 * inside dial plans. Ranges with more than 2 permutations are compressed
 * automatically. When querying this the value of the longest matching path
 * will be returned. So if there are the ranges 1 and 11-13 when querying with
 * 12, the later's value will be returned. This is as it holds the longer
 * prefix (2 digits instead of 1).
 *
 * Example usage:
 * <pre>
 * val lpm = new LongestPrefixMatch[String]
 * lpm.addValueForRange("123", "456", "V1")
 * lpm.addValueForRange("12345", "12349", "V2")
 * lpm.getValueFromPrefix("1234")  // will return Some("V1")
 * lpm.getValueFromPrefix("12347") // will return Some("V2")
 * </pre>
 */
class LongestPrefixMatch[T: Manifest] {
  private val root = new TreeNode[T]

  private def prefixFromString(prefix: String) =
    prefix.toCharArray.map(_.toString.toInt)

  /**
   * Adds a range-specific value. The rangeStart and rangeEnd parameters must
   * be of the same length.
   * @param rangeStart the inclusive start of the range
   * @param rangeEnd the inclusive end of the range
   * @param value the data to be stored for this range
   */
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
      tree.values(path.last) = Some(rangeStart.length, value)
    }
  }

  private def getValueFromPrefix(tree: TreeNode[T], prefix: Seq[Int]): Option[(Int, T)] = {
    tree.subNodes(prefix.head) match {
      case Some(subTree) if prefix.length > 1 =>
        getValueFromPrefix(subTree, prefix.drop(1)) match {
          case subValue @ Some((subDepth, _)) =>
            tree.values(prefix.head) match {
              case s @ Some((depth, value)) if depth > subDepth => s
              case _ => subValue
            }
          case _ => tree.values(prefix.head)
        }
      case _ =>
        tree.values(prefix.head)
    }
  }

  /**
   * Retrieves the value stored under the longest range matching.
   * @param prefix the number to match prefixes against
   */
  def getValueFromPrefix(prefix: String): Option[T] =
    getValueFromPrefix(root, prefixFromString(prefix)).map(_._2)
}
