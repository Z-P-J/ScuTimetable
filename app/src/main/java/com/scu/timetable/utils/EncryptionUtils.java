package com.scu.timetable.utils;

/**
 * Time: 2019/11/29 0029
 * Author: zoulong
 */
public class EncryptionUtils {
    static {
        System.loadLibrary("encryption");
    }

    // 加密
    public static native String encryptByAES(String data);

    // 解密
    public static native String decryptByAES(String data);
}
