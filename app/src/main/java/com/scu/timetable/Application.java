package com.scu.timetable;

import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import com.felix.atoast.library.AToast;
import com.scu.timetable.utils.FileUtil;
import com.scu.timetable.utils.content.SPHelper;
import com.zpj.qxdownloader.QXDownloader;
import com.zpj.qxdownloader.config.QianXunConfig;

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
        QianXunConfig config = QianXunConfig.with(this)
                .setDownloadPath(FileUtil.getDiskCacheDir(this));
        Log.d("cachePath", "cachePath=" + FileUtil.getDiskCacheDir(this));
        QXDownloader.init(config);
    }

}
