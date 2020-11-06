package com.scu.timetable.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;

import com.scu.timetable.events.UpdateEvent;
import com.scu.timetable.model.UpdateInfo;
import com.zpj.utils.PrefsHelper;
import com.zpj.http.ZHttp;
import com.zpj.http.core.IHttp;
import com.zpj.http.core.ObservableTask;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;

import java.io.File;
import java.net.Proxy;

/**
 * @author Z-P-J
 */
public final class UpdateUtil {

    private final Context context;
    private IHttp.OnSuccessListener<UpdateEvent> onSuccessListener;
    private IHttp.OnErrorListener onErrorListener;


    private UpdateUtil(Context context) {
        this.context = context;
    }

    public static UpdateUtil with(Context context) {
        return new UpdateUtil(context);
    }

    public static synchronized String getVersionName(Context context){
        String versionName = "1.4.0";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public UpdateUtil setOnErrorListener(IHttp.OnErrorListener onErrorListener) {
        this.onErrorListener = onErrorListener;
        return this;
    }

    public UpdateUtil setOnSuccessListener(IHttp.OnSuccessListener<UpdateEvent> onSuccessListener) {
        this.onSuccessListener = onSuccessListener;
        return this;
    }

    public void checkUpdate() {
        ZHttp.get("http://tt.shouji.com.cn/androidv3/soft_show.jsp?id=1555815")
                .proxy(Proxy.NO_PROXY)
                .referer("https://wap.shouji.com.cn/")
                .ignoreContentType(true)
                .toHtml()
//                .flatMap((ObservableTask.OnFlatMapListener<Document, UpdateEvent>) (document, emitter) -> {
//
//                })
                .onSuccess(new IHttp.OnSuccessListener<Document>() {
                    @Override
                    public void onSuccess(Document document) throws Exception {
                        String versionName = document.selectFirst("versionname").text();

                        String newVersionName = versionName.trim();
                        Log.d("newVersionName", "newVersionName=" + newVersionName);

                        String ignoreVersion = PrefsHelper.with().getString("ignore_version", "");
                        if (ignoreVersion.equals(newVersionName)) {
                            return;
                        }

                        String baseinfof = document.selectFirst("baseinfof").text();
                        Log.d("baseinfof", "baseinfof=" + baseinfof);

                        String currentVersionName = getVersionName(context).trim();
                        Log.d("currentVersionName", "currentVersionName=" + newVersionName);

                        boolean isNew = compareVersions(currentVersionName, newVersionName);
                        Log.d("isNew", "isNew=" + isNew);

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
//                            EventBus.getDefault().post(UpdateEvent.create().setUpdateInfo(bean));




                            ZHttp.get("http://tt.shouji.com.cn/wap/down/cmwap/package?sjly=199&id=1555815&package=" + context.getPackageName())
                                    .proxy(Proxy.NO_PROXY)
                                    .referer("https://wap.shouji.com.cn/")
                                    .ignoreContentType(true)
                                    .toXml()
                                    .onSuccess(data -> {
                                        String url = data.selectFirst("url").text();
                                        if (TextUtils.isEmpty(url)) {
                                            onSuccessListener.onSuccess(UpdateEvent.create().setLatestVersion(true));
                                        } else {
                                            bean.setDownloadUrl(url);
                                            onSuccessListener.onSuccess(UpdateEvent.create().setUpdateInfo(bean));
                                        }
                                    })
                                    .onError(onErrorListener)
                                    .subscribe();

                        } else {
//                            EventBus.getDefault().post(UpdateEvent.create().setLatestVersion(true));
//                            emitter.onNext(UpdateEvent.create().setLatestVersion(true));
                            onSuccessListener.onSuccess(UpdateEvent.create().setLatestVersion(true));
                        }
                    }
                })
                .onError(onErrorListener)
                .subscribe();
    }

    public boolean compareVersions(String oldVersion, String newVersion) {
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

    public static Intent getInstallAppIntent(Context context, File appFile) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //区别于 FLAG_GRANT_READ_URI_PERMISSION 跟 FLAG_GRANT_WRITE_URI_PERMISSION， URI权限会持久存在即使重启，直到明确的用 revokeUriPermission(Uri, int) 撤销。 这个flag只提供可能持久授权。但是接收的应用必须调用ContentResolver的takePersistableUriPermission(Uri, int)方法实现
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                Uri fileUri = FileProvider.getUriForFile(context, "com.zpj.qxdownloader.fileprovider", appFile);
                intent.setDataAndType(fileUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(appFile), "application/vnd.android.package-archive");
            }
            return intent;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
