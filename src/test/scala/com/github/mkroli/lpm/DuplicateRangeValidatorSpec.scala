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
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.Spec

@RunWith(classOf[JUnitRunner])
class DuplicateRangeValidatorSpec extends Spec {
  private def assertDuplicateRange(f: => Any) {
    try {
      f
      fail()
    } catch {
      case _: DuplicateRangeException =>
      case e @ _ => throw e
    }
  }

  describe("DuplicateRangeValidator") {
    it("should fail on duplicates only") {
      val lpm = new LongestPrefixMatch[Int] with DuplicateRangeValidator[Int]
      lpm.addValueForRange("11", "18", 1)
      assertDuplicateRange(lpm.addValueForRange("14", "15", 2))
      assertDuplicateRange(lpm.addValueForRange("11", "15", 3))
      assertDuplicateRange(lpm.addValueForRange("14", "18", 4))
      assertDuplicateRange(lpm.addValueForRange("11", "18", 4))
      lpm.addValueForRange("10", "17", 5)
      lpm.addValueForRange("12", "19", 6)
    }

    it("shouldn't care about ranges with different weights at all") {
      val lpm = new LongestPrefixMatch[Int] with DuplicateRangeValidator[Int]
      lpm.addValueForRange("1", "1", 1)
      lpm.addValueForRange("10", "19", 2)
      lpm.addValueForRange("111", "189", 3)
      lpm.addValueForRange("20", "29", 4)
      lpm.addValueForRange("2", "2", 5)
    }
  }
}
