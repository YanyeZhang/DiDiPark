package com.zhangyanye.didipark.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

public class ImageUtil {

	/**
	 * 根据一个网络连接(String)获取bitmap图像
	 * 
	 * @param imageUri
	 * @return
	 * @throws MalformedURLException
	 */
	public static Bitmap getbitmap(String imageUri) {
		// 显示网络上的图片
		Bitmap bitmap = null;
		try {
			URL myFileUrl = new URL(imageUri);
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}



	/*
	 * 将图片保存到本地时进行压缩, 即将图片从Bitmap形式变为File形式时进行压缩
	 * File形式的图片确实被压缩了, 但是当你重新读取压缩后的file为 Bitmap是,它占用的内存并没有改变   
	 */
	public static void compressBmpToFileLow(Bitmap bmp, File file) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int options = 80;// 个人喜欢从80开始,
		//质量压缩方法，100表示不压缩，把压缩后的数据存放到baos中
		bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
		//循环判断如果压缩后图片是否大于100kb,大于继续压缩        
		while (baos.toByteArray().length / 1024 > 100) {
			baos.reset();//重置baos即清空baos
			options -= 10;//每次都减少10
			bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
		}
		try {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(baos.toByteArray());
			fos.flush();
			fos.close();
			System.gc();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/*
	 * 将图片保存到本地时进行压缩, 即将图片从Bitmap形式变为File形式时进行压缩
	 * File形式的图片确实被压缩了, 但是当你重新读取压缩后的file为 Bitmap是,它占用的内存并没有改变   
	 */
	public static void compressBmpToFileHigh(Bitmap bmp, File file) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int options = 80;// 个人喜欢从80开始,
		//质量压缩方法，100表示不压缩，把压缩后的数据存放到baos中
		bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
		//循环判断如果压缩后图片是否大于100kb,大于继续压缩        
		while (baos.toByteArray().length / 1024 > 1000) {
			baos.reset();//重置baos即清空baos
			options -= 10;//每次都减少10
			bmp.compress(Bitmap.CompressFormat.PNG, options, baos);
		}
		try {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(baos.toByteArray());
			fos.flush();
			fos.close();
			System.gc();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public  static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while ( baos.toByteArray().length / 1024>300) {    //循环判断如果压缩后图片是否大于100kb,大于继续压缩        
            baos.reset();//重置baos即清空baos
            options -= 10;//每次都减少10
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中

        }
        System.gc();
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }
	
	public static Bitmap getimage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了,只是获取框高
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath,newOpts);//此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        System.gc();
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }
	
	/*
	 * 将图片从本地读到内存时,进行压缩 ,即图片从File形式变为Bitmap形式
	 * 通过设置采样率, 减少图片的像素
	 */
	public static Bitmap compressBmpFromFile(String path) {
		Bitmap image = BitmapFactory.decodeFile(path);
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    int options = 100;
	    image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
	    while (baos.toByteArray().length / 1024 > 100) { 
	      baos.reset();
	      options -= 10;
	      image.compress(Bitmap.CompressFormat.JPEG, options, baos);
	    }
	    ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
	    Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
	    System.gc();
	    return bitmap;
	  }
	
	
	public static Bitmap downFigureSize(String path) {
		Bitmap picture = BitmapFactory.decodeFile(path);
		// 設定寬度不超過width，並利用Options.inSampleSize來縮圖
		int width = 125;
		if (picture.getWidth() > width) {
			Options options = new Options();
			// 若原始照片寬度無法整除width，則inSampleSize + 1
			options.inSampleSize = picture.getWidth() % width == 0 ? picture
					.getWidth() / width : picture.getWidth() / width + 1;
			picture = BitmapFactory.decodeFile(path, options);
			System.gc();
		}
		return picture;
	}
	
	public static Bitmap downPhotoSize(String path) {
		Bitmap picture = BitmapFactory.decodeFile(path);
		// 設定寬度不超過width，並利用Options.inSampleSize來縮圖
		int width = 200;
		if (picture.getWidth() > width) {
			Options options = new Options();
			// 若原始照片寬度無法整除width，則inSampleSize + 1
			options.inSampleSize = picture.getWidth() % width == 0 ? picture
					.getWidth() / width : picture.getWidth() / width + 1;
			picture = BitmapFactory.decodeFile(path, options);
			System.gc();
		}
		return picture;
	}
}
