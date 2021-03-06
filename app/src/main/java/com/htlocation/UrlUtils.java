package com.htlocation;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Administrator on 2017/4/17.
 */

public class UrlUtils {

    //获取时间戳
    public static String getTime() {
        long l = System.currentTimeMillis();
        String m = l + "";

        //获取当前时间
        Date date=new Date();
        DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time=format.format(date);


        return time;
    }

    //MD5加密
    public static String getMd5(String s) {
        try {
            MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
            String newVal = s + "huidong@2017";
            localMessageDigest.update(newVal.getBytes());
            byte[] arrayOfByte = localMessageDigest.digest();
            StringBuffer localStringBuffer = new StringBuffer("");
            for (int i = 0; i < arrayOfByte.length; i++) {
                int j = arrayOfByte[i];
                if (j < 0)
                    j += 256;
                if (j < 16)
                    localStringBuffer.append("0");
                localStringBuffer.append(Integer.toHexString(j));
            }
            String str = localStringBuffer.toString();
            return str;
        } catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
            localNoSuchAlgorithmException.printStackTrace();
        }
        return null;
    }

}
