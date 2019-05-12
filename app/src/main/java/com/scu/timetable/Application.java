package com.scu.timetable;

import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
        SPHelper.init(this);
    }

    public static Context getContext() {
        return context;
    }
}
