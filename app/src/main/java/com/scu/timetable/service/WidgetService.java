package com.scu.timetable.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.scu.timetable.utils.TimetableWidgtHelper;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Z-P-J
 */
public class WidgetService extends Service {

    //定时器
//    private static Timer timer;

    @Override
    public void onCreate() {
        super.onCreate();
//        Log.d("MainActivity", "服务开始了");
//
//        timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
////                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
////                int [] appIds = appWidgetManager.getAppWidgetIds(new ComponentName(getApplicationContext(), TimetableWidget.class));
////                appWidgetManager.updateAppWidget(appIds, TimetableWidgtHelper.refreshViews(getApplicationContext()));
//                Log.d("MainActivity", "定时器开始了");
//            }
//        }, 0, 86400000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        AlarmReceiver.startAlarm(this);
//        AlarmReceiver.startAlarm(this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        timer = null;
    }

}
