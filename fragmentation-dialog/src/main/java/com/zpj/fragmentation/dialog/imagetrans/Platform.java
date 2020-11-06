//package com.zpj.fragmentation.dialog.imagetrans;
//
//import android.os.Build;
//import android.os.Handler;
//import android.os.Looper;
//import android.util.Log;
//
//import java.util.concurrent.Executor;
//import java.util.concurrent.Executors;
//
///**
// * Created by liuting on 16/8/26.
// */
//public class Platform {
//    private static final Platform PLATFORM = findPlatform();
//
//    public static Platform get() {
//        Log.e("Platform",PLATFORM.getClass().toString());
//        return PLATFORM;
//    }
//
//    private static Platform findPlatform() {
//        try {
//            Class.forName("android.os.Build");
//            if (Build.VERSION.SDK_INT != 0) {
//                return new Android();
//            }
//        } catch (ClassNotFoundException ignored) {
//        }
//        return new Platform();
//    }
//
//    public Executor defaultCallbackExecutor() {
//        return Executors.newCachedThreadPool();
//    }
//
//    public void execute(Runnable runnable) {
//        defaultCallbackExecutor().execute(runnable);
//    }
//
//
//    static class Android extends Platform {
//
//        @Override
//        public Executor defaultCallbackExecutor() {
//            return new MainThreadExecutor();
//        }
//
//        static class MainThreadExecutor implements Executor {
//            private final Handler handler = new Handler(Looper.getMainLooper());
//
//            @Override
//            public void execute(Runnable r) {
//                handler.post(r);
//            }
//        }
//    }
//
//
//}
