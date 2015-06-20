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
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.baidu.android.common.logging.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhangyanye.didipark.R;
import com.zhangyanye.didipark.application.MyApplication;
import com.zhangyanye.didipark.pojo.Carport;
import com.zhangyanye.didipark.pojo.Comment;
import com.zhangyanye.didipark.pojo.Photo;
import com.zhangyanye.didipark.pojo.User;
import com.zhangyanye.didipark.utils.BFImageCache;
import com.zhangyanye.didipark.utils.MyContants;
import com.zhangyanye.didipark.utils.SharedPreferencesUtil;
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
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MyCommentActivity extends Activity {
	private ListView listView;
	private TopBar topbar;
	private List<Comment> comments;
	private List<Carport> carports;
	private BaseAdapter myAdapter;
	private ImageLoader imageLoader;
	private ProgressDialog dialog;
	private List<Photo> photos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_comment);
		initView();
		getComment();
	}

	private void getComment() {
		dialog = new ProgressDialog(MyCommentActivity.this);
		dialog.show();
		RequestQueue requestQueue = MyApplication.queue;
		StringRequest stringRequest = new StringRequest(Request.Method.GET,
				MyContants.URL_GET_MY_COMMENT + "userId=" + SharedPreferencesUtil.getData(MyCommentActivity.this,
						"user_id", 0),
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
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
							carports = gson.fromJson(json.get("carports")
									.toString(),
									new TypeToken<List<Carport>>() {
									}.getType());
							photos = gson.fromJson(json.get("photos")
									.toString(), new TypeToken<List<Photo>>() {
							}.getType());
							if (comments.size() == 0)
								Crouton.makeText(MyCommentActivity.this,
										"你还有评论哦！", Style.INFO,
										R.id.my_comment_view_group).show();
							listView.setAdapter(myAdapter);
							Utils.setListViewHeightBasedOnChildren(listView);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Crouton.makeText(MyCommentActivity.this, "服务器出错！",
								Style.ALERT, R.id.my_comment_view_group).show();
					}
				});
		requestQueue.add(stringRequest);
	}

	private void initView() {
		imageLoader = new ImageLoader(MyApplication.queue,
				BFImageCache.getInstance());
		myAdapter = new MyAdapter(MyCommentActivity.this);
		listView = (ListView) findViewById(R.id.my_comment_list);
		topbar = (TopBar) findViewById(R.id.my_comment_topBar);
		topbar.setOnTopbarClickListener(new ToolBarOnClickListener() {
			@Override
			public void rightBtnClick() {
			}

			@Override
			public void leftBtnClick() {
				finish();
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Intent intent = new Intent(MyCommentActivity.this,
						MoreCarportActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("carport", carports.get(position));
				bundle.putString("photo", photos.get(position).getPhotoUrl());
				intent.putExtras(bundle);
				startActivity(intent);
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
						R.layout.item_my_comment, null);
				ItemViewCache viewCache = new ItemViewCache();
				viewCache.ratingBar = (RatingBar) convertView
						.findViewById(R.id.my_comment_ratingbar);
				viewCache.tv_nickAddr = (TextView) convertView
						.findViewById(R.id.my_commtent_addr);
				viewCache.tv_content = (TextView) convertView
						.findViewById(R.id.my_comment_content);
				viewCache.iv_photo = (NetworkImageView) convertView
						.findViewById(R.id.my_comment_img);
				viewCache.tv_time = (TextView) convertView
						.findViewById(R.id.my_comment_time);
				convertView.setTag(viewCache);

			}
			ItemViewCache cache = (ItemViewCache) convertView.getTag();
			// 设置文本和图片，然后返回这个View，用于ListView的Item的展示
			cache.iv_photo.setImageUrl(photos.get(position).getPhotoUrl(), imageLoader);
			try {
				cache.tv_content.setText(new String(comments.get(position)
						.getContent().getBytes("ISO-8859-1"), "UTF-8"));
				cache.tv_time.setText(comments
						.get(position)
						.getTime()
						.substring(
								comments.get(position).getTime().length() - 13,
								comments.get(position).getTime().length() - 3));
				cache.tv_nickAddr.setText(new String(carports.get(position)
						.getAddr().getBytes("ISO-8859-1"), "UTF-8"));
				cache.ratingBar.setRating((float) comments.get(position)
						.getLevel());
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return convertView;
		}
	}

	private static class ItemViewCache {
		public TextView tv_nickAddr, tv_time, tv_content;
		public RatingBar ratingBar;
		public NetworkImageView iv_photo;
	}

}
