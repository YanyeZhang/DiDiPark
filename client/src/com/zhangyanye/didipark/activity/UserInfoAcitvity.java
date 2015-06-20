package com.zhangyanye.didipark.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.zhangyanye.didipark.R;
import com.zhangyanye.didipark.application.MyApplication;
import com.zhangyanye.didipark.pojo.User;
import com.zhangyanye.didipark.utils.DbOperate;
import com.zhangyanye.didipark.utils.MyContants;
import com.zhangyanye.didipark.utils.SharedPreferencesUtil;
import com.zhangyanye.didipark.utils.Utils;
import com.zhangyanye.didipark.view.ToolBarOnClickListener;
import com.zhangyanye.didipark.view.TopBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class UserInfoAcitvity extends Activity {

	private User user;
	private DbUtils db;
	private Button bt_phone, bt_password, et_nickName;
	private TopBar topbar;
	private String phone;
	private TextView tv_security;
	private ProgressDialog dialog;
	private String SMS_APPKEY = "689a07e36072";
	private String SMS_APPSECRET = "3b36629e632dbe0c884aef2ba28790cf";
	private Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_info);
		getUser();
		inintView();
		SMSSDK.initSDK(this, SMS_APPKEY, SMS_APPSECRET);
	}

	private void getUser() {
		db = DbOperate.getInstance();
		try {
			user = db.findFirst(Selector.from(User.class).where(
					"id",
					"=",
					SharedPreferencesUtil.getData(UserInfoAcitvity.this,
							"user_id", 0)));
		} catch (DbException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			getUser();
			bt_password = (Button) findViewById(R.id.update_bt_password);
			if (user.getPassword().length() > 12)
				bt_password.setText("第三方登录不允许修改密码");
			bt_phone = (Button) findViewById(R.id.update_bt_phone);
			et_nickName = (Button) findViewById(R.id.update_et_nickName);
			if (user != null) {
				char[] temp = user.getPhone().toString().toCharArray();
				temp[3] = '*';
				temp[4] = '*';
				temp[5] = '*';
				temp[6] = '*';
				bt_phone.setText(String.valueOf(temp));
				et_nickName.setText(user.getNickName());
				String security = Utils.checkPassword(user.getPassword());
				tv_security.setText(security);
			}
		}
	}

	private void inintView() {
		tv_security = (TextView) findViewById(R.id.user_tv_security);
		topbar = (TopBar) findViewById(R.id.user_info_topbar);
		topbar.setOnTopbarClickListener(new ToolBarOnClickListener() {
			@Override
			public void rightBtnClick() {
			}

			@Override
			public void leftBtnClick() {
				setResult(RESULT_OK);
				finish();
			}
		});
		bt_password = (Button) findViewById(R.id.update_bt_password);
		if (user.getPassword().length() > 12)
			bt_password.setText("第三方登录不允许修改密码");
		bt_phone = (Button) findViewById(R.id.update_bt_phone);
		et_nickName = (Button) findViewById(R.id.update_et_nickName);
		if (user != null) {
			char[] temp = user.getPhone().toString().toCharArray();
			temp[3] = '*';
			temp[4] = '*';
			temp[5] = '*';
			temp[6] = '*';
			bt_phone.setText(String.valueOf(temp));
			et_nickName.setText(user.getNickName());
			String security = Utils.checkPassword(user.getPassword());
			tv_security.setText(security);
		}

	}

	public void btUpdateClick(View v) {
		switch (v.getId()) {
		case R.id.update_bt_phone:
			RegisterPage registerPage = new RegisterPage();
			registerPage.setRegisterCallback(new EventHandler() {
				public void afterEvent(int event, int result, Object data) {
					// 解析注册结果
					if (result == SMSSDK.RESULT_COMPLETE) {
						@SuppressWarnings("unchecked")
						HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
						phone = (String) phoneMap.get("phone");
						if (!phone.equals(""))
							updatePhone();
					}
				}
			});
			registerPage.show(this);
			break;
		case R.id.update_et_nickName:
			intent = new Intent(UserInfoAcitvity.this,
					UpdateNickNameActivity.class);
			startActivityForResult(intent, 1);
			break;
		case R.id.update_bt_password:
			if (user.getPassword().length() < 12) {
				intent = new Intent(UserInfoAcitvity.this,
						UpdatePasActivity.class);
				intent.putExtra("password", user.getPassword());
				startActivity(intent);
			}
			break;
		}
	}

	private void updatePhone() {
		dialog = new ProgressDialog(UserInfoAcitvity.this);
		dialog.show();
		RequestQueue requestQueue = MyApplication.queue;
		StringRequest stringRequest = new StringRequest(Request.Method.POST,
				MyContants.URL_UPDATE_INFO, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						dialog.dismiss();
						dialog.cancel();
						Toast.makeText(UserInfoAcitvity.this, "手机号修改成功！",
								Toast.LENGTH_LONG).show();
						JSONObject json = null;
						Gson gson = new Gson();
						try {
							json = new JSONObject(response);
							User user = gson.fromJson(json.get("user")
									.toString(), User.class);
							user.setNickName(new String(user.getNickName()
									.getBytes("ISO-8895-1"), "UTF-8"));
							DbUtils db = DbOperate.getInstance();
							db.dropTable(User.class);
							db.save(user);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						dialog.dismiss();
						dialog.cancel();
						Toast.makeText(UserInfoAcitvity.this, "手机号修改失败！",
								Toast.LENGTH_LONG).show();
					}
				}) {
			@Override
			protected Map<String, String> getParams() {
				// 在这里设置需要post的参数
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", phone);
				params.put(
						"id",
						SharedPreferencesUtil.getData(UserInfoAcitvity.this,
								"user_id", 0) + "");
				return params;
			}
		};
		requestQueue.add(stringRequest);
	}

}
