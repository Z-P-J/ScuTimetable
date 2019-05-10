package com.scu.timetable.utils;

import android.content.Context;

import com.scu.timetable.utils.content.SPHelper;

import java.util.Date;

/**
 * @author 25714
 */
public final class TimetableHelper {

    public static final String UA = "Mozilla/5.0 (Linux; U; Android 8.1.0; zh-cn; BLA-AL00 Build/HUAWEIBLA-AL00) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/57.0.2987.132 MQQBrowser/8.9 Mobile Safari/537.36";

    private TimetableHelper() {

    }

    public static boolean isLogined(Context context) {
        if (SPHelper.getBoolean("logined", false) && SubjectUtil.hasJsonFile(context)) {
            String date = getCurrentDate();
            if (!date.isEmpty()) {
                Date oldDate = DateUtil.parse(date);
                int weeks = DateUtil.computeWeek(oldDate, new Date());
                int currentWeek = getCurrentWeek();
                setCurrentWeek(currentWeek + weeks);
            } else {
                setCurrentDate(DateUtil.currentDate());
            }
            return true;
        }
        return false;
    }

    public static void setCurrentWeek(int week) {
        SPHelper.putInt("currrent_weak", week);
    }

    public static int getCurrentWeek() {
        return SPHelper.getInt("currrent_weak", 1);
    }

    public static void setCurrentDate(String date) {
        SPHelper.putString("current_date", date);
    }

    public static String getCurrentDate() {
        return SPHelper.getString("current_date", "");
    }

}
