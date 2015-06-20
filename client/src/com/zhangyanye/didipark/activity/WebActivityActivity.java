package com.zhangyanye.didipark.activity;

import com.zhangyanye.didipark.R;
import com.zhangyanye.didipark.view.ToolBarOnClickListener;
import com.zhangyanye.didipark.view.TopBar;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class WebActivityActivity extends Activity {
	private TopBar topbar;
	private WebView webView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web2);
		initView();
	}
	private void initView(){
		webView=(WebView) findViewById(R.id.web2_view);
		topbar=(TopBar) findViewById(R.id.web2_topBar);
		webView.loadUrl("http://didipark.duapp.com/act.html");
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
}
