package com.scu.timetable.ui.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;

/**
 * @author Z-P-J
 * @date 2019/5/18 13:57
 */
public class AlarmReceiver extends BroadcastReceiver {

    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver", "haha    " + new Date().toString());
//        Toast.makeText(context,  new Date().toString(), Toast.LENGTH_SHORT).show();
        startAlarm(context);
//        Intent i = new Intent(context, LongRunningService.class);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            //这是8.0以后的版本需要这样跳转
//            context.startForegroundService(i);
//        } else {
//            context.startService(i);
//        }
    }

    public static void startAlarm(Context context) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //int anHour = 10* 1000; // 10秒
//        int anHour = 1000; // 10秒
//        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
//        Intent i = new Intent(context, AlarmReceiver.class);
        Intent intent = new Intent();
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
//        manager.set(AlarmManager.RTC_WAKEUP, triggerAtTime, pi);
        manager.setWindow(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, 0, pi);
    }

}
