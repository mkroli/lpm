package com.github.mkroli.lpm
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.Spec

@RunWith(classOf[JUnitRunner])
class LongestPrefixMatchSpec extends Spec {
  describe("LongestPrefixMatch") {
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
  }
}
