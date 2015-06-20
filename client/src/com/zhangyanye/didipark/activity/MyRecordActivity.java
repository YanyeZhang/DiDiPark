package com.zhangyanye.didipark.activity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhangyanye.didipark.R;
import com.zhangyanye.didipark.application.MyApplication;
import com.zhangyanye.didipark.pojo.Carport;
import com.zhangyanye.didipark.pojo.Comment;
import com.zhangyanye.didipark.pojo.Order;
import com.zhangyanye.didipark.pojo.Photo;
import com.zhangyanye.didipark.utils.BFImageCache;
import com.zhangyanye.didipark.utils.MyContants;
import com.zhangyanye.didipark.utils.SharedPreferencesUtil;
import com.zhangyanye.didipark.view.ToolBarOnClickListener;
import com.zhangyanye.didipark.view.TopBar;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class MyRecordActivity extends Activity {
	private ProgressDialog dialog;
	private TopBar topbar;
	private List<Carport> carports, no_carports;
	private List<Photo> photos, no_photos;
	private List<Order> orders, no_orders;
	private ListView listView_all, listView_no;
	private ImageLoader imageLoader;
	private BaseAdapter allAdapter, noAdapter;
	private ViewPager pager = null;
	private List<Comment> comments;
	private PagerTabStrip tabStrip = null;
	private ArrayList<View> viewContainter = new ArrayList<View>();
	private ArrayList<String> titleContainer = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record);
		initView();
		initPaper();
		getRecord();
	}

	@SuppressLint("InlinedApi")
	private void initView() {
		pager = (ViewPager) this.findViewById(R.id.record_viewpager);
		tabStrip = (PagerTabStrip) findViewById(R.id.record_tabstrip);
		tabStrip.setBackgroundColor(this.getResources().getColor(R.color.white));
		// 设置当前tab页签的下划线颜色
		tabStrip.setTabIndicatorColor(this.getResources().getColor(R.color.red));
		tabStrip.setTextSpacing(200);
		View view_all = LayoutInflater.from(this).inflate(
				R.layout.view_paper_all, null);
		View view_no = LayoutInflater.from(this).inflate(
				R.layout.view_paper_no, null);
		viewContainter.add(view_all);
		viewContainter.add(view_no);
		titleContainer.add("全部");
		titleContainer.add("未评价");
		imageLoader = new ImageLoader(MyApplication.queue,
				BFImageCache.getInstance());
		listView_all = (ListView) view_all
				.findViewById(R.id.record_all_listView);
		listView_no = (ListView) view_no.findViewById(R.id.record_no_listView);
		topbar = (TopBar) findViewById(R.id.record_topBar);
		topbar.setOnTopbarClickListener(new ToolBarOnClickListener() {

			@Override
			public void rightBtnClick() {
			}

			@Override
			public void leftBtnClick() {
				finish();
			}
		});
		noAdapter = new MyAdapterNo(MyRecordActivity.this);
		allAdapter = new MyAdapterAll(MyRecordActivity.this);
		listView_no.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Intent intent = new Intent(MyRecordActivity.this,
						MoreCarportActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("carport", no_carports.get(position));
				bundle.putString("photo", no_photos.get(position).getPhotoUrl());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		listView_all.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Intent intent = new Intent(MyRecordActivity.this,
						MoreCarportActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("carport", carports.get(position));
				bundle.putString("photo", photos.get(position).getPhotoUrl());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK)
			getRecord();
	}

	private void initPaper() {
		pager.setAdapter(new PagerAdapter() {

			// viewpager中的组件数量
			@Override
			public int getCount() {
				return viewContainter.size();
			}

			// 滑动切换的时候销毁当前的组件
			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {
				((ViewPager) container).removeView(viewContainter.get(position));
			}

			// 每次滑动的时候生成的组件
			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				((ViewPager) container).addView(viewContainter.get(position));
				return viewContainter.get(position);
			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getItemPosition(Object object) {
				return super.getItemPosition(object);
			}

			@Override
			public CharSequence getPageTitle(int position) {
				return titleContainer.get(position);
			}
		});

		pager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageSelected(int arg0) {
			}
		});

	}
    private void comment(int position){
    	Intent intent = new Intent(MyRecordActivity.this,
				AddCommentActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("carport", no_carports.get(position));
		bundle.putString("photo", no_photos.get(position).getPhotoUrl2());
		bundle.putString("orderId", no_orders.get(position).getId());
		intent.putExtras(bundle);
		startActivityForResult(intent, 0);
    	
    }
	private void getRecord() {
		dialog = new ProgressDialog(MyRecordActivity.this);
		dialog.show();
		RequestQueue requestQueue = MyApplication.queue;
		StringRequest stringRequest = new StringRequest(Request.Method.GET,
				MyContants.URL_GET_RECORD
						+ "userId="
						+ SharedPreferencesUtil.getData(MyRecordActivity.this,
								"user_id", 0), new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						Gson gson = new Gson();
						JSONObject json;
						try {
							json = new JSONObject(response);
							carports = gson.fromJson(json.get("carports")
									.toString(),
									new TypeToken<List<Carport>>() {
									}.getType());
							orders = gson.fromJson(json.get("orders")
									.toString(), new TypeToken<List<Order>>() {
							}.getType());
							photos = gson.fromJson(json.get("photos")
									.toString(), new TypeToken<List<Photo>>() {
							}.getType());
							comments = gson.fromJson(json.get("comments")
									.toString(), new TypeToken<List<Comment>>() {
							}.getType());
							no_orders = new ArrayList<Order>();
							no_photos = new ArrayList<Photo>();
							no_carports = new ArrayList<Carport>();
							for (int i = 0; i < orders.size(); i++) {
								if (orders.get(i).getCommentId() == 0) {
									no_orders.add(orders.get(i));
									no_photos.add(photos.get(i));
									no_carports.add(carports.get(i));

								}

							}
							listView_no.setAdapter(noAdapter);
							listView_all.setAdapter(allAdapter);
							if (carports.size() == 0) {
								Crouton.makeText(MyRecordActivity.this,
										"您还未停过任何车位哦~", Style.INFO,
										R.id.record_alternate_view_group)
										.show();
							}
							dialog.dismiss();
							dialog.cancel();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						dialog.dismiss();
						dialog.cancel();
						Crouton.makeText(MyRecordActivity.this, "服务器出错！",
								Style.ALERT, R.id.record_alternate_view_group)
								.show();
					}
				});
		requestQueue.add(stringRequest);
	}

	private class MyAdapterAll extends BaseAdapter {
		private Context mContext;

		public MyAdapterAll(Context context) {
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			// 优化ListView
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.item_record, null);
				ItemViewCache viewCache = new ItemViewCache();
				viewCache.tv_addr = (TextView) convertView
						.findViewById(R.id.record_tv_place);
				viewCache.tv_time = (TextView) convertView
						.findViewById(R.id.record_tv_time);
				viewCache.tv_money = (TextView) convertView
						.findViewById(R.id.record_tv_money);
				viewCache.tv_mode = (TextView) convertView
						.findViewById(R.id.record_tv_level);
				viewCache.iv_photo = (NetworkImageView) convertView
						.findViewById(R.id.record_img);
				convertView.setTag(viewCache);

			}
			ItemViewCache cache = (ItemViewCache) convertView.getTag();
			// 设置文本和图片，然后返回这个View，用于ListView的Item的展示
			cache.iv_photo.setImageUrl(photos.get(position).getPhotoUrl2(),
					imageLoader);
			cache.tv_money.setText("-" + orders.get(position).getMoney());
			try {
				if(comments.get(position).getLevel()!=0)
				cache.tv_mode.setText("已评价  "
						+ comments.get(position).getLevel()+"分");
				else
					cache.tv_mode.setText("尚未评价");
				cache.tv_addr.setText(new String(carports.get(position)
						.getAddr().getBytes("ISO-8859-1"), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			cache.tv_time.setText(orders
					.get(position)
					.getTime()
					.substring(orders.get(position).getTime().length() - 13,
							orders.get(position).getTime().length() - 3));
			return convertView;
		}
	}

	private static class ItemViewCache {
		public TextView tv_addr, tv_money, tv_time, tv_mode;
		public NetworkImageView iv_photo;
		public ImageButton imgButton;
	}

	private class MyAdapterNo extends BaseAdapter {
		private Context mContext;

		public MyAdapterNo(Context context) {
			this.mContext = context;
		}

		/**
		 * 元素的个数
		 */
		public int getCount() {
			if (no_orders != null)
				return no_orders.size();
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			// 优化ListView
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.item_record_no, null);
				ItemViewCache viewCache = new ItemViewCache();
				viewCache.imgButton = (ImageButton) convertView
						.findViewById(R.id.record_no_ibt_comment);
				viewCache.tv_addr = (TextView) convertView
						.findViewById(R.id.record_no_tv_place);
				viewCache.tv_time = (TextView) convertView
						.findViewById(R.id.record_no_tv_time);
				viewCache.tv_money = (TextView) convertView
						.findViewById(R.id.record_no_tv_money);
				viewCache.iv_photo = (NetworkImageView) convertView
						.findViewById(R.id.record_no_img);
				convertView.setTag(viewCache);

			}

			ItemViewCache cache = (ItemViewCache) convertView.getTag();
			// 设置文本和图片，然后返回这个View，用于ListView的Item的展示
			cache.imgButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
				    comment(position);
				}
			});
			cache.iv_photo.setImageUrl(no_photos.get(position).getPhotoUrl2(),
					imageLoader);
			cache.tv_money.setText("-" + no_orders.get(position).getMoney());
			try {
				cache.tv_addr.setText(new String(no_carports.get(position)
						.getAddr().getBytes("ISO-8859-1"), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			cache.tv_time.setText(no_orders
					.get(position)
					.getTime()
					.substring(no_orders.get(position).getTime().length() - 13,
							no_orders.get(position).getTime().length() - 3));

			return convertView;

		}
	}

}
