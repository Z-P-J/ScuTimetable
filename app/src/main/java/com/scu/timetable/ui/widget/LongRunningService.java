package com.scu.timetable.ui.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Date;

/**
 * @author Z-P-J
 * @date 2019/5/18 13:56
 */
public class LongRunningService extends Service {

    public LongRunningService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("LongRunningService", "executed at " + new Date().toString());
//        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        //int anHour = 10* 1000; // 10秒
////        int anHour = 1000; // 10秒
////        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
//        Intent i = new Intent(this, AlarmReceiver.class);
//        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
////        manager.set(AlarmManager.RTC_WAKEUP, triggerAtTime, pi);
//        manager.setWindow(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(), 1000, pi);
//        //   stopForeground(true);
////        stopSelf();
        AlarmReceiver.startAlarm(this);
        return super.onStartCommand(intent, flags, startId);
    }

}
