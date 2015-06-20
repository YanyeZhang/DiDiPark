package com.zhangyanye.didipark.view;

import com.zhangyanye.didipark.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TopBar extends RelativeLayout {
	private TextView tv_title;
	private ImageView btn_right, bt_left;
	private ToolBarOnClickListener tbarOnClickListener;
	private LayoutParams lp_title, lp_right, lp_left;

	public TopBar(Context context) {
		super(context);
	}

	public TopBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		btn_right = new ImageView(context);
		bt_left = new ImageView(context);
		tv_title = new TextView(context);
		TypedArray ta = context.obtainStyledAttributes(attrs,
				R.styleable.Topbar);
		CharSequence title = ta.getText(R.styleable.Topbar_title_text);
		Drawable drawable_right = ta.getDrawable(R.styleable.Topbar_right_img);
		Drawable drawable_left = ta.getDrawable(R.styleable.Topbar_left_img);
		ta.recycle();
		if (drawable_right != null)
			btn_right.setImageDrawable(drawable_right);
		else
			btn_right.setVisibility(GONE);
		if (drawable_left != null)
			bt_left.setImageDrawable(drawable_left);
		else
			bt_left.setVisibility(GONE);
		if (title != null)
			tv_title.setText(title);

		tv_title.setTextSize(20);
		tv_title.setTextColor(Color.WHITE);
		tv_title.setGravity(CENTER_IN_PARENT);

		lp_title = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		lp_title.addRule(RelativeLayout.CENTER_IN_PARENT);

		lp_left = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		lp_right = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		lp_left.addRule(RelativeLayout.CENTER_VERTICAL);
		lp_right.addRule(RelativeLayout.CENTER_VERTICAL);
		lp_right.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		lp_left.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

		btn_right.setPadding(0, 0, 10, 0);
		bt_left.setPadding(10, 0, 0, 0);
		addView(bt_left, lp_left);
		addView(tv_title, lp_title);
		addView(btn_right, lp_right);
		// setBackgroundColor(getResources().getColor(R.color.top_bar_color));
		bt_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tbarOnClickListener.leftBtnClick();

			}
		});
		btn_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tbarOnClickListener.rightBtnClick();
			}
		});
	}



	/**
	 * 接口回调 设置点击事件监听
	 * 
	 * @param tbarOnClickListener
	 */
	public void setOnTopbarClickListener(
			ToolBarOnClickListener tbarOnClickListener) {
		this.tbarOnClickListener = tbarOnClickListener;
	}
}
