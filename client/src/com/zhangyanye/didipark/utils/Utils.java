package com.zhangyanye.didipark.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class Utils {

	public static String checkPassword(String passwordStr) {
		String regexZ = "\\d*";
		String regexS = "[a-zA-Z]+";
		String regexT = "\\W+$";
		String regexZT = "\\D*";
		String regexST = "[\\d\\W]*";
		String regexZS = "\\w*";
		String regexZST = "[\\w\\W]*";

		if (passwordStr.matches(regexZ)) {
			return "弱";
		}
		if (passwordStr.matches(regexS)) {
			return "弱";
		}
		if (passwordStr.matches(regexT)) {
			return "弱";
		}
		if (passwordStr.matches(regexZT)) {
			return "中";
		}
		if (passwordStr.matches(regexST)) {
			return "中";
		}
		if (passwordStr.matches(regexZS)) {
			return "中";
		}
		if (passwordStr.matches(regexZST)) {
			return "强";
		}
		return passwordStr;

	}

	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);

	}
}