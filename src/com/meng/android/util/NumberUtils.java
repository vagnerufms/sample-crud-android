package com.meng.android.util;

import java.math.BigDecimal;

public class NumberUtils {

	public static BigDecimal bigDecimalValueOf(String bigDecimalString) {
		try {
			return new BigDecimal(bigDecimalString);
		} catch (Exception e) {
			return BigDecimal.ZERO;
		}
	}

	public static Double doubleValueOf(String doubleString) {
		try {
			return Double.valueOf(doubleString);
		} catch (Exception e) {
			return Double.valueOf(0d);
		}
	}

}