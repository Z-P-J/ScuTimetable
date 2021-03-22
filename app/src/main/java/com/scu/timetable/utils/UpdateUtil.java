package com.scu.timetable.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;

import com.scu.timetable.bean.UpdateInfo;
import com.scu.timetable.ui.activity.MainActivity;
import com.scu.timetable.ui.fragment.dialog.UpdateDialog;
import com.zpj.http.ZHttp;
import com.zpj.http.core.IHttp;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.toast.ZToast;
import com.zpj.utils.AppUtils;
import com.zpj.utils.PrefsHelper;

import java.io.File;
import java.net.Proxy;

/**
 * @author Z-P-J
 */
public final class UpdateUtil {

    private static final String KEY_HAS_CHECKED = "has_checked";
    private static final String KEY_HAS_UPDATE = "has_update";
    private static final String KEY_DOWNLOAD = "download_url";
    private static final String KEY_VERSION_NAME = "version_name";
    private static final String KEY_FILE_SIZE = "file_size";
    private static final String KEY_UPDATE_TIME = "update_time";
    private static final String KEY_UPDATE_CONTENT = "update_content";

    public static PrefsHelper getPrefs() {
        return PrefsHelper.with("update_manager");
    }

    public static boolean hasChecked() {
        return getPrefs().getBoolean(KEY_HAS_CHECKED, false);
    }

    public static boolean hasUpdate() {
        return getPrefs().getBoolean(KEY_HAS_UPDATE, false);
    }

    public static void ignoreVersion(String versionName) {
        getPrefs().putString("ignore_version", versionName);
    }

    public static String getDownloadUrl() {
        return getPrefs().getString(KEY_DOWNLOAD);
    }

    public static String getVersionName() {
        return getPrefs().getString(KEY_VERSION_NAME);
    }

    public static String getFileSize() {
        return getPrefs().getString(KEY_FILE_SIZE);
    }

    public static String getUpdateTime() {
        return getPrefs().getString(KEY_UPDATE_TIME);
    }

    public static String getUpdateContent() {
        return getPrefs().getString(KEY_UPDATE_CONTENT);
    }

    public static void checkUpdate(Activity context) {
        getPrefs().putBoolean(KEY_HAS_CHECKED, false);
        ZHttp.get("http://tt.shouji.com.cn/androidv3/soft_show.jsp?id=1555815")
                .proxy(Proxy.NO_PROXY)
                .referer("https://wap.shouji.com.cn/")
                .ignoreContentType(true)
                .toHtml()
                .bindActivity(context)
                .onSuccess(document -> {
                    String versionName = document.selectFirst("versionname").text();

                    String newVersionName = versionName.trim();
                    Log.d("newVersionName", "newVersionName=" + newVersionName);

                    String ignoreVersion = getPrefs().getString("ignore_version", "");
                    if (TextUtils.equals(ignoreVersion, newVersionName)) {
                        return;
                    }

                    String baseinfof = document.selectFirst("baseinfof").text();
                    Log.d("baseinfof", "baseinfof=" + baseinfof);


                    String currentVersionName = AppUtils.getAppVersionName(context, context.getPackageName()).trim();
                    Log.d("currentVersionName", "currentVersionName=" + newVersionName);

                    boolean isNew = compareVersions(currentVersionName, newVersionName);
                    Log.d("isNew", "isNew=" + isNew);

                    getPrefs().putBoolean(KEY_HAS_UPDATE, isNew);
                    if (isNew) {
                        String updateContent = "";
                        String fileSize = "";
                        String updateTime = "";
                        Elements elements = document.select("introduce");
                        for (Element element : elements) {
                            String title = element.selectFirst("introducetitle").text();
                            if ("更新内容".equals(title)) {
                                updateContent = element.select("introduceContent").get(0).text();
                                Log.d("更新内容", "更新内容=" + updateContent);
                            } else if ("软件信息".equals(title)) {
                                String content = element.selectFirst("introduceContent").text();
                                fileSize = content.substring(content.indexOf("大小：") + 3, content.indexOf("MB") + 2);
                                int index = content.indexOf("更新：");
                                updateTime = content.substring(index + 3, index + 13);
                            }
                        }
                        UpdateInfo bean = new UpdateInfo();
                        bean.setVersionName(versionName);
                        bean.setUpdateContent(updateContent);
                        bean.setFileSize(fileSize);
                        bean.setUpdateTime(updateTime);
                        Log.d("bean", "bean=" + bean.toString());
                        getPrefs().putString(KEY_VERSION_NAME, versionName);
                        getPrefs().putString(KEY_UPDATE_CONTENT, updateContent);
                        getPrefs().putString(KEY_UPDATE_TIME, updateTime);
                        getPrefs().putString(KEY_FILE_SIZE, fileSize);
                        getPrefs().registerOnChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
                            @Override
                            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

                            }
                        });

                        ZHttp.get("http://tt.shouji.com.cn/wap/down/cmwap/package?sjly=199&id=1555815&package=" + context.getPackageName())
                                .proxy(Proxy.NO_PROXY)
                                .referer("https://wap.shouji.com.cn/")
                                .ignoreContentType(true)
                                .toXml()
                                .bindActivity(context)
                                .onSuccess(data -> {
                                    String url = data.selectFirst("url").text();
                                    if (TextUtils.isEmpty(url)) {
//                                        onUpdateCheckedListener.onChecked(null, true);
                                    } else {
                                        bean.setDownloadUrl(url);
                                        getPrefs().putString(KEY_DOWNLOAD, url);
                                        getPrefs().putBoolean(KEY_HAS_CHECKED, true);
//                                        onUpdateCheckedListener.onChecked(bean, false);
//                                        ZToast.normal("开始更新！");
                                        new UpdateDialog().show(context);
                                    }
                                })
                                .onError(new IHttp.OnErrorListener() {
                                    @Override
                                    public void onError(Throwable throwable) {
                                        ZToast.error("检查更新出错!" + throwable.getMessage());
                                    }
                                })
                                .subscribe();

                    } else {
//                        onUpdateCheckedListener.onChecked(null, true);
                        getPrefs().putBoolean(KEY_HAS_CHECKED, true);
                    }
                })
                .onError(new IHttp.OnErrorListener() {
                    @Override
                    public void onError(Throwable throwable) {
                        ZToast.error("检查更新出错!" + throwable.getMessage());
                    }
                })
                .subscribe();
    }

    private static boolean compareVersions(String oldVersion, String newVersion) {
        //返回结果: -2 错误,-1 ,0 ,1
        int result = 0;
        String matchStr = "[0-9]+(\\.[0-9]+)*";
        oldVersion = oldVersion.trim();
        newVersion = newVersion.trim();
        //非版本号格式,返回error
        if (!oldVersion.matches(matchStr) || !newVersion.matches(matchStr)) {
//            return -2;
            return false;
        }
        String[] oldVs = oldVersion.split("\\.");
        String[] newVs = newVersion.split("\\.");
        int oldLen = oldVs.length;
        int newLen = newVs.length;

        if (oldLen == newLen) {
            for (int i = 0; i < newLen; i++) {
                int oldNum = Integer.parseInt(oldVs[i]);
                int newNum = Integer.parseInt(newVs[i]);
                if (newNum > oldNum) {
                    return true;
                }
            }
        }
        return false;
    }

    public interface OnUpdateCheckedListener {
        void onChecked(UpdateInfo info, boolean isLastedVersion);
    }

}
