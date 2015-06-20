package com.zhangyanye.didipark.activity;

import java.io.File;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.zhangyanye.didipark.R;
import com.zhangyanye.didipark.pojo.Carport;
import com.zhangyanye.didipark.pojo.Photo;
import com.zhangyanye.didipark.utils.DbOperate;
import com.zhangyanye.didipark.utils.ImageUtil;
import com.zhangyanye.didipark.utils.MyContants;
import com.zhangyanye.didipark.utils.SharedPreferencesUtil;
import com.zhangyanye.didipark.view.ToolBarOnClickListener;
import com.zhangyanye.didipark.view.TopBar;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class PublishActivity extends Activity {

	private static final int REQUEST_TAKE_PICTURE = 0;
	private LocationClient mLocClient;
	private ImageButton ib_photo;
	private EditText et_decribe, et_num_carport, et_money_time, et_addr;
	private TopBar topbar;
	private File out;
	private Bitmap picture;
	private boolean flag = false;
	private double mLatitude, mLongitude;
	private ProgressDialog dialog;
	private MyLocationListenner myListener = new MyLocationListenner();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_carport);
		initView();
		initLocation();
	}

	@Override
	public void onStop() {
		super.onStop();
		mLocClient.stop();
	}

	private void initView() {
		et_money_time = (EditText) findViewById(R.id.publish_et_money_hour);
		et_num_carport = (EditText) findViewById(R.id.publish_et_num_carport);
		et_decribe = (EditText) findViewById(R.id.publish_et_decribe);
		ib_photo = (ImageButton) findViewById(R.id.publish_ib_photo);
		et_addr = (EditText) findViewById(R.id.publish_et_addr);
		topbar = (TopBar) findViewById(R.id.publish_add_topBar);
		topbar.setOnTopbarClickListener(new ToolBarOnClickListener() {
			@Override
			public void rightBtnClick() {
				addCarport();
			}

			@Override
			public void leftBtnClick() {
				mLocClient.stop();
				finish();
			}
		});
	}

	private void initLocation() {
		mLocClient = new LocationClient(PublishActivity.this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		option.setOpenGps(true);
		option.setIsNeedAddress(true);
		mLocClient.setLocOption(option);
		mLocClient.start();
	}

	private void addCarport() {

		if (mLatitude == 0 || mLongitude == 0
				|| et_addr.getText().toString().equals(""))
			Crouton.makeText(PublishActivity.this, "获取不到当前地址", Style.ALERT,
					R.id.publish_add_view_group).show();
		else if (flag == false) {
			Crouton.makeText(PublishActivity.this, "添加照片会大家更好的知道您的车位哦",
					Style.ALERT, R.id.publish_add_view_group).show();
		} else if (et_decribe.getText().toString().equals("")
				|| et_money_time.getText().toString().equals("")
				|| et_num_carport.getText().toString().equals("")) {
			Crouton.makeText(PublishActivity.this, "请将资料填写完整", Style.ALERT,
					R.id.publish_add_view_group).show();
		} else {
			Log.e("zyy", mLatitude + "");
			submit("xxx", et_decribe.getText().toString(), et_num_carport
					.getText().toString(), et_money_time.getText().toString(),
					et_addr.getText().toString(), mLatitude, mLongitude);
		}
	}

	private void submit(String data, String describe, String num,
			String perHourMoney, String addr, double latitude, double longitude) {
		dialog = new ProgressDialog(PublishActivity.this);
		dialog.show();
		Bitmap bitmap = ImageUtil.getimage(out.getPath());
		ImageUtil.compressBmpToFileHigh(bitmap, out);
		RequestParams params = new RequestParams();
		params.addBodyParameter("data", out);
		params.addBodyParameter("type", "png");
		params.addBodyParameter("describe", describe);
		params.addBodyParameter("addr", addr);
		params.addBodyParameter("perHoursMoney", perHourMoney);
		params.addBodyParameter("num", num);
		params.addBodyParameter("latitude", String.valueOf(latitude));
		params.addBodyParameter("longitude", String.valueOf(longitude));
		params.addBodyParameter("id", String.valueOf(SharedPreferencesUtil
				.getData(PublishActivity.this, "user_id", 0)));
		HttpUtils http = new HttpUtils();
		http.send(HttpMethod.POST, MyContants.URL_ADD_CARPORT, params,
				new RequestCallBack<String>() {
					@Override
					public void onFailure(HttpException arg0, String arg1) {
					}

					@Override
					public void onSuccess(ResponseInfo<String> response) {
						dialog.cancel();
						dialog.dismiss();
						Gson gson = new Gson();
						JSONObject json = null;
						try {
							json = new JSONObject(response.result.toString());
							Carport carport = gson.fromJson(json.get("carport")
									.toString(), Carport.class);
							DbUtils db = DbOperate.getInstance();
							db.save(carport);
							String url = (String) json.get("photoUrl");
							String url2 = (String) json.get("photoUrl2");
							Photo photo = new Photo(carport.getId(), url, url2);
							db.save(photo);
							Log.e("zyy","ok");
						} catch (JSONException | DbException e) {
							e.printStackTrace();
						}
						try {
							if (json.get("message").toString()
									.equals("success")) {
								Intent intent = new Intent();
								setResult(RESULT_OK, intent);
								finish();
								Toast.makeText(PublishActivity.this,
										"提交成功,等待第一笔订单吧", Toast.LENGTH_SHORT)
										.show();
								System.gc();
							} else {
								Crouton.makeText(PublishActivity.this,
										"提交失败，请联系客服", Style.ALERT,
										R.id.publish_add_view_group).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});

	}

	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null)
				return;
			et_addr.setText(location.getAddrStr());
			mLatitude = location.getLatitude();
			mLongitude = location.getLongitude();
		}
	}

	public void onTakePhoto(View view) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// 指定存檔路徑
		out = Environment.getExternalStorageDirectory();
		out = new File(out, "photo.png");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(out));
		if (isIntentAvailable(PublishActivity.this, intent)) {
			startActivityForResult(intent, REQUEST_TAKE_PICTURE);
		} else {
			Crouton.makeText(PublishActivity.this, "相机开启失败", Style.ALERT,
					R.id.alternate_view).show();
		}
	}

	/*
	 * 判断相机是否启动
	 */
	private boolean isIntentAvailable(Context context, Intent intent) {
		PackageManager packageManager = context.getPackageManager();
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			// 手機拍照App拍照完成後可以取得照片圖檔
			case REQUEST_TAKE_PICTURE:
				picture = ImageUtil.downPhotoSize(out.getPath());
				ib_photo.setImageBitmap(picture);
				flag = true;
				break;
			}
		}
	}

}
