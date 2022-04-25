package com.zlt.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class StringUtil {

    private static String str = "qwertyuiopasdfghjklzxcvbnm0987654321";

    /**
     * 获取一个UUID
     * @return
     */
    public static String uuid(){
        return UUID.randomUUID().toString();
    }


    /**
     * 判断一个字符串是否为非空
     * @param str
     * @return
     */
    public static boolean isNotNul(String str){
        return str != null && !"".equals(str);
    }

    /**
     * 随机生成指定长度的字符串
     * @param len
     * @return
     */
    public static String randomStr(int len){
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            int index = (int) (Math.random() * str.length());
            stringBuilder.append(str.charAt(index));
        }
        return stringBuilder.toString();
    }

    /**
     * 获取当前时间的字符串类型
     * @return
     */
    public static String getCurrentTime(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
}
