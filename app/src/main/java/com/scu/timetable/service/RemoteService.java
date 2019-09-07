package com.scu.timetable.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.scu.timetable.IAlarmAidlInterface;

/**
 * 远程服务，用于与闹钟服务AlarmService，进行双进程保护。
 * 1、开启两个不同进程的服务，android:process。
 * 2、在服务启动之后，绑定两一个服务。
 */
public class RemoteService extends Service {
    private int startArgFlags;
    private boolean isCreate;

    private RemoteConn conn;
    private RemoteBinder binder;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        isCreate = true;
        init();
    }

    // 初始化
    private void init() {
        // 保证内存不足，杀死会重新创建
        startArgFlags = getApplicationInfo().targetSdkVersion < Build.VERSION_CODES.ECLAIR ? START_STICKY_COMPATIBILITY : START_STICKY;

        conn = new RemoteConn();
        binder = new RemoteBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("RemoteService", "onStartCommand");
        if (isCreate) {
            bindAlarmService();
        }
        return super.onStartCommand(intent, startArgFlags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 启动闹钟服务
        startAlarmService();
        // 绑定闹钟服务
        bindAlarmService();
    }

    /**
     * 自定义Binder
     */
    class RemoteBinder extends IAlarmAidlInterface.Stub {
        @Override
        public String getServiceName() {
            return RemoteService.class.getSimpleName();
        }
    }

    /**
     * 自定义ServiceConnection
     */
    class RemoteConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
//            Toast.makeText(RemoteService.this, "远程服务", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // 启动闹钟服务
            startAlarmService();
            // 绑定闹钟服务
            bindAlarmService();
        }

    }

    /**
     * 开启闹钟服务
     */
    private void startAlarmService() {
        Intent intent = new Intent(this, AlarmService.class);
        intent.setAction("com.scu.timetable.alarm_service");
        intent.putExtra("isOpenStartForeground", false);
        intent.putExtra("isUpdateAlarmCalendar", false);
        startService(intent);
    }

    /**
     * 绑定闹钟服务
     */
    private void bindAlarmService() {
        Intent intent = new Intent(this, AlarmService.class);
        intent.setAction("com.scu.timetable.alarm_service");
        bindService(
                intent,
                conn,
                Context.BIND_IMPORTANT);
    }
}
