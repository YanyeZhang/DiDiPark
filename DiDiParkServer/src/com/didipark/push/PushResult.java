package com.didipark.push;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.didipark.utils.MyConstant;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.ListMessage;
import com.gexin.rp.sdk.base.IIGtPush;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;

public class PushResult {

	@SuppressWarnings("unchecked")
	public static void pushResultToCustom(String cid, int customerId,String content) throws Exception {
		// 配置返回每个用户返回用户状态，可选
		System.setProperty("gexin.rp.sdk.pushlist.needDetails", "true");
		IGtPush push = new IGtPush(MyConstant.PUSH_APP_HOST,
				MyConstant.PUSH_APP_KEY, MyConstant.PUSH_APP_MASTER_SECRET);
		// 建立连接，开始鉴权
		push.connect();
		// 通知透传模板
		TransmissionTemplate template = transmissionTemplateDemo("1002-"+content+","+customerId);

		ListMessage message = new ListMessage();
		message.setData(template);

		// 设置消息离线，并设置离线时间
		message.setOffline(true);
		// 离线有效时间，单位为毫秒，可选
		message.setOfflineExpireTime(24 * 1000 * 3600);

		// 配置推送目标
		List targets = new ArrayList();
		Target target = new Target();
		target.setAppId(MyConstant.PUSH_APP_ID);
		// 用户别名推送，cid和用户别名2者只能选其一
		// String alias1 = "个";
		// target1.setAlias(alias1);
		target.setClientId(cid);
		targets.add(target);
		// 获取taskID
		String taskId = push.getContentId(message);
		// 使用taskID对目标进行推送
		IPushResult ret = push.pushMessageToList(taskId, targets);
		// 打印服务器返回信息
		System.out.println(ret.getResponse().toString());
	}
	public static void pushResultToCarportOwner(String cid, String content,String carportId) throws Exception {
		// 配置返回每个用户返回用户状态，可选
		System.setProperty("gexin.rp.sdk.pushlist.needDetails", "true");
		IGtPush push = new IGtPush(MyConstant.PUSH_APP_HOST,
				MyConstant.PUSH_APP_KEY, MyConstant.PUSH_APP_MASTER_SECRET);
		// 建立连接，开始鉴权
		push.connect();
		// 通知透传模板
		TransmissionTemplate template = transmissionTemplateDemo("1003-"+content+","+carportId);

		ListMessage message = new ListMessage();
		message.setData(template);

		// 设置消息离线，并设置离线时间
		message.setOffline(true);
		// 离线有效时间，单位为毫秒，可选
		message.setOfflineExpireTime(24 * 1000 * 3600);

		// 配置推送目标
		List targets = new ArrayList();
		Target target = new Target();
		target.setAppId(MyConstant.PUSH_APP_ID);
		// 用户别名推送，cid和用户别名2者只能选其一
		// String alias1 = "个";
		// target1.setAlias(alias1);
		target.setClientId(cid);
		targets.add(target);
		// 获取taskID
		String taskId = push.getContentId(message);
		// 使用taskID对目标进行推送
		IPushResult ret = push.pushMessageToList(taskId, targets);
		// 打印服务器返回信息
		System.out.println(ret.getResponse().toString());
	}
	public static TransmissionTemplate transmissionTemplateDemo(String content) {
		TransmissionTemplate template = new TransmissionTemplate();
		template.setAppId(MyConstant.PUSH_APP_ID);
		template.setAppkey(MyConstant.PUSH_APP_KEY);
		// 透传消息设置，1为强制启动应用，客户端接收到消息后就会立即启动应用；2为等待应用启动
		template.setTransmissionType(2);
		template.setTransmissionContent(content);
		return template;
	}

}
