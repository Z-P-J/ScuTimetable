package com.scu.timetable.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.scu.timetable.service.AlarmService;

import java.util.Date;
import java.util.LinkedList;

/**
 * @author Z-P-J
 * @date 2019/5/18 13:57
 */
public class AlarmReceiver extends BroadcastReceiver {

    private final LinkedList<AlarmService.Alarm> alarmLinkedList;
    private final AlarmListener alarmListener;

    public AlarmReceiver(LinkedList<AlarmService.Alarm> alarmLinkedList, AlarmListener alarmListener) {
        this.alarmLinkedList = alarmLinkedList;
        this.alarmListener = alarmListener;
    }

    public interface AlarmListener {
        void onAlarm();
        void updateAlarm(AlarmService.Alarm alarm);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver", "haha    " + new Date().toString());
        if (alarmLinkedList == null || alarmListener == null) {
            return;
        }

        if (alarmLinkedList.isEmpty()) {
            alarmListener.onAlarm();
        } else {
            AlarmService.Alarm alarm = alarmLinkedList.pop();
            alarmListener.updateAlarm(alarm);
        }
    }

//    public static void startAlarm(Context context) {
//        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        //int anHour = 10* 1000; // 10秒
////        int anHour = 1000; // 10秒
////        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
////        Intent i = new Intent(context, AlarmReceiver.class);
//        Intent intent = new Intent();
//        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
//        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
////        manager.set(AlarmManager.RTC_WAKEUP, triggerAtTime, pi);
//        manager.setWindow(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, 0, pi);
//    }

}
