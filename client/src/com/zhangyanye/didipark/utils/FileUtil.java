package com.zhangyanye.didipark.utils;

import java.io.File;



import android.os.Environment;
import android.util.Log;

/**
 * @ClassName: FileUtil
 * @Description: 实现照片的存储
 * @author zhangyanye
 * @date 2015年4月13日 下午11:18:32
 * 
 */
public class FileUtil {

	final String SDPATH = Environment.getExternalStorageDirectory() + "/足下";
	File albumDir;

	public FileUtil() {
	}

	public void createFileDir() {
		File fileDir = new File(SDPATH);
		File photosDir = new File(SDPATH + "/相册/");
		try {
			if (!fileDir.exists()) {
				fileDir.mkdirs();
				photosDir.mkdirs();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("zyy", "create fail");
		}
	}

	public boolean creatAlbum(String albumName) {
		String photosDir = SDPATH + "/相册/";
		albumDir = new File(photosDir + albumName + "/");
		albumDir.mkdirs();
		if (albumDir.exists())
			return true;
		else
			return false;
	}

	public File getAlbumFile() {
		if (albumDir != null)
			return albumDir;
		return null;
	}

	public String getAlbumPath() {
		if (albumDir != null)
			return albumDir.getPath();
		
		return null;
	}
	
	
	public boolean checkSDExist() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {

			return true;
		} else {

			return false;
		}
	}

	
}
