package com.github.mkroli.lpm;

import org.junit.Assert;
import org.junit.Test;

public class LongestPrefixMatchWithDuplicateRangeValidatorJTest {
	@Test
	public void wrapperTest() {
		LongestPrefixMatchJ<Integer> lpm = new LongestPrefixMatchJ<Integer>();
		LongestPrefixMatchWithDuplicateRangeValidatorJ<Integer> lpmDrv = new LongestPrefixMatchWithDuplicateRangeValidatorJ<Integer>();

		lpm.addValueForRange("10", "19", 1);
		lpm.addValueForRange("12", "13", 2);

		lpmDrv.addValueForRange("10", "19", 1);
		try {
			lpmDrv.addValueForRange("12", "13", 2);
			Assert.fail();
		} catch (RuntimeException e) {
		}
	}
}
