package com.zhangyanye.didipark.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
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
import com.bmob.pay.tool.BmobPay;
import com.bmob.pay.tool.OrderQueryListener;
import com.bmob.pay.tool.PayListener;
import com.google.gson.Gson;
import com.zhangyanye.didipark.R;
import com.zhangyanye.didipark.application.MyApplication;
import com.zhangyanye.didipark.pojo.Carport;
import com.zhangyanye.didipark.utils.BFImageCache;
import com.zhangyanye.didipark.utils.MyContants;
import com.zhangyanye.didipark.utils.SharedPreferencesUtil;
import com.zhangyanye.didipark.view.MyNotification;
import com.zhangyanye.didipark.view.ToolBarOnClickListener;
import com.zhangyanye.didipark.view.TopBar;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PayMoneyActivity extends Activity {
	private BmobPay bmobPay;
	private Dialog dialog;
	private TextView tv_addr, tv_money, tv_time;
	private CheckBox payByAli, payByWeChat;
	private Carport carport;
	private double money;
	private String type;
	private TopBar topbar;
	private ImageLoader imageLoader;
	private ImageListener listener_carport;
	private ImageView photo;
	private String orderId;
	private Intent intent;
	private MyNotification myNotification;
	private OrderQueryListener orderQueryListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pay);
		initView();
		getCarport();
		BmobPay.init(this, "f7fcb51d4c7fe3222cf96d24c8bc3158");
		bmobPay = new BmobPay(PayMoneyActivity.this);
	}

	private void initView() {
		myNotification=MyNotification.getInstance(PayMoneyActivity.this);
		myNotification.dismissNotification(0);
		intent = new Intent(PayMoneyActivity.this, MainActivity.class);
		topbar = (TopBar) findViewById(R.id.pay_topBar);
		tv_time = (TextView) findViewById(R.id.pay_tv_time);
		tv_money = (TextView) findViewById(R.id.pay_tv_money);
		tv_addr = (TextView) findViewById(R.id.pay_tv_addr);
		imageLoader = new ImageLoader(MyApplication.queue,
				BFImageCache.getInstance());
		payByAli = (CheckBox) findViewById(R.id.pay_cb_ali);
		payByWeChat = (CheckBox) findViewById(R.id.pay_cb_wechat);
		photo = (ImageView) findViewById(R.id.pay_iv_photo);
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
		;
		payByAli.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked == true)
					payByWeChat.setChecked(false);
				else {
					payByWeChat.setChecked(true);
				}
			}
		});
		payByWeChat.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked == true)
					payByAli.setChecked(false);
				else
					payByAli.setChecked(true);
			}
		});
		orderQueryListener = new OrderQueryListener() {

			@Override
			public void succeed(String status) {
				if (status.equals("SUCCESS"))
					submitOrder();
				else if (status.equals("NOTPAY")) {
					Crouton.makeText(PayMoneyActivity.this, "支付失败！",
							Style.CONFIRM, R.id.pay_alternate_view_group)
							.show();
				}
			}

			@Override
			public void fail(int code, String reason) {
				Crouton.makeText(PayMoneyActivity.this, "网络连接错误！", Style.ALERT,
						R.id.pay_alternate_view_group).show();
			}
		};
	}

	public void payMoney(View v) {
		if (payByAli.isChecked() == true) {
			type = "余额宝";
			payByAli();
		} else if (payByWeChat.isChecked() == true) {
			type = "微信支付";
			payByWeiXin();
		}
	}

	private void getCarport() {
		dialog = new ProgressDialog(PayMoneyActivity.this);
		dialog.show();
		RequestQueue requestQueue = MyApplication.queue;
		StringRequest stringRequest = new StringRequest(Request.Method.GET,
				MyContants.URL_ORDER_DETAIL + "&carport_id="
						+ getIntent().getStringExtra("carportId"),
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						Gson gson = new Gson();
						try {
							JSONObject json = new JSONObject(response);
							carport = gson.fromJson(json.get("carport")
									.toString(), Carport.class);
							carport.setAddr(new String(carport.getAddr()
									.getBytes("ISO8859-1"), "utf-8"));
							carport.setDescribe(new String(carport
									.getDescribe().getBytes("ISO8859-1"),
									"utf-8"));
							listener_carport = ImageLoader.getImageListener(
									photo, R.drawable.ic_carport_def,
									R.drawable.ic_carport_def);
							imageLoader.get(json.get("photo").toString(),
									listener_carport);
							tv_time.setText("订单时间："
									+ getIntent().getStringExtra("time"));
							tv_addr.setText(carport.getAddr());
							tv_money.setText(carport.getPerHoursMoney() + "");
							money = carport.getPerHoursMoney();
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
						Crouton.makeText(PayMoneyActivity.this, "服务器出错！",
								Style.ALERT, R.id.pay_alternate_view_group)
								.show();
					}
				});
		requestQueue.add(stringRequest);
	}

	private void payByAli() {
		dialog = new ProgressDialog(PayMoneyActivity.this);
		dialog.show();
		bmobPay.pay(0.02, "预支付停车费", new PayListener() {

			// 因为网络等原因,支付结果未知(小概率事件),出于保险起见稍后手动查询
			@Override
			public void fail(int arg0, String arg1) {
				dialog.dismiss();
				dialog.cancel();
				Crouton.makeText(PayMoneyActivity.this, "支付中断!", Style.INFO,
						R.id.pay_alternate_view_group).show();

			}

			// 支付成功,如果金额较大请手动查询确认
			@Override
			public void orderId(String result) {
				orderId = result;
			}

			// 无论成功与否,返回订单号
			@Override
			public void succeed() {
				bmobPay.query(orderId, orderQueryListener);
			}

			// 支付失败,原因可能是用户中断支付操作,也可能是网络原因
			@Override
			public void unknow() {
				Crouton.makeText(PayMoneyActivity.this, "支付结果未知,请稍后手动查询",
						Style.INFO, R.id.pay_alternate_view_group).show();
				dialog.cancel();
				dialog.dismiss();
			}
		});
	}

	private void payByWeiXin() {
		dialog = new ProgressDialog(PayMoneyActivity.this);
		dialog.show();
		bmobPay.payByWX(0.02, "预支付停车费", new PayListener() {
			// 因为网络等原因,支付结果未知(小概率事件),出于保险起见稍后手动查询
			@Override
			public void unknow() {
				Crouton.makeText(PayMoneyActivity.this, "支付结果未知,请稍后手动查询",
						Style.INFO, R.id.pay_alternate_view_group).show();
				dialog.cancel();
				dialog.dismiss();
			}

			// 支付成功,如果金额较大请手动查询确认
			@Override
			public void succeed() {
				bmobPay.query(orderId, orderQueryListener);
			}

			// 无论成功与否,返回订单号
			@Override
			public void orderId(String result) {
				// 此处应该保存订单号,比如保存进数据库等,以便以后查询
				orderId = result;
			}

			// 支付失败,原因可能是用户中断支付操作,也可能是网络原因
			@Override
			public void fail(int code, String reason) {
				dialog.cancel();
				dialog.dismiss();
				// 当code为-2,意味着用户中断了操作
				// code为-3意味着没有安装BmobPlugin插件
				if (code == -3) {
					new AlertDialog.Builder(PayMoneyActivity.this)
							.setMessage(
									"监测到你尚未安装支付插件,无法进行微信支付,请选择安装插件(无流量消耗)还是用支付宝支付")
							.setPositiveButton("安装",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											installBmobPayPlugin("WeiChatPayPlugin.apk");
										}
									})
							.setNegativeButton("支付宝支付",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											payByAli();
										}
									}).create().show();
				} else {
					Crouton.makeText(PayMoneyActivity.this, "支付中断!",
							Style.INFO, R.id.pay_alternate_view_group).show();
				}
			}
		});
	}

	private void submitOrder() {
		RequestQueue requestQueue = MyApplication.queue;
		StringRequest stringRequest = new StringRequest(Request.Method.POST,
				MyContants.URL_CREATE_ORDER, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						System.out.println(response.toString());
						dialog.dismiss();
						dialog.cancel();
						Toast.makeText(PayMoneyActivity.this, 
								"支付成功，您已经可以入停！", Toast.LENGTH_LONG).show();
						intent.setClassName("com.zhangyanye.didipark.activity",
								"MainActivity");
						if (intent.resolveActivity(getPackageManager()) == null) {
							// 说明系统中不存在这个activity
							startActivity(intent);
						}
						System.gc();
						finish();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						dialog.dismiss();
						dialog.cancel();
						Log.e("zyy", error.toString());
						Crouton.makeText(PayMoneyActivity.this, "网络连接错误！",
								Style.ALERT, R.id.pay_alternate_view_group)
								.show();
					}
				}) {
			@Override
			protected Map<String, String> getParams() {
				// 在这里设置需要post的参数
				Map<String, String> params = new HashMap<String, String>();
				params.put("time", getIntent().getStringExtra("time"));
				params.put("orderId", orderId);
				params.put("carport_id", getIntent()
						.getStringExtra("carportId"));
				params.put("type", type);
				params.put("money", money + "");
				params.put("user_id", String.valueOf(SharedPreferencesUtil
						.getData(PayMoneyActivity.this, "user_id", 0)));
				return params;
			}
		};
		requestQueue.add(stringRequest);
	}

	private void installBmobPayPlugin(String fileName) {
		try {
			InputStream is = getAssets().open(fileName);
			File file = new File(Environment.getExternalStorageDirectory()
					+ File.separator + fileName);
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			byte[] temp = new byte[1024];
			int i = 0;
			while ((i = is.read(temp)) > 0) {
				fos.write(temp, 0, i);
			}
			fos.close();
			is.close();
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.parse("file://" + file),
					"application/vnd.android.package-archive");
			startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
