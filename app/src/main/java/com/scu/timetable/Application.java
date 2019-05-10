package com.scu.timetable;

import android.content.Context;

import com.scu.timetable.utils.content.SPHelper;

/**
 * @author 25714
 */
public class Application extends android.app.Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        SPHelper.init(this);
    }

    public static Context getContext() {
        return context;
    }
}
