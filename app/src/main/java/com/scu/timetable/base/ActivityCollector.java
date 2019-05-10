package com.scu.timetable.base;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 25714
 */
public final class ActivityCollector {

    private static final List<Activity> ACTIVITIES = new ArrayList<Activity>();

    private ActivityCollector() {

    }

    static void addActivity(Activity activity) {
        ACTIVITIES.add(activity);
    }

    static void removeActivity(Activity activity) {
        ACTIVITIES.remove(activity);
    }

    public static void finishAll() {
        for (Activity activity : ACTIVITIES) {
            activity.finish();
        }
    }

}
