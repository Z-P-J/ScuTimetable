package com.scu.timetable;

import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import com.felix.atoast.library.AToast;
import com.scu.timetable.utils.FileUtil;
import com.scu.timetable.utils.content.SPHelper;
import com.zpj.downloader.ZDownloader;
import com.zpj.downloader.config.DownloaderConfig;

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
        AToast.onInit(this);
        DownloaderConfig config = DownloaderConfig.with(this)
                .setDownloadPath(FileUtil.getDiskCacheDir(this));
        Log.d("cachePath", "cachePath=" + FileUtil.getDiskCacheDir(this));
        ZDownloader.init(config);
    }

}
