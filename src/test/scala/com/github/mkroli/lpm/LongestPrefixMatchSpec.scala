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
class LongestPrefixMatchSpec extends Spec {
  describe("LongestPrefixMatch") {
    it("should return None if nothing is found") {
      val lpm = new LongestPrefixMatch[Int]
      assert(None == lpm.getValueFromPrefix("789"))
      lpm.addValueForRange("123", "456", 1)
      assert(None == lpm.getValueFromPrefix("789"))
    }

    it("should return value no matter how deep input is") {
      val lpm = new LongestPrefixMatch[Int]
      lpm.addValueForRange("123", "456", 1)
      assert(1 == lpm.getValueFromPrefix("33333333333333333333").get)
    }

    it("should return value on exact matches") {
      val lpm = new LongestPrefixMatch[Int]
      lpm.addValueForRange("123", "456", 1)
      assert(None == lpm.getValueFromPrefix("122"))
      assert(1 == lpm.getValueFromPrefix("123").get)
      assert(1 == lpm.getValueFromPrefix("456").get)
      assert(None == lpm.getValueFromPrefix("457"))
    }

    it("should return longest prefix matching on overlap") {
      val lpm = new LongestPrefixMatch[Int]
      lpm.addValueForRange("3123", "3123", 1);
      lpm.addValueForRange("312", "312", 2);
      assert(1 == lpm.getValueFromPrefix("3123").get)
      assert(2 == lpm.getValueFromPrefix("3122").get)
    }

    it("should return longest prefix matching as if it wasn't optimized") {
      val lpm = new LongestPrefixMatch[Int]
      lpm.addValueForRange("100", "199", 1)
      lpm.addValueForRange("15", "19", 2)
      assert(1 == lpm.getValueFromPrefix("17").get)
    }

    it("should return longest not latest prefix even if optimized") {
      val lpm = new LongestPrefixMatch[Int]
      lpm.addValueForRange("1", "1", 2)
      lpm.addValueForRange("100", "199", 1)
      assert(1 == lpm.getValueFromPrefix("1").get)
      lpm.addValueForRange("200", "299", 1)
      lpm.addValueForRange("2", "2", 2)
      assert(1 == lpm.getValueFromPrefix("2").get)
    }
  }
}
