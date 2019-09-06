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
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Messenger;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.felix.atoast.library.AToast;
import com.scu.timetable.MainActivity;
import com.scu.timetable.R;
import com.scu.timetable.model.ScuSubject;
import com.scu.timetable.utils.TimetableHelper;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;


public class AlarmService extends Service implements TextToSpeech.OnInitListener {
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
    private List<ScuSubject> scuSubjects;
    private final LinkedList<ScuSubject> scuSubjectLinkedList = new LinkedList<>();

    private Notification currentNotification;
    private ScuSubject nextSubject;
    private final LinkedList<Alarm> alarmQueue = new LinkedList<>();
    private TextToSpeech textToSpeech;

    // 与闹钟相关
    private AlarmManager alarmManager;
    private Calendar calendar;
    private PendingIntent sender;
    private Timer timer;

    public class Alarm{

        static final int TYPE_BEFORE_CLASS = 0;
        static final int TYPE_BEFORE_CLASS_TEN_MIN = 1;
        static final int TYPE_CLASS_BEGAIN = 2;
        static final int TYPE_CLASS_BREAK = 3;
        static final int TYPE_CLASS_END = 4;
//        static final int TYPE_CLASS_AFTER_CLASS = 5;

        private Calendar calendar;
        private int alarmType;

        void setCalendar(Calendar calendar) {
            this.calendar = calendar;
        }

        Calendar getCalendar() {
            return calendar;
        }

        int getAlarmType() {
            return alarmType;
        }

        void setAlarmType(int alarmType) {
            this.alarmType = alarmType;
        }
    }

    private class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("onReceive", "onReceive:" + new Date().toString());
            if (alarmQueue.isEmpty()) {
                onAlarm();
            } else {
                Alarm alarm = alarmQueue.pop();
                updateAlarm(alarm);
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("AlarmService", "onCreate");
        isCreate = true;
        init();
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(CHANNEL_FOREGROUND_SERVICE, "Foreground service", NotificationManager.IMPORTANCE_LOW);
                    channel.setShowBadge(false);
                    notificationManager.createNotificationChannel(channel);
                }
                return super.onStartCommand(intent, flags, startId);
            }
        }

        broadcastManager.sendBroadcast(new Intent(EVENT_STOPPED));
        stopSelf();

        return START_NOT_STICKY; // Process will stop
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

    @Override
    public void onDestroy() {
        if (alarmReceiver != null)
            unregisterReceiver(alarmReceiver);
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {
        Log.d("onInit", "status=" + status);
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.CHINA);
            Log.d("onInit", "result=" + result);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                AToast.normal("数据丢失或不支持");
            }
        }
    }

    private void init() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        startForeground(FOREGROUND_SERVICE_NOTIF_ID, createForegroundServiceNotification());
        // 保证内存不足，杀死会重新创建
        startArgFlags = getApplicationInfo().targetSdkVersion < Build.VERSION_CODES.ECLAIR ? START_STICKY_COMPATIBILITY : START_STICKY;

        textToSpeech = new TextToSpeech(this, this);

        // 与闹钟相关
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        alarmReceiver = new AlarmReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("alarm_receiver");
        registerReceiver(alarmReceiver, intentFilter);

