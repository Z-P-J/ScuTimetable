package com.scu.timetable.ui.widget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
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
    private static Timer timer;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MainActivity", "服务开始了");
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
                int [] appIds = appWidgetManager.getAppWidgetIds(new ComponentName(getApplicationContext(), TimetableWidget.class));
                appWidgetManager.updateAppWidget(appIds, TimetableWidgtHelper.refreshViews(getApplicationContext()));
                Log.d("MainActivity", "定时器开始了");
            }
        }, 0, 86400000);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer = null;
    }

}
