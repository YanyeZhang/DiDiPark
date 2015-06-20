package com.zhangyanye.didipark.view;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.zhangyanye.didipark.R;
import com.zhangyanye.didipark.application.MyApplication;
import com.zhangyanye.didipark.utils.MyContants;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

public class PopMenu {
	private Context context;
	private int user_id, carport_id;
	private PopupWindow popWindow = new PopupWindow();

	public PopMenu(Context context, int user_id, int carport_id) {
		this.context = context;
		this.carport_id = carport_id;
		this.user_id = user_id;
	}

	public PopMenu() {

	}

	public void close() {
		popWindow.dismiss();
	}

	class Buttonlistener implements OnClickListener {
		Intent intent;

		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.pop_report:
				Toast.makeText(context, "已将该车位举报", Toast.LENGTH_SHORT).show();
				popWindow.dismiss();
				break;
			case R.id.pop_favorite:
				favoriteSubmit();
				popWindow.dismiss();
				break;

			}
		}
	}

	public void showPopWindow(View parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View vPopWindow = inflater
				.inflate(R.layout.popwindow_item, null, false);
		popWindow = new PopupWindow(vPopWindow, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		popWindow.setBackgroundDrawable(new BitmapDrawable());
		popWindow.setOutsideTouchable(true);
		popWindow.showAsDropDown(parent);
		Button pop_add_Course = (Button) vPopWindow
				.findViewById(R.id.pop_favorite);
		pop_add_Course.setOnClickListener(new Buttonlistener());
		Button pop_grade = (Button) vPopWindow.findViewById(R.id.pop_report);
		pop_grade.setOnClickListener(new Buttonlistener());

	}

	private void favoriteSubmit() {
		RequestQueue requestQueue = MyApplication.queue;
		StringRequest stringRequest = new StringRequest(Request.Method.POST,
				MyContants.URL_FAORITE, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						try {
							JSONObject json = new JSONObject(response);
							if (json.get("message").equals("success")) {
								Toast.makeText(context, "成功加入我的收藏",
										Toast.LENGTH_SHORT).show();
							}else{
								Toast.makeText(context, "您已收藏过此车位",
										Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
					}
				}) {
			@Override
			protected Map<String, String> getParams() {
				// 在这里设置需要post的参数
				Map<String, String> params = new HashMap<String, String>();
				params.put("carport_id", carport_id + "");
				params.put("user_id", user_id + "");
				return params;
			}
		};
		requestQueue.add(stringRequest);

	}

	private void reportSubmit() {
		RequestQueue requestQueue = MyApplication.queue;
		StringRequest stringRequest = new StringRequest(Request.Method.POST,
				MyContants.URL_ORDER_RESULT, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						try {
							JSONObject json = new JSONObject(response);
							if (json.get("message").equals("success")) {
								Toast.makeText(context, "已将该车位举报",
										Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
					}
				}) {
			@Override
			protected Map<String, String> getParams() {
				// 在这里设置需要post的参数
				Map<String, String> params = new HashMap<String, String>();
				params.put("carport_id", carport_id + "");
				return params;
			}
		};
		requestQueue.add(stringRequest);

	}

}
