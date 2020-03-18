package com.zpj.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import www.linwg.org.lib.LCardView;

public class SquareWidthCardView extends LCardView {

    public SquareWidthCardView(@NonNull Context context) {
        super(context);
    }

    public SquareWidthCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
