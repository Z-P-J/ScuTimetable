//package com.zpj.qianxundialoglib.base;
//
//import android.animation.Animator;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.view.View;
//import android.view.ViewAnimationUtils;
//import android.view.ViewTreeObserver;
//import android.view.animation.DecelerateInterpolator;
//
//import com.labo.kaji.swipeawaydialog.SwipeAwayDialogFragment;
//
///**
// * @author Z-P-J
// * @date 2019/5/16 20:57
// */
//public class BaseDialogFragment extends DialogFragment {
//
//    @Override
//    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            @Override
//            public boolean onPreDraw() {
//                if (view.getViewTreeObserver().isAlive()) {
//                    view.getViewTreeObserver().removeOnPreDrawListener(this);
//                }
//                int x = view.getMeasuredWidth();
//                int y = view.getMeasuredHeight();
//                int r = (int) Math.sqrt(Math.pow(x / 2, 2) + Math.pow(y / 2, 2));
//                Animator animator = ViewAnimationUtils.createCircularReveal(view, x / 2, y / 2, 0, r);
//                animator.setInterpolator(new DecelerateInterpolator());
//                animator.start();
//                return true;
//            }
//        });
//    }
//
//    @Override
//    public void dismiss() {
//        int x = getView().getMeasuredWidth();
//        int y = getView().getMeasuredHeight();
//        int r = (int) Math.sqrt(Math.pow(x / 2, 2) + Math.pow(y / 2, 2));
//        Animator animator = ViewAnimationUtils.createCircularReveal(getView(), x / 2, y / 2, r, 0);
//        animator.setInterpolator(new DecelerateInterpolator());
//        animator.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                BaseDialogFragment.super.dismiss();
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//
//            }
//        });
//        animator.start();
//    }
//}
