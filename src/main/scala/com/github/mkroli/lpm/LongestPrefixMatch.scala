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
import scala.annotation.tailrec
import scala.math.max
import scala.math.pow

class DuplicateRangeException extends Exception

/**
 * This class stores data belonging to decimal number ranges. This can be used
 * inside dial plans. Ranges with more than 2 permutations are compressed
 * automatically. When querying this the value of the longest matching path
 * will be returned. So if there are the ranges 1 and 11-13 when querying with
 * 12, the later's value will be returned. This is as it holds the longer
 * prefix (2 digits instead of 1).
 *
 * Example usage:
 * {{{
 * val lpm = new LongestPrefixMatch[String].
 *   addValueForRange("123", "456", "V1").
 *   addValueForRange("12345", "12349", "V2")
 * lpm.getValueFromPrefix("1234")  // will return Some("V1")
 * lpm.getValueFromPrefix("12347") // will return Some("V2")
 * }}}
 */
class LongestPrefixMatch[T] private (root: TreeNode[T], ranges: List[(Long, Long)]) extends LongestPrefixMatchSugar[T] {
  def this() = this(new TreeNode, Nil)

  @tailrec
  private def isDuplicate(start: Long, end: Long, ranges: List[(Long, Long)] = this.ranges): Boolean = {
    if (ranges.length == 0)
      false
    else {
      val (s, e) = ranges.head
      if ((start >= s && end <= e) || (start < s && end > e))
        true
      else
        isDuplicate(start, end, ranges.drop(1))
    }
  }

  /**
   * Adds a range-specific value. The rangeStart and rangeEnd parameters must
   * be of the same length.
   * @param rangeStart the inclusive start of the range
   * @param rangeEnd the inclusive end of the range
   * @param value the data to be stored for this range
   * @return an updated instance of [this]
   */
  @throws(classOf[IllegalArgumentException])
  @throws(classOf[DuplicateRangeException])
  def addValueForRange(rangeStart: String, rangeEnd: String, value: T) = {
    if (rangeStart.length != rangeEnd.length)
      throw new IllegalArgumentException("the string size of rangeStart and rangeEnd must be the same")
    val start = rangeStart.toLong
    val end = rangeEnd.toLong
    if (isDuplicate(start, end))
      throw new DuplicateRangeException

    var i = start
    var tree = root
    while (i <= end) {
      for {
        o <- 1 to max(rangeStart.length, rangeEnd.length)
        if (lower(i, o) == 0 && upper(i, o) == end) ||
          ((lower(i, o) < start || upper(i, o) > end) &&
            (lower(i, o - 1) >= start && upper(i, o - 1) <= end))
      } {
        val path = prefixFromString((lower(i, o - 1) / pow(10, o - 1).asInstanceOf[Long]).toString)
        i += pow(10, o - 1).toLong
        tree = tree.update(path, tree(path) match {
          case oldValue @ Some((oldWeight, _)) if (oldWeight > rangeStart.length) => oldValue
          case _ => Some(rangeStart.length, value)
        })
      }
    }
    new LongestPrefixMatch(tree, (start, end) :: ranges)
  }

  private def valueFromPrefix(tree: TreeNode[T], prefix: Seq[Int]): Option[(Int, T)] = {
    tree.subNodes(prefix.head) match {
      case Some(subTree) if prefix.length > 1 =>
        valueFromPrefix(subTree, prefix.drop(1)) match {
          case subValue @ Some((subDepth, _)) =>
            tree.values(prefix.head) match {
              case s @ Some((depth, _)) if depth > subDepth => s
              case _ => subValue
            }
          case _ => tree.values(prefix.head)
        }
      case _ =>
        tree.values(prefix.head)
    }
  }

  private def valueFromPrefix(prefix: String): Option[(Int, T)] =
    valueFromPrefix(root, prefixFromString(prefix))

  /**
   * Retrieves the value stored under the longest range matching.
   * @param prefix the number to match prefixes against
   */
  def getValueFromPrefix(prefix: String): Option[T] =
    valueFromPrefix(prefix).map(_._2)
}
