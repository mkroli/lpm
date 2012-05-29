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
  private def isDuplicate(start: Long, end: Long, ranges: List[(Long, Long)]): Boolean = {
    ranges match {
      case Nil => false
      case (s, e) :: _ if ((start >= s && end <= e) || (start < s && end > e)) => true
      case _ :: tail => isDuplicate(start, end, tail)
    }
  }

  @tailrec
  private def addValueForRange(start: Long, end: Long, depth: Int, value: T, r: Long, root: TreeNode[T]): LongestPrefixMatch[T] = {
    val maxOptimization = (for {
      o <- 1 to depth
      if (lower(r, o) == 0 && upper(r, o) == end) ||
        ((lower(r, o) < start || upper(r, o) > end) &&
          (lower(r, o - 1) >= start && upper(r, o - 1) <= end))
    } yield o - 1).head
    val path = prefixFromString((lower(r, maxOptimization) / pow(10, maxOptimization).asInstanceOf[Long]).toString)
    val ntree = root.update(path, root(path) match {
      case oldValue @ Some((oldWeight, _)) if (oldWeight > depth) => oldValue
      case _ => Some(depth, value)
    })
    (r + pow(10, maxOptimization).toLong) match {
      case i if i <= end => addValueForRange(start, end, depth, value, i, ntree)
      case _ => new LongestPrefixMatch(ntree, (start, end) :: ranges)
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
  def addValueForRange(rangeStart: String, rangeEnd: String, value: T): LongestPrefixMatch[T] = {
    if (rangeStart.length != rangeEnd.length)
      throw new IllegalArgumentException("the string size of rangeStart and rangeEnd must be the same")
    val start = rangeStart.toLong
    val end = rangeEnd.toLong
    if (isDuplicate(start, end, ranges))
      throw new DuplicateRangeException
    addValueForRange(start, end, rangeStart.length, value, start, root)
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

  /**
   * Retrieves the value stored under the longest range matching.
   * @param prefix the number to match prefixes against
   */
  def getValueFromPrefix(prefix: String): Option[T] =
    valueFromPrefix(root, prefixFromString(prefix)).map(_._2)
}
