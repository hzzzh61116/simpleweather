package com.alen.simpleweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;


import com.alen.simpleweather.gson.CaiyunData;
import com.alen.simpleweather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;



import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Alen on 2017/11/17.
 */

public class Utility {
    protected static final String HEFENG_KEY = "cc54dcd786564115aedda56f0f75ed53";
    protected static final String CAIYUN_KEY = "ewWNtS9CGMuxfo00/";
    private static Toast toast;

    //设置透明状态栏
    public static void statusBar(Window window){
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = window.getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }
    //toast工具
    public static void showToast(Context context,  String msg) {
        if (toast == null) {
            toast = Toast.makeText(context,msg,  Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    //处理两个数据提供商的工具
    public static Weather handleWeatherResponse(String response) {
        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
            String str = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(str, Weather.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static CaiyunData handleCaiyunWeatherResponse(String response) {
        try{
            JSONObject jsonObject = new JSONObject(response).getJSONObject("result").getJSONObject("hourly");
            String str = jsonObject.toString();
            return new Gson().fromJson(str, CaiyunData.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    //获取api链接
    public static String getURL(String lbsString, int i){
        switch (i){
            case 1:
                return "https://free-api.heweather.com/s6/weather?key=" + HEFENG_KEY + "&location=" + lbsString;
            case 2:
                return "https://api.caiyunapp.com/v2/" + CAIYUN_KEY + lbsString + "/forecast.json";
            default:
        }
        return null;
    }

    //设置字体
    public static void setTypeFace(Context context, TextView[] textViews) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Miui-Light.ttf");
        for (TextView textView : textViews) {
            textView.setTypeface(typeface);
        }
    }

    //获取星期几
    public static String getWeekOfDate(String date) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
            Calendar cal = Calendar.getInstance();
            cal.setTime(format.parse(date));
            int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
            if (w < 0)
                w = 0;
            return weekDays[w];
        }catch (ParseException e){
            e.printStackTrace();
            return null;
        }
    }

    //获取更新时间差
    public static long getTime(Context context, String fileName, String key){
        long currentTime=System.currentTimeMillis();
        String txt = getPrefe(context, fileName, key);
        txt = txt == null ? "1500000000000":txt;
        long updateTime = Long.parseLong(txt);
        return (currentTime - updateTime)/1000;
    }

    //获取数据库对象
    public static SQLiteDatabase getDB(Context context){
        String DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        String DB_NAME = "city.db";

        File file = new File(DB_PATH);

        if (!file.exists()){
            file.mkdir();
            try {
                // 得到 assets 目录下我们实现准备好的 SQLite 数据库作为输入流
                InputStream is = context.getAssets().open(DB_NAME);
                // 输出流
                OutputStream os = new FileOutputStream(DB_PATH + DB_NAME);

                // 文件写入
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }

                // 关闭文件流
                os.flush();
                os.close();
                is.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return SQLiteDatabase.openDatabase(DB_PATH+DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
    }

    //获取与设置SharedPreferences
    public static String getPrefe(Context context, String fileName, String key){
        SharedPreferences prefe = context.getSharedPreferences(fileName,MODE_PRIVATE);
        String value = prefe.getString(key,null);
        if (value != null){
            return value;
        }
        return null;
    }
    public static void setPrefe(Context context, String fileName, String key, String value){
        SharedPreferences.Editor editor = context.getSharedPreferences(fileName,MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }
}