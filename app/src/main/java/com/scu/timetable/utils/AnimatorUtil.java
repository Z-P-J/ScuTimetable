package com.scu.timetable.utils;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * @author Z-P-J
 */
public final class AnimatorUtil {

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

    public static void circleAnimator(final View view, int x, int y, int duration) {
        //隐藏
        if (view.getVisibility() == View.VISIBLE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                final Animator animatorHide = ViewAnimationUtils.createCircularReveal(view,
                        x,
                        y,
                        //确定元的半径（算长宽的斜边长，这样半径不会太短也不会很长效果比较舒服）
                        (float) Math.hypot(view.getWidth(), view.getHeight()),
                        0);
                animatorHide.addListener(new Animator.AnimatorListener() {
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
                animatorHide.setDuration(duration);
                animatorHide.start();
            } else {
                view.setVisibility(View.GONE);
            }
            view.setEnabled(false);
        }
        //显示
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                final Animator animator = ViewAnimationUtils.createCircularReveal(view,
                        x,
                        y,
                        0,
                        (float) Math.hypot(view.getWidth(), view.getHeight()));
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
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
                view.setVisibility(View.VISIBLE);
                if (view.getVisibility() == View.VISIBLE) {
                    animator.setDuration(duration);
                    animator.start();
                    view.setEnabled(true);
                }
            } else {
                view.setVisibility(View.VISIBLE);
                view.setEnabled(true);
            }
        }
    }

    public static void circleAnimator(final View view, int duration) {
        //隐藏
        int x = view.getMeasuredWidth() / 2;
        int y = view.getMeasuredHeight() / 2;
        if (view.getVisibility() == View.VISIBLE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                final Animator animatorHide = ViewAnimationUtils.createCircularReveal(view,
                        x,
                        y,
                        //确定元的半径（算长宽的斜边长，这样半径不会太短也不会很长效果比较舒服）
                        (float) Math.hypot(view.getWidth(), view.getHeight()),
                        0);
                animatorHide.addListener(new Animator.AnimatorListener() {
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
                animatorHide.setDuration(duration);
                animatorHide.start();
            } else {
                view.setVisibility(View.GONE);
            }
            view.setEnabled(false);
        }
        //显示
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                final Animator animator = ViewAnimationUtils.createCircularReveal(view,
                        x,
                        y,
                        0,
                        (float) Math.hypot(view.getWidth(), view.getHeight()));
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
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
                view.setVisibility(View.VISIBLE);
                if (view.getVisibility() == View.VISIBLE) {
                    animator.setDuration(duration);
                    animator.start();
                    view.setEnabled(true);
                }
            } else {
                view.setVisibility(View.VISIBLE);
                view.setEnabled(true);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static Animator createCircularRevealInAnim(@NonNull final View target, int centerX, int centerY) {
        int x = target.getMeasuredWidth();
        int y = target.getMeasuredHeight();
        int r = (int) Math.sqrt(Math.pow(Math.max(centerX, x - centerX), 2) + Math.pow(Math.max(centerY, y - centerY), 2));
        Animator animator = ViewAnimationUtils.createCircularReveal(target, centerX, centerY, 0, r);
        animator.setInterpolator(new DecelerateInterpolator());
        return animator;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static Animator createCircularRevealInAnim(@NonNull final View target,
                                                      @FloatRange(from = 0, to = 1) float centerPercentX,
                                                      @FloatRange(from = 0, to = 1) float centerPercentY) {
        int centerX = (int) (target.getMeasuredWidth() * centerPercentX);
        int centerY = (int) (target.getMeasuredHeight() * centerPercentY);
        return createCircularRevealInAnim(target, centerX, centerY);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static Animator createCircularRevealOutAnim(@NonNull final View target, int centerX, int centerY) {
        int x = target.getMeasuredWidth();
        int y = target.getMeasuredHeight();
        int r = (int) Math.sqrt(Math.pow(Math.max(centerX, x - centerX), 2) + Math.pow(Math.max(centerY, y - centerY), 2));
        Animator animator = ViewAnimationUtils.createCircularReveal(target, centerX, centerY, r, 0);
        animator.setInterpolator(new DecelerateInterpolator());
        return animator;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static Animator createCircularRevealOutAnim(@NonNull final View target,
                                                       @FloatRange(from = 0, to = 1) float centerPercentX,
                                                       @FloatRange(from = 0, to = 1) float centerPercentY) {
        int centerX = (int) (target.getMeasuredWidth() * centerPercentX);
        int centerY = (int) (target.getMeasuredHeight() * centerPercentY);
        return createCircularRevealOutAnim(target, centerX, centerY);
    }

    private static final class JellyInterpolator extends LinearInterpolator {
        private float factor;

        JellyInterpolator() {
            this.factor = 0.15f;
        }

        @Override
        public float getInterpolation(float input) {
            return (float) (Math.pow(2, -10 * input)
                    * Math.sin((input - factor / 4) * (2 * Math.PI) / factor) + 1);
        }
    }

    public static int dip2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5);
    }

}
