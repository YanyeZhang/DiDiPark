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
import com.baidu.android.common.logging.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhangyanye.didipark.R;
import com.zhangyanye.didipark.application.MyApplication;
import com.zhangyanye.didipark.pojo.Carport;
import com.zhangyanye.didipark.pojo.Comment;
import com.zhangyanye.didipark.pojo.User;
import com.zhangyanye.didipark.utils.BFImageCache;
import com.zhangyanye.didipark.utils.MyContants;
import com.zhangyanye.didipark.utils.Utils;
import com.zhangyanye.didipark.view.CircleImageView;
import com.zhangyanye.didipark.view.ToolBarOnClickListener;
import com.zhangyanye.didipark.view.TopBar;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

public class CustomerCommentActivity extends Activity {
	private ListView listView;
	private TopBar topbar;
	private int carportId;
	private List<Comment> comments;
	private List<User> users;
	private BaseAdapter myAdapter;
	private ImageLoader imageLoader;
	private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_custom_comment);
		initView();
		getBundle();
		getComment();
	}
	private void getBundle() {
		carportId = getIntent().getIntExtra("carportId", 0);
	}
	private void getComment() {
		dialog=new ProgressDialog(CustomerCommentActivity.this);
		dialog.show();
		RequestQueue requestQueue = MyApplication.queue;
		StringRequest stringRequest = new StringRequest(Request.Method.GET,
				MyContants.URL_GET_COMMENT + "carportId=" + carportId,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						Log.e("zyy", response);
						dialog.dismiss();
						dialog.cancel();
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
							if(comments.size()==0)
								Crouton.makeText(CustomerCommentActivity.this, "还没有人评论哦！",
										Style.CONFIRM, R.id.customer_comment_view_group)
										.show();
							listView.setAdapter(myAdapter);
							Utils.setListViewHeightBasedOnChildren(listView);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Crouton.makeText(CustomerCommentActivity.this, "服务器出错！",
								Style.ALERT, R.id.customer_comment_view_group)
								.show();
					}
				});
		requestQueue.add(stringRequest);
	}
	
	private void initView() {
		imageLoader = new ImageLoader(MyApplication.queue,
				BFImageCache.getInstance());
		myAdapter=new MyAdapter(CustomerCommentActivity.this);
		listView = (ListView) findViewById(R.id.customer_comment_list);
		topbar = (TopBar) findViewById(R.id.customer_comment_topBar);
		topbar.setOnTopbarClickListener(new ToolBarOnClickListener() {
			@Override
			public void rightBtnClick() {
			}

			@Override
			public void leftBtnClick() {
				finish();
			}
		});
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
					return comments.size();
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

}
