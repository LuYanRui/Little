package com.didi.little;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Utils {
    // 获取当前时间格式 yyyy-MM-dd
    public static String getCurtime(){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String date=sdf.format(new java.util.Date());
        return date;
    }
    // 获取当前时间格式 yyyy-MM-dd HH:mm
    public static String getCurtimeHH(){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String date=sdf.format(new java.util.Date());
        return date;
    }
    // 判断是否超时
    public static boolean isTimeOut(String oldtime){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try
        {
            Date d1 = df.parse(oldtime);
            Date d2 = new Date(System.currentTimeMillis());
            long diff = d1.getTime() - d2.getTime();
            long days = diff / (1000 * 60 * 60 * 24);

            if (days >= 10){
                return true;
            }else{
                return false;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
