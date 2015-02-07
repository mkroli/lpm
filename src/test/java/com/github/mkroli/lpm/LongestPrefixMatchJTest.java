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
package com.github.mkroli.lpm;

import org.junit.Assert;
import org.junit.Test;

public class LongestPrefixMatchJTest {
	@Test
	public void wrapperTest() {
		LongestPrefixMatchJ<Integer> lpm = new LongestPrefixMatchJ<Integer>()
				.addValueForRange("1", "1", 1).addValueForRange("1000", "1999",
						2);
		Assert.assertNull(lpm.getValueFromPrefix("234567890"));
		Assert.assertEquals(2, lpm.getValueFromPrefix("1234567890").intValue());
	}
}
