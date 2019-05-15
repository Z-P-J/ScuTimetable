package com.scu.timetable.utils;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.RemoteViews;

import com.scu.timetable.LoginActivity;
import com.scu.timetable.MainActivity;
import com.scu.timetable.R;
import com.scu.timetable.model.MySubject;
import com.scu.timetable.ui.widget.TimetableWidget;
import com.scu.timetable.utils.content.SPHelper;
import com.zhuangfei.timetable.model.ScheduleColorPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TimetableWidgtHelper2 {

    private static RemoteViews remoteViews;
    @IdRes
    private static int res;

    private static final SparseArray<List<MySubject>> SUBJECT_SPARSE_ARRAY = new SparseArray<>(7);

    private static List<MySubject> mySubjects;

    private static PendingIntent pendingIntent;

    private static boolean showWeekends;

    private static int currentDay;

    private TimetableWidgtHelper2() { }

    public static RemoteViews refreshViews(Context context) {
        boolean isSmartShowWeekends = isSmartShowWeekends();
        currentDay = DateUtil.dayOfWeek();
        showWeekends = true;
        if (isSmartShowWeekends) {
            if (currentDay != 1 && currentDay != 7) {
                showWeekends = false;
            }
        }
        if (remoteViews == null) {
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.course_widget_4_4);
        }
        //如果可以显示课表
        if (showTimetable(context)) {
            mySubjects = getColorReflect(TimetableHelper.getSubjects(context));
//            if (mySubjects.isEmpty()) {
//                return remoteViews;
//            }
            initSubjects();
            showTimetable(context, R.id.course_widget_4_4_course_layout);
            showTimetableWeekBar(context, R.id.course_widget_4_4_week_bar);
            remoteViews.setTextViewText(R.id.cur_week, "第" + TimetableHelper.getCurrentWeek() + "周");
            return remoteViews;
        }
        showTimetableWeekBar(context, R.id.course_widget_4_4_no_course_week_bar);
        return remoteViews;
    }

    public static RemoteViews getRemoteViews(Context context) {
//        if (remoteViews == null) {
//            refreshViews(context, false);
//        }
        return remoteViews;
    }

    public static boolean isSmartShowWeekends() {
        return SPHelper.getBoolean("widget_smart_show_weekends", true);
    }

    public static void toggleSmartShowWeekends(Context context) {
        SPHelper.putBoolean("widget_smart_show_weekends", !isSmartShowWeekends());
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int [] appIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, TimetableWidget.class));
        appWidgetManager.updateAppWidget(appIds, refreshViews(context));
    }

    private static boolean showTimetable(Context ctx) {
        if (TimetableHelper.isLogined(ctx)) {
            remoteViews.setViewVisibility(R.id.widget_llyt_no_course, View.INVISIBLE);
            remoteViews.setViewVisibility(R.id.course_widget_4_4_week_course, View.VISIBLE);
            return true;
        } else {
            clickToLoginActivity(ctx, R.id.widget_btn_enter_treehole);
            remoteViews.setViewVisibility(R.id.widget_btn_enter_treehole, View.VISIBLE);
            remoteViews.setTextViewText(R.id.widget_btn_enter_treehole, ctx.getResources().getString(R.string.goto_login));
            remoteViews.setTextViewText(R.id.widget_txv_no_course_text, ctx.getResources().getString(R.string.no_login_body_tip));
            return false;
        }
    }

    private static void clickToLoginActivity(Context context, int i) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        remoteViews.setOnClickPendingIntent(i, PendingIntent.getActivity(context, 17,intent, PendingIntent.FLAG_UPDATE_CURRENT));
    }

    private static List<MySubject> getColorReflect(List<MySubject> schedules) {
        if (schedules == null || schedules.size() == 0) {
            return schedules;
        }

        //保存课程名、颜色的对应关系
        Map<String, Integer> colorMap = new HashMap<>();
        int colorCount = 1;

        //开始转换
        for (int i = 0; i < schedules.size(); i++) {
            MySubject mySubject = schedules.get(i);
            //计算课程颜色
            int color;
            if (colorMap.containsKey(mySubject.getCourseName())) {
                color = colorMap.get(mySubject.getCourseName());
            } else {
                colorMap.put(mySubject.getCourseName(), colorCount);
                color = colorCount;
                colorCount++;
            }
            mySubject.setColorRandom(color);
        }

        return schedules;
    }

    private static void initSubjects() {
        SUBJECT_SPARSE_ARRAY.clear();
        for (int i = 0; i < 7; i++) {
            List<MySubject> mySubjectList = new ArrayList<>(12);
            for (int j = 0; j < 12; j++) {
                mySubjectList.add(new MySubject(j + 1, i));
            }
            SUBJECT_SPARSE_ARRAY.put(i, mySubjectList);
        }
        for (MySubject mySubject : mySubjects) {
            int day = mySubject.getDay();
            List<MySubject> mySubjectList = SUBJECT_SPARSE_ARRAY.get(day - 1);
            int start = mySubject.getStart();
            mySubjectList.set(start - 1, mySubject);
        }

        for (int i = 0; i < 7; i++) {
            List<MySubject> mySubjectList = SUBJECT_SPARSE_ARRAY.get(i);
            for (int j = mySubjectList.size() - 1; j >= 0; j--) {
                MySubject mySubject = mySubjectList.get(j);
                if (!TextUtils.isEmpty(mySubject.getCourseName())) {
                    int start = mySubject.getStart();
                    int end = mySubject.getEnd();
                    for (int k = end; k > start; k--) {
                        mySubjectList.remove(k - 1);
                    }
                }
            }
        }
    }

    private static void showTimetable(Context context, @IdRes int i) {
        res = i;
        remoteViews.removeAllViews(i);
        //初始化节数
        initSlideBar(context);
        initColumns(context);
    }

    //初始化节数侧边栏
    private static void initSlideBar(Context context) {
        RemoteViews colum = new RemoteViews(context.getPackageName(), R.layout.course_widget_4_4_section_colum);
        int i = 1;
        while (i < 13) {
            //SectionName第几节课
            RemoteViews remoteViews2 = new RemoteViews(context.getPackageName(), R.layout.course_widget_4_4_section_item);
            remoteViews2.setTextViewText(R.id.course_widget_4_4_section_item, "" + i);
            colum.addView(R.id.course_widget_4_4_section_colum, remoteViews2);
            i++;
        }
        remoteViews.addView(res, colum);
    }

    private static void showTimetableWeekBar(Context context, @IdRes int resId) {
        remoteViews.removeAllViews(resId);
        remoteViews.addView(resId, new RemoteViews(context.getPackageName(), R.layout.course_widget_4_4_week_view_section_colum));

        //当前是星期几
        for (int i = 1; i < 8; i++) {
            if (!showWeekends) {
                if (i == 1 || i == 7) {
                    continue;
                }
            }
            RemoteViews remoteViews2;
            //如果是当天
            if (i == currentDay) {
                remoteViews2 = new RemoteViews(context.getPackageName(), R.layout.course_widget_4_4_week_view_today);
            } else {
                remoteViews2 = new RemoteViews(context.getPackageName(), R.layout.course_widget_4_4_week_view_normal);
            }
            remoteViews2.setTextViewText(R.id.course_widget_4_4_week_bar_text, DateUtil.dayOfWeekStr(i - 1));
            remoteViews.addView(resId, remoteViews2);
        }
    }

    //初始化课程显示的每列
    private static void initColumns(Context context) {
        ScheduleColorPool colorPool = new ScheduleColorPool(context);
        for (int i = 1; i <= 7; i++) {
            if (!showWeekends) {
                if (i == 1 || i == 7) {
                    continue;
                }
            }
            RemoteViews colum = new RemoteViews(context.getPackageName(), R.layout.course_widget_4_4_day_colum);
            List<MySubject> mySubjectList = SUBJECT_SPARSE_ARRAY.get(i - 1);
            for (MySubject mySubject : mySubjectList) {
                RemoteViews remoteViews1 = new RemoteViews(context.getPackageName(), getCourseViewRes(mySubject.getStep()));
                if (TextUtils.isEmpty(mySubject.getCourseName())) {
                    remoteViews1.setInt(R.id.course_widget_4_4_course_view_img, "setBackgroundColor", Color.TRANSPARENT);
                    remoteViews1.setTextViewText(R.id.course_widget_4_4_course_view_text, "");
                } else {
                    remoteViews1.setInt(R.id.course_widget_4_4_course_view_img, "setBackgroundColor", colorPool.getColorAutoWithAlpha(mySubject.getColorRandom(), 0.8f));
                    remoteViews1.setTextViewText(R.id.course_widget_4_4_course_view_text, mySubject.getRoom() + "@" + mySubject.getCourseName());
                }
                colum.addView(R.id.course_widget_4_4_day_colum, remoteViews1);
            }
            remoteViews.addView(res, colum);
        }
    }

    //获取每个课程的view
    @LayoutRes
    private static int getCourseViewRes(int step) {
        switch (step) {
            case 1:
                return R.layout.course_widget_4_4_course_view_1;
            case 2:
                return R.layout.course_widget_4_4_course_view_2;
            case 3:
                return R.layout.course_widget_4_4_course_view_3;
            case 4:
                return R.layout.course_widget_4_4_course_view_4;
            case 5:
                return R.layout.course_widget_4_4_course_view_5;
            case 6:
                return R.layout.course_widget_4_4_course_view_6;
            default:
                return R.layout.course_widget_4_4_course_view_2;
        }
    }

    @LayoutRes
    private static int getDisplayViewRes(int step) {
        switch (step) {
            case 1:
                return R.layout.course_widget_4_4_display_view_1;
            case 2:
                return R.layout.course_widget_4_4_display_view_2;
            case 3:
                return R.layout.course_widget_4_4_display_view_3;
            case 4:
                return R.layout.course_widget_4_4_display_view_4;
            case 5:
                return R.layout.course_widget_4_4_display_view_5;
            case 6:
                return R.layout.course_widget_4_4_display_view_6;
            default:
                return R.layout.course_widget_4_4_display_view_1;
        }
    }

    //设置点击事件
    private static void onClick(Context context, RemoteViews remoteViews, int i) {
        if (i > 0) {
            if (pendingIntent == null) {
                Intent intent = new Intent(context, MainActivity.class);
                pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            }
            RemoteViews remoteViews2 = new RemoteViews(context.getPackageName(), getDisplayViewRes(i));
            remoteViews2.setOnClickPendingIntent(R.id.course_widget_4_4_display_view, pendingIntent);
            remoteViews.addView(R.id.course_widget_4_4_day_colum, remoteViews2);
        }
    }

}
