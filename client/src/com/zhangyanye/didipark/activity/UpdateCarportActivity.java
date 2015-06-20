package com.zhangyanye.didipark.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.google.gson.Gson;
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
import com.zhangyanye.didipark.application.MyApplication;
import com.zhangyanye.didipark.pojo.Carport;
import com.zhangyanye.didipark.pojo.Photo;
import com.zhangyanye.didipark.utils.BFImageCache;
import com.zhangyanye.didipark.utils.DbOperate;
import com.zhangyanye.didipark.utils.ImageUtil;
import com.zhangyanye.didipark.utils.MyContants;
import com.zhangyanye.didipark.view.CustomerSpinner;
import com.zhangyanye.didipark.view.ToolBarOnClickListener;
import com.zhangyanye.didipark.view.TopBar;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import android.app.Activity;
import android.app.Dialog;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class UpdateCarportActivity extends Activity {

	private ImageListener listener;
	private ImageLoader imageLoader;
	private static final int REQUEST_TAKE_PICTURE = 0;
	private boolean flag = false;
	private ArrayAdapter<String> adapter;
	private TopBar topbar;
	private CustomerSpinner spinner;
	private Dialog dialog;
	private ArrayList<String> list = new ArrayList<String>();
	private DbUtils db;
	private Bitmap picture;
	private Photo photo;
	private File out;
	private Carport carport;
	private EditText update_carport_describe, update_carport_addr,
			update_carport_num, update_carport_money;
	private ImageButton iv_photo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_carport);
		getCarport();
		initView();
	}
	
	private void getCarport() {
		Bundle bundle = getIntent().getExtras();
		int id = bundle.getInt("carport_id");
		db = DbOperate.getInstance();

		try {
			carport = db.findById(Carport.class, id);
			photo = db.findFirst(Selector.from(Photo.class).where("carport_id",
					"=", carport.getId() + ""));
		} catch (DbException e) {
			e.printStackTrace();
		}

	}

	private void initView() {
		list.add("运营");
		list.add("停运");
		list.add("下架");
		topbar = (TopBar) findViewById(R.id.update_carport_topBar);
		topbar.setOnTopbarClickListener(new ToolBarOnClickListener() {
			@Override
			public void rightBtnClick() {
				submit(update_carport_describe.getText().toString(),
						update_carport_num.getText().toString(),
						update_carport_money.getText().toString(),
						update_carport_addr.getText().toString());
			}

			@Override
			public void leftBtnClick() {
				finish();
			}
		});
		spinner = (CustomerSpinner) findViewById(R.id.spinner);
		spinner.setList(list);
		if (carport.getState().toString().equals("run"))
			spinner.setSelection(1, true);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		spinner.setAdapter(adapter);
		iv_photo = (ImageButton) findViewById(R.id.update_carport_photo);
		update_carport_describe = (EditText) findViewById(R.id.update_carport_decribe);
		update_carport_addr = (EditText) findViewById(R.id.update_carport_addr);
		update_carport_money = (EditText) findViewById(R.id.update_carport_money_hour);
		update_carport_num = (EditText) findViewById(R.id.update_carport_num_carport);

		update_carport_addr.setText(carport.getAddr());
		update_carport_describe.setText(carport.getDescribe());
		update_carport_money.setText(carport.getPerHoursMoney() + "");
		update_carport_num.setText(carport.getNum() + "");

		imageLoader = new ImageLoader(MyApplication.queue,
				BFImageCache.getInstance());
		listener = ImageLoader.getImageListener(iv_photo,
				R.drawable.ic_account_avatar_default,
				R.drawable.ic_account_avatar_default);
		imageLoader.get(photo.getPhotoUrl2(), listener);
	}

	private void submit(String describe, String num, String perHourMoney,
			String addr) {
		Log.e("zyy", spinner.getSelectedItem().toString());
		dialog = new ProgressDialog(UpdateCarportActivity.this);
		dialog.show();
		RequestParams params = new RequestParams();

		if (flag == true) {
			Bitmap bitmap = ImageUtil.getimage(out.getPath());
			ImageUtil.compressBmpToFileHigh(bitmap, out);
			params.addBodyParameter("data", out);
			params.addBodyParameter("type", "png");
		}

		params.addBodyParameter("state", spinner.getSelectedItem().toString());
		params.addBodyParameter("describe", describe);
		params.addBodyParameter("addr", addr);
		params.addBodyParameter("perHoursMoney", perHourMoney);
		params.addBodyParameter("num", num);
		params.addBodyParameter("id", carport.getId() + "");
		HttpUtils http = new HttpUtils();
		http.send(HttpMethod.POST, MyContants.URL_UPDATE_CARPORT, params,
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
						String result = null;
						try {
							json = new JSONObject(response.result.toString());
							result = json.getString("message");
							Log.e("zyy", json.toString());
						} catch (JSONException e) {

							e.printStackTrace();
						}
						switch (result) {
						case "delet_success":
							try {
								db.delete(carport);
								db.delete(photo);
								Toast.makeText(UpdateCarportActivity.this,
										"车位已下架", Toast.LENGTH_SHORT).show();
								Intent intent=new Intent();
								setResult(RESULT_OK, intent);  
								finish();
							} catch (DbException e) {
								e.printStackTrace();
							}
							break;
						case "update_success":
							try {
								Carport temp_carport = gson.fromJson(
										json.get("carport").toString(),
										Carport.class);
								temp_carport.setDb_id(carport.getDb_id());
								if (flag == true) {
									String url = (String) json.get("photoUrl");
									String url2 = (String) json
											.get("photoUrl2");
									Photo temp_photo = new Photo(carport
											.getId(), url, url2);
									temp_photo.setDb_id(photo.getDb_id());
									db.saveOrUpdate(temp_photo);
								}
								db.saveOrUpdate(temp_carport);
								Toast.makeText(UpdateCarportActivity.this,
										"车位信息已修改", Toast.LENGTH_SHORT).show();
								Intent intent=new Intent();
								setResult(RESULT_OK, intent);  
								finish();
								System.gc();
							} catch (Exception e) {
								e.printStackTrace();
							}
							break;
						}
					}
				});

	}

	public void onChangePhoto(View view) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// 指定存檔路徑
		out = Environment.getExternalStorageDirectory();
		out = new File(out, "photo.png");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(out));
		if (isIntentAvailable(UpdateCarportActivity.this, intent)) {
			startActivityForResult(intent, REQUEST_TAKE_PICTURE);
		} else {
			Crouton.makeText(UpdateCarportActivity.this, "相机开启失败", Style.ALERT,
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
				iv_photo.setImageBitmap(picture);
				flag = true;
				break;
			}
		}
	}
}