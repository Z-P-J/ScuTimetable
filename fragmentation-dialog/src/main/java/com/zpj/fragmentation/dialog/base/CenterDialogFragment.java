package com.zpj.fragmentation.dialog.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.zpj.fragmentation.dialog.R;
import com.zpj.fragmentation.dialog.animator.PopupAnimator;
import com.zpj.fragmentation.dialog.animator.ScaleAlphaAnimator;
import com.zpj.fragmentation.dialog.enums.PopupAnimation;
import com.zpj.fragmentation.dialog.impl.FullScreenDialogFragment;
import com.zpj.fragmentation.dialog.utils.DialogThemeUtils;
import com.zpj.utils.ScreenUtils;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public abstract class CenterDialogFragment extends BaseDialogFragment {

    protected View contentView;

    @Override
    protected final int getImplLayoutId() {
        return R.layout._dialog_layout_center_view;
    }

    protected abstract int getContentLayoutId();

    @Override
    protected int getGravity() {
        return Gravity.CENTER;
    }

    @Override
    protected PopupAnimator getDialogAnimator(ViewGroup contentView) {
        return new ScaleAlphaAnimator(contentView, PopupAnimation.ScaleAlphaFromCenter);
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);

        FrameLayout centerPopupContainer = findViewById(R.id.centerPopupContainer);
        if (getContentLayoutId() > 0) {
            contentView = LayoutInflater.from(context).inflate(getContentLayoutId(), null, false);
            centerPopupContainer.addView(contentView);
        }

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) centerPopupContainer.getLayoutParams();
        if (this instanceof FullScreenDialogFragment) {
            params.height = MATCH_PARENT;
            params.width = MATCH_PARENT;
        } else {
            int maxHeight = getMaxHeight();
            if (maxHeight == WRAP_CONTENT || maxHeight == MATCH_PARENT) {
                int margin = (int) (ScreenUtils.getScreenHeight(context) * 0.07f);
                params.topMargin = margin;
                params.bottomMargin = margin;
            }
            params.height = maxHeight;

            int maxWidth = getMaxWidth();
            if (maxWidth == WRAP_CONTENT || maxWidth == MATCH_PARENT) {
                int margin = (int) (ScreenUtils.getScreenWidth(context) * 0.07f);
                params.leftMargin = margin;
                params.rightMargin = margin;
            }
            params.width = getMaxWidth();

        }

        params.gravity = Gravity.CENTER;


        if (contentView != null) {
            if (bgDrawable != null) {
                contentView.setBackground(bgDrawable);
            } else {
                contentView.setBackground(DialogThemeUtils.getCenterDialogBackground(context));
            }
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) contentView.getLayoutParams();
            layoutParams.height = WRAP_CONTENT;
            layoutParams.width = MATCH_PARENT;
            layoutParams.gravity = Gravity.CENTER;
        }

    }

    public View getContentView() {
        return contentView;
    }

    protected int getMaxWidth() {
        return MATCH_PARENT;
    }

    protected int getMaxHeight() {
        return WRAP_CONTENT;
    }

}
