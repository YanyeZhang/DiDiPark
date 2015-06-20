package com.zhangyanye.didipark.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
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
import com.zhangyanye.didipark.view.SwipeMenu;
import com.zhangyanye.didipark.view.SwipeMenuCreator;
import com.zhangyanye.didipark.view.SwipeMenuItem;
import com.zhangyanye.didipark.view.SwipeMenuListView;
import com.zhangyanye.didipark.view.SwipeMenuListView.OnMenuItemClickListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhangyanye.didipark.R;
import com.zhangyanye.didipark.application.MyApplication;
import com.zhangyanye.didipark.pojo.Carport;
import com.zhangyanye.didipark.pojo.Photo;
import com.zhangyanye.didipark.utils.BFImageCache;
import com.zhangyanye.didipark.utils.MyContants;
import com.zhangyanye.didipark.utils.SharedPreferencesUtil;
import com.zhangyanye.didipark.view.ToolBarOnClickListener;
import com.zhangyanye.didipark.view.TopBar;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class MyFavoriteActivity extends Activity {
	private ProgressDialog dialog;
	private TopBar topbar;
	private List<Carport> carports;
	private List<Photo> photos;
	private SwipeMenuListView listView;
	private ImageLoader imageLoader;
	private BaseAdapter myAdapter;
	private SwipeRefreshLayout freshLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favorite);
		initView();
		getFavorite();
	}

	@SuppressLint("InlinedApi")
	private void initView() {
		imageLoader = new ImageLoader(MyApplication.queue,
				BFImageCache.getInstance());
		listView = (SwipeMenuListView) findViewById(R.id.favorite_listView);
		initMenuListView();
		topbar = (TopBar) findViewById(R.id.favorite_topBar);
		topbar.setOnTopbarClickListener(new ToolBarOnClickListener() {

			@Override
			public void rightBtnClick() {
			}

			@Override
			public void leftBtnClick() {
				finish();
			}
		});
		myAdapter = new MyAdapter(MyFavoriteActivity.this);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Intent intent = new Intent(MyFavoriteActivity.this,
						MoreCarportActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("carport", carports.get(position));
				bundle.putString("photo", photos.get(position).getPhotoUrl());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	private void initMenuListView() {
		// 创建一个SwipeMenuCreator供ListView使用
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				// 创建一个侧滑菜单
				SwipeMenuItem openItem = new SwipeMenuItem(
						getApplicationContext());
				/*
				 * //给该侧滑菜单设置背景 openItem.setBackground(new
				 * ColorDrawable(Color.rgb(0xC9, 0xC9,0xCE))); //设置宽度
				 * openItem.setWidth(dp2px(80)); //设置名称 openItem.setTitle("打开");
				 * //字体大小 openItem.setTitleSize(18); //字体颜色
				 * openItem.setTitleColor(Color.WHITE); //加入到侧滑菜单中
				 * menu.addMenuItem(openItem);
				 */
				// 创建一个侧滑菜单
				SwipeMenuItem delItem = new SwipeMenuItem(
						getApplicationContext());
				// 给该侧滑菜单设置背景
				delItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F,
						0x25)));
				// 设置宽度
				delItem.setWidth(dp2px(70));
				// 设置图片
				delItem.setIcon(R.drawable.icon_del);
				// 加入到侧滑菜单中
				menu.addMenuItem(delItem);
			}
		};

		listView.setMenuCreator(creator);

		// 侧滑菜单的相应事件
		listView.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(int position, SwipeMenu menu,
					int index) {
				deletFavorite(position);
				return false;
			}
		});
	}

	private void deletFavorite(final int position) {
		dialog=new ProgressDialog(MyFavoriteActivity.this);
		dialog.show();
		RequestQueue requestQueue = MyApplication.queue;
		StringRequest stringRequest = new StringRequest(Request.Method.POST,
				MyContants.URL_DELET_FAVORITE, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						try {
							JSONObject json = new JSONObject(response);
							if (json.get("message").equals("success")) {
								carports.remove(position);
								myAdapter.notifyDataSetChanged();
								Crouton.makeText(MyFavoriteActivity.this,
										"已删除车位！", Style.CONFIRM,
										R.id.favorite_alternate_view_group)
										.show();
								dialog.cancel();
								dialog.dismiss();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Crouton.makeText(MyFavoriteActivity.this, "删除失败！",
								Style.ALERT, R.id.favorite_alternate_view_group)
								.show();
					}
				}) {
			@Override
			protected Map<String, String> getParams() {
				// 在这里设置需要post的参数
				Map<String, String> params = new HashMap<String, String>();
				params.put("carport_id", carports.get(position).getId() + "");
				params.put(
						"user_id",
						SharedPreferencesUtil.getData(MyFavoriteActivity.this,
								"user_id", 0) + "");
				return params;
			}
		};
		requestQueue.add(stringRequest);
	}

	private void getFavorite() {
		dialog=new ProgressDialog(MyFavoriteActivity.this);
		dialog.setCancelable(false);
		dialog.show();
		RequestQueue requestQueue = MyApplication.queue;
		StringRequest stringRequest = new StringRequest(Request.Method.GET,
				MyContants.URL_GET_FAVORITE
						+ "userId="
						+ SharedPreferencesUtil.getData(
								MyFavoriteActivity.this, "user_id", 0),
				new Response.Listener<String>() {
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
							photos = gson.fromJson(json.get("photos")
									.toString(), new TypeToken<List<Photo>>() {
							}.getType());
							listView.setAdapter(myAdapter);
							if(carports.size()==0)
							{
								Crouton.makeText(MyFavoriteActivity.this, "您还未收藏过任何车位哦~",
										Style.INFO, R.id.favorite_alternate_view_group)
										.show();
							}
							dialog.cancel();
							dialog.dismiss();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						dialog.dismiss();
						dialog.cancel();
						Crouton.makeText(MyFavoriteActivity.this, "服务器出错！",
								Style.ALERT, R.id.favorite_alternate_view_group)
								.show();
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
			if (carports != null)
				return carports.size();
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
						R.layout.item_favorite, null);
				ItemViewCache viewCache = new ItemViewCache();
				viewCache.tv_addr = (TextView) convertView
						.findViewById(R.id.favorite_carport_addr);
				viewCache.tv_money = (TextView) convertView
						.findViewById(R.id.favorite_carport_moeny);
				viewCache.tv_num = (TextView) convertView
						.findViewById(R.id.favorite_carport_remain);
				viewCache.iv_photo = (NetworkImageView) convertView
						.findViewById(R.id.favorite_photo);
				viewCache.iv_state = (ImageView) convertView
						.findViewById(R.id.favorite_state);
				convertView.setTag(viewCache);

			}
			ItemViewCache cache = (ItemViewCache) convertView.getTag();
			// 设置文本和图片，然后返回这个View，用于ListView的Item的展示
			cache.iv_photo.setImageUrl(photos.get(position).getPhotoUrl2(),
					imageLoader);
			cache.tv_money.setText(carports.get(position).getPerHoursMoney()
					+ "¥/H");
			cache.tv_num
					.setText("剩余" + carports.get(position).getNum() + "个车位");
			try {
				cache.tv_addr.setText(new String(carports.get(position)
						.getAddr().getBytes("ISO-8859-1"), "UTF-8"));
				if (new String(carports.get(position).getState()
						.getBytes("ISO-8859-1"), "UTF-8").equals("运营"))
					cache.iv_state.setImageDrawable(getResources().getDrawable(
							R.drawable.ic_carport_run));
				else
					cache.iv_state.setImageDrawable(getResources().getDrawable(
							R.drawable.ic_carport_stop));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return convertView;
		}
	}

	private static class ItemViewCache {
		public TextView tv_addr, tv_money, tv_num;
		public ImageView iv_state;
		public NetworkImageView iv_photo;
	}

	// 将dp转化为px
	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}
}