//        Intent intent = new Intent(this, AlarmReceiver.class);
        Intent intent = new Intent();
        intent.setAction("alarm_receiver");
        sender = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        initDayOfSubjects(calendar.get(Calendar.DAY_OF_WEEK));
        onAlarm();
    }

    private void initDayOfSubjects(int day) {
        if (scuSubjects == null) {
            scuSubjects = TimetableHelper.getSubjects(this);
        }
        scuSubjectLinkedList.clear();

        Log.d("initQueue", "day=" + day);
        for (ScuSubject subject : scuSubjects) {
            if (day == subject.getDay() && subject.getWeekList().contains(TimetableHelper.getCurrentWeek())) {
                scuSubjectLinkedList.add(subject);
            }
        }
        Collections.sort(scuSubjectLinkedList, new Comparator<ScuSubject>() {
            @Override
            public int compare(ScuSubject o1, ScuSubject o2) {
                return o1.getStart() - o2.getStart();
            }
        });
        Log.d("initQueue", "scuSubjectLinkedList=" + scuSubjectLinkedList);
    }

    private void initAlarmQueue() {
        if (nextSubject == null)  {
            return;
        }
        alarmQueue.clear();
        int offsetMin = 0;
        for (int i = nextSubject.getStart(); i <= nextSubject.getEnd(); i++) {
            if (alarmQueue.isEmpty()) {

                //test
//                Alarm alarm = new Alarm();
//                alarm.setAlarmType(Alarm.TYPE_BEFORE_CLASS);
//                Calendar calendar = Calendar.getInstance();
//                calendar.add(Calendar.MINUTE, 1);
//                calendar.set(Calendar.SECOND, 0);
//                calendar.set(Calendar.MILLISECOND, 0);
//                alarm.setCalendar(calendar);
//                alarmQueue.add(alarm);

                alarmQueue.add(createAlarm(-10, Alarm.TYPE_BEFORE_CLASS));
                alarmQueue.add(createAlarm(0, Alarm.TYPE_BEFORE_CLASS_TEN_MIN));
                alarmQueue.add(createAlarm(45, Alarm.TYPE_CLASS_BEGAIN));
                offsetMin = 45;
            } else {
                if (i == 3 || i == 8) {
                    offsetMin += 20;
                } else {
                    offsetMin += 10;
                }
                alarmQueue.add(createAlarm(offsetMin, Alarm.TYPE_CLASS_BREAK));
                offsetMin += 45;
                alarmQueue.add(createAlarm(offsetMin, Alarm.TYPE_CLASS_BEGAIN));
            }
        }
    }

    private Alarm createAlarm(int offsetMin, int alarmType) {
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_WEEK);
        String[] arr = TimetableHelper.TIMES_1[nextSubject.getStart() - 1].split(":");
        int subjectDay = nextSubject.getDay();
        int subjectHour = Integer.valueOf(arr[0]);
        int subjectMin = Integer.valueOf(arr[1]);
        Alarm alarm = new Alarm();
        alarm.setAlarmType(alarmType);
        calendar.set(Calendar.DAY_OF_WEEK, subjectDay);
        Log.d("createAlarm", "currentDay=" + currentDay + " subjectDay=" + subjectDay);
        if (subjectDay >= currentDay) {
            calendar.add(Calendar.DAY_OF_WEEK, subjectDay - currentDay);
        } else {
            calendar.add(Calendar.DAY_OF_WEEK, 7);
        }
        Log.d("createAlarm", "day=" + calendar.get(Calendar.DAY_OF_WEEK));
        calendar.set(Calendar.HOUR_OF_DAY, subjectHour);
        calendar.set(Calendar.MINUTE, subjectMin);
        calendar.add(Calendar.MINUTE, offsetMin);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        alarm.setCalendar(calendar);
        return alarm;
    }

    private void onAlarm() {
        Log.d("onAlarm", "scuSubjectLinkedList=" + scuSubjectLinkedList.isEmpty());
        if (scuSubjectLinkedList.isEmpty()) {
            Log.d("onAlarm", "11111111111");
            Calendar calendar = Calendar.getInstance();
            do {
                calendar.add(Calendar.DAY_OF_WEEK, 1);
                initDayOfSubjects(calendar.get(Calendar.DAY_OF_WEEK));
            } while (scuSubjectLinkedList.isEmpty());

            nextSubject = scuSubjectLinkedList.pop();
            Log.d("onAlarm1", "nextSubject=" + nextSubject);
        } else {
            Log.d("onAlarm", "222222222222");
            int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
            int currentMinute = calendar.get(Calendar.MINUTE);
            while (true) {
                nextSubject = scuSubjectLinkedList.pop();

                if (nextSubject.getDay() > calendar.get(Calendar.DAY_OF_WEEK)) {
                    break;
                }

                Log.d("onAlarm2", "nextSubject=" + nextSubject);
                String[] arr = TimetableHelper.TIMES_1[nextSubject.getStart() - 1].split(":");
                String[] arr2 = TimetableHelper.TIMES_END_1[nextSubject.getEnd() - 1].split(":");
                int hourOfSubject = Integer.valueOf(arr[0]);
                int minuteOfSubject = Integer.valueOf(arr[1]);
                int hourOfEnd = Integer.valueOf(arr2[0]);
                int minOfEnd = Integer.valueOf(arr2[1]);

                if (currentHour < hourOfEnd || (currentHour == hourOfEnd && currentMinute < minOfEnd)) {
                    break;
                }

                if ((currentHour > hourOfSubject)
                        || (currentHour == hourOfSubject && currentMinute > minuteOfSubject)) {
                    if (scuSubjectLinkedList.isEmpty()) {
                        onAlarm();
                        return;
                    }
                } else {
                    break;
                }
            }
        }
        initAlarmQueue();
        Log.d("onAlarm", "alarmQueue=" + alarmQueue);
        Alarm alarm;
        do {
//            if (alarmQueue.isEmpty()) {
//                onAlarm();
//                return;
//            }
            alarm = alarmQueue.pop();
        } while (alarm.getCalendar().getTimeInMillis() <= System.currentTimeMillis());

        updateAlarm(alarm);
    }

    private void updateAlarm(Alarm alarm) {
        if (alarmManager == null || nextSubject == null) {
            return;
        }
        Calendar calendar = alarm.getCalendar();
        int alarmType = alarm.getAlarmType();

        if (alarmType == Alarm.TYPE_BEFORE_CLASS || alarmType == Alarm.TYPE_BEFORE_CLASS_TEN_MIN) {
            if (timer == null)
                timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    long deltaMin = (calendar.getTime().getTime() - System.currentTimeMillis()) / (1000 * 60);
                    long deltaHour = deltaMin / 60;
                    deltaMin = deltaMin % 60;
                    Notification notification = createForegroundServiceNotification("下一节课：" + nextSubject.getCourseName(),
                            "上课地点：" + nextSubject.getCampusName() + nextSubject.getTeachingBuilding() + nextSubject.getClassroom(),
                            "上课时间：" + TimetableHelper.TIMES_1[nextSubject.getStart() - 1] + "  " + deltaHour + "小时" + deltaMin + "分钟后开始上课",
                            "任课老师：" + nextSubject.getTeacher());
                    notificationManager.notify(FOREGROUND_SERVICE_NOTIF_ID, notification);
                }
            }, 0, 1000 * 60);
        } else {
            if (timer != null) {
                timer.cancel();
            }
        }
        switch (alarmType) {
            case Alarm.TYPE_BEFORE_CLASS:
                break;
            case Alarm.TYPE_BEFORE_CLASS_TEN_MIN:
                textToSpeech.speak(nextSubject.getCourseName() + "课程即将开始", TextToSpeech.QUEUE_FLUSH, null);
                break;
            case Alarm.TYPE_CLASS_BEGAIN:
                Notification notification = createForegroundServiceNotification(nextSubject.getCourseName() + "课程正在上课中",
                        "上课地点：" + nextSubject.getCampusName() + nextSubject.getTeachingBuilding() + nextSubject.getClassroom(),
                        "上课时间：" + TimetableHelper.TIMES_1[nextSubject.getStart() - 1],
                        "任课老师：" + nextSubject.getTeacher());
                notificationManager.notify(FOREGROUND_SERVICE_NOTIF_ID, notification);
                break;
            case Alarm.TYPE_CLASS_BREAK:
                notificationManager.notify(FOREGROUND_SERVICE_NOTIF_ID, createForegroundServiceNotification(nextSubject.getCourseName() + "课间休息时间", "休息一会儿吧！"));
                break;
            case Alarm.TYPE_CLASS_END:
                break;
            default:
                break;
        }

        // 取消闹钟
        if (sender != null) {
            alarmManager.cancel(sender);
        }
