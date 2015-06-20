package com.didipark.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtil {

	
	public static String saveFile(String fileType, String fileData, String destDir)
			throws IOException {
		Date date = new Date();
		String newFileName = new SimpleDateFormat("yyyyMMddHHmmss_SSS")
				.format(date)+"." + fileType;
		String newFilePath = destDir + newFileName;
		//String newFilePath = MyConstant.DOMAIN_IMG + newFileName;
		FileInputStream in = new FileInputStream(new File(fileData));
		FileOutputStream out = new FileOutputStream(newFilePath);
		int length = 2097152;
		byte[] buffer = new byte[length];
		while (true) {
			int ins = in.read(buffer);
			if (ins == -1) {
				in.close();
				out.flush();
				out.close();
				try {
					TimeUtil.start();
					//ImageUtil.makeSmallImage(new File(newFilePath), MyConstant.carport2+newFileName);
					ImageUtil.makeSmallImage(new File(newFilePath), MyConstant.DOMAIN_IMG2+newFileName);
				    TimeUtil.finish("ËõÍ¼");
				} catch (Exception e) {
					e.printStackTrace();
				}
				return newFileName;
			} else
				out.write(buffer, 0, ins);
		}
		
		
	}

}
