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

import org.scalatest.funspec.AnyFunSpec

class LongestPrefixMatchSpec extends AnyFunSpec {
  describe("LongestPrefixMatch") {
    it("should return None if nothing is found") {
      val lpm = new LongestPrefixMatch[Int]()
      assert(None == lpm.getValueFromPrefix("789"))
      val lpm2 = lpm.addValueForRange("123", "456", 1)
      assert(None == lpm2.getValueFromPrefix("789"))
    }

    it("should return value no matter how deep input is") {
      val lpm = new LongestPrefixMatch[Int]()
        .addValueForRange("123", "456", 1)
      assert(1 == lpm.getValueFromPrefix("3" * 1024).get)
    }

    it("should return value on exact matches") {
      val lpm = new LongestPrefixMatch[Int]()
        .addValueForRange("123", "456", 1)
      assert(None == lpm.getValueFromPrefix("122"))
      assert(1 == lpm.getValueFromPrefix("123").get)
      assert(1 == lpm.getValueFromPrefix("456").get)
      assert(None == lpm.getValueFromPrefix("457"))
    }

    it("should return longest prefix matching on overlap") {
      val lpm = new LongestPrefixMatch[Int]()
        .addValueForRange("3123", "3123", 1)
        .addValueForRange("312", "312", 2)
      assert(1 == lpm.getValueFromPrefix("3123").get)
      assert(2 == lpm.getValueFromPrefix("3122").get)
    }

    it("should return longest prefix matching as if it wasn't optimized") {
      val lpm = new LongestPrefixMatch[Int]().
        addValueForRange("100", "199", 1).
        addValueForRange("15", "19", 2)
      assert(1 == lpm.getValueFromPrefix("17").get)
    }

    it("should return longest not latest prefix even if optimized") {
      val lpm = new LongestPrefixMatch[Int]().
        addValueForRange("1", "1", 2).
        addValueForRange("100", "199", 1)
      assert(1 == lpm.getValueFromPrefix("1").get)
      val lpm2 = lpm.addValueForRange("200", "299", 1).
        addValueForRange("2", "2", 2)
      assert(1 == lpm2.getValueFromPrefix("2").get)
    }

    it("should accept a range without any prefix (0-9)") {
      val lpm1 = new LongestPrefixMatch[Int]().addValueForRange("0", "9", 1)
      val lpm2 = new LongestPrefixMatch[Int]().addValueForRange("00", "99", 1)
      for (i <- 0 to 999) {
        assert(1 == lpm1.getValueFromPrefix(i.toString).get)
        assert(1 == lpm2.getValueFromPrefix(i.toString).get)
      }
    }

    it("should accept a top-level range (1-9)") {
      val lpm = new LongestPrefixMatch[Int]().
        addValueForRange("1", "9", 1)
      assert(None == lpm.getValueFromPrefix("0"))
      for (i <- 1 to 9)
        assert(1 == lpm.getValueFromPrefix(i.toString).get)
    }

    it("should accept a top-level range (0-8)") {
      val lpm = new LongestPrefixMatch[Int]().
        addValueForRange("0", "8", 1)
      for (i <- 0 to 8)
        assert(1 == lpm.getValueFromPrefix(i.toString).get)
      assert(None == lpm.getValueFromPrefix("9"))
    }

    it("should not accept invalid ranges") {
      val lpm = new LongestPrefixMatch[Int]()
      intercept[IllegalArgumentException](lpm.addValueForRange("", "", 1))
      intercept[IllegalArgumentException](lpm.addValueForRange("a", "b", 2))
      intercept[IllegalArgumentException](lpm.addValueForRange("1", "23", 3))
    }

    it("should be accessable using shorter methods") {
      val lpm = new LongestPrefixMatch[Int] + ("1", "5", 1) + ("6", "9", 2)
      assert(1 === lpm("123").get)
      assert(2 === lpm("789").get)
    }
  }
}
