package com.scu.timetable;

import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import com.felix.atoast.library.AToast;
import com.scu.timetable.utils.EncryptionUtils;
import com.scu.timetable.utils.FileUtil;
import com.scu.timetable.utils.content.SPHelper;
import com.zpj.downloader.ZDownloader;
import com.zpj.downloader.config.DownloaderConfig;
import com.zpj.utils.ZUtils;

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
        ZUtils.init(this);
        SPHelper.init(this);
        AToast.onInit(this);
        DownloaderConfig config = DownloaderConfig.with(this)
                .setDownloadPath(FileUtil.getDiskCacheDir(this));
        Log.d("cachePath", "cachePath=" + FileUtil.getDiskCacheDir(this));
        ZDownloader.init(config);

        String test = "kseugrbfjkhdlf";
        Log.d("Application", "test=" + test);
        String test2 = EncryptionUtils.encryptByAES(test);
        Log.d("Application", "test2=" + test2);
        String test3 = EncryptionUtils.decryptByAES(test2);
        Log.d("Application", "test3=" + test3);
    }

}
