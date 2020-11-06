
package com.zpj.fragmentation.swipeback;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;

import com.zpj.fragmentation.ISupportActivity;
import com.zpj.fragmentation.SupportActivity;


/**
 * You can also refer to {@link SwipeBackActivity} to implement YourSwipeBackActivity
 * (extends Activity and impl {@link ISupportActivity})
 * <p>
 * Created by YoKey on 16/4/19.
 */
public class SwipeBackActivity extends SupportActivity implements ISwipeBack {
    private SwipeBackLayout mSwipeBackLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
        mSwipeBackLayout = new SwipeBackLayout(this);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mSwipeBackLayout.setLayoutParams(params);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mSwipeBackLayout.attachToActivity(this);
    }

    @Override
    public SwipeBackLayout getSwipeBackLayout() {
        return mSwipeBackLayout;
    }

    /**
     * 是否可滑动
     * @param enable
     */
    @Override
    public void setSwipeBackEnable(boolean enable) {
        mSwipeBackLayout.setEnableGesture(enable);
    }

    @Override
    public void setEdgeLevel(SwipeBackLayout.EdgeLevel edgeLevel) {
        mSwipeBackLayout.setEdgeLevel(edgeLevel);
    }

    @Override
    public void setEdgeLevel(int widthPixel) {
        mSwipeBackLayout.setEdgeLevel(widthPixel);
    }

    @Override
    public void setEdgeOrientation(int orientation) {
        mSwipeBackLayout.setEdgeOrientation(orientation);
    }

    @Override
    public void setParallaxOffset(float offset) {

    }

    /**
     * 限制SwipeBack的条件,默认栈内Fragment数 <= 1时 , 优先滑动退出Activity , 而不是Fragment
     *
     * @return true: Activity优先滑动退出;  false: Fragment优先滑动退出
     */
    @Override
    public boolean swipeBackPriority() {
        return getSupportFragmentManager().getBackStackEntryCount() <= 1;
    }
}
