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
import com.zhangyanye.didipark.pojo.Order;
import com.zhangyanye.didipark.utils.BFImageCache;
import com.zhangyanye.didipark.utils.MyContants;
import com.zhangyanye.didipark.utils.SharedPreferencesUtil;
import com.zhangyanye.didipark.view.CircleImageView;
import com.zhangyanye.didipark.view.ToolBarOnClickListener;
import com.zhangyanye.didipark.view.TopBar;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class MyOrderActivity extends Activity {
	private TopBar topbar;
	private ListView listView;
	private List<Order> orders;
	private SwipeRefreshLayout freshLayout;
	private List<String> addrs, photos, names;
	private ImageLoader imageLoader;
	private BaseAdapter myAdapter;
	private Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order);
		initView();
		getOrder();
	}

	@SuppressWarnings("deprecation")
	private void initView() {
		intent=new Intent(MyOrderActivity.this,MoreOrderActvity.class);
		myAdapter = new MyAdapter(MyOrderActivity.this);
		imageLoader = new ImageLoader(MyApplication.queue,
				BFImageCache.getInstance());
		freshLayout = (SwipeRefreshLayout) findViewById(R.id.order_fresh_swipe);
		freshLayout.setColorScheme(android.R.color.holo_blue_dark,
				android.R.color.holo_green_light,
				android.R.color.holo_green_light,
				android.R.color.holo_blue_light);
		freshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				getOrder();
			}
		});
		topbar = (TopBar) findViewById(R.id.order_topBar);
		topbar.setOnTopbarClickListener(new ToolBarOnClickListener() {
			@Override
			public void rightBtnClick() {

			}

			@Override
			public void leftBtnClick() {
				finish();
			}
		});
		listView = (ListView) findViewById(R.id.order_listView);
        listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Bundle bundle=new Bundle();
				bundle.putSerializable("order", orders.get(position));
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	private void getOrder() {
		freshLayout.setRefreshing(true);
		RequestQueue requestQueue = MyApplication.queue;
		StringRequest stringRequest = new StringRequest(Request.Method.GET,
				MyContants.URL_GET_ORDER
						+ "userId="
						+ SharedPreferencesUtil.getData(MyOrderActivity.this,
								"user_id", 0), new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						Gson gson = new Gson();
						try {
							JSONObject json=new JSONObject(response);
							orders = gson.fromJson(json.get("orders")
									.toString(), new TypeToken<List<Order>>() {
							}.getType());
							photos = gson.fromJson(json.get("photos")
									.toString(), new TypeToken<List<String>>() {
							}.getType());
							addrs = gson.fromJson(json.get("addrs").toString(),
									new TypeToken<List<String>>() {
									}.getType());
							names = gson.fromJson(json.get("names").toString(),
									new TypeToken<List<String>>() {
									}.getType());
							listView.setAdapter(myAdapter);
							freshLayout.setRefreshing(false);
							if(orders.size()==0)
								Crouton.makeText(MyOrderActivity.this, "您还没有相关记录哦！",
										Style.CONFIRM, R.id.order_alternate_view_group)
										.show();
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Crouton.makeText(MyOrderActivity.this, "服务器出错！",
								Style.ALERT, R.id.order_alternate_view_group)
								.show();
						freshLayout.setRefreshing(false);
					}
				});
		requestQueue.add(stringRequest);
	}

	private class MyAdapter extends BaseAdapter {
		private Context mContext;

		public MyAdapter(Context context) {
			this.mContext = context;
		}

		/**
		 * 元素的个数
		 */
		public int getCount() {
			if (orders != null)
				return orders.size();
			else
				return 0;
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		// 用以生成在ListView中展示的一个个元素View
		@SuppressLint("InflateParams")
		@SuppressWarnings("deprecation")
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			// 优化ListView
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.item_customer, null);
				ItemViewCache viewCache = new ItemViewCache();
				viewCache.tv_addr = (TextView) convertView
						.findViewById(R.id.order_tv_place);
				viewCache.tv_time = (TextView) convertView
						.findViewById(R.id.order_tv_time);
				viewCache.tv_money = (TextView) convertView
						.findViewById(R.id.order_tv_money);
				viewCache.tv_name = (TextView) convertView
						.findViewById(R.id.order_tv_nick);
				viewCache.iv_photo = (CircleImageView) convertView
						.findViewById(R.id.order_iv_img);
				convertView.setTag(viewCache);

			}
			ItemViewCache cache = (ItemViewCache) convertView.getTag();
			// 设置文本和图片，然后返回这个View，用于ListView的Item的展示
			if (photos.get(position) != null) {
				ImageListener listener = ImageLoader.getImageListener(cache.iv_photo,
						R.drawable.ic_user_on, R.drawable.ic_user_on);
				imageLoader.get(photos.get(position), listener);
			} else
				cache.iv_photo.setImageDrawable
				(getResources().getDrawable(
						R.drawable.ic_figure_def));
			cache.tv_money.setText("+" + orders.get(position).getMoney());
			try {
				cache.tv_name.setText("昵称："+new String(names.get(position)
				        .getBytes("ISO-8859-1"), "UTF-8"));
				cache.tv_addr.setText(new String(addrs.get(position)
				        .getBytes("ISO-8859-1"), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			cache.tv_time.setText(orders.get(position).getTime().substring(orders.get(position).getTime().length()-13, orders.get(position).getTime().length()-3));
			return convertView;
		}
	}

	private static class ItemViewCache {
		public TextView tv_addr, tv_money, tv_time, tv_name;
		public CircleImageView iv_photo;
	}

}
