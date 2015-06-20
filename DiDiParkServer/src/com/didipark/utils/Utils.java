package com.didipark.utils;

public class Utils {

	public String checkPassword(String passwordStr) {
		String str = "/^[0-9]{1,20}$/"; // 不超过20位的数字组合
		String str1 = "/^[0-9|a-z|A-Z]{1,20}$/"; // 由字母、数字组成，不超过20位
		String str2 = "/^[a-zA-Z]{1,20}$/"; // 由字母不超过20位
		if (passwordStr.matches(str)) {
			return "弱";
		}
		if (passwordStr.matches(str2)) {
			return "中";
		}
		if (passwordStr.matches(str1)) {
			return "强";
		}
		return passwordStr;

	}
}
