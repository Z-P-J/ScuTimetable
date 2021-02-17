package com.zpj.fragmentation.dialog.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.zpj.fragmentation.dialog.R;


/**
 * Created by felix on 16/11/20.
 */
public class PopLayout extends FrameLayout implements View.OnLayoutChangeListener {
    private static final String TAG = "PopLayout";

    private float mOffset = 0;

    private int mRadiusSize = DEFAULT_RADIUS;

    private int mBulgeSize = DEFAULT_BULGE_SIZE;

    private Paint mPaint;

    private Path mPopMaskPath;

    private Path mBulgePath;

    private Path mDestBulgePath;

    private Matrix mCornuteMatrix;

    private int mSiteMode = SITE_BOTTOM;

    public static final int SITE_TOP = 0;

    public static final int SITE_LEFT = 1;

    public static final int SITE_RIGHT = 2;

    public static final int SITE_BOTTOM = 3;

    private static final int DEFAULT_RADIUS = 8;

    private static final int DEFAULT_BULGE_SIZE = 16;

//    private static final Xfermode MODE = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    private Xfermode xfermode;

    public PopLayout(Context context) {
        this(context, null, 0);
    }

    public PopLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PopLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    public PopLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        initialize(context, attrs, defStyleAttr);
//    }

    private void initialize(Context context, AttributeSet attrs, int defStyleAttr) {

//        setCardElevation(5);
//        setUseCompatPadding(true);
//////        setContentPadding(0, 20, 0, 0);
//        setRadius(mRadiusSize);

//        setLayerType(View.LAYER_TYPE_HARDWARE, null);


        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PopLayout);
        mSiteMode = a.getInt(R.styleable.PopLayout_siteMode, SITE_BOTTOM);
        mRadiusSize = a.getDimensionPixelSize(R.styleable.PopLayout_radiusSize,
                getResources().getDimensionPixelSize(R.dimen.pop_radius));
        mBulgeSize = a.getDimensionPixelSize(R.styleable.PopLayout_bulgeSize,
                getResources().getDimensionPixelSize(R.dimen.bulge_size));
        mOffset = a.getDimensionPixelSize(R.styleable.PopLayout_offsetSize, 0);
        a.recycle();

        if (getBackground() == null) {
            // 需要设置背景，可能是因为没有背景Layout就不会去执行绘制操作
            setBackgroundColor(Color.parseColor("#cccccc"));//Color.WHITE
//            setBackgroundColor(Color.WHITE);
        }

        xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
//        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
//            xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
//        } else {
//            xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
//            layoutPath = new Path();
//        }

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setXfermode(xfermode);

        mBulgePath = new Path();
        mPopMaskPath = new Path();
        mDestBulgePath = new Path();
        mCornuteMatrix = new Matrix();

