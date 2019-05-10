package com.scu.timetable.ui.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.scu.timetable.LoginActivity;
import com.scu.timetable.R;
import com.scu.timetable.utils.TimetableHelper;
import com.scu.timetable.utils.TimetableWidgtHelper;

public class TimetableWidget extends AppWidgetProvider {

    private TimetableWidgtHelper widgtHelper;
    private int[] no;
    private int oh;
    private final int ok = 0;
    private RemoteViews remoteViews;


    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
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
                initView(context);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                int [] appIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, TimetableWidget.class));
                appWidgetManager.updateAppWidget(appIds, remoteViews);
            }
        }


//        Bundle extras = intent.getExtras();
//        if (extras != null) {
//            this.oh = extras.getInt("index_part", 1);
//            if (this.no == null) {
//                this.no = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, TimetableWidget.class));
//            }
//            intent.putExtra("appWidgetIds", this.no);
//        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] iArr) {
        for (int i : iArr) {
            initView(context);
            appWidgetManager.updateAppWidget(i, this.remoteViews);
        }
    }

    private void initView(Context context) {
        if (remoteViews == null) {
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.course_widget_4_4);
        }
        if (widgtHelper == null) {
            widgtHelper = new TimetableWidgtHelper(context, this.oh);
        }
        //如果可以显示课表
        if (showTimetable(context)) {
            Toast.makeText(context, "showTimetable", Toast.LENGTH_SHORT).show();
            widgtHelper.showTimetable(remoteViews, R.id.course_widget_4_4_course_layout);
            widgtHelper.showTimetableWeekBar(remoteViews, R.id.course_widget_4_4_week_bar);
//            clickToLoginActivity(context);
//            dodo(context);
            this.remoteViews.setTextViewText(R.id.cur_week, "第" + TimetableHelper.getCurrentWeek() + "周");
            return;
        }
        widgtHelper.showTimetableWeekBar(remoteViews, R.id.course_widget_4_4_no_course_week_bar);
    }

    private boolean showTimetable(Context ctx) {
        if (TimetableHelper.isLogined(ctx)) {
            Log.d("showTimetable", "1111111111111111111111");
            this.remoteViews.setViewVisibility(R.id.widget_llyt_no_course, View.INVISIBLE);
            this.remoteViews.setViewVisibility(R.id.course_widget_4_4_week_course, View.VISIBLE);
            return true;
        } else {
            Log.d("showTimetable", "2222222222222222222222");
            clickToLoginActivity(ctx, R.id.widget_btn_enter_treehole);
            this.remoteViews.setViewVisibility(R.id.widget_btn_enter_treehole, View.VISIBLE);
            this.remoteViews.setTextViewText(R.id.widget_btn_enter_treehole, ctx.getResources().getString(R.string.goto_login));
            this.remoteViews.setTextViewText(R.id.widget_txv_no_course_text, ctx.getResources().getString(R.string.no_login_body_tip));
            return false;
        }
    }

//    private void startMainActivity(Context context, int i) {
//        Intent intent = new Intent(context, MainActivity.class);
//        this.remoteViews.setOnClickPendingIntent(i, PendingIntent.getActivity(context, 18, intent, PendingIntent.FLAG_UPDATE_CURRENT));
//    }
//
//    private void startMainActivity22222(Context context, int i) {
//        Intent intent = new Intent(context, MainActivity.class);
//        this.remoteViews.setOnClickPendingIntent(i, PendingIntent.getActivity(context, 18, intent, PendingIntent.FLAG_UPDATE_CURRENT));
//    }
//
//    private void startMainActivity333333333333(Context context, int i) {
//        Intent intent = new Intent(context, MainActivity.class);
//        this.remoteViews.setOnClickPendingIntent(i, PendingIntent.getActivity(context, 20, intent, PendingIntent.FLAG_UPDATE_CURRENT));
//    }

    private void clickToLoginActivity(Context context, int i) {
        this.remoteViews.setOnClickPendingIntent(i, PendingIntent.getActivity(context, 17, new Intent(context, LoginActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int i, Bundle bundle) {
        initView(context);
        appWidgetManager.updateAppWidget(i, this.remoteViews);
    }

//    private void clickToLoginActivity(Context ctx) {
//        int i;
//        boolean z;
//        boolean z2 = true;
//        int oh = SPHelper.getInt("cur_maxCount", 0);
//        Intent intent = new Intent(ctx, TimetableWidget.class);
//        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
//        intent.putExtra("appWidgetIds", this.no);
//        intent.putExtra("index_part", this.oh > 1 ? this.oh - 1 : 1);
//        PendingIntent broadcast = PendingIntent.getBroadcast(ctx, 21, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        Intent intent2 = new Intent(ctx, TimetableWidget.class);
//        intent2.setAction("android.appwidget.action.APPWIDGET_UPDATE");
//        intent2.putExtra("appWidgetIds", this.no);
//        String str = "index_part";
//        if (this.oh < oh) {
//            i = this.oh + 1;
//        } else {
//            i = oh;
//        }
//        intent2.putExtra(str, i);
//        PendingIntent broadcast2 = PendingIntent.getBroadcast(ctx, 22, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
//        z = oh != this.oh;
//        if (this.oh == 1) {
//            z2 = false;
//        }
//        if (z || z2) {
//            this.remoteViews.setViewVisibility(R.id.course_widget_4_4_change_layout, 0);
//            if (z2) {
//                this.remoteViews.setOnClickPendingIntent(R.id.widget_pre_pager, broadcast);
//            }
//            if (z) {
//                this.remoteViews.setOnClickPendingIntent(R.id.widget_next_pager, broadcast2);
//            }
//            this.remoteViews.setBoolean(R.id.widget_pre_pager, "setEnabled", z2);
//            this.remoteViews.setBoolean(R.id.widget_next_pager, "setEnabled", z);
//            return;
//        }
//        this.remoteViews.setViewVisibility(R.id.course_widget_4_4_change_layout, View.INVISIBLE);
//    }

//    private void dodo(Context context) {
//        //i表示今天有几门课
//        int i = 0;
//        int on2 = 1;
//        this.remoteViews.setTextViewText(R.id.cur_week_english, "第%d周 in English");
//        this.remoteViews.setTextViewText(R.id.cur_week, "第%d周");
//        List<MySubject> list = new ArrayList<>();
//        for (MySubject mySubject : list) {
//            if (mySubject.getDay() == 1) {
//                i += 1;
//            }
//        }
//        this.remoteViews.setTextViewText(R.id.course_count, "今天有" + i + "门课程");
//        if (i <= 0) {
//            this.remoteViews.setImageViewBitmap(R.id.widget_bottom_status, BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_widget_course_none));
//        } else if (i == 1) {
//            this.remoteViews.setImageViewBitmap(R.id.widget_bottom_status, BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_widget_course_few));
//        } else if (i <= 3) {
//            this.remoteViews.setImageViewBitmap(R.id.widget_bottom_status, BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_widget_course_several));
//        } else {
//            this.remoteViews.setImageViewBitmap(R.id.widget_bottom_status, BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_widget_course_many));
//        }
//    }


}
