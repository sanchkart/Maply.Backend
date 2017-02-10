package com.maply.util;

public class StringUtility {

	public static boolean isNullOrEmpty(String value) {
		if (value == null)
			return true;

		return value.trim().isEmpty();
	}
}
