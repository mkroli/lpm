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
package com.github.mkroli.lpm;

import scala.Option;
import scala.reflect.Manifest;
import scala.reflect.Manifest$;

/**
 * This is a wrapper around LongestPrefixMatch for easier use with the Java
 * language. For information about LongestPrefixMatch see the scala
 * documentation of the LongestPrefixMatch class.
 */
public class LongestPrefixMatchJ<T> {
	private Manifest m = Manifest$.MODULE$.Object();
	@SuppressWarnings("unchecked")
	private LongestPrefixMatch<T> lpm = new LongestPrefixMatch<T>(m);

	public void addValueForRange(String rangeStart, String rangeEnd, T value) {
		lpm.addValueForRange(rangeStart, rangeEnd, value);
	}

	public T getValueFromPrefix(String prefix) {
		Option<T> o = lpm.getValueFromPrefix(prefix);
		return o.isEmpty() ? null : o.get();
	}
}
