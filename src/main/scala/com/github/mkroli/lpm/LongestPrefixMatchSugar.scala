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

trait LongestPrefixMatchSugar[T] { self: LongestPrefixMatch[T] =>
  def +(rangeStart: String, rangeEnd: String, value: T) =
    addValueForRange(rangeStart, rangeEnd, value)

  def -(rangeStart: String, rangeEnd: String) =
    deleteValueForRange(rangeStart, rangeEnd)

  def apply(prefix: String) =
    getValueFromPrefix(prefix)
}
