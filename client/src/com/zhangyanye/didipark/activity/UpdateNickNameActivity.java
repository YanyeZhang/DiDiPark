package com.zhangyanye.didipark.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.lidroid.xutils.DbUtils;
import com.zhangyanye.didipark.R;
import com.zhangyanye.didipark.application.MyApplication;
import com.zhangyanye.didipark.pojo.User;
import com.zhangyanye.didipark.utils.DbOperate;
import com.zhangyanye.didipark.utils.MyContants;
import com.zhangyanye.didipark.utils.SharedPreferencesUtil;
import com.zhangyanye.didipark.view.ToolBarOnClickListener;
import com.zhangyanye.didipark.view.TopBar;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UpdateNickNameActivity extends Activity {
	private ProgressDialog dialog;
	private EditText et_nickName;
	private TopBar topbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_nickname);
		initView();
	}

	private void initView() {
		et_nickName = (EditText) findViewById(R.id.update_nick_new);
		topbar = (TopBar) findViewById(R.id.update_nick_topbar);
		topbar.setOnTopbarClickListener(new ToolBarOnClickListener() {
			@Override
			public void rightBtnClick() {
			}
			@Override
			public void leftBtnClick() {
				finish();
			}
		});
		;
	}

	public void btNickClik(View v) {
		if (!et_nickName.getText().toString().equals("")) {
			dialog = new ProgressDialog(UpdateNickNameActivity.this);
			dialog.show();
			RequestQueue requestQueue = MyApplication.queue;
			StringRequest stringRequest = new StringRequest(
					Request.Method.POST, MyContants.URL_UPDATE_INFO,
					new Response.Listener<String>() {
						@Override
						public void onResponse(String response) {
							dialog.dismiss();
							dialog.cancel();
							Toast.makeText(UpdateNickNameActivity.this,
									"昵称修改成功！", Toast.LENGTH_LONG).show();
							JSONObject json = null;
							Gson gson = new Gson();
							Log.e("zyy", response);
							try {
								json = new JSONObject(response);
								User user = gson.fromJson(json.get("user")
										.toString(), User.class);
								user.setNickName(new String(user.getNickName()
										.getBytes("ISO-8859-1"), "UTF-8"));
								DbUtils db = DbOperate.getInstance();
								db.dropTable(User.class);
								db.save(user);
							} catch (Exception e1) {
								e1.printStackTrace();
							}
							setResult(RESULT_OK);
							finish();
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							dialog.dismiss();
							dialog.cancel();
							Crouton.makeText(UpdateNickNameActivity.this,
									"昵称修改失败！", Style.ALERT,
									R.id.update_psd_alternate_view_group)
									.show();
						}
					}) {
				@Override
				protected Map<String, String> getParams() {
					// 在这里设置需要post的参数
					Map<String, String> params = new HashMap<String, String>();
					params.put("nickName", et_nickName.getText().toString());
					params.put(
							"id",
							SharedPreferencesUtil.getData(
									UpdateNickNameActivity.this, "user_id", 0)
									+ "");
					return params;
				}
			};
			requestQueue.add(stringRequest);
		} else {
			Crouton.makeText(UpdateNickNameActivity.this, "请将资料填写完整！",
					Style.ALERT, R.id.update_nick_alternate_view_group).show();
		}
	}
}
