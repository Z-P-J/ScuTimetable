package com.scu.timetable.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public final class ApkUtil {

    private ApkUtil() {

    }

    public static synchronized String getVersionName(Context context){
        String versionName = "1.0.0";
        try {

            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static synchronized int compareVersion(String oldVersion, String newVersion) {
        //返回结果: -2 错误,-1 ,0 ,1
        int result = 0;
        String matchStr = "[0-9]+(\\.[0-9]+)*";
        oldVersion = oldVersion.trim();
        newVersion = newVersion.trim();
        //非版本号格式,返回error
        if (!oldVersion.matches(matchStr) || !newVersion.matches(matchStr)) {
            return -2;
        }
        String[] oldVs = oldVersion.split("\\.");
        String[] newVs = newVersion.split("\\.");
        int oldLen = oldVs.length;
        int newLen = newVs.length;
        int len = oldLen > newLen ? oldLen : newLen;
        for (int i = 0; i < len; i++) {
            if (i < oldLen && i < newLen) {
                int o = Integer.parseInt(oldVs[i]);
                int n = Integer.parseInt(newVs[i]);
                if (o > n) {
                    result = -1;
                    break;
                } else if (o < n) {
                    result = 1;
                    break;
                }
            } else {
                if (oldLen > newLen) {
                    for (int j = i; j < oldLen; j++) {
                        if (Integer.parseInt(oldVs[j]) > 0) {
                            result = -1;
                        }
                    }
                    break;
                } else if (oldLen < newLen) {
                    for (int j = i; j < newLen; j++) {
                        if (Integer.parseInt(newVs[j]) > 0) {
                            result = 1;
                        }
                    }
                    break;
                }
            }
        }
        return result;
    }

}
