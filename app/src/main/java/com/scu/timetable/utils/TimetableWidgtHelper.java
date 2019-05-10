package com.scu.timetable.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.text.TextUtils;
import android.util.SparseArray;
import android.widget.RemoteViews;

import com.scu.timetable.MainActivity;
import com.scu.timetable.R;
import com.scu.timetable.model.MySubject;
import com.scu.timetable.utils.DateUtil;
import com.scu.timetable.utils.SubjectUtil;
import com.zhuangfei.timetable.model.ScheduleColorPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimetableWidgtHelper {

    private RemoteViews remoteViews;
    @IdRes
    private int res;

    private SparseArray<List<MySubject>> subjectSparseArray = new SparseArray<>(7);

    private List<MySubject> mySubjects;

    private PendingIntent pendingIntent;

    private ScheduleColorPool colorPool;

    private Context context;

    public TimetableWidgtHelper(Context context, int i) {
        this.context = context;
        mySubjects = getColorReflect(SubjectUtil.getSubjects(context));
        if (mySubjects.isEmpty()) {
            return;
        }
        colorPool = new ScheduleColorPool(context);
        init();
    }

    private List<MySubject> getColorReflect(List<MySubject> schedules) {
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
            if (colorMap.containsKey(mySubject.getName())) {
                color = colorMap.get(mySubject.getName());
            } else {
                colorMap.put(mySubject.getName(), colorCount);
                color = colorCount;
                colorCount++;
            }
            mySubject.setColorRandom(color);
        }

        return schedules;
    }

    private void init() {
        subjectSparseArray.clear();
        for (int i = 0; i < 7; i++) {
            List<MySubject> mySubjectList = new ArrayList<>(12);
            for (int j = 0; j < 12; j++) {
                mySubjectList.add(new MySubject(j + 1, i));
            }
            subjectSparseArray.put(i, mySubjectList);
        }
        for (MySubject mySubject : mySubjects) {
            int day = mySubject.getDay();
            List<MySubject> mySubjectList = subjectSparseArray.get(day);
            int start = mySubject.getStart();

            mySubjectList.set(start - 1, mySubject);
        }

        for (int i = 0; i < 7; i++) {
            List<MySubject> mySubjectList = subjectSparseArray.get(i);
            for (int j = mySubjectList.size() - 1; j >= 0; j--) {
                MySubject mySubject = mySubjectList.get(j);
                if (!TextUtils.isEmpty(mySubject.getName())) {
                    int start = mySubject.getStart();
                    int end = mySubject.getEnd();
                    for (int k = end; k > start; k--) {
                        mySubjectList.remove(k - 1);
                    }
                }
            }
        }
    }

    public RemoteViews showTimetable(RemoteViews remoteViews, @IdRes int i) {
        this.remoteViews = remoteViews;
        this.res = i;
        remoteViews.removeAllViews(i);
        //初始化节数
        on();
        oh();
        return this.remoteViews;
    }

    //初始化节数侧边栏
    private void on() {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.course_widget_4_4_section_colum);
        int i = 1;
        while (i < 13) {
            //SectionName第几节课
            RemoteViews remoteViews2 = new RemoteViews(context.getPackageName(), R.layout.course_widget_4_4_section_item);
            remoteViews2.setTextViewText(R.id.course_widget_4_4_section_item, "" + i);
            remoteViews.addView(R.id.course_widget_4_4_section_colum, remoteViews2);
            i++;
        }
        this.remoteViews.addView(this.res, remoteViews);
    }

    public RemoteViews showTimetableWeekBar(RemoteViews remoteViews, @IdRes int i) {
        remoteViews.removeAllViews(i);
        remoteViews.addView(i, new RemoteViews(context.getPackageName(), R.layout.course_widget_4_4_week_view_section_colum));

        //当前是星期几

        for (int i3 = 1; i3 < 8; i3++) {
            RemoteViews remoteViews2;
            //如果是当天
            if (i3 == DateUtil.dayOfWeek()) {
                remoteViews2 = new RemoteViews(context.getPackageName(), R.layout.course_widget_4_4_week_view_today);
            } else {
                remoteViews2 = new RemoteViews(context.getPackageName(), R.layout.course_widget_4_4_week_view_normal);
            }
            remoteViews2.setTextViewText(R.id.course_widget_4_4_week_bar_text, DateUtil.dayOfWeekStr(i3 - 1));
            remoteViews.addView(i, remoteViews2);
        }
        return remoteViews;
    }

    //初始化课程显示的每列
    private void oh() {
        for (int i = 1; i <= 7; i++) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.course_widget_4_4_day_colum);
            List<MySubject> mySubjectList = subjectSparseArray.get(i - 1);
            for (MySubject mySubject : mySubjectList) {
                RemoteViews remoteViews1 = new RemoteViews(context.getPackageName(), getCourseViewRes(mySubject.getStep()));
                if (TextUtils.isEmpty(mySubject.getName())) {
                    remoteViews1.setInt(R.id.course_widget_4_4_course_view_img, "setBackgroundColor", Color.TRANSPARENT);
                    remoteViews1.setTextViewText(R.id.course_widget_4_4_course_view_text, "");
                } else {
                    remoteViews1.setInt(R.id.course_widget_4_4_course_view_img, "setBackgroundColor", colorPool.getColorAutoWithAlpha(mySubject.getColorRandom(), 0.8f));
                    remoteViews1.setTextViewText(R.id.course_widget_4_4_course_view_text, mySubject.getRoom() + "@" + mySubject.getName());
                }
                remoteViews.addView(R.id.course_widget_4_4_day_colum, remoteViews1);
            }
            this.remoteViews.addView(this.res, remoteViews);
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
    private void onClick(RemoteViews remoteViews, int i) {
        if (i > 0) {
            if (this.pendingIntent == null) {
                Intent intent = new Intent(context, MainActivity.class);
                this.pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            }
            RemoteViews remoteViews2 = new RemoteViews(context.getPackageName(), getDisplayViewRes(i));
            remoteViews2.setOnClickPendingIntent(R.id.course_widget_4_4_display_view, this.pendingIntent);
            remoteViews.addView(R.id.course_widget_4_4_day_colum, remoteViews2);
        }
    }

}
