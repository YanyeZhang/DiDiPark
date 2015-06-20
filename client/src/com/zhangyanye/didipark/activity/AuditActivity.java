package com.zhangyanye.didipark.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.google.gson.Gson;
import com.zhangyanye.didipark.R;
import com.zhangyanye.didipark.application.MyApplication;
import com.zhangyanye.didipark.pojo.Carport;
import com.zhangyanye.didipark.pojo.User;
import com.zhangyanye.didipark.utils.BFImageCache;
import com.zhangyanye.didipark.utils.MyContants;
import com.zhangyanye.didipark.utils.SharedPreferencesUtil;
import com.zhangyanye.didipark.view.CircleImageView;
import com.zhangyanye.didipark.view.MyNotification;
import com.zhangyanye.didipark.view.ToolBarOnClickListener;
import com.zhangyanye.didipark.view.TopBar;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class AuditActivity extends Activity {

	private ProgressDialog dialog;
	private String customer_id;
	private String carport_id;
	private ImageLoader imageLoader;
	private Carport carport;
	private User user;
	private Intent intent;
	private TopBar topbar;
	private CircleImageView img;
	private ImageView photo;
	private ImageListener listener_figure, listener_carport;
	private TextView tv_nickName, tv_addr, tv_time, tv_phone, tv_num;
	private String time;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_audit);
		getBundle();
		initView();
		getInfo();

	}

	private void getBundle() {
		topbar = (TopBar) findViewById(R.id.audit_topBar);

		topbar.setOnTopbarClickListener(new ToolBarOnClickListener() {

			@Override
			public void rightBtnClick() {
			}

			@Override
			public void leftBtnClick() {
				intent.setClassName("com.zhangyanye.didipark.activity",
						"MainActivity");
				if (intent.resolveActivity(getPackageManager()) == null) {
					// 说明系统中不存在这个activity
					startActivity(intent);
				}
				System.gc();
				finish();
			}
		});
		Bundle bundle = getIntent().getExtras();
		time = bundle.getString("time");
		customer_id = bundle.getString("customer_id");
		carport_id = bundle.getString("carport_id");
		MyNotification manager = MyNotification.getInstance(AuditActivity.this);
		manager.dismissNotification(Integer.parseInt(customer_id));
	}

	private void initView() {

		tv_num = (TextView) findViewById(R.id.audit_tv_num);
		tv_phone = (TextView) findViewById(R.id.audit_tv_phone);
		tv_time = (TextView) findViewById(R.id.audit_time);
		tv_addr = (TextView) findViewById(R.id.audit_tv_addr);
		photo = (ImageView) findViewById(R.id.audit_iv_photo);
		imageLoader = new ImageLoader(MyApplication.queue,
				BFImageCache.getInstance());
		img = (CircleImageView) findViewById(R.id.audit_iv_figure);
		tv_nickName = (TextView) findViewById(R.id.audit_tv_nickname);
		intent = new Intent(AuditActivity.this, MainActivity.class);

	}

	private void getInfo() {
		dialog = new ProgressDialog(AuditActivity.this);
		dialog.show();
		RequestQueue requestQueue = MyApplication.queue;
		StringRequest stringRequest = new StringRequest(Request.Method.GET,
				MyContants.URL_ORDER_DETAIL + "customer_id=" + customer_id
						+ "&carport_id=" + carport_id,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						Gson gson = new Gson();
						try {
							Log.e("zyy", response);
							JSONObject json = new JSONObject(response);
							user = gson.fromJson(json.get("user").toString(),
									User.class);
							user.setNickName(new String(user.getNickName()
									.getBytes("ISO8859-1"), "utf-8"));
							carport = gson.fromJson(json.get("carport")
									.toString(), Carport.class);
							carport.setAddr(new String(carport.getAddr()
									.getBytes("ISO8859-1"), "utf-8"));
							carport.setDescribe(new String(carport
									.getDescribe().getBytes("ISO8859-1"),
									"utf-8"));
							tv_nickName.setText(user.getNickName());
							listener_figure = ImageLoader.getImageListener(img,
									R.drawable.ic_figure_def,
									R.drawable.ic_figure_def);
							imageLoader.get(user.getImageUrl(), listener_figure);
							listener_carport = ImageLoader.getImageListener(
									photo, R.drawable.ic_carport_def,
									R.drawable.ic_carport_def);
							imageLoader.get(json.get("photo").toString(),
									listener_carport);
							tv_num.setText("剩余" + carport.getNum() + "个车位");
							tv_addr.setText(carport.getAddr());
							tv_time.setText(time);
							tv_phone.setText("手机尾号:"
									+ user.getPhone().substring(
											user.getPhone().length() - 4,
											user.getPhone().length()));
							dialog.cancel();
							dialog.dismiss();
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						dialog.dismiss();
						dialog.cancel();
						Crouton.makeText(AuditActivity.this, "服务器出错！",
								Style.ALERT, R.id.audit_alternate_view_group)
								.show();
					}
				});
		requestQueue.add(stringRequest);
	}

	private void submitResult(final String result) {
		dialog = new ProgressDialog(AuditActivity.this);
		dialog.show();
		RequestQueue requestQueue = MyApplication.queue;
		StringRequest stringRequest = new StringRequest(Request.Method.POST,
				MyContants.URL_ORDER_RESULT, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						Log.e("zyy", response);
						dialog.dismiss();
						dialog.cancel();
						intent.setClassName("com.zhangyanye.didipark.activity",
								"MainActivity");
						if (intent.resolveActivity(getPackageManager()) == null) {
							// 说明系统中不存在这个activity
							startActivity(intent);
						}
						System.gc();
						finish();
						finish();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						dialog.dismiss();
						dialog.cancel();
						Crouton.makeText(AuditActivity.this, "服务器出错！",
								Style.ALERT, R.id.audit_alternate_view_group)
								.show();
					}
				}) {
			@Override
			protected Map<String, String> getParams() {
				// 在这里设置需要post的参数
				Map<String, String> params = new HashMap<String, String>();
				params.put("result", result);
				params.put("carport_id", carport_id);
				params.put("customer_id", customer_id);
				params.put(
						"user_id",
						SharedPreferencesUtil.getData(AuditActivity.this,
								"user_id", 0) + "");
				return params;
			}
		};
		requestQueue.add(stringRequest);
	}

	public void buttonClick(View v) {
		switch (v.getId()) {
		case R.id.audit_agree:
			submitResult("agree");
			break;
		case R.id.audit_disagree:
			submitResult("disagree");
			break;
		}

	}
}
