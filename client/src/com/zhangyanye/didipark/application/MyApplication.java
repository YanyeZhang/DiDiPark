package com.zhangyanye.didipark.application;



import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.baidu.mapapi.SDKInitializer;
import com.igexin.sdk.PushManager;
import com.zhangyanye.didipark.utils.BFImageCache;

import android.app.Application;
import android.content.Context;

/**
 * @ClassName: MyApplication
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author zhangyanye
 * @date 2015年4月19日 下午5:14:16
 * 
 */
public class MyApplication extends Application {
	private static Context context;
	public static RequestQueue queue;

	@Override
	public void onCreate() {
		super.onCreate();
		PushManager.getInstance().initialize(this.getApplicationContext());
		context = getApplicationContext();
		queue = Volley.newRequestQueue(context);
		BFImageCache.getInstance().initilize(this);
		//初始化百度地图sdk
	    SDKInitializer.initialize(this);
	}

	public static Context getContext() {
		return context;
	}
}