        resetBulge();
        resetMask();
        onBulgeChange();
        addOnLayoutChangeListener(this);
    }

    private void resetBulge() {
        mBulgePath.reset();
        mBulgePath.lineTo(mBulgeSize << 1, 0);
        mBulgePath.lineTo(mBulgeSize, mBulgeSize);
        mBulgePath.close();
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        onBulgeChange();
    }

    private void onBulgeChange() {
        if (getChildCount() > 0) {
            View view = getChildAt(0);
            if (view instanceof OnBulgeChangeCallback) {
                ((OnBulgeChangeCallback) view).onBulgeChanged(mSiteMode, mBulgeSize);
            }
        }
    }

    private void resetMask() {
        mPopMaskPath.reset();
        int width = getMeasuredWidth(), height = getMeasuredHeight();
        if (width <= mRadiusSize || height <= mRadiusSize) {
            return;
        }
        float offset = reviseOffset(mOffset);
        mPopMaskPath.addRect(new RectF(0, 0, width, height), Path.Direction.CW);
        mPopMaskPath.addRoundRect(new RectF(mBulgeSize, mBulgeSize, width - mBulgeSize, height - mBulgeSize), mRadiusSize, mRadiusSize, Path.Direction.CCW);
        mPopMaskPath.setFillType(Path.FillType.INVERSE_EVEN_ODD);

        switch (mSiteMode) {
            case SITE_TOP:
                mCornuteMatrix.setRotate(180, mBulgeSize, 0);
                mCornuteMatrix.postTranslate(0, mBulgeSize);
                mBulgePath.transform(mCornuteMatrix, mDestBulgePath);
                mPopMaskPath.addPath(mDestBulgePath, offset - mBulgeSize, 0);
                break;
            case SITE_LEFT:
                mCornuteMatrix.setRotate(90, mBulgeSize, 0);
                mBulgePath.transform(mCornuteMatrix, mDestBulgePath);
                mPopMaskPath.addPath(mDestBulgePath, 0, offset);
                break;
            case SITE_RIGHT:
                mCornuteMatrix.setRotate(-90, mBulgeSize, 0);
                mCornuteMatrix.postTranslate(-mBulgeSize, 0);
                mBulgePath.transform(mCornuteMatrix, mDestBulgePath);
                mPopMaskPath.addPath(mDestBulgePath, width - mBulgeSize, offset);
                break;
            case SITE_BOTTOM:
                mCornuteMatrix.setTranslate(-mBulgeSize, 0);
                mBulgePath.transform(mCornuteMatrix, mDestBulgePath);
                mPopMaskPath.addPath(mDestBulgePath, offset, height - mBulgeSize);
                break;
        }
    }

    private float reviseOffset(float offset) {
        int size = 0, bulgeWidth = mBulgeSize << 1;
        switch (mSiteMode) {
            case SITE_TOP:
            case SITE_BOTTOM:
                size = getMeasuredWidth();
                break;
            case SITE_LEFT:
            case SITE_RIGHT:
                size = getMeasuredHeight();
                break;
        }
        offset = Math.max(offset, mRadiusSize + bulgeWidth);
        if (size > 0) {
            offset = Math.min(offset, size - mRadiusSize - bulgeWidth);
            if (mRadiusSize + bulgeWidth > offset) {
                offset = size >> 1;
            }
        }
        Log.d("reviseOffset", "offset=" + offset);
        return offset;
    }

    public void setOffset(float offset) {
        Log.d("setOffset", "offset=" + offset);
        if (mOffset != offset) {
            mOffset = offset;
            resetMask();
            postInvalidate();
        }
    }

    public void setRadiusSize(int radius) {
        if (mRadiusSize != radius) {
            mRadiusSize = radius;
            resetMask();
            postInvalidate();
        }
    }

    public void setBulgeSize(int size) {
        if (mBulgeSize != size) {
            mBulgeSize = size;
            resetBulge();
            resetMask();
            postInvalidate();
        }
    }

    public void setSiteMode(int mode) {
        if (mSiteMode != mode) {
            mSiteMode = mode;
            onBulgeChange();
            resetMask();
            postInvalidate();
        }
    }

    public float getOffset() {
        return mOffset;
    }

    public int getRadiusSize() {
        return mRadiusSize;
    }

    public int getBugleSize() {
        return mBulgeSize;
    }

    public int getSiteMode() {
        return mSiteMode;
    }

    @Override
    public void draw(Canvas canvas) {
        int layer = canvas.saveLayer(0, 0, getWidth(),
                getHeight(), null, Canvas.ALL_SAVE_FLAG);
        super.draw(canvas);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            canvas.drawPath(mPopMaskPath, mPaint);
        } else {
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
//            if (layoutPath == null) {
//                layoutPath = new Path();
//            }
            final Path path = new Path();
            path.addRect(0, 0, getWidth(), getHeight(), Path.Direction.CW);
            path.op(mPopMaskPath, Path.Op.DIFFERENCE);
            canvas.drawPath(path, mPaint);
        }

        canvas.restoreToCount(layer);
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom,
                               int oldLeft, int oldTop, int oldRight, int oldBottom) {
        resetMask();
        postInvalidate();
    }

    public interface OnBulgeChangeCallback {

        void onBulgeChanged(int site, int size);
    }
}
