package com.zhangyanye.didipark.activity;

import com.zhangyanye.didipark.R;
import com.zhangyanye.didipark.utils.SharedPreferencesUtil;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

/**
 * @ClassName: SplashActivity
 * @Description: 开机动画
 * @author zhangyanye
 * @date 2015年4月19日 下午4:20:44
 * 
 */
public class SplashActivity extends Activity {

	private Intent intent;
	private boolean flag = false;
	private MediaPlayer mp;
	private TextView tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		initMap3();
		initView();
		setAnimation();
	}

	private void myIntent() {
		flag = (boolean) SharedPreferencesUtil.getData(SplashActivity.this,
				"login_state", false);
		if (flag == false) {
			intent = new Intent(SplashActivity.this, RegisterActivity.class);
			startActivity(intent);
			finish();
		} else {
			intent = new Intent(SplashActivity.this, MainActivity.class);
			startActivity(intent);
			finish();
		}
	}

	private void initView() {
		Typeface typeFace = Typeface.createFromAsset(getAssets(),
				"fonts/poem.ttf");
		tv = (TextView) findViewById(R.id.splash_tv);
		tv.setTypeface(typeFace);
        
	}

	private void setAnimation() {
		final Animation animation = AnimationUtils.loadAnimation(this,
				R.anim.text_app_name);
		tv.setAnimation(animation);
		animation.startNow();
		new Handler().postDelayed(new Runnable() {
			public void run() {
				myIntent();
			}
		}, 5000);
	}

	private void initMap3() {
		mp = MediaPlayer.create(SplashActivity.this, R.raw.speak);
		mp.start();
		mp.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				mp.release();// 释放音频资源
			}
		});
	}
}
