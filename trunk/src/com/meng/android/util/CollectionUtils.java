package com.meng.android.util;

import java.util.List;

@SuppressWarnings("rawtypes")
public class CollectionUtils {
	public static boolean isEmpty(List list) {
		if (list != null && !list.isEmpty()) {
			return false;
		}
		return true;
	}

	public static boolean isNotEmpty(List list) {
		return !isEmpty(list);
	}
}