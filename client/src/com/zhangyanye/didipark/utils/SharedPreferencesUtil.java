package com.zhangyanye.didipark.utils;

/** 
* @Title: ss.java 
* @Package com.zhangyanye.onestep.utils 
* @Description: TODO(用一句话描述该文件做什么) 
* @author 名字 
* @date 2015年4月13日 下午11:59:26 
* @version V1.0 
*/  


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/** 
 * @ClassName: SharedPreferencesUtil 
 * @Description: 封装 SharedPreferences
 * @author名字 
 * @date 2015年4月13日 下午11:59:26 
 *  
 */
public class SharedPreferencesUtil { 
    
    //存储的sharedpreferences文件名 
    private static final String FILE_NAME = "save_file_name"; 
       
    /**
     * 保存数据到文件
     * @param context
     * @param key
     * @param data
     */ 
    public static void saveData(Context context, String key,Object data){ 
   
        String type = data.getClass().getSimpleName(); 
        SharedPreferences sharedPreferences = context 
                .getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE); 
        Editor editor = sharedPreferences.edit(); 
           
        if ("Integer".equals(type)){ 
            editor.putInt(key, (Integer)data); 
        }else if ("Boolean".equals(type)){ 
            editor.putBoolean(key, (Boolean)data); 
        }else if ("String".equals(type)){ 
            editor.putString(key, (String)data); 
        }else if ("Float".equals(type)){ 
            editor.putFloat(key, (Float)data); 
        }else if ("Long".equals(type)){ 
            editor.putLong(key, (Long)data); 
        } 
           
        editor.commit(); 
    } 
       
    /**
     * 从文件中读取数据
     * @param context
     * @param key
     * @param defValue
     * @return
     */ 
    public static Object getData(Context context, String key, Object defValue){ 
           
        String type = defValue.getClass().getSimpleName(); 
        SharedPreferences sharedPreferences = context.getSharedPreferences 
                (FILE_NAME, Context.MODE_PRIVATE); 
           
        //defValue为为默认值，如果当前获取不到数据就返回它 
        if ("Integer".equals(type)){ 
            return sharedPreferences.getInt(key, (Integer)defValue); 
        }else if ("Boolean".equals(type)){ 
            return sharedPreferences.getBoolean(key, (Boolean)defValue); 
        }else if ("String".equals(type)){ 
            return sharedPreferences.getString(key, (String)defValue); 
        }else if ("Float".equals(type)){ 
            return sharedPreferences.getFloat(key, (Float)defValue); 
        }else if ("Long".equals(type)){ 
            return sharedPreferences.getLong(key, (Long)defValue); 
        } 
           
        return null; 
    } 
   
}