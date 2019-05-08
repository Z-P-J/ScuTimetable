package com.scu.timetable;

import com.scu.timetable.utils.content.SPHelper;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SPHelper.init(this);
    }
}
