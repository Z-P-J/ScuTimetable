package com.scu.timetable;

import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import com.scu.timetable.utils.content.SPHelper;

/**
 * @author Z-P-J
 */
public final class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
        SPHelper.init(this);
    }

}
