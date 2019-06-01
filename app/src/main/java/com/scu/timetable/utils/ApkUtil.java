package com.scu.timetable.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.scu.timetable.model.UpdateBean;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.lang.ref.WeakReference;
import java.net.Proxy;

public final class ApkUtil {

    private static final class MyHandler extends Handler {
        private final WeakReference<ApkUtil> reference;

        MyHandler(ApkUtil apkUtil) {
            this.reference = new WeakReference<>(apkUtil);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ApkUtil apkUtil = reference.get();
            apkUtil.handleMessage(msg);
        }
    }

    public interface UpdateCallback {
        void onError(String errMsg);
        void onGetLatestVersion(UpdateBean bean);
        void isLatestVersion();
    }

    private UpdateCallback callback;

    private Handler handler = new MyHandler(this);;

    private ApkUtil(UpdateCallback callback) {
        this.callback = callback;
    }

    public static ApkUtil with(UpdateCallback callback) {
        return new ApkUtil(callback);
    }

    private void sendMessage(int what, Object obj) {
        Message msg = new Message();
        msg.obj = obj;
        msg.what = what;
        handler.sendMessage(msg);
    }

    private void handleMessage(Message msg) {
        if (msg.what == -1) {
            if (callback != null) {
                String errMsg = (String) msg.obj;
                callback.onError(errMsg);
            }
        } else if (msg.what == 1) {
            UpdateBean bean = (UpdateBean) msg.obj;
            if (callback != null) {
                callback.onGetLatestVersion(bean);
            }
        } else if (msg.what == 2) {
            if (callback != null) {
                callback.isLatestVersion();
            }
        }
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

    public void checkUpdate(Context context) {
        ExecutorHelper.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Document document = Jsoup.connect("http://tt.shouji.com.cn/androidv3/soft_show.jsp?id=1555815")
                            .proxy(Proxy.NO_PROXY)
                            .header("Connection", "Keep-Alive")
                            .header("Referer", "https://wap.shouji.com.cn/")
                            .header("User-Agent", TimetableHelper.UA)
                            .header("Accept-Encoding", "gzip")
                            .get();
                    String versionName = document.select("versionname").get(0).text();
                    String baseinfof = document.select("baseinfof").get(0).text();
                    Log.d("baseinfof", "baseinfof=" + baseinfof);
                    String newVersionName = versionName.trim();
                    Log.d("newVersionName", "newVersionName=" + newVersionName);
                    String currentVersionName = getVersionName(context).trim();
                    Log.d("currentVersionName", "currentVersionName=" + newVersionName);
                    boolean isNew = compareVersions(currentVersionName, newVersionName);
                    Log.d("isNew", "isNew=" + isNew);

                    if (true) {
                        String updateContent = "";
                        String fileSize = "";
                        String updateTime = "";
                        Elements elements = document.select("introduce");
                        for (Element element : elements) {
                            String title = element.select("introducetitle").get(0).text();
                            if ("更新内容".equals(title)) {
                                updateContent = "更新内容:\n" + element.select("introduceContent").get(0).text();
                                Log.d("更新内容", "更新内容=" + updateContent);
                            } else if ("软件信息".equals(title)) {
                                String content = element.select("introduceContent").get(0).text();
                                fileSize = content.substring(content.indexOf("大小：") + 3, content.indexOf("MB") + 2);
                                int index = content.indexOf("更新：");
                                updateTime = content.substring(index + 3, index + 13);
                            }
                        }
                        UpdateBean bean = new UpdateBean();
                        bean.setVersionName(versionName);
                        bean.setUpdateContent(updateContent);
                        bean.setFileSize(fileSize);
                        bean.setUpdateTime(updateTime);
                        Log.d("bean", "bean=" + bean.toString());
                        sendMessage(1, bean);
                    } else {
                        sendMessage(2, null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    sendMessage(-1, e.getMessage());
                }
            }
        });
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


//        int len = oldLen > newLen ? oldLen : newLen;
//        for (int i = 0; i < len; i++) {
//            if (i < oldLen && i < newLen) {
//                int o = Integer.parseInt(oldVs[i]);
//                int n = Integer.parseInt(newVs[i]);
//                if (o > n) {
//                    result = -1;
//                    break;
//                } else if (o < n) {
//                    result = 1;
//                    break;
//                }
//            } else {
//                if (oldLen > newLen) {
//                    for (int j = i; j < oldLen; j++) {
//                        if (Integer.parseInt(oldVs[j]) > 0) {
//                            result = -1;
//                        }
//                    }
//                    break;
//                } else if (oldLen < newLen) {
//                    for (int j = i; j < newLen; j++) {
//                        if (Integer.parseInt(newVs[j]) > 0) {
//                            result = 1;
//                        }
//                    }
//                    break;
//                }
//            }
//        }
//        return result;
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
