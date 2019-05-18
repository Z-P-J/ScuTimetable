package com.scu.timetable.ui.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.scu.timetable.utils.TimetableWidgtHelper;

import java.util.Timer;
import java.util.TimerTask;

public class TimetableWidget extends AppWidgetProvider {

//    private TimetableWidgtHelper widgtHelper;
//    private RemoteViews remoteViews;


    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.d("TimetableWidget", "onEnabled");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, WidgetService.class));
        } else {
            context.startService(new Intent(context, WidgetService.class));
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            super.onReceive(context, intent);
            return;
        }

        String action = intent.getAction();
        if (action != null) {
            if ("android.appwidget.action.APPWIDGET_UPDATE".equals(action)) {
                Toast.makeText(context, "com.scu.timetable.login.success", Toast.LENGTH_SHORT).show();
//                AlarmReceiver.startAlarm(context);
//                refreshViews(context);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                int [] appIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, TimetableWidget.class));
                appWidgetManager.updateAppWidget(appIds, TimetableWidgtHelper.refreshViews(context));
            }
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] iArr) {
        for (int i : iArr) {
//            refreshViews(context);
//            appWidgetManager.updateAppWidget(i, this.remoteViews);
            appWidgetManager.updateAppWidget(i, TimetableWidgtHelper.refreshViews(context));
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Log.d("TimetableWidget", "onDeleted");
        context.stopService(new Intent(context, WidgetService.class));
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Log.d("TimetableWidget", "onDisabled");
        context.stopService(new Intent(context, WidgetService.class));
    }

//    private void initView(Context context) {
//        if (remoteViews == null) {
//            remoteViews = new RemoteViews(context.getPackageName(), R.layout.course_widget_4_4);
//        }
//        if (widgtHelper == null) {
//            widgtHelper = new TimetableWidgtHelper(context);
//        }
//        //如果可以显示课表
//        if (showTimetable(context)) {
////            Toast.makeText(context, "showTimetable", Toast.LENGTH_SHORT).show();
//            widgtHelper.showTimetable(remoteViews, R.id.course_widget_4_4_course_layout);
//            widgtHelper.showTimetableWeekBar(remoteViews, R.id.course_widget_4_4_week_bar);
////            clickToLoginActivity(context);
////            dodo(context);
//            this.remoteViews.setTextViewText(R.id.cur_week, "第" + TimetableHelper.getCurrentWeek() + "周");
//            return;
//        }
//        widgtHelper.showTimetableWeekBar(remoteViews, R.id.course_widget_4_4_no_course_week_bar);
//    }
//
//    private boolean showTimetable(Context ctx) {
//        if (TimetableHelper.isLogined(ctx)) {
//            this.remoteViews.setViewVisibility(R.id.widget_llyt_no_course, View.INVISIBLE);
//            this.remoteViews.setViewVisibility(R.id.course_widget_4_4_week_course, View.VISIBLE);
//            return true;
//        } else {
//            clickToLoginActivity(ctx, R.id.widget_btn_enter_treehole);
//            this.remoteViews.setViewVisibility(R.id.widget_btn_enter_treehole, View.VISIBLE);
//            this.remoteViews.setTextViewText(R.id.widget_btn_enter_treehole, ctx.getResources().getString(R.string.goto_login));
//            this.remoteViews.setTextViewText(R.id.widget_txv_no_course_text, ctx.getResources().getString(R.string.no_login_body_tip));
//            return false;
//        }
//    }
//
//    private void clickToLoginActivity(Context context, int i) {
//        Intent intent = new Intent(context, LoginActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        this.remoteViews.setOnClickPendingIntent(i, PendingIntent.getActivity(context, 17,intent, PendingIntent.FLAG_UPDATE_CURRENT));
//    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int i, Bundle bundle) {
//        initView(context);;
//        appWidgetManager.updateAppWidget(i, this.remoteViews);
        appWidgetManager.updateAppWidget(i, TimetableWidgtHelper.refreshViews(context));
    }

}
