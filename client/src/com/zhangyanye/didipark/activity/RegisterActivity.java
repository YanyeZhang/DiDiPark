package com.zhangyanye.didipark.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.igexin.sdk.PushManager;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQAuth;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.UsersAPI;
import com.weibo.sdk.android.net.RequestListener;
import com.weibo.sdk.android.sso.SsoHandler;
import com.zhangyanye.didipark.R;
import com.zhangyanye.didipark.application.MyApplication;
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
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 用户注册
 * 
 * @author zhangyanye
 *
 */
public class RegisterActivity extends Activity {

	private Intent intent;
	private EditText et_account, et_password, et_confirm_password;
	private CheckBox checkBox;
	private RelativeLayout rl_account, rl_password, rl_confirm_password;
	private TextView tv_terms, tv_policy;
	private Button bt_register;
	private String userName, phone, openId, qq_imageUrl;
	private Thread myThread;
	private String cid = "";
	private ProgressDialog dialog;
	private int isFinish = 0;
	// 0 普通，1 qq 2 微博
	private int register_mode = 0;
	// 第三方服务
	private QQAuth mQQAuth;
	private Tencent mTencent;
	private final String CONSUMER_KEY = "2366049830";
	private final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
	private final String QQ_APP_ID = "1104505966";
	private String SMS_APPKEY = "689a07e36072";
	private String SMS_APPSECRET = "3b36629e632dbe0c884aef2ba28790cf";
	private UserInfo mInfo;
	public Oauth2AccessToken mAccessToken;
	private SsoHandler mSsoHandler;
    private Weibo mWeibo;
    private String uid;
    
    
	private class AuthListener implements WeiboAuthListener {

		@Override
		public void onCancel() {		
		}
		@Override
		public void onComplete(Bundle values) {
			uid=values.getString("uid");
			register_mode = 2;
			SMSIntent();
		}

		@Override
		public void onError(WeiboDialogError arg0) {
		}

