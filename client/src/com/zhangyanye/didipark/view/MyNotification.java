package com.zhangyanye.didipark.view;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.zhangyanye.didipark.R;
import com.zhangyanye.didipark.activity.AuditActivity;
import com.zhangyanye.didipark.activity.PayMoneyActivity;
import com.zhangyanye.didipark.pojo.Carport;
import com.zhangyanye.didipark.pojo.Photo;
import com.zhangyanye.didipark.pojo.User;
import com.zhangyanye.didipark.utils.DbOperate;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class MyNotification {
	private static NotificationManager mNotificationManager;
	private static MyNotification myNotification;
	private Notification notification;
	private Thread myThread;
	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static MyNotification getInstance(Context context) {
		if (myNotification == null) {
			myNotification = new MyNotification();
			mNotificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
		}
		return myNotification;
	}

	@SuppressWarnings("deprecation")
	public void showNotification(Context context, String customerId,
			String content, String carportId) {
		Log.e("zyy", customerId + "  " + content + "  " + carportId);
		Intent intent = new Intent(context, AuditActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("customer_id", customerId + "");
		bundle.putString("carport_id", carportId + "");
		bundle.putString("time", df.format(new Date()));
		intent.putExtras(bundle);
		notification = new Notification(R.drawable.ic_launcher, content,
				System.currentTimeMillis());
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				intent, PendingIntent.FLAG_CANCEL_CURRENT);
		notification.setLatestEventInfo(context, "停车申请", "来自手机尾号为:" + content
				+ "的用户", pendingIntent);
		notification.defaults = Notification.DEFAULT_VIBRATE;
		mNotificationManager.notify(Integer.parseInt(customerId), notification);
	}

	@SuppressWarnings("deprecation")
	public void showNotification(Context context, String content,
			String carportId) {
		notification = new Notification(R.drawable.ic_launcher, content,
				System.currentTimeMillis());
		Log.e("zyy", "sss");
		if (!content.equals("申请被拒绝，请换一个车位试试吧")) {
			Intent intent = new Intent(context, PayMoneyActivity.class);

			intent.putExtra("carportId", carportId);
			intent.putExtra("time", df.format(new Date()));
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
					intent, PendingIntent.FLAG_CANCEL_CURRENT);
			notification.setLatestEventInfo(context, "申请结果", content,
					pendingIntent);
		} else
			notification.setLatestEventInfo(context, "申请结果", content, null);
		notification.defaults = Notification.DEFAULT_VIBRATE;
		mNotificationManager.notify(0, notification);
	}

	@SuppressWarnings("deprecation")
	public void showNotificationResult(Context context, String content,String carportId) {
		notification = new Notification(R.drawable.ic_launcher, content,
				System.currentTimeMillis());
		/*
		 * Intent intent = new Intent(context, PayMoneyActivity.class);
		 * intent.putExtra("time", df.format(new Date())); PendingIntent
		 * pendingIntent = PendingIntent.getActivity(context, 0, intent,
		 * PendingIntent.FLAG_CANCEL_CURRENT);
		 */
		notification.setLatestEventInfo(context, "订单已支付", "来自手机尾号为:" + content
				+ "的用户", null);
		notification.defaults = Notification.DEFAULT_VIBRATE;
		notification.defaults=Notification.FLAG_AUTO_CANCEL;
		mNotificationManager.notify(0, notification);
		updateCarport(carportId);
	}
    public void updateCarport(final String  carportId){
    	myThread=new Thread(new Runnable() {
			@Override
			public void run() {
				DbUtils db = DbOperate.getInstance();
				try {
					Carport carport = db.findFirst(Selector.from(Carport.class).where(
							"id", "=",
							carportId + ""));
					carport.setNum(carport.getNum()-1);
					carport.setUser_num(carport.getUser_num()+1);
					db.saveOrUpdate(carport);
				} catch (DbException e) {
					e.printStackTrace();
				
			}
		}});
    	myThread.start();
    }
	public void dismissNotification(int customer_id) {
		mNotificationManager.cancel(customer_id);
	}
}
