package com.zpj.popup.imagetrans;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.zpj.popup.imagetrans.listener.ProgressViewGet;
import com.zpj.popup.imagetrans.listener.SourceImageViewGet;
import com.zpj.popup.impl.FullScreenPopup;

import java.util.List;

/**
 * Created by liuting on 18/3/14.
 */

public class ImageTransBuild<T> {
    public int clickIndex;
    public int nowIndex;
    public List<T> imageList;
    public SourceImageViewGet sourceImageViewGet;
    public ProgressViewGet progressViewGet;
    public ITConfig itConfig;
    public ImageTransAdapter imageTransAdapter;
    public ImageLoad<T> imageLoad;
    public ScaleType scaleType = ScaleType.CENTER_CROP;
    public FullScreenPopup dialog;

    public void checkParam() {
        if (itConfig == null)
            itConfig = new ITConfig();
        if (imageTransAdapter == null) {
            imageTransAdapter = new ImageTransAdapter() {
                @Override
                protected View onCreateView(View parent, ViewPager viewPager, FullScreenPopup dialogInterface) {
                    return null;
                }
            };
        }
        if (sourceImageViewGet == null)
            throw new NullPointerException("not set SourceImageViewGet");
        if (imageLoad == null)
            throw new NullPointerException("not set ImageLoad");
        if (imageList == null)
            throw new NullPointerException("not set ImageList");
    }

    boolean needTransOpen(int pos, boolean change) {
        boolean need = pos == clickIndex;
        if (need && change) {
            clickIndex = -1;
        }
        return need;
    }

    View inflateProgress(Context context, FrameLayout rootView) {
        if (progressViewGet != null) {
            View progress = progressViewGet.getProgress(context);
            if (progress == null) return null;
            int width = ViewGroup.LayoutParams.WRAP_CONTENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            if (progress.getLayoutParams() != null) {
                width = progress.getLayoutParams().width;
                height = progress.getLayoutParams().height;
            }
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width, height);
            lp.gravity = Gravity.CENTER;
            rootView.addView(progress, lp);
            return progress;
        }
        return null;
    }
}
