package com.zpj.fragmentation.dialog.imagetrans;

import android.graphics.drawable.Drawable;

/**
 * Created by liuting on 18/3/15.
 */

public interface ImageLoad<T> {
    /**
     * 加载图片
     *
     * @param url       图片的url
     * @param callback  回调 {@link LoadCallback}
     * @param imageView 加载图片的imageView
     * @param uniqueStr 加载图片请求的唯一tag,为了是
     */
    void loadImage(T url, LoadCallback callback, TransImageView imageView, String uniqueStr);

    /**
     * 判断当前图片是否有本地缓存，用来判断是否显示缩略图
     *
     * @param url 图片地址
     * @return
     */
    boolean isCached(T url);

    /**
     * 页面被销毁
     *
     * @param url
     * @param unique
     */
    void cancel(T url, String unique);

    /**
     * 图片加载器中用来回传下载好的图片
     */
    interface LoadCallback {
        /**
         * 进度
         *
         * @param progress
         */
        void progress(float progress);

        void loadFinish(Drawable drawable);
    }
}
