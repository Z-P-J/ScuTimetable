package com.scu.timetable.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class AnimUtils {

    public static void doDelayShowAnim(long dur, long delay, final View... targets) {
        for (int i = 0; i < targets.length; i++) {
            final View target = targets[i];
            target.setAlpha(0);
            ObjectAnimator animatorY = ObjectAnimator.ofFloat(target, "translationY", 1000, 0);
            ObjectAnimator animatorA = ObjectAnimator.ofFloat(target, "alpha", 0, 1);
            animatorY.setDuration(dur);
            animatorA.setDuration((long) (dur * 0.618F));
            AnimatorSet animator = new AnimatorSet();
            animator.playTogether(animatorA, animatorY);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.setStartDelay(delay * i);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    for (View view : targets) {
                        view.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            animator.start();
        }
    }

}
