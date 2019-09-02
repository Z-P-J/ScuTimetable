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
import android.net.wifi.WifiManager;
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

import java.util.Date;
import java.util.Objects;


public class NotificationService extends Service {
    public static final String EVENT_STOPPED = NotificationService.class.getName() + ".STOPPED";
    private static final int FOREGROUND_SERVICE_NOTIF_ID = 1;
    private static final String CHANNEL_FOREGROUND_SERVICE = "foreground";
    private static final String ACTION_START = NotificationService.class.getName() + ".START";
    private static final String ACTION_STOP = NotificationService.class.getName() + ".STOP";
    private final HandlerThread serviceThread = new HandlerThread("aria2app notification service");
    private WifiManager wifiManager;
    private NotificationManager notificationManager;
    private Messenger messenger;
    private LocalBroadcastManager broadcastManager;
    private ConnectivityChangedReceiver connectivityChangedReceiver;

    private static void debug(String msg) {
        Log.d(NotificationService.class.getSimpleName(),  ": " + msg);
    }

    public static void start(@NonNull Context context) {
        ContextCompat.startForegroundService(context, new Intent(context, NotificationService.class)
                .setAction(ACTION_START));
    }

    public static void stop(@NonNull Context context) {
        debug("Called stop service");
        context.startService(new Intent(context, NotificationService.class).setAction(ACTION_STOP));
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        broadcastManager = LocalBroadcastManager.getInstance(this);

        if (intent != null) {
            if (Objects.equals(intent.getAction(), ACTION_STOP)) {
                stopForeground(true);
                stopSelf();
            } else if (Objects.equals(intent.getAction(), ACTION_START)) {
                notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    createMainChannel();
                }

                connectivityChangedReceiver = new ConnectivityChangedReceiver();
                registerReceiver(connectivityChangedReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
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
                .setSmallIcon(R.drawable.ic_access_alarms_black_24dp)
                .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .addAction(new NotificationCompat.Action(R.drawable.ic_close_black_24dp, "停止服务", PendingIntent.getService(this, 1, new Intent(this, NotificationService.class).setAction(ACTION_STOP), PendingIntent.FLAG_UPDATE_CURRENT)))
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

//    private void handleEvent(@NonNull MultiProfile.UserProfile profile, @NonNull String gid, @NonNull EventType type) {
//
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, type.channelName());
//        builder.setContentTitle("title")
//                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//                .setContentText("GID#" + gid)
//                .setContentInfo(profile.getPrimaryText(this))
//                .setCategory(Notification.CATEGORY_EVENT)
//                .setGroup(gid)
//                .setAutoCancel(true)
//                .setSmallIcon(R.drawable.ic_notification_icon)
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_new_releases_grey_48dp))
//                .setColor(ContextCompat.getColor(this, R.color.colorAccent));
//
//        Bundle bundle = new Bundle();
//        bundle.putString("profileId", profile.getParent().id);
//        bundle.putString("gid", gid);
//        builder.setContentIntent(PendingIntent.getActivity(this, 1, new Intent(this, MainActivity.class)
//                .putExtras(bundle), PendingIntent.FLAG_UPDATE_CURRENT));
//
//        notificationManager.notify(ThreadLocalRandom.current().nextInt(), builder.build());
//    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createMainChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_FOREGROUND_SERVICE, "Foreground service", NotificationManager.IMPORTANCE_LOW);
        channel.setShowBadge(false);
        notificationManager.createNotificationChannel(channel);
    }

    @Override
    public void onDestroy() {
        if (connectivityChangedReceiver != null)
            unregisterReceiver(connectivityChangedReceiver);
    }

    private class ConnectivityChangedReceiver extends BroadcastReceiver {

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
            Intent intent = new Intent(context, ConnectivityChangedReceiver.class);
//            Intent intent = new Intent();
//            intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
//        manager.set(AlarmManager.RTC_WAKEUP, triggerAtTime, pi);
            manager.setWindow(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 100000, 0, pi);
        }

    }

}
