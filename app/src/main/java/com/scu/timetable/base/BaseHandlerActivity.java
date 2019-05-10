package com.scu.timetable.base;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * @author 25714
 * 将Handler封装进Activity
 */
public abstract class BaseHandlerActivity extends BaseActivity {

    private final Handler handler = new MyHandler(this);

    private static final class MyHandler extends Handler {
        private final WeakReference<BaseHandlerActivity> mActivityReference;

        MyHandler(BaseHandlerActivity baseHandlerActivity) {
            this.mActivityReference = new WeakReference<>(baseHandlerActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //获取弱引用队列中的activity
            BaseHandlerActivity baseHandlerActivity = mActivityReference.get();
            baseHandlerActivity.handleMessage(msg);
        }
    }

    /**
     * 在handleMessage中处理发送的消息，子类必须实现该抽象方法
     * @param msg 待处理的消息
     * */
    protected abstract void handleMessage(Message msg);

    /**
     * 发送消息
     * @param msg 发送的消息
     * */
    protected void sendMessage(Message msg) {
        handler.sendMessage(msg);
    }

    /**
     * Runnable延迟执行
     * @param runnable a runnable object that will be executed
     * @param delayMillis The delay (in milliseconds) until the Runnable will be executed.
     * */
    protected void postDelayed(Runnable runnable, long delayMillis) {
        handler.postDelayed(runnable, delayMillis);
    }
}
