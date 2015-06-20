package com.zhangyanye.didipark.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.baidu.android.common.logging.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.igexin.sdk.PushManager;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.zhangyanye.didipark.R;
import com.zhangyanye.didipark.application.MyApplication;
import com.zhangyanye.didipark.pojo.Carport;
import com.zhangyanye.didipark.pojo.Comment;
import com.zhangyanye.didipark.pojo.User;
import com.zhangyanye.didipark.utils.BFImageCache;
import com.zhangyanye.didipark.utils.DbOperate;
import com.zhangyanye.didipark.utils.MyContants;
import com.zhangyanye.didipark.utils.SharedPreferencesUtil;
import com.zhangyanye.didipark.utils.Utils;
import com.zhangyanye.didipark.view.CircleImageView;
import com.zhangyanye.didipark.view.PopMenu;
import com.zhangyanye.didipark.view.ToolBarOnClickListener;
import com.zhangyanye.didipark.view.TopBar;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class MoreCarportActivity extends Activity {

	private ProgressDialog dialog;
	private TopBar topbar;
	private String photo_url;
	private Carport carport;
	private TextView view;
	private ImageLoader imageLoader;
	private NetworkImageView img;
	private List<Comment> comments;
	private List<User> users;
	private TextView tv_remain, tv_addr, tv_money, tv_describe, tv_perRat;
	private String distance;
	private ListView listview;
	private float perRating = 0;
	private BaseAdapter myAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_more_carport);
		getBundle();
		try {
			initView();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		getComment();
	}

	private void getBundle() {
		Bundle bundle = getIntent().getExtras();
		if (bundle.containsKey("distance"))
			distance = bundle.getString("distance");
		photo_url = bundle.getString("photo");
		carport = (Carport) bundle.getSerializable("carport");
	}

	private void initView() throws UnsupportedEncodingException {
		tv_perRat = (TextView) findViewById(R.id.more_carport_per_rat);
		myAdapter = new MyAdapter(MoreCarportActivity.this);
		listview = (ListView) findViewById(R.id.more_carport_bt_list);
		view = (TextView) findViewById(R.id.flag);
		imageLoader = new ImageLoader(MyApplication.queue,
				BFImageCache.getInstance());
		topbar = (TopBar) findViewById(R.id.more_carport_topbar);
		topbar.setOnTopbarClickListener(new ToolBarOnClickListener() {

			@Override
			public void rightBtnClick() {
				PopMenu menu = new PopMenu(MoreCarportActivity.this,
						(int) SharedPreferencesUtil.getData(
								MoreCarportActivity.this, "user_id", 0),
						carport.getId());
				menu.showPopWindow(view);
			}

			@Override
			public void leftBtnClick() {
				finish();
			}
		});
		img = (NetworkImageView) findViewById(R.id.more_carport_img);
		img.setImageUrl(photo_url, imageLoader);
		tv_remain = (TextView) findViewById(R.id.more_carport_remain);
		tv_addr = (TextView) findViewById(R.id.more_carport_addr);
		tv_money = (TextView) findViewById(R.id.more_carport_money);
		tv_describe = (TextView) findViewById(R.id.more_carport_describe);
		if (distance != null) {
			tv_addr.setText(distance);
			tv_describe.setText(carport.getDescribe());
		} else {
			tv_addr.setText(new String(
					carport.getAddr().getBytes("ISO-8859-1"), "UTF-8"));
			tv_describe.setText(new String(carport.getDescribe().getBytes(
					"ISO-8859-1"), "UTF-8"));
		}
		tv_remain.setText(carport.getNum() + "");
		tv_money.setText(carport.getPerHoursMoney() + " (元/每小时)");

	}

	public void callPhone(View v) {
		dialog = new ProgressDialog(MoreCarportActivity.this);
		dialog.show();
		RequestQueue requestQueue = MyApplication.queue;
		StringRequest stringRequest = new StringRequest(Request.Method.GET,
				MyContants.URL_GET_PHONE + "id=" + carport.getUser_id(),
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						JSONObject json;
						String phone = null;
						try {
							json = new JSONObject(response);
							phone = (String) json.get("phone");
							Intent intent = new Intent(Intent.ACTION_CALL,
									Uri.parse("tel:" + phone));
							startActivity(intent);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Crouton.makeText(MoreCarportActivity.this, "服务器出错！",
								Style.ALERT, R.id.more_carport_alternate_view)
								.show();
					}
				});
		requestQueue.add(stringRequest);
	}

	private void getComment() {
		RequestQueue requestQueue = MyApplication.queue;
		StringRequest stringRequest = new StringRequest(Request.Method.GET,
				MyContants.URL_GET_COMMENT + "carportId=" + carport.getId(),
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						Log.e("zyy", response);
						JSONObject json;
						Gson gson = new Gson();
						try {
							json = new JSONObject(response);
							comments = gson.fromJson(json.get("comments")
									.toString(),
									new TypeToken<List<Comment>>() {
									}.getType());
							users = gson.fromJson(json.get("users").toString(),
									new TypeToken<List<User>>() {
									}.getType());
							listview.setAdapter(myAdapter);
							for (Comment temp : comments) {
								perRating = perRating + temp.getLevel();
							}
							if (perRating == 0) {
								tv_perRat.setText("暂无");
							} else {
								perRating = perRating / comments.size();
								tv_perRat.setText("总体评价  " + perRating);
							}
							Utils.setListViewHeightBasedOnChildren(listview);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Crouton.makeText(MoreCarportActivity.this, "服务器出错！",
								Style.ALERT, R.id.more_carport_alternate_view)
								.show();
					}
				});
		requestQueue.add(stringRequest);
	}

	public void submitRequest(View v) {
		dialog = new ProgressDialog(MoreCarportActivity.this);
		dialog.show();
		RequestQueue requestQueue = MyApplication.queue;
		StringRequest stringRequest = new StringRequest(Request.Method.GET,
				MyContants.URL_ORDER_REQUEST
						+ "userId="
						+ carport.getUser_id()
						+ "&id="
						+ SharedPreferencesUtil.getData(
								MoreCarportActivity.this, "user_id", 0)
						+ "&carportId=" + carport.getId(),
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						dialog.dismiss();
						dialog.cancel();
						JSONObject json;
						try {
							json = new JSONObject(response);
							if (json.get("message").equals("success"))
								Crouton.makeText(MoreCarportActivity.this,
										"申请已提交,请稍等", Style.CONFIRM,
										R.id.more_carport_alternate_view)
										.show();
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						dialog.dismiss();
						dialog.cancel();
						Crouton.makeText(MoreCarportActivity.this, "服务器出错！",
								Style.ALERT, R.id.more_carport_alternate_view)
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
			if (comments != null) {
				if (comments.size() < 3)
					return comments.size();
				else
					return 3;

			} else
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
						R.layout.item_comment, null);
				ItemViewCache viewCache = new ItemViewCache();
				viewCache.ratingBar = (RatingBar) convertView
						.findViewById(R.id.comment_ratingbar);
				viewCache.tv_nickName = (TextView) convertView
						.findViewById(R.id.commtent_nickName);
				viewCache.tv_content = (TextView) convertView
						.findViewById(R.id.comment_content);
				viewCache.iv_photo = (CircleImageView) convertView
						.findViewById(R.id.comment_img);
				viewCache.tv_time = (TextView) convertView
						.findViewById(R.id.comment_time);
				convertView.setTag(viewCache);

			}
			ItemViewCache cache = (ItemViewCache) convertView.getTag();
			// 设置文本和图片，然后返回这个View，用于ListView的Item的展示
			if (!users.get(position).getImageUrl().equals("")) {
				ImageListener listener = ImageLoader.getImageListener(
						cache.iv_photo, R.drawable.ic_user_on,
						R.drawable.ic_user_on);
				imageLoader.get(users.get(position).getImageUrl(), listener);
			} else
				cache.iv_photo.setImageDrawable(getResources().getDrawable(
						R.drawable.ic_figure_def));
			try {
				cache.tv_content.setText(new String(comments.get(position)
						.getContent().getBytes("ISO-8859-1"), "UTF-8"));
				cache.tv_time.setText(comments
						.get(position)
						.getTime()
						.substring(
								comments.get(position).getTime().length() - 13,
								comments.get(position).getTime().length() - 3));
				cache.tv_nickName.setText(new String(users.get(position)
						.getNickName().getBytes("ISO-8859-1"), "UTF-8"));
				cache.ratingBar.setRating((float) comments.get(position)
						.getLevel());
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return convertView;
		}
	}

	private static class ItemViewCache {
		public TextView tv_nickName, tv_time, tv_content;
		public RatingBar ratingBar;
		public CircleImageView iv_photo;
	}

	public void allComment(View v) {
		Intent intent = new Intent(MoreCarportActivity.this,
				CustomerCommentActivity.class);
		intent.putExtra("carportId", carport.getId());
		startActivity(intent);

	}
}
