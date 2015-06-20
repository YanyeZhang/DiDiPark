package com.zhangyanye.didipark.activity;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.zhangyanye.didipark.R;
import com.zhangyanye.didipark.application.MyApplication;
import com.zhangyanye.didipark.pojo.Carport;
import com.zhangyanye.didipark.utils.BFImageCache;
import com.zhangyanye.didipark.utils.MyContants;
import com.zhangyanye.didipark.utils.SharedPreferencesUtil;
import com.zhangyanye.didipark.view.ToolBarOnClickListener;
import com.zhangyanye.didipark.view.TopBar;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class AddCommentActivity extends Activity {

	private TopBar topbar;
	private Carport carport;
	private String photo_url, orderId;
	private NetworkImageView img;
	private ImageLoader imageLoader;
	private TextView tv_addr, tv_descr;
	private EditText et_content;
	private RatingBar ratingBar;
	private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_comment);
		getBundle();
		inintView();
	}

	private void getBundle() {
		Bundle bundle = getIntent().getExtras();
		photo_url = bundle.getString("photo");
		carport = (Carport) bundle.getSerializable("carport");
		orderId = bundle.getString("orderId");
	}

	private void inintView() {
		tv_addr = (TextView) findViewById(R.id.add_comment_tv_addr);
		tv_descr = (TextView) findViewById(R.id.add_comment_tv_des);
		try {
			tv_addr.setText(new String(
					carport.getAddr().getBytes("ISO-8859-1"), "UTF-8"));
			tv_descr.setText(new String(carport.getDescribe().getBytes(
					"ISO-8859-1"), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		ratingBar = (RatingBar) findViewById(R.id.add_comment_ratingbar);
		et_content = (EditText) findViewById(R.id.add_comment_et_content);

		imageLoader = new ImageLoader(MyApplication.queue,
				BFImageCache.getInstance());
		img = (NetworkImageView) findViewById(R.id.add_comment_iv_photo);
		img.setImageUrl(photo_url, imageLoader);
		topbar = (TopBar) findViewById(R.id.add_comment_topBar);
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

	public void submitComment(View view) {
		if(et_content.length()<10)
		{
			Crouton.makeText(AddCommentActivity.this, "要写满10字哦~",
					Style.ALERT, R.id.add_comment_alternate_view_group)
					.show();
		}
		else{
		dialog = new ProgressDialog(AddCommentActivity.this);
		dialog.setCancelable(false);
		dialog.show();
		RequestQueue requestQueue = MyApplication.queue;
		StringRequest stringRequest = new StringRequest(Request.Method.POST,
				MyContants.URL_CREATE_COMMENT, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						System.out.println(response.toString());
						dialog.dismiss();
						dialog.cancel();
						Toast.makeText(AddCommentActivity.this, "评论递交成功！",
								Toast.LENGTH_LONG).show();
						Intent intent=new Intent();
						setResult(RESULT_OK, intent); 
						finish();
						System.gc();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						dialog.dismiss();
						dialog.cancel();
						Crouton.makeText(AddCommentActivity.this, "评论递交失败！",
								Style.ALERT,
								R.id.add_comment_alternate_view_group).show();
					}
				}) {
			@Override
			protected Map<String, String> getParams() {
				// 在这里设置需要post的参数
				Map<String, String> params = new HashMap<String, String>();
				params.put("content", et_content.getText().toString());
				params.put("level", (int)ratingBar.getRating()+"");
				params.put("orderId", orderId);
				params.put("carport_id", carport.getId()+"");
				params.put("user_id", String.valueOf(SharedPreferencesUtil
						.getData(AddCommentActivity.this, "user_id", 0)));
				return params;
			}
		};
		requestQueue.add(stringRequest);
	}}
}
