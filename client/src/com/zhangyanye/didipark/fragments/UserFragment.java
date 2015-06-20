package com.zhangyanye.didipark.fragments;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.zhangyanye.didipark.R;
import com.zhangyanye.didipark.activity.LoginActivity;
import com.zhangyanye.didipark.activity.MyCommentActivity;
import com.zhangyanye.didipark.activity.MyFavoriteActivity;
import com.zhangyanye.didipark.activity.MyOrderActivity;
import com.zhangyanye.didipark.activity.MyRecordActivity;
import com.zhangyanye.didipark.activity.SplashActivity;
import com.zhangyanye.didipark.activity.UserInfoAcitvity;
import com.zhangyanye.didipark.activity.WebActivity;
import com.zhangyanye.didipark.activity.WebActivityActivity;
import com.zhangyanye.didipark.application.MyApplication;
import com.zhangyanye.didipark.pojo.Carport;
import com.zhangyanye.didipark.pojo.Photo;
import com.zhangyanye.didipark.pojo.User;
import com.zhangyanye.didipark.utils.BFImageCache;
import com.zhangyanye.didipark.utils.DbOperate;
import com.zhangyanye.didipark.utils.ImageUtil;
import com.zhangyanye.didipark.utils.MyContants;
import com.zhangyanye.didipark.utils.SharedPreferencesUtil;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UserFragment extends Fragment {

	private static final int REQUEST_TAKE_PICTURE = 0;
	private static final int REQUEST_UPDATE_USER = 3;
	private TextView tv_nickName;
	private ImageView iv_figure;
	private Button bt_takeout, bt_userInfo, bt_favorite, bt_order, bt_record,
			bt_comment, bt_web,bt_web2;
	private File out;
	private User user;
	@SuppressWarnings("unused")
	private Bitmap picture;
	private ImageLoader imageLoader;
	private ImageListener listener;
	private DbUtils db;
	private Intent intent;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_user, container, false);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getUser();
		initView();

	}

	private void getUser() {
		db = DbOperate.getInstance();
		try {

			user = db
					.findFirst(Selector.from(User.class).where(
							"id",
							"=",
							SharedPreferencesUtil.getData(getActivity(),
									"user_id", 0)));
		} catch (DbException e) {
			e.printStackTrace();
		}
	}

	/*
	 * 初始化view控件 绑定监听事件
	 */
	private void initView() {
		bt_web2= (Button) getView().findViewById(R.id.user_webview2);
		bt_web2.setOnClickListener(new ClickEventListener());
		bt_web = (Button) getView().findViewById(R.id.user_bt_web);
		bt_web.setOnClickListener(new ClickEventListener());
		bt_comment = (Button) getView().findViewById(R.id.user_bt_comment);
		bt_comment.setOnClickListener(new ClickEventListener());
		bt_record = (Button) getView().findViewById(R.id.user_bt_record);
		bt_record.setOnClickListener(new ClickEventListener());
		bt_order = (Button) getView().findViewById(R.id.user_bt_order);
		bt_order.setOnClickListener(new ClickEventListener());
		bt_favorite = (Button) getView().findViewById(R.id.user_bt_favorite);
		bt_favorite.setOnClickListener(new ClickEventListener());
		iv_figure = (ImageView) getView().findViewById(R.id.user_iv_figure);
		iv_figure.setOnClickListener(new ClickEventListener());
		tv_nickName = (TextView) getView().findViewById(R.id.user_tv_nickName);
		bt_takeout = (Button) getView().findViewById(R.id.user_bt_takeout);
		bt_takeout.setOnClickListener(new ClickEventListener());
		bt_userInfo = (Button) getView().findViewById(R.id.user_bt_userInfo);
		bt_userInfo.setOnClickListener(new ClickEventListener());
		imageLoader = new ImageLoader(MyApplication.queue,
				BFImageCache.getInstance());
		if (user != null) {
			try {
				String name = new String(user.getNickName().toString()
						.getBytes(), "utf-8");
				tv_nickName.setText(name);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			if (user.getImageUrl() != null) {

				listener = ImageLoader.getImageListener(iv_figure,
						R.drawable.ic_account_avatar_default,
						R.drawable.ic_account_avatar_default);
				imageLoader.get(user.getImageUrl(), listener);
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.e("??", "......");
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			// 手機拍照App拍照完成後可以取得照片圖檔
			case REQUEST_TAKE_PICTURE:
				ImageUtil.compressBmpToFileLow(
						ImageUtil.downFigureSize(out.getPath()), out);
				picture = ImageUtil.downFigureSize(out.getPath());
				System.gc();
				uploadFigure();
				break;
			case REQUEST_UPDATE_USER:
				getUser();
				if (user != null) {
					try {
						String name = new String(user.getNickName().toString()
								.getBytes(), "utf-8");
						tv_nickName.setText(name);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					break;
				}
			}
		}
	}

	public void onTakePictrue() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// 指定存檔路徑
		out = Environment.getExternalStorageDirectory();
		out = new File(out, "photo.png");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(out));
		if (isIntentAvailable(this.getActivity(), intent)) {
			startActivityForResult(intent, REQUEST_TAKE_PICTURE);
		} else {
			Crouton.makeText(this.getActivity(), "相机开启失败", Style.ALERT,
					R.id.alternate_view).show();
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private void uploadFigure() {

		RequestParams params = new RequestParams();
		params.addBodyParameter("data", out);
		params.addBodyParameter("type", "png");
		params.addBodyParameter("id", String.valueOf(user.getId()));
		HttpUtils http = new HttpUtils();
		http.send(HttpMethod.POST, MyContants.URL_FIGURE, params,
				new RequestCallBack<String>() {
					@Override
					public void onFailure(HttpException arg0, String arg1) {
					}

					@Override
					public void onSuccess(ResponseInfo<String> response) {
						JSONObject json;
						try {
							json = new JSONObject(response.result.toString());
							Log.e("zyy", response.result.toString());
							String url = (String) json.get("figure");
							user.setImageUrl(url);
							db.saveOrUpdate(user);
							listener = ImageLoader.getImageListener(iv_figure,
									R.drawable.ic_account_avatar_default,
									R.drawable.ic_account_avatar_default);
							imageLoader.get(url, listener);
						} catch (JSONException e) {
							e.printStackTrace();
						} catch (DbException e) {
							e.printStackTrace();
						}
					}
				});

	}

	/*
	 * 判断相机是否启动
	 */
	public boolean isIntentAvailable(Context context, Intent intent) {
		PackageManager packageManager = context.getPackageManager();
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}

	class ClickEventListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.user_iv_figure:
				onTakePictrue();
				break;
			case R.id.user_bt_takeout:
				SharedPreferencesUtil.saveData(getActivity(), "user_id", 0);
				SharedPreferencesUtil.saveData(getActivity(), "login_state",
						false);
				try {
					db.dropTable(User.class);
					db.dropTable(Photo.class);
					db.dropTable(Carport.class);
					getActivity().finish();
				} catch (DbException e) {
					e.printStackTrace();
				}
				intent = new Intent(getActivity(),LoginActivity.class);
				startActivity(intent);
				break;
			case R.id.user_bt_userInfo:
				intent = new Intent(getActivity(), UserInfoAcitvity.class);
				startActivityForResult(intent, REQUEST_UPDATE_USER);
				break;
			case R.id.user_bt_favorite:
				intent = new Intent(getActivity(), MyFavoriteActivity.class);
				startActivity(intent);
				break;
			case R.id.user_bt_order:
				intent = new Intent(getActivity(), MyOrderActivity.class);
				startActivity(intent);
				break;
			case R.id.user_bt_record:
				intent = new Intent(getActivity(), MyRecordActivity.class);
				startActivity(intent);
				break;
			case R.id.user_bt_comment:
				intent = new Intent(getActivity(), MyCommentActivity.class);
				startActivity(intent);
				break;
			case R.id.user_bt_web:
				intent = new Intent(getActivity(), WebActivity.class);
				startActivity(intent);
				break;
			case R.id.user_webview2:
				intent = new Intent(getActivity(), WebActivityActivity.class);
				startActivity(intent);
				break;
			}

		}

	}

}