//        Intent intent = new Intent(this, AlarmReceiver.class);
////        intent.putExtra("alarm_type", alarmType);
//        sender = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        Log.d("updateAlarm", "day=" + calendar.get(Calendar.DAY_OF_WEEK));
        Log.d("updateAlarm", "hour=" + calendar.get(Calendar.HOUR_OF_DAY));
        Log.d("updateAlarm", "min=" + calendar.get(Calendar.MINUTE));
        Log.d("updateAlarm", "sec=" + calendar.get(Calendar.SECOND));
        Log.d("updateAlarm", "ms=" + calendar.get(Calendar.MILLISECOND));
        Log.d("updateAlarm", "getTimeInMillis=" + calendar.getTimeInMillis());
        Log.d("updateAlarm", "currentTimeMillis=" + System.currentTimeMillis());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
        }
    }

    private void updateForegroundNotification() {
        notificationManager.notify(FOREGROUND_SERVICE_NOTIF_ID, createForegroundServiceNotification());
    }

    private Notification createForegroundServiceNotification() {
        return createForegroundServiceNotification("notificationService", "content text");
    }

    private Notification createForegroundServiceNotification(String title, String contentText) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_FOREGROUND_SERVICE);
        builder.setShowWhen(false)
                .setContentTitle(title)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentText(contentText)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setGroup(CHANNEL_FOREGROUND_SERVICE)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOngoing(true)
