package com.example.cardbossapp2.util;

import com.cardlan.utils.ByteUtil;

/**
 * @Description: java类作用描述
 * @Author: jakezhang
 * @CreateDate: 2020/11/22 18:57
 */
public class CardReadWriteUtil2 {

    //字节数组按位取反，并转为16进制字符串输出
    public static String byteNotHexStr(byte[] bytes) {
        String bytesNotHexStr = "";
        byte[] bytesNot = new byte[32];//存放32位字节数组取反的值
        for (int i = 0; i < bytes.length; i++) {
            bytesNot[i] = (byte) ~bytes[i];
        }
        bytesNotHexStr = ByteUtil.byteArrayToHexString(bytesNot);
        return bytesNotHexStr;
    }

    //转为char
    public static char stringToChar(String string) {
        if (!ByteUtil.notNull(string)) {
            string = "0";
        }
//        int ivalue = Integer.parseInt(string);
//        return (char) ivalue;
        return ByteUtil.intStringToChar(Integer.valueOf(string,16).toString());
    }
}
