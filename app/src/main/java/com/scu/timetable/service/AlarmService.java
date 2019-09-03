package com.scu.timetable.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Messenger;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.scu.timetable.MainActivity;
import com.scu.timetable.R;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;


public class AlarmService extends Service {
    public static final String EVENT_STOPPED = AlarmService.class.getName() + ".STOPPED";
    private static final int FOREGROUND_SERVICE_NOTIF_ID = 1;
    private static final String CHANNEL_FOREGROUND_SERVICE = "foreground";
    private static final String ACTION_START = AlarmService.class.getName() + ".START";
    private static final String ACTION_STOP = AlarmService.class.getName() + ".STOP";
    private final HandlerThread serviceThread = new HandlerThread("aria2app notification service");
    private NotificationManager notificationManager;
    private Messenger messenger;
    private LocalBroadcastManager broadcastManager;
    private AlarmReceiver alarmReceiver;

    private boolean isCreate = false;
    private int startArgFlags;
    private boolean isOpenStartForeground = true;
    private boolean isOpenAlarmRemind;

    // 与闹钟相关
    private AlarmManager alarmManager;
    private Calendar calendar;
    private PendingIntent sender;

    public static final int TYPE_TEMP = 1, TYPE_ONE_DAY = 2;

    private static void debug(String msg) {
        Log.d(AlarmService.class.getSimpleName(),  ": " + msg);
    }

    public static void start(@NonNull Context context) {
        ContextCompat.startForegroundService(context, new Intent(context, AlarmService.class)
                .setAction(ACTION_START));
    }

    public static void stop(@NonNull Context context) {
        debug("Called stop service");
        context.startService(new Intent(context, AlarmService.class).setAction(ACTION_STOP));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("AlarmService", "onCreate");
        isCreate = true;
        init();
    }

    private void init() {
        // 保证内存不足，杀死会重新创建
        startArgFlags = getApplicationInfo().targetSdkVersion < Build.VERSION_CODES.ECLAIR ? START_STICKY_COMPATIBILITY : START_STICKY;

        // 与闹钟相关
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        Intent intent = new Intent(this, com.scu.timetable.receiver.AlarmReceiver.class);
        sender = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if (!isCreate) {
            onCreate();
        }
        broadcastManager = LocalBroadcastManager.getInstance(this);

        if (intent != null) {
            if (Objects.equals(intent.getAction(), ACTION_STOP)) {
                stopForeground(true);
                stopSelf();
            } else if (Objects.equals(intent.getAction(), ACTION_START)) {
                notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    createMainChannel();
                }

                alarmReceiver = new AlarmReceiver();
                registerReceiver(alarmReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
                startForeground(FOREGROUND_SERVICE_NOTIF_ID, createForegroundServiceNotification());
                return super.onStartCommand(intent, flags, startId);
            }
        }

        broadcastManager.sendBroadcast(new Intent(EVENT_STOPPED));
        stopSelf();

        return START_NOT_STICKY; // Process will stop
    }

    private void updateForegroundNotification() {
        notificationManager.notify(FOREGROUND_SERVICE_NOTIF_ID, createForegroundServiceNotification());
    }

    @NonNull
    private Notification createForegroundServiceNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_FOREGROUND_SERVICE);
        builder.setShowWhen(false)
                .setContentTitle("notificationService")
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentText("content text")
                .setCategory(Notification.CATEGORY_SERVICE)
                .setGroup(CHANNEL_FOREGROUND_SERVICE)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
//                .addAction(new NotificationCompat.Action(R.drawable.ic_close_black_24dp, "停止服务", PendingIntent.getService(this, 1, new Intent(this, AlarmService.class).setAction(ACTION_STOP), PendingIntent.FLAG_UPDATE_CURRENT)))
                .setContentIntent(PendingIntent.getActivity(this, 1, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));

        return builder.build();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (messenger == null) {
            serviceThread.start();
            broadcastManager = LocalBroadcastManager.getInstance(this);
            messenger = new Messenger(new Handler());
        }

        return messenger.getBinder();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createMainChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_FOREGROUND_SERVICE, "Foreground service", NotificationManager.IMPORTANCE_LOW);
        channel.setShowBadge(false);
        notificationManager.createNotificationChannel(channel);
    }

    @Override
    public void onDestroy() {
        if (alarmReceiver != null)
            unregisterReceiver(alarmReceiver);
    }

    private class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
//            if (wifiManager != null && Objects.equals(intent.getAction(), ConnectivityManager.CONNECTIVITY_ACTION)) {
//                boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
//                if (!noConnectivity) {
//                    int networkType = intent.getIntExtra(ConnectivityManager.EXTRA_NETWORK_TYPE, ConnectivityManager.TYPE_DUMMY);
//                    if (networkType == ConnectivityManager.TYPE_DUMMY) return;
//
////                    recreateWebsockets(networkType);
//                    updateForegroundNotification();
//                }
//            }
            startAlarm(context);
        }

        public void startAlarm(Context context) {
            Log.d("ChangedReceiver", new Date().toString());
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            //int anHour = 10* 1000; // 10秒
//        int anHour = 1000; // 10秒
//        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
            Intent intent = new Intent(context, AlarmReceiver.class);
//            Intent intent = new Intent();
//            intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
//        manager.set(AlarmManager.RTC_WAKEUP, triggerAtTime, pi);
            manager.setWindow(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 100000, 0, pi);
        }

    }

    /**
     * 更新闹钟时间
     *
     * @param hour   时
     * @param minute 分
     * @param type   类型
     */
    private void updateAlram(int hour, int minute, int type) {
        if (hour >= 0 && minute >= 0 && alarmManager != null) {
            // 取消闹钟
            alarmManager.cancel(sender);

            // 处理时间
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            // 防止设置的时间戳比当前系统时间戳小而响应闹钟问题
            if (System.currentTimeMillis() > calendar.getTimeInMillis()) {
                calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 1);
            }

            // 重新设置闹钟
            switch (type) {
                case TYPE_TEMP:// 临时
                    // 开启闹钟
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
                    } else {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
                    }
                    break;

                case TYPE_ONE_DAY:// 每天
                    // 开启闹钟
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        alarmManager.setWindow(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 100, sender);
                    } else {
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24 * 60 * 60 * 1000, sender);
                    }
                    break;
            }
        }
    }

}