//                .addAction(new NotificationCompat.Action(R.drawable.ic_close_black_24dp, "停止服务", PendingIntent.getService(this, 1, new Intent(this, AlarmService.class).setAction(ACTION_STOP), PendingIntent.FLAG_UPDATE_CURRENT)))
                .setContentIntent(PendingIntent.getActivity(this, 1, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
        currentNotification =  builder.build();
        return currentNotification;
    }

    private Notification createForegroundServiceNotification(String title, String classRoom, String time, String teacher) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_FOREGROUND_SERVICE);
        RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.layout_notification);
        remoteViews.setTextViewText(R.id.text_title, title);
        remoteViews.setTextViewText(R.id.text_classroom, classRoom);
        remoteViews.setTextViewText(R.id.text_time, time);
        remoteViews.setTextViewText(R.id.text_teacher, teacher);
        builder.setShowWhen(false)
                .setContentTitle(title)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentText(classRoom + " " + time)
                .setCustomBigContentView(remoteViews)
                .setCustomContentView(remoteViews)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setGroup(CHANNEL_FOREGROUND_SERVICE)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOngoing(true)
//                .addAction(new NotificationCompat.Action(R.drawable.ic_close_black_24dp, "停止服务", PendingIntent.getService(this, 1, new Intent(this, AlarmService.class).setAction(ACTION_STOP), PendingIntent.FLAG_UPDATE_CURRENT)))
                .setContentIntent(PendingIntent.getActivity(this, 1, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
        currentNotification =  builder.build();
        return currentNotification;
    }

    public static void start(Context context) {
        ContextCompat.startForegroundService(context, new Intent(context, AlarmService.class)
                .setAction(ACTION_START));
    }

    public static void stop(Context context) {
        context.startService(new Intent(context, AlarmService.class).setAction(ACTION_STOP));
    }

    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        System.out.println("DAY_OF_WEEK111=" + calendar.get(Calendar.DAY_OF_WEEK));
        System.out.println("DAY_OF_WEEK111=" + calendar.get(Calendar.DAY_OF_MONTH));
        calendar.add(Calendar.DAY_OF_WEEK, 1);
        System.out.println("DAY_OF_WEEK222=" + calendar.get(Calendar.DAY_OF_WEEK));
        System.out.println("DAY_OF_WEEK222=" + calendar.get(Calendar.DAY_OF_MONTH));
        calendar.add(Calendar.DAY_OF_WEEK, 2);
        System.out.println("DAY_OF_WEEK333=" + calendar.get(Calendar.DAY_OF_WEEK));
        System.out.println("DAY_OF_WEEK333=" + calendar.get(Calendar.DAY_OF_MONTH));
    }

}
