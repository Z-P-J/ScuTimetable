package com.scu.timetable.ui.fragment.base;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.labo.kaji.swipeawaydialog.SwipeAwayDialogFragment;

/**
 * @author Z-P-J
 */
public class FullscreenDialogFragment extends SwipeAwayDialogFragment {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        Window dialogWindow = getDialog().getWindow();

        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        DisplayMetrics dm = new DisplayMetrics();
        if (getActivity() != null) {
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        }
        lp.width = dm.widthPixels;
//        lp.height = dm.heightPixels - StatusBarUtil.getStatusBarHeight(getContext());
        lp.height = dm.heightPixels;
        lp.dimAmount = 0.0f;
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);
    }

    public void setWindowAnimations(@StyleRes int resId) {
        Window dialogWindow = getDialog().getWindow();
        dialogWindow.setWindowAnimations(resId);
    }

//    public void setBackgroudAlpha(float alpha) {
//        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, alpha);
//        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                float value = (Float) animation.getAnimatedValue();
//
//            }
//        });
//    }

}
