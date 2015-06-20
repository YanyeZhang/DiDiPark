package com.didipark.utils;


import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import javax.imageio.ImageIO;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class ImageUtil {

    /**
     * 生成缩略图
     * @param srcImageFile 源图片文件的File实例      File file = new File("文件名");
     * @param dstImageFileName 待生成的缩略图片完整路径(生成的格式为：image/jpeg);
     * @throws Exception
     */
    public static void makeSmallImage(File srcImageFile,String dstImageFileName) throws Exception {
    	FileOutputStream fileOutputStream = null;
        JPEGImageEncoder encoder = null;
        BufferedImage tagImage = null;
        Image srcImage = null;
        try{
            srcImage = ImageIO.read(srcImageFile);
            int srcWidth = srcImage.getWidth(null);//原图片宽度
            int srcHeight = srcImage.getHeight(null);//原图片高度
            int dstMaxSize = 80;//目标缩略图的最大宽度/高度，宽度与高度将按比例缩写
            int dstWidth = srcWidth;//缩略图宽度
            int dstHeight = srcHeight;//缩略图高度
            float scale = 0;
            //计算缩略图的宽和高
            if(srcWidth>dstMaxSize){
                dstWidth = dstMaxSize;
                scale = (float)srcWidth/(float)dstMaxSize;
                dstHeight = Math.round((float)srcHeight/scale);
            }
            srcHeight = dstHeight;
            if(srcHeight>dstMaxSize){
                dstHeight = dstMaxSize;
                scale = (float)srcHeight/(float)dstMaxSize;
                dstWidth = Math.round((float)dstWidth/scale);
            }
            //生成缩略图
            tagImage = new BufferedImage(dstWidth,dstHeight,BufferedImage.TYPE_INT_RGB);
            tagImage.getGraphics().drawImage(srcImage,0,0,dstWidth,dstHeight,null);
            fileOutputStream = new FileOutputStream(dstImageFileName);
            encoder = JPEGCodec.createJPEGEncoder(fileOutputStream);
            encoder.encode(tagImage);
            fileOutputStream.close();
            fileOutputStream = null;
        }finally{
            if(fileOutputStream!=null){
                try{
                    fileOutputStream.close();
                }catch(Exception e){
                }
                fileOutputStream = null;
            }
            encoder = null;
            tagImage = null;
            srcImage = null;
            System.gc();
        }
    }
}