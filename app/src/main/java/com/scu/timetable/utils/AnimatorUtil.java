package com.scu.timetable.utils;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * @author 25714
 */
public class AnimatorUtil {

    private AnimatorUtil() {

    }

    public static void shakeAnimator(final View view, long duration) {
        PropertyValuesHolder animator = PropertyValuesHolder.ofFloat("scaleX",
                0.5f, 1f);
        PropertyValuesHolder animator2 = PropertyValuesHolder.ofFloat("scaleY",
                0.5f, 1f);
        ObjectAnimator animator3 = ObjectAnimator.ofPropertyValuesHolder(view,
                animator, animator2);
        animator3.setDuration(duration);
        animator3.setInterpolator(new JellyInterpolator());
        animator3.start();
    }

    public static void showViewAnimator(final View view, long duration) {

        AnimatorSet set = new AnimatorSet();
        view.setVisibility(View.VISIBLE);

        ObjectAnimator animator = ObjectAnimator.ofFloat(view,
                "alpha", 0f, 1.0f);
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(view,
                "scaleX", 0f, 1f);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(view,
                "scaleY", 0f, 1f);
        set.setDuration(1000);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.playTogether(animator, animator1, animator2);
        set.start();
        set.addListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // TODO Auto-generated method stub

            }
        });

    }

    public static void hideViewAnimator(final View view, long duration, AnimatorListener listener) {

        AnimatorSet set = new AnimatorSet();

//		ValueAnimator animator = ValueAnimator.ofFloat(0, w);
//		animator.addUpdateListener(new AnimatorUpdateListener() {
//
//			@Override
//			public void onAnimationUpdate(ValueAnimator animation) {
//				float value = (Float) animation.getAnimatedValue();
//				MarginLayoutParams params = (MarginLayoutParams) view
//						.getLayoutParams();
//				params.leftMargin = (int) value;
//				params.rightMargin = (int) value;
//				view.setLayoutParams(params);
//			}
//		});

//		ValueAnimator animator4 = ValueAnimator.ofFloat(h, 0);
//		animator.addUpdateListener(new AnimatorUpdateListener() {
//
//			@Override
//			public void onAnimationUpdate(ValueAnimator animation) {
//				float value = (Float) animation.getAnimatedValue();
//				MarginLayoutParams params = (MarginLayoutParams) view
//						.getLayoutParams();
//				params.topMargin = (int) value;
//				params.bottomMargin = (int) value;
//				view.setLayoutParams(params);
//			}
//		});

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(view,
                "scaleX", 1f, 0.0f);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(view,
                "scaleY", 1f, 0.0f);
        set.setDuration(duration);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.playTogether(animator2, animator3);
        set.start();
        set.addListener(listener);

    }

    public static void hideViewAnimator(final View view, long duration) {
        hideViewAnimator(view, duration, new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

}
