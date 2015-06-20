package com.zhangyanye.didipark.activity;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhangyanye.didipark.R;
import com.zhangyanye.didipark.application.MyApplication;
import com.zhangyanye.didipark.pojo.Carport;
import com.zhangyanye.didipark.pojo.Order;
import com.zhangyanye.didipark.pojo.User;
import com.zhangyanye.didipark.utils.BFImageCache;
import com.zhangyanye.didipark.utils.MyContants;
import com.zhangyanye.didipark.utils.SharedPreferencesUtil;
import com.zhangyanye.didipark.view.CircleImageView;
import com.zhangyanye.didipark.view.ToolBarOnClickListener;
import com.zhangyanye.didipark.view.TopBar;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class MoreOrderActvity extends Activity {
	private TopBar topbar;
	private TextView tv_userName, tv_phone, tv_addr, tv_descr, tv_orderId,
			tv_orderTime, tv_orderMode, tv_orderMoney;
	private User user;
	private Carport carport;
	private ProgressDialog dialog;
	private ImageLoader imageLoader;
	private Order order;
	private CircleImageView iv_user;
	private ImageView iv_carport;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_more_order);
		initView();
		getBunde();
		getDetail();
	}

	private void initView() {
		imageLoader = new ImageLoader(MyApplication.queue,
				BFImageCache.getInstance());
		iv_user = (CircleImageView) findViewById(R.id.more_order_iv_figure);
		iv_carport = (ImageView) findViewById(R.id.more_order_iv_carport);
		tv_addr = (TextView) findViewById(R.id.more_order_tv_addr);
		tv_userName = (TextView) findViewById(R.id.more_order_tv_nickname);
		tv_phone = (TextView) findViewById(R.id.more_order_tv_phone);
		tv_descr = (TextView) findViewById(R.id.more_order_tv_descri);
		tv_orderId = (TextView) findViewById(R.id.more_order_id);
		tv_orderMode = (TextView) findViewById(R.id.more_order_mode);
		tv_orderTime = (TextView) findViewById(R.id.more_order_time);
		tv_orderMoney = (TextView) findViewById(R.id.more_order_money);
		topbar = (TopBar) findViewById(R.id.more_order_topBar);
		topbar.setOnTopbarClickListener(new ToolBarOnClickListener() {
			@Override
			public void rightBtnClick() {
				if (user.getPhone() != null) {
					Intent intent = new Intent(Intent.ACTION_CALL, Uri
							.parse("tel:" + user.getPhone()));
					startActivity(intent);
				}else{
					Crouton.makeText(MoreOrderActvity.this, "电话拨号失败！",
							Style.ALERT,
							R.id.more_order_alternate_view_group).show();
				}
			}

			@Override
			public void leftBtnClick() {
				finish();
			}
		});
	}

	private void getBunde() {
		Bundle bundle = getIntent().getExtras();
		order = (Order) bundle.getSerializable("order");
		tv_orderId.setText(order.getId());
		try {
			tv_orderMode.setText(new String(order.getType().getBytes(
					"ISO-8859-1"), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		tv_orderTime.setText(order.getTime());
		tv_orderMoney.setText(order.getMoney() + "");
	}

	private void getDetail() {
		dialog = new ProgressDialog(MoreOrderActvity.this);
		dialog.show();
		RequestQueue requestQueue = MyApplication.queue;
		StringRequest stringRequest = new StringRequest(Request.Method.GET,
				MyContants.URL_ORDER_INFO + "userId=" + order.getUser_id()
						+ "&carportId=" + order.getCarport_id(),
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						Gson gson = new Gson();
						dialog.dismiss();
						dialog.cancel();
						try {
							JSONObject json = new JSONObject(response);
							user = gson.fromJson(json.get("user").toString(),
									User.class);
							carport = gson.fromJson(json.get("carport")
									.toString(), Carport.class);
							tv_addr.setText(new String(carport.getAddr()
									.getBytes("ISO-8859-1"), "UTF-8"));
							tv_descr.setText(new String(carport.getDescribe()
									.getBytes("ISO-8859-1"), "UTF-8"));
							tv_userName.setText(new String(user.getNickName()
									.getBytes("ISO-8859-1"), "UTF-8"));
							tv_phone.setText("联系电话：" + user.getPhone());
							ImageListener listener = ImageLoader
									.getImageListener(iv_user,
											R.drawable.ic_figure_def,
											R.drawable.ic_figure_def);
							imageLoader.get(user.getImageUrl(), listener);

							ImageListener listener2 = ImageLoader
									.getImageListener(iv_carport,
											R.drawable.ic_carport_def,
											R.drawable.ic_carport_def);
							imageLoader.get(json.get("photo").toString(),
									listener2);

						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						dialog.dismiss();
						dialog.cancel();
						Crouton.makeText(MoreOrderActvity.this, "服务器出错！",
								Style.ALERT,
								R.id.more_order_alternate_view_group).show();
					}
				});
		requestQueue.add(stringRequest);
	}
}
