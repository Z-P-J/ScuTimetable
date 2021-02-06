package com.scu.timetable;

import android.app.Application;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.maning.librarycrashmonitor.MCrashMonitor;
import com.scu.timetable.utils.EncryptionUtils;
import com.scu.timetable.utils.TimetableHelper;
import com.zpj.downloader.ZDownloader;
import com.zpj.http.ZHttp;
import com.zpj.http.core.DefaultCookieJar;
import com.zpj.http.core.IHttp;
import com.zpj.utils.FileUtils;

import java.util.Map;

/**
 * @author Z-P-J
 */
public final class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
        MCrashMonitor.init(this, true, file -> {
//                MCrashMonitor.startCrashShowPage(getContext());
        });
//        ZUtils.init(this);
//        ZToast.init(this);

        ZHttp.config()
                .allowAllSSL(true)
                .onRedirect(new IHttp.OnRedirectListener() {
                    @Override
                    public boolean onRedirect(int redirectCount, String redirectUrl) {
                        Log.d("connect", "onRedirect redirectUrl=" + redirectUrl);
                        return true;
                    }
                })
                .userAgent(TimetableHelper.UA)
                .ignoreContentType(true)
                .baseUrl("http://zhjw.scu.edu.cn") // http://zhjw.scu.edu.cn  202.115.47.141
                .connectTimeout(15000)
                .readTimeout(50000)
                .init();

        ZDownloader.config(this)
                .setDownloadPath(FileUtils.getCacheDir(this))
                .init();

        String test = "kseugrbfjkhdlf";
        Log.d("Application", "test=" + test);
        String test2 = EncryptionUtils.encryptByAES(test);
        Log.d("Application", "test2=" + test2);
        String test3 = EncryptionUtils.decryptByAES(test2);
        Log.d("Application", "test3=" + test3);
    }

}
