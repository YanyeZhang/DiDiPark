package com.zhangyanye.didipark.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.igexin.sdk.PushConsts;
import com.zhangyanye.didipark.service.BaiduVoiceService;
import com.zhangyanye.didipark.view.MyNotification;

public class PushReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		Intent serviceIntent = new Intent(context,
				BaiduVoiceService.class);
		Bundle mbundle = new Bundle();
		String[] temp;
		MyNotification mNotification = MyNotification.getInstance(context);
		switch (bundle.getInt(PushConsts.CMD_ACTION)) {
		case PushConsts.GET_MSG_DATA:
			// 获取透传（payload）数据
			byte[] payload = bundle.getByteArray("payload");
			if (payload != null) {
				String data = new String(payload);
				Log.e("GetuiSdkDemo", "Got Payload:" + data);
				String[] type = data.split("-");
				switch (type[0]) {
				case "1001":
					temp = type[1].split(",");
					StringBuilder sb=new StringBuilder(temp[0]);
					sb.insert(1," ");
					sb.insert(3," ");
					sb.insert(5," ");
					String content = "手机尾号为 " + sb + "的用户申请停车,请及时审核 ";
					mbundle.putString("content", content);
					serviceIntent.putExtras(mbundle);
					context.startService(serviceIntent);
					mNotification.showNotification(context,
							temp[1], temp[0],temp[2]);
					break;
				case "1002":
					temp=type[1].split(",");
					mbundle.putString("content", temp[0]);
					serviceIntent.putExtras(mbundle);
					context.startService(serviceIntent);
					mNotification.showNotification(context,temp[0],temp[1]);
					break;
				case "1003":
					temp=type[1].split(",");
					StringBuilder pay=new StringBuilder(temp[0]);
					pay.insert(1," ");
					pay.insert(3," ");
					pay.insert(5," ");
					String ok = "手机尾号为 " + pay + "的用户已预支付一小时停车费费用 ";
					mbundle.putString("content",  ok);
					serviceIntent.putExtras(mbundle);
					context.startService(serviceIntent);
					mNotification.showNotificationResult(context, temp[0],temp[1]);
					break;
				}
				
			}
			break;
		// 添加其他case
		// .........
		default:
			break;
		}
	}
}