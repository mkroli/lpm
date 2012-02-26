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

class DuplicateRangeException extends Exception

/**
 * This trait changes [[com.github.mkroli.lpm.LongestPrefixMatch]]'S behavior
 * of overwriting duplicate ranges by throwing a
 * [[com.github.mkroli.lpm.DuplicateRangeException]] instead. Ranges are
 * automatically being validated inside the addValueForRange method.
 */
trait DuplicateRangeValidator[T] extends LongestPrefixMatch[T] {
  private var ranges: List[(Long, Long)] = List()

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

  @throws(classOf[DuplicateRangeException])
  abstract override def addValueForRange(rangeStart: String, rangeEnd: String, value: T) = {
    val start = rangeStart.toLong
    val end = rangeEnd.toLong
    if (isDuplicate(start, end))
      throw new DuplicateRangeException
    ranges = (start, end) :: ranges
    super.addValueForRange(rangeStart, rangeEnd, value)
  }
}
