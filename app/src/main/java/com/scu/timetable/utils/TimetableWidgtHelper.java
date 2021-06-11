package com.scu.timetable.utils;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.RemoteViews;

import com.scu.timetable.R;
import com.scu.timetable.bean.ScuSubject;
import com.scu.timetable.ui.activity.LoginActivity;
import com.scu.timetable.ui.activity.MainActivity;
import com.scu.timetable.ui.widget.ColorPool;
import com.scu.timetable.ui.widget.TimetableWidget;
import com.zpj.utils.PrefsHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Z-P-J
 * 课程表桌面插件工具类
 */
public final class TimetableWidgtHelper {

    private volatile static TimetableWidgtHelper helper;

    private RemoteViews remoteViews;
    @IdRes
    private int res;

    private static final SparseArray<List<ScuSubject>> SUBJECT_SPARSE_ARRAY = new SparseArray<>(7);

    private List<ScuSubject> scuSubjects;

    private PendingIntent pendingIntent;

    private boolean showWeekends;

    private int currentDay;

    private final List<Integer> canHideRows = new ArrayList<>();
    private final List<Integer> canHideColumns = new ArrayList<>();

    private TimetableWidgtHelper() { }

    public static TimetableWidgtHelper getInstance() {
        if (helper == null) {
            synchronized (TimetableWidgtHelper.class) {
                if (helper == null) {
                    helper = new TimetableWidgtHelper();
                }
            }
        }
        return helper;
    }

