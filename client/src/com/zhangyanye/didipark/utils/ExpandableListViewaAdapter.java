package com.zhangyanye.didipark.utils;

import java.util.List;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class ExpandableListViewaAdapter extends BaseExpandableListAdapter {
	private Activity activity;
	private List<String> groupArray;// 组列表
	private List<List<String>> childArray;// 子列表

	public ExpandableListViewaAdapter(Activity activity,
			List<String> groupArray, List<List<String>> childArray) {
		this.activity = activity;
		this.groupArray = groupArray;
		this.childArray = childArray;

	}

	/*Child*/
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return childArray.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		String string = childArray.get(groupPosition).get(childPosition);

		return getGenericView(string);
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return childArray.get(groupPosition).size();
	}

	/*Group */
	@Override
	public Object getGroup(int groupPosition) {
		return groupArray.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return groupArray.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {

		String string = groupArray.get(groupPosition);
		return getGenericView(string);
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	private TextView getGenericView(String string) {
		AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);

		TextView textView = new TextView(activity);
		textView.setLayoutParams(layoutParams);

		textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);

		textView.setPadding(40, 0, 0, 0);
		textView.setText(string);
		return textView;
	}
}