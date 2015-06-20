package com.zhangyanye.didipark.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.igexin.sdk.PushManager;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQAuth;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.sso.SsoHandler;
import com.zhangyanye.didipark.R;
import com.zhangyanye.didipark.application.MyApplication;
import com.zhangyanye.didipark.pojo.Carport;
import com.zhangyanye.didipark.pojo.Photo;
import com.zhangyanye.didipark.pojo.User;
import com.zhangyanye.didipark.utils.DbOperate;
import com.zhangyanye.didipark.utils.MyContants;
import com.zhangyanye.didipark.utils.SharedPreferencesUtil;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private RelativeLayout rl_login_account, rl_login_password;
	private EditText et_login_account, et_login_password;
	private String userName, password;
	private UserInfo mInfo;
	private Thread myThread;
	private Intent intent;
	private ProgressDialog dialog;
	private final String CONSUMER_KEY = "2366049830";
	private final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
	private final String QQ_APP_ID = "1104505966";
	private QQAuth mQQAuth;
	private String cid = "xx";
	private Tencent mTencent;
	private int isFinish = 0;
	private List<Carport> carports;
	private List<Photo> photos;
	private Weibo mWeibo;
	private SsoHandler mSsoHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initView();
		getCid();
		mQQAuth = QQAuth.createInstance(QQ_APP_ID, this);
		mTencent = Tencent.createInstance(QQ_APP_ID, this);
		mWeibo = Weibo.getInstance(CONSUMER_KEY, REDIRECT_URL);

	}

	public void intentRegister(View v) {
		Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
		startActivity(intent);
		finish();
	}

	/*
	 * 初始化空间
	 */
	private void initView() {
		rl_login_account = (RelativeLayout) findViewById(R.id.rl_login_account);
		rl_login_password = (RelativeLayout) findViewById(R.id.rl_login_password);
		et_login_account = (EditText) findViewById(R.id.et_login_account);
		et_login_password = (EditText) findViewById(R.id.et_login_password);
	}

	public void loginWeibo(View v) {
		mSsoHandler = new SsoHandler(LoginActivity.this, mWeibo);
		mSsoHandler.authorize(new AuthListener());
	}

	private void getCid() {
		if (PushManager.getInstance().getClientid(LoginActivity.this) != null)
			cid = PushManager.getInstance().getClientid(LoginActivity.this);
	}

	private class AuthListener implements WeiboAuthListener {

		@Override
		public void onCancel() {
		}

		@Override
		public void onComplete(Bundle values) {
			password = values.getString("uid");
			loginByQQ();
		}

		@Override
		public void onError(WeiboDialogError arg0) {
		}

		@Override
		public void onWeiboException(WeiboException arg0) {
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// SSO 授权回调
		// 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
		if (mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}

	}

	/*
	 * editText 背景变化监听
	 */
	public void editTextSelector(View view) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		switch (view.getId()) {
		case R.id.rl_login_account:
			rl_login_account
					.setBackgroundResource(R.drawable.signup_email_focus);
			rl_login_password.setBackgroundResource(R.drawable.signup_password);
			et_login_account.setFocusable(true);
			et_login_account.setFocusableInTouchMode(true);
			et_login_password.setFocusable(false);
			et_login_password.setFocusableInTouchMode(false);
			et_login_account.requestFocus();
			et_login_account.requestFocusFromTouch();
			imm.showSoftInputFromInputMethod(et_login_account.getWindowToken(),
					0);
			break;
		case R.id.rl_login_password:
			rl_login_account.setBackgroundResource(R.drawable.signup_email);
			rl_login_password
					.setBackgroundResource(R.drawable.signup_password_focus);
			et_login_password.setFocusable(true);
			et_login_password.setFocusableInTouchMode(true);
			et_login_account.setFocusable(false);
			et_login_account.setFocusableInTouchMode(false);
			et_login_password.requestFocus();
			et_login_password.requestFocusFromTouch();
			imm.showSoftInputFromInputMethod(
					et_login_password.getWindowToken(), 0);
			break;

		}
	}

	private void login() {
		dialog = new ProgressDialog(LoginActivity.this);
		dialog.show();
		try {
			userName = URLEncoder.encode(userName, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		;
		RequestQueue requestQueue = MyApplication.queue;
		StringRequest stringRequest = new StringRequest(Request.Method.GET,
				MyContants.URL_LOGIN + "nickname=" + userName + "&password="
						+ password + "&cid=" + cid,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						String result = "failed";
						JSONObject json = null;
						Log.e("zyy", response);
						try {
							json = new JSONObject(response);
							result = (String) json.get("message");
						} catch (JSONException e1) {
							e1.printStackTrace();
						}
						dialog.dismiss();
						dialog.cancel();
						if (result.equals("success")) {
							Gson gson = new Gson();
							try {
								User user = gson.fromJson(json.get("user")
										.toString(), User.class);
								carports = gson.fromJson(json.get("carports")
										.toString(),
										new TypeToken<List<Carport>>() {
										}.getType());
								photos = gson.fromJson(json.get("photos")
										.toString(),
										new TypeToken<List<Photo>>() {
										}.getType());
								try {
									DbUtils db = DbOperate.getInstance();
									try {
										user.setNickName(new String(user
												.getNickName().getBytes(
														"ISO8859-1"), "utf-8"));
										for (Carport temp : carports) {
											temp.setAddr(new String(temp
													.getAddr().getBytes(
															"ISO8859-1"),
													"utf-8"));
											temp.setDescribe(new String(temp
													.getDescribe().getBytes(
															"ISO8859-1"),
													"utf-8"));
											temp.setState(new String(temp
													.getState().getBytes(
															"ISO8859-1"),
													"utf-8"));
										}
									} catch (UnsupportedEncodingException e) {
										e.printStackTrace();
									}
									db.saveAll(carports);
									db.saveAll(photos);
									db.save(user);
									SharedPreferencesUtil.saveData(
											LoginActivity.this, "user_id",
											user.getId());
								} catch (DbException e) {
									e.printStackTrace();
								}
								intent = new Intent(LoginActivity.this,
										MainActivity.class);
							} catch (JSONException e) {
								e.printStackTrace();
							}
							SharedPreferencesUtil.saveData(LoginActivity.this,
									"login_state", true);
							Toast.makeText(LoginActivity.this, "登陆成功！",
									Toast.LENGTH_LONG).show();
							startActivity(intent);
							finish();
						} else {
							Crouton.makeText(LoginActivity.this, "密码错误或账号不存在",
									Style.ALERT,
									R.id.login_alternate_view_group).show();
						}

					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						dialog.dismiss();
						dialog.cancel();
						Crouton.makeText(LoginActivity.this, "服务器出错！",
								Style.ALERT, R.id.login_alternate_view_group)
								.show();
					}
				});
		requestQueue.add(stringRequest);
	}

	private void loginByQQ() {
		dialog = new ProgressDialog(LoginActivity.this);
		dialog.show();
		RequestQueue requestQueue = MyApplication.queue;
		StringRequest stringRequest = new StringRequest(Request.Method.GET,
				MyContants.URL_LOGIN
						+ "&password="
						+ password
						+ "&cid="
						+ PushManager.getInstance().getClientid(
								LoginActivity.this),
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						String result = "failed";
						JSONObject json = null;
						try {

							json = new JSONObject(response);
							result = (String) json.get("message");
						} catch (JSONException e1) {
							e1.printStackTrace();
						}
						dialog.dismiss();
						dialog.cancel();
						if (result.equals("success")) {
							Gson gson = new Gson();
							try {
								User user = gson.fromJson(json.get("user")
										.toString(), User.class);
								carports = gson.fromJson(json.get("carports")
										.toString(),
										new TypeToken<List<Carport>>() {
										}.getType());
								photos = gson.fromJson(json.get("photos")
										.toString(),
										new TypeToken<List<Photo>>() {
										}.getType());
								try {
									DbUtils db = DbOperate.getInstance();
									try {
										user.setNickName(new String(user
												.getNickName().getBytes(
														"ISO8859-1"), "utf-8"));
										for (Carport temp : carports) {
											temp.setAddr(new String(temp
													.getAddr().getBytes(
															"ISO8859-1"),
													"utf-8"));
											temp.setDescribe(new String(temp
													.getDescribe().getBytes(
															"ISO8859-1"),
													"utf-8"));
											temp.setState(new String(temp
													.getState().getBytes(
															"ISO8859-1"),
													"utf-8"));
										}
									} catch (UnsupportedEncodingException e) {
										e.printStackTrace();
									}
									db.saveAll(carports);
									db.saveAll(photos);
									db.save(user);
									SharedPreferencesUtil.saveData(
											LoginActivity.this, "user_id",
											user.getId());
								} catch (DbException e) {
									e.printStackTrace();
								}
								intent = new Intent(LoginActivity.this,
										MainActivity.class);
							} catch (JSONException e) {
								e.printStackTrace();
							}
							SharedPreferencesUtil.saveData(LoginActivity.this,
									"login_state", true);
							Toast.makeText(LoginActivity.this, "登陆成功！",
									Toast.LENGTH_LONG).show();
							startActivity(intent);
							finish();
						} else {
							Crouton.makeText(LoginActivity.this, "密码错误或账号不存在",
									Style.ALERT,
									R.id.login_alternate_view_group).show();
						}

					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						dialog.dismiss();
						dialog.cancel();
						Crouton.makeText(LoginActivity.this, "服务器出错！",
								Style.ALERT, R.id.login_alternate_view_group)
								.show();
					}
				});
		requestQueue.add(stringRequest);
	}

	public void bt_listen(View view) {
		if (!et_login_account.getText().toString().equals("")
				&& !et_login_password.getText().toString().equals("")) {
			userName = et_login_account.getText().toString();
			password = et_login_password.getText().toString();
			login();
		} else {
			Crouton.makeText(LoginActivity.this, "账号密码不能为空！", Style.ALERT,
					R.id.login_alternate_view_group).show();
		}
	}

	/*
	 * qq登陆监听
	 */
	public void qqLogin_login(View view) {
		if (!mQQAuth.isSessionValid()) {
			IUiListener listener = new BaseUiListener() {
				@Override
				protected void doComplete(JSONObject values) {
					updateUserInfo();
				}
			};
			mQQAuth.login(this, "all", listener);
			mTencent.login(this, "all", listener);
		} else {
			mQQAuth.logout(this);
			updateUserInfo();

		}
	}

	/*
	 * 获取信息
	 */
	private void updateUserInfo() {
		if (mQQAuth != null && mQQAuth.isSessionValid()) {
			IUiListener listener = new IUiListener() {
				@Override
				public void onError(UiError e) {
					Crouton.makeText(LoginActivity.this, "获取用户信息失败",
							Style.ALERT, R.id.login_alternate_view_group)
							.show();
				}

				@Override
				public void onComplete(final Object response) {
					myThread = new Thread() {
						@Override
						public void run() {
							JSONObject json = (JSONObject) response;
							if (json.has("figureurl")) {
								isFinish = 1;
							} else
								isFinish = 2;
						}
					};
					myThread.start();
					while (true) {
						if (isFinish == 1) {
							loginByQQ();
							break;
						} else {
							if (isFinish == 2) {
								Crouton.makeText(LoginActivity.this,
										"获取用户信息失败", Style.ALERT,
										R.id.login_alternate_view_group).show();
							}
						}
					}
				}

				@Override
				public void onCancel() {
				}
			};
			mInfo = new UserInfo(this, mQQAuth.getQQToken());
			mInfo.getUserInfo(listener);
		} else {
			Crouton.makeText(LoginActivity.this, "未认证，无法登陆，请再试一次", Style.ALERT,
					R.id.login_alternate_view_group).show();
		}
	}

	/*
	 * qq登陆类
	 */
	private class BaseUiListener implements IUiListener {
		@Override
		public void onComplete(Object response) {
			JSONObject json = (JSONObject) response;
			try {
				password = json.getString("openid");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			doComplete((JSONObject) response);
		}

		protected void doComplete(JSONObject values) {
		}

		@Override
		public void onError(UiError e) {
			Crouton.makeText(LoginActivity.this, e.toString(), Style.ALERT,
					R.id.login_alternate_view_group).show();
		}

		@Override
		public void onCancel() {
		}
	}

}