    public RemoteViews refreshViews(Context context) {
        boolean isSmartShowWeekends = isSmartShowWeekends();
        currentDay = DateUtil.dayOfWeek();
        showWeekends = true;
        if (isSmartShowWeekends) {
            if (currentDay != 1 && currentDay != 7) {
                showWeekends = false;
            }
        }
        if (remoteViews == null) {
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.course_widget);
        }
        //如果可以显示课表
        if (showTimetable(context)) {
            scuSubjects = TimetableHelper.getSubjects(context);
            initSubjects();
            showTimetable(context, R.id.course_widget_4_4_course_layout);

            if (!isTransparentMode()) {
                remoteViews.setInt(R.id.layout_widget, "setBackgroundResource", R.drawable.widget_background);
                remoteViews.setViewVisibility(R.id.course_widget_4_4_week_bar, View.VISIBLE);
                showTimetableWeekBar(context, R.id.course_widget_4_4_week_bar);
            } else {
                remoteViews.setInt(R.id.layout_widget, "setBackgroundColor", Color.TRANSPARENT);
                remoteViews.setViewVisibility(R.id.course_widget_4_4_week_bar, View.GONE);
            }
            remoteViews.setTextViewText(R.id.cur_week, "第" + TimetableHelper.getCurrentWeek() + "周");
            return remoteViews;
        }
        showTimetableWeekBar(context, R.id.course_widget_4_4_no_course_week_bar);
        return remoteViews;
    }

    public RemoteViews getRemoteViews(Context context) {
        return remoteViews;
    }

    public static boolean isSmartShowWeekends() {
        return PrefsHelper.with().getBoolean("widget_smart_show_weekends", true);
    }

    public static void toggleSmartShowWeekends(Context context) {
        PrefsHelper.with().putBoolean("widget_smart_show_weekends", !isSmartShowWeekends());
        getInstance().update(context);
    }

    public static boolean isTransparentMode() {
        return PrefsHelper.with().getBoolean("widget_transparent_mode", false);
    }

    public static void toggleTransparentMode(Context context) {
        PrefsHelper.with().putBoolean("widget_transparent_mode", !isTransparentMode());
        getInstance().update(context);
    }

    private void update(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int [] appIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, TimetableWidget.class));
        appWidgetManager.updateAppWidget(appIds, refreshViews(context));
    }

    private boolean showTimetable(Context ctx) {
        if (TimetableHelper.isLogined(ctx) || TimetableHelper.isVisitorMode()) {
            remoteViews.setViewVisibility(R.id.widget_llyt_no_course, View.INVISIBLE);
            remoteViews.setViewVisibility(R.id.course_widget_4_4_week_course, View.VISIBLE);
            return true;
        } else {
            remoteViews.setInt(R.id.layout_widget, "setBackgroundResource", R.drawable.widget_background);
            remoteViews.setViewVisibility(R.id.widget_llyt_no_course, View.VISIBLE);
            remoteViews.setViewVisibility(R.id.course_widget_4_4_week_course, View.INVISIBLE);
            clickToLoginActivity(ctx, R.id.widget_btn_enter_treehole);
            remoteViews.setViewVisibility(R.id.widget_btn_enter_treehole, View.VISIBLE);
            remoteViews.setViewVisibility(R.id.widget_btn_enter_visitor_mode, View.VISIBLE);
            clickToVisitorMode(ctx, R.id.widget_btn_enter_visitor_mode);
            remoteViews.setTextViewText(R.id.widget_btn_enter_treehole, ctx.getResources().getString(R.string.goto_login));
            remoteViews.setTextViewText(R.id.widget_txv_no_course_text, ctx.getResources().getString(R.string.no_login_body_tip));
            return false;
        }
    }

    private void clickToLoginActivity(Context context, int i) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        remoteViews.setOnClickPendingIntent(i, PendingIntent.getActivity(context, 17,intent, PendingIntent.FLAG_UPDATE_CURRENT));
    }

    private void clickToVisitorMode(Context context, int i) {
        Intent intent = new Intent();
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        intent.putExtra("visitor_mode", true);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, UUID.randomUUID().hashCode(),intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(i, pendingIntent);
    }

    private void initSubjects() {
        SUBJECT_SPARSE_ARRAY.clear();
        for (int i = 0; i < 7; i++) {
            List<ScuSubject> scuSubjectList = new ArrayList<>(12);
            for (int j = 0; j < 12; j++) {
                scuSubjectList.add(new ScuSubject(j + 1, i));
            }
            SUBJECT_SPARSE_ARRAY.put(i, scuSubjectList);
        }
        for (ScuSubject scuSubject : scuSubjects) {
            int day = scuSubject.getDay();
            List<ScuSubject> scuSubjectList = SUBJECT_SPARSE_ARRAY.get(day - 1);
            int start = scuSubject.getStart();
            scuSubjectList.set(start - 1, scuSubject);
        }

        for (int i = 0; i < 7; i++) {
            List<ScuSubject> scuSubjectList = SUBJECT_SPARSE_ARRAY.get(i);
            for (int j = scuSubjectList.size() - 1; j >= 0; j--) {
                ScuSubject scuSubject = scuSubjectList.get(j);
                if (!TextUtils.isEmpty(scuSubject.getCourseName())) {
                    int start = scuSubject.getStart();
                    int end = scuSubject.getEnd();
                    for (int k = end; k > start; k--) {
                        scuSubjectList.remove(k - 1);
                    }
                }
            }
        }

        for (int i = 0; i < 7; i++) {
            List<ScuSubject> scuSubjectList = SUBJECT_SPARSE_ARRAY.get(i);
            if (scuSubjectList.size() == 12) {
                int count = 0;
                for (ScuSubject subject : scuSubjectList) {
                    count++;
                    if (!subject.getCourseName().isEmpty()) {
                        break;
                    }
                    if (count == 12) {
                        canHideColumns.add(i);
                    }
                }
            }
        }

        canHideRows.clear();
        for (int i = 0; i < 12; i++) {
            int count = 0;
            for (int j = 0; j < 7; j++) {
                List<ScuSubject> scuSubjectList = SUBJECT_SPARSE_ARRAY.get(j);
                ScuSubject scuSubject = null;
                for (ScuSubject subject : scuSubjectList) {
                    int start = subject.getStart() - 1;
                    int end = subject.getEnd() - 1;
                    if (i >= start && i <= end) {
                        scuSubject = subject;
                        break;
                    }
                }
                if (scuSubject !=null && !scuSubject.getCourseName().isEmpty()) {
                    break;
                }
                count++;
                if (count == 6) {
                    Log.d("initSubjects", "" + i);
                    canHideRows.add(i);
                }
            }
        }
    }

    private void showTimetable(Context context, @IdRes int i) {
        res = i;
        remoteViews.removeAllViews(i);
        //初始化节数
        if (!isTransparentMode()) {
            initSlideBar(context);
        }
        initColumns(context);
    }

    //初始化节数侧边栏
    private void initSlideBar(Context context) {
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

    private void showTimetableWeekBar(Context context, @IdRes int resId) {
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
    private void initColumns(Context context) {
        ColorPool colorPool = new ColorPool();
        for (int i = 1; i <= 7; i++) {
            if (!showWeekends) {
                if (i == 1 || i == 7) {
                    continue;
                }
            }
            if (isTransparentMode() && canHideColumns.contains(i - 1)) {
                continue;
            }
            RemoteViews colum = new RemoteViews(context.getPackageName(), R.layout.course_widget_4_4_day_colum);
            List<ScuSubject> scuSubjectList = SUBJECT_SPARSE_ARRAY.get(i - 1);
            for (ScuSubject scuSubject : scuSubjectList) {
                if (isTransparentMode() && canHideRows.contains(scuSubject.getStart() - 1)) {
                    continue;
                }
                RemoteViews remoteViews1 = new RemoteViews(context.getPackageName(), getCourseViewRes(scuSubject.getStep()));
                if (TextUtils.isEmpty(scuSubject.getCourseName())) {
                    remoteViews1.setInt(R.id.course_widget_4_4_course_view_img, "setBackgroundColor", Color.TRANSPARENT);
                    remoteViews1.setTextViewText(R.id.course_widget_4_4_course_view_text, "");
                } else {
                    int color = colorPool.getColor(scuSubject.getCourseName(), isTransparentMode() ? 0.6f : 0.8f);
                    remoteViews1.setInt(R.id.course_widget_4_4_course_view_img, "setBackgroundColor", color);
                    if (isTransparentMode()) {
                        remoteViews1.setViewVisibility(R.id.title, View.VISIBLE);
                        remoteViews1.setTextColor(R.id.title, colorPool.getColor(scuSubject.getCourseName()));

                        if (showWeekends && canHideRows.size() < 2) {
                            String weekStr = DateUtil.dayOfWeekStr(scuSubject.getDay() - 1).replace("周", "");
                            remoteViews1.setTextViewText(R.id.title, weekStr + "-" + scuSubject.getStart() + "-" + scuSubject.getEnd());
                        } else {
                            remoteViews1.setInt(R.id.title, "setMaxLines", 2);
                            String title = DateUtil.dayOfWeekStr(scuSubject.getDay() - 1) + scuSubject.getStart() + "-" + scuSubject.getEnd() + "节";
                            remoteViews1.setTextViewText(R.id.title, title);
                            if (canHideColumns.size() >= 1) {
                                remoteViews1.setFloat(R.id.title, "setTextSize", 11);
                                remoteViews1.setFloat(R.id.course_widget_4_4_course_view_text, "setTextSize", 12);
                            }
                        }
                    }
                    String room = scuSubject.getRoom();
                    remoteViews1.setTextViewText(R.id.course_widget_4_4_course_view_text,  Html.fromHtml("<b><tt>" + room + "</tt></b>@" + scuSubject.getCourseName()));
                }
                colum.addView(R.id.course_widget_4_4_day_colum, remoteViews1);
            }
            remoteViews.addView(res, colum);
        }
    }

    //获取每个课程的view
    @LayoutRes
    private int getCourseViewRes(int step) {
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
    private int getDisplayViewRes(int step) {
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
    private void onClick(Context context, RemoteViews remoteViews, int i) {
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
