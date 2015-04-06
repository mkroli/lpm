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

/**
 * This is a wrapper around [[com.github.mkroli.lpm.LongestPrefixMatch]] for
 * easier use with the Java language.
 */
class LongestPrefixMatchJ[T >: Object] private (lpm: LongestPrefixMatch[T]) {
  def this() = this(new LongestPrefixMatch())

  private def translateExceptions[R](f: => R) = try {
    f
  } catch {
    case e: RuntimeException => throw e
    case t: Throwable => throw new RuntimeException(t)
  }

  def addValueForRange(rangeStart: String, rangeEnd: String, value: T): LongestPrefixMatchJ[T] = {
    new LongestPrefixMatchJ(
      translateExceptions(
        lpm.addValueForRange(rangeStart, rangeEnd, value)))
  }

  def compact(): LongestPrefixMatchJ[T] = {
    new LongestPrefixMatchJ(lpm.compact())
  }

  def getValueFromPrefix(prefix: String) = {
    translateExceptions(lpm.getValueFromPrefix(prefix)) match {
      case Some(v) => v
      case None => null
    }
  }

  def size(): Int = lpm.size()

  override def toString(): String = lpm.toString()
}
