package com.zhangyanye.didipark.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.android.volley.toolbox.ImageLoader.ImageCache;


public class BFImageCache implements ImageCache {
    /** 单例 */
    private static BFImageCache cache;
    /** 内存缓存 */
    private HashMap<String, Bitmap> memory;
    /** 缓存目录 */
    private String cacheDir;
    
    /** 获取单例 */
    public static BFImageCache getInstance() {
        if (null == cache) {
            cache = new BFImageCache();
        }
        return cache;
    }
    /** 初始化方法，Application的onCreate中调用 */
    public void initilize(Context context) {
        memory = new HashMap<String, Bitmap>();
        cacheDir = context.getCacheDir().toString()+File.separator;
    }
    
    @Override
    public Bitmap getBitmap(String url) {
        // 获取图片
        try {
            String key = Md5Util.getMD5Str(url);
            if (memory.containsKey(key)) {
                return memory.get(key);
            } else {
                File file = new File(cacheDir + key);
                if (file.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(file.toString());
                    return bitmap;
                }
            }
        } catch (Exception e) {
            Log.d("zyy", e.getMessage());
        }
        return null;
    }
    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        // 尺寸超过10时，清理缓存并放入内存
        if (memory.size() == 10) {
            Iterator<String> it = memory.keySet().iterator();
            while (it.hasNext()) {
                try {
                    String key = it.next();
                    Bitmap temp = memory.get(key);
                    File file = new File(cacheDir+key);
                    FileOutputStream os;
                    os = new FileOutputStream(file, false);
                    temp.compress(CompressFormat.JPEG, 100, os);
                    os.flush();
                    os.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            memory.clear();
        }
        // 放入图片到内存
        memory.put(Md5Util.getMD5Str(url), bitmap);
    }
}
