package com.zhangyanye.didipark.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.baidu.speechsynthesizer.SpeechSynthesizer;
import com.baidu.speechsynthesizer.SpeechSynthesizerListener;
import com.baidu.speechsynthesizer.publicutility.SpeechError;
import com.baidu.speechsynthesizer.publicutility.SpeechLogger;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

public class BaiduVoiceService extends Service implements
		SpeechSynthesizerListener {


	public void onCreate() {
		super.onCreate();
	}

	private SpeechSynthesizer speechSynthesizer;
	/** 指定license路径，需要保证该路径的可读写权限 */
	private static final String LICENCE_FILE_NAME = Environment
			.getExternalStorageDirectory() + "/tts/baidu_tts_licence.dat";


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String content = intent.getStringExtra("content");
		if (content != null)
			baiDuSpeaker(content);
		return super.onStartCommand(intent, flags, startId);
	} 
		
	

	private void baiDuSpeaker(String content) {
		try {
			System.loadLibrary("BDSpeechDecoder_V1");
		} catch (UnsatisfiedLinkError e) {
			SpeechLogger.logD("load BDSpeechDecoder_V1 failed, ignore");
		}
		System.loadLibrary("bd_etts");
		System.loadLibrary("bds");

		// 第二个参数当前请传入任意非空字符串即可
		speechSynthesizer = SpeechSynthesizer.newInstance(
				SpeechSynthesizer.SYNTHESIZER_AUTO, getApplicationContext(),
				"holder", this);
		// 请替换为在百度开发者中心注册应用得到的 apikey 和 secretkey
		speechSynthesizer.setApiKey("PlgmVNpR1N7uE5A5wzW2mNzX",
				"VraV9v6uMl3GljQxHPA651DKZMdAzyQo");
		// 设置授权文件路径，LICENCE_FILE_NAME 请替换成实际路径
		speechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_LICENCE_FILE,
				LICENCE_FILE_NAME);
		// TTS 所需的资源文件，可以放在任意可读目录，可以任意改名
		setParams();
		speechSynthesizer.speak(content);
		stopSelf();
	}

	private void setParams() {
		speechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "5");
		speechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5");
		speechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5");
		// 线程优先级（仅离线引擎）
		speechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_THREAD_PRIORITY,
				"5");
		// 优化等级（仅离线引擎）
		speechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOCODER_OPTIM_LEVEL,
				"0");
	}

	@Override
	public void onDestroy() {
		System.gc();
		super.onDestroy();
	}

	@Override
	public void onBufferProgressChanged(SpeechSynthesizer arg0, int arg1) {
	}

	@Override
	public void onCancel(SpeechSynthesizer arg0) {
	}

	@Override
	public void onError(SpeechSynthesizer arg0, SpeechError arg1) {
	}

	@Override
	public void onNewDataArrive(SpeechSynthesizer arg0, byte[] arg1,
			boolean arg2) {
	}

	@Override
	public void onSpeechFinish(SpeechSynthesizer arg0) {
	}

	@Override
	public void onSpeechPause(SpeechSynthesizer arg0) {
	}

	@Override
	public void onSpeechProgressChanged(SpeechSynthesizer arg0, int arg1) {
	}

	@Override
	public void onSpeechResume(SpeechSynthesizer arg0) {
	}

	@Override
	public void onSpeechStart(SpeechSynthesizer arg0) {
	}

	@Override
	public void onStartWorking(SpeechSynthesizer arg0) {
	}

	@Override
	public void onSynthesizeFinish(SpeechSynthesizer arg0) {
	}


	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
