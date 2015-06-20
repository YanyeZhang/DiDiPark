package com.zhangyanye.didipark.activity;

import java.util.HashMap;
import java.util.Map;

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
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UpdatePasActivity extends Activity {
	private ProgressDialog dialog;
	private EditText et_old, et_new, et_confi;
	private TopBar topbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_psd);
		initView();
	}

	private void initView() {
		topbar=(TopBar)findViewById(R.id.update_psd_topbar);
		topbar.setOnTopbarClickListener(new ToolBarOnClickListener() {
			
			@Override
			public void rightBtnClick() {
				
			}
			
			@Override
			public void leftBtnClick() {
				finish();
			}
		});;
		et_confi = (EditText) findViewById(R.id.update_psd_confi);
		et_new = (EditText) findViewById(R.id.update_psd_new);
		et_old = (EditText) findViewById(R.id.update_psd_old);
	}

	public void btPsdClik(View v) {
		if (!et_old.getText().toString().equals("")
				&& !et_new.getText().toString().equals("")
				&& !et_confi.getText().toString().equals("")) {

			if (et_old.getText().toString()
					.equals(getIntent().getStringExtra("password"))) {
				Crouton.makeText(UpdatePasActivity.this, "旧密码输入错误！",
						Style.ALERT, R.id.update_psd_alternate_view_group)
						.show();
			} else if (et_old.getText().toString()
					.equals(et_new.getText().toString())) {
				Crouton.makeText(UpdatePasActivity.this, "新旧密码不能一样！",
						Style.ALERT, R.id.update_psd_alternate_view_group)
						.show();
			} else if (!et_confi.getText().toString()
					.equals(et_new.getText().toString())) {
				Crouton.makeText(UpdatePasActivity.this, "新密码两次输入不一致！",
						Style.ALERT, R.id.update_psd_alternate_view_group)
						.show();
			} else {
				dialog = new ProgressDialog(UpdatePasActivity.this);
				dialog.show();
				RequestQueue requestQueue = MyApplication.queue;
				StringRequest stringRequest = new StringRequest(
						Request.Method.POST, MyContants.URL_UPDATE_INFO,
						new Response.Listener<String>() {
							@Override
							public void onResponse(String response) {
								dialog.dismiss();
								dialog.cancel();
								Toast.makeText(UpdatePasActivity.this,
										"密码修改成功！", Toast.LENGTH_LONG).show();
								JSONObject json = null;
								Gson gson = new Gson();
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
							}
						}, new Response.ErrorListener() {
							@Override
							public void onErrorResponse(VolleyError error) {
								dialog.dismiss();
								dialog.cancel();
								Crouton.makeText(UpdatePasActivity.this,
										"密码修改失败！", Style.ALERT,
										R.id.update_psd_alternate_view_group)
										.show();
							}
						}) {
					@Override
					protected Map<String, String> getParams() {
						// 在这里设置需要post的参数
						Map<String, String> params = new HashMap<String, String>();
						params.put("password", et_confi.getText().toString());
						params.put("id",SharedPreferencesUtil.getData(UpdatePasActivity.this, 
								"user_id", 0)+"");
						return params;
					}
				};
				requestQueue.add(stringRequest);
			}
		} else {
			Crouton.makeText(UpdatePasActivity.this, "请将资料填写完整！", Style.ALERT,
					R.id.update_psd_alternate_view_group).show();
		}
	}
}
