package com.zhangyanye.didipark.view;

import java.util.ArrayList;

import com.zhangyanye.didipark.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemClickListener;

public class CustomerSpinner extends Spinner implements OnItemClickListener {

	public static SelectDialog dialog = null;
	private ArrayList<String> list;// ArrayList<String> list存储所要显示的数据
	public static String text;

	public CustomerSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	// 如果视图定义了OnClickListener监听器，调用此方法来执行
	@Override
	public boolean performClick() {
		Context context = getContext();
		final LayoutInflater inflater = LayoutInflater.from(getContext());
		final View view = inflater.inflate(R.layout.formcustomspinner, null);
		final ListView listview = (ListView) view
				.findViewById(R.id.formcustomspinner_list);
		ListviewAdapter adapters = new ListviewAdapter(context, getList());
		listview.setAdapter(adapters);
		listview.setOnItemClickListener(this);
		dialog = new SelectDialog(context, R.style.dialog);// 创建Dialog并设置样式主题
		LayoutParams params = new LayoutParams(650, LayoutParams.FILL_PARENT);
		dialog.setCanceledOnTouchOutside(true);// 设置点击Dialog外部任意区域关闭Dialog
		dialog.show();
		dialog.addContentView(view, params);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> view, View itemView, int position,
			long id) {
		setSelection(position);
		setText(list.get(position));
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
	}

	public ArrayList<String> getList() {
		return list;
	}

	public void setList(ArrayList<String> list) {
		this.list = list;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