		@Override
		public void onWeiboException(WeiboException arg0) {
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// SSO 授权回调
		// 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
		if (mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		initView();
		setTextViewUrl();
		getCid();
		mQQAuth = QQAuth.createInstance(QQ_APP_ID, this);
		mTencent = Tencent.createInstance(QQ_APP_ID, this);
		mWeibo = Weibo.getInstance(CONSUMER_KEY, REDIRECT_URL);
		SMSSDK.initSDK(this, SMS_APPKEY, SMS_APPSECRET);

	}

	public void registerWeiBo(View v) {
		mSsoHandler =new SsoHandler(RegisterActivity.this,mWeibo);
        mSsoHandler.authorize( new AuthListener());
	}

	/*
	 * 跳转至用户登陆
	 */
	public void intentLogin(View view) {
		intent = new Intent(RegisterActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
	}

	private void getCid() {

		if (PushManager.getInstance().getClientid(RegisterActivity.this) != null)
			cid = PushManager.getInstance().getClientid(RegisterActivity.this);

	}

	/*
	 * 初始化view
	 */
	private void initView() {
		et_account = (EditText) findViewById(R.id.et_register_account);
		et_password = (EditText) findViewById(R.id.et_register_password);
		et_confirm_password = (EditText) findViewById(R.id.et_register_confirm_password);
		checkBox = (CheckBox) findViewById(R.id.checkBox);
		rl_account = (RelativeLayout) findViewById(R.id.rl_register_account);
		rl_password = (RelativeLayout) findViewById(R.id.rl_register_password);
		rl_confirm_password = (RelativeLayout) findViewById(R.id.rl_register_confirm_password);
		tv_policy = (TextView) findViewById(R.id.tv_policy);
		tv_terms = (TextView) findViewById(R.id.tv_terms);
		rl_account.setFocusable(true);
		rl_account.setFocusableInTouchMode(true);
		bt_register = (Button) findViewById(R.id.btn_register);
	}

	/*
	 * 注册按钮事件
	 */
	public void bt_register(View view) {
		if (!bt_register.getText().toString().equals("注册")) {
			if (checkBox.isChecked() == true) {
				Crouton.makeText(RegisterActivity.this, "请阅读条款与政策",
						Style.ALERT, R.id.alternate_view_group).show();
			} else {
				if (et_confirm_password.getText().toString().equals("")
						|| et_account.getText().toString().equals("")
						|| et_password.getText().toString().equals("")) {
					Crouton.makeText(RegisterActivity.this, "请填写完资料",
							Style.ALERT, R.id.alternate_view_group).show();
				} else if (et_password.getText().toString().length() < 6) {
					Crouton.makeText(RegisterActivity.this, "密码长度不符合",
							Style.ALERT, R.id.alternate_view_group).show();
					et_password.setText("");
					et_confirm_password.setText("");
				} else {
					if (!et_confirm_password.getText().toString()
							.equals(et_password.getText().toString())) {
						Crouton.makeText(RegisterActivity.this, "两次输入密码不同",
								Style.ALERT, R.id.alternate_view_group).show();
						et_password.setText("");
						et_confirm_password.setText("");
					} else {
						SMSIntent();
					}
				}
			}
		} else {
			if (register_mode == 0) { // 普通注册
				register(et_password.getText().toString(), et_confirm_password
						.getText().toString(), phone, null);
			} else if (register_mode == 1) {
				// qq注册
				register(userName, openId, phone, qq_imageUrl);
			} else if (register_mode == 2) {
				// 微博注册
				register("微博网友"+uid, uid, phone, null);
			}
		}
	}

	/*
	 * 进入短信验证
	 */
	private void SMSIntent() {
		RegisterPage registerPage = new RegisterPage();
		registerPage.setRegisterCallback(new EventHandler() {
			public void afterEvent(int event, int result, Object data) {
				// 解析注册结果
				if (result == SMSSDK.RESULT_COMPLETE) {
					@SuppressWarnings("unchecked")
					HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
					phone = (String) phoneMap.get("phone");
					bt_register.setText("注册");
					et_account.setEnabled(false);
					et_password.setEnabled(false);
					et_confirm_password.setEnabled(false);
					if (register_mode == 0)
						register(et_account.getText().toString(),
								et_confirm_password.getText().toString(),
								phone, null);
					else if (register_mode == 1) {
						et_account.setText("");
						et_password.setText("");
						et_confirm_password.setText("");
						updateUserInfo();
					} else if (register_mode == 2) {
						et_account.setText("");
						et_password.setText("");
						et_confirm_password.setText("");
						register("微博网友"+uid, uid, phone, null);
					}
				}
			}
		});
		registerPage.show(this);
	}

	/*
	 * 用户注册
	 * 
	 * @param 账号，密码，电话
	 */
	private void register(final String account, final String password,
			final String phone, final String imageUrl) {
		dialog = new ProgressDialog(RegisterActivity.this);
		dialog.show();
		String temp = null;
		try {
			temp = URLEncoder.encode(account, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		final String nickname = temp;
		RequestQueue requestQueue = MyApplication.queue;
		StringRequest stringRequest = new StringRequest(Request.Method.POST,
				MyContants.URL_REGISTER, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						String result = "failed";
						Gson gson = new Gson();
						Log.e("zyy", response.toString());
						try {
							JSONObject json = new JSONObject(response);
							result = (String) json.get("message");
							User user = gson.fromJson(json.get("user")
									.toString(), User.class);

							try {
								user.setNickName(new String(user.getNickName()
										.getBytes("ISO-8859-1"), "UTF-8"));
								DbUtils db = DbOperate.getInstance();
								db.save(user);
							} catch (Exception e) {
								e.printStackTrace();
							}
							SharedPreferencesUtil.saveData(
									RegisterActivity.this, "user_id",
									user.getId());
							intent = new Intent(RegisterActivity.this,
									MainActivity.class);
						} catch (JSONException e1) {
							e1.printStackTrace();
						}
						dialog.dismiss();
						dialog.cancel();
						if (result.equals("success")) {
							SharedPreferencesUtil.saveData(
									RegisterActivity.this, "login_state", true);
							Toast.makeText(RegisterActivity.this, "注册成功！",
									Toast.LENGTH_LONG).show();
							startActivity(intent);
							finish();
						} else if (result.equals("phone non-uniqueness")) {
							Crouton.makeText(RegisterActivity.this, "手机号已被注册！",
									Style.ALERT, R.id.alternate_view_group)
									.show();
						} else if (result.equals("register failed")) {
							Crouton.makeText(RegisterActivity.this, "注册失败！",
									Style.ALERT, R.id.alternate_view_group)
									.show();
						}

					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						dialog.dismiss();
						dialog.cancel();
						Crouton.makeText(RegisterActivity.this, "服务器出错！",
								Style.ALERT, R.id.alternate_view_group).show();
					}
				}) {
			@Override
			protected Map<String, String> getParams() {
				// 在这里设置需要post的参数
				Map<String, String> params = new HashMap<String, String>();
				params.put("nickname", nickname);
				params.put("password", password);
				params.put("phone", phone);
				params.put("clientid", cid);
				if (imageUrl != null)
					params.put("imageUrl", imageUrl);
				return params;
			}
		};
		requestQueue.add(stringRequest);
	}

	/*
	 * textview——url 隐私政策与服务条款
	 */
	private void setTextViewUrl() {
		// SpannableString字体修改类
		SpannableString text_policy = new SpannableString(" 隐私政策 ");
		text_policy.setSpan(new URLSpanNoUnderline(MyContants.PRIVACY_POLICY),
				0, text_policy.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		tv_policy.setText(text_policy);
		tv_policy.setMovementMethod(LinkMovementMethod.getInstance());

		SpannableString text_terms = new SpannableString(" 服务条款 ");
		text_terms.setSpan(new URLSpanNoUnderline(MyContants.SERVICE_TERMS), 0,
				text_terms.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		tv_terms.setText(text_terms);
		tv_terms.setMovementMethod(LinkMovementMethod.getInstance());
	}

	/*
	 * editText 背景变化监听
	 */
	public void editTextSelector(View view) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		switch (view.getId()) {
		case R.id.rl_register_account:
			rl_account.setBackgroundResource(R.drawable.signup_email_focus);
			rl_password.setBackgroundResource(R.drawable.signup_password);
			rl_confirm_password
					.setBackgroundResource(R.drawable.signup_password);
			et_account.setFocusable(true);
			et_account.setFocusableInTouchMode(true);
			et_password.setFocusable(false);
			et_password.setFocusableInTouchMode(false);
			et_confirm_password.setFocusable(false);
			et_confirm_password.setFocusableInTouchMode(false);
			et_account.requestFocus();
			et_account.requestFocusFromTouch();
			imm.showSoftInputFromInputMethod(et_account.getWindowToken(), 0);
			break;
		case R.id.rl_register_password:
			rl_account.setBackgroundResource(R.drawable.signup_email);
			rl_password.setBackgroundResource(R.drawable.signup_password_focus);
			rl_confirm_password
					.setBackgroundResource(R.drawable.signup_password);
			et_password.setFocusable(true);
			et_password.setFocusableInTouchMode(true);
			et_account.setFocusable(false);
			et_account.setFocusableInTouchMode(false);
			et_confirm_password.setFocusable(false);
			et_confirm_password.setFocusableInTouchMode(false);
			et_password.requestFocus();
			et_password.requestFocusFromTouch();
			imm.showSoftInputFromInputMethod(et_password.getWindowToken(), 0);
			break;
		case R.id.rl_register_confirm_password:
			rl_account.setBackgroundResource(R.drawable.signup_email);
			rl_password.setBackgroundResource(R.drawable.signup_password);
			rl_confirm_password
					.setBackgroundResource(R.drawable.signup_password_focus);
			et_confirm_password.setFocusable(true);
			et_confirm_password.setFocusableInTouchMode(true);
			et_password.setFocusable(false);
			et_password.setFocusableInTouchMode(false);
			et_account.setFocusable(false);
			et_account.setFocusableInTouchMode(false);
			et_confirm_password.requestFocus();
			et_confirm_password.requestFocusFromTouch();
			imm.showSoftInputFromInputMethod(
					et_confirm_password.getWindowToken(), 0);
			break;
		}
	}

	/*
	 * 重写URLSpan
	 */
	public class URLSpanNoUnderline extends URLSpan {
		public URLSpanNoUnderline(String url) {
			super(url);
		}

		@Override
		public void updateDrawState(TextPaint ds) {
			super.updateDrawState(ds);
			ds.setUnderlineText(false);
			ds.setColor(getResources().getColor(R.color.splash_color));
		}
	}

	/*
	 * qq登陆事件监听
	 */
	public void qqLogin_register(View v) {
		if (!mQQAuth.isSessionValid()) {
			IUiListener listener = new BaseUiListener() {
				@Override
				protected void doComplete(JSONObject values) {
					register_mode = 1;
					SMSIntent();
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
	 * qq登陆成功后获取用户信息
	 */
	private void updateUserInfo() {
		if (mQQAuth != null && mQQAuth.isSessionValid()) {
			IUiListener listener = new IUiListener() {
				@Override
				public void onError(UiError e) {
					Crouton.makeText(RegisterActivity.this, "获取用户信息失败",
							Style.ALERT, R.id.alternate_view_group).show();
				}

				@Override
				public void onComplete(final Object response) {
					myThread = new Thread() {
						@Override
						public void run() {
							JSONObject json = (JSONObject) response;
							if (json.has("figureurl")) {
								try {
									qq_imageUrl = json
											.getString("figureurl_qq_2");
									intent = new Intent(RegisterActivity.this,
											MainActivity.class);
									Log.e("json", json.toString());
									userName = json.getString("nickname");
									isFinish = 1;
									//
									/*
									 * intent.putExtra("bitmap", bitmapByte);
									 * startActivity(intent); //
									 * handler.sendEmptyMessage(2); finish();
									 */
								} catch (JSONException e) {
									isFinish = 2;
								}
							}
						}
					};
					myThread.start();
					while (true) {
						if (isFinish == 1) {
							register(userName, openId, phone, qq_imageUrl);
							break;
						} else {
							if (isFinish == 2) {
								Crouton.makeText(RegisterActivity.this,
										"获取用户信息失败", Style.ALERT,
										R.id.alternate_view_group).show();
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
			Crouton.makeText(RegisterActivity.this, "未认证，无法登陆", Style.ALERT,
					R.id.alternate_view_group).show();
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
				openId = json.getString("openid");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			doComplete((JSONObject) response);
		}

		protected void doComplete(JSONObject values) {
		}

		@Override
		public void onError(UiError e) {
			Crouton.makeText(RegisterActivity.this, e.toString(), Style.ALERT,
					R.id.alternate_view_group).show();
		}

		@Override
		public void onCancel() {
		}
	}

}
