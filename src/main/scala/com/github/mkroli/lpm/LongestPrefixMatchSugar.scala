package com.github.mkroli.lpm

trait LongestPrefixMatchSugar[T] { self: LongestPrefixMatch[T] =>
  def <<(rangeStart: String, rangeEnd: String, value: T) =
    addValueForRange(rangeStart, rangeEnd, value)

  def apply(prefix: String) =
    getValueFromPrefix(prefix)
}
