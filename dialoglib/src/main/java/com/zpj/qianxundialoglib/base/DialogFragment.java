package com.zpj.qianxundialoglib.base;

import android.animation.Animator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.StyleRes;
import android.support.annotation.RestrictTo.Scope;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.zpj.qianxundialoglib.utils.AnimHelper;

/**
 * @author Z-P-J
 * @date 2019/5/16 21:36
 */
public class DialogFragment extends Fragment implements OnCancelListener, OnDismissListener {
    public static final int STYLE_NORMAL = 0;
    public static final int STYLE_NO_TITLE = 1;
    public static final int STYLE_NO_FRAME = 2;
    public static final int STYLE_NO_INPUT = 3;
    private static final String SAVED_DIALOG_STATE_TAG = "android:savedDialogState";
    private static final String SAVED_STYLE = "android:style";
    private static final String SAVED_THEME = "android:theme";
    private static final String SAVED_CANCELABLE = "android:cancelable";
    private static final String SAVED_SHOWS_DIALOG = "android:showsDialog";
    private static final String SAVED_BACK_STACK_ID = "android:backStackId";
    int mStyle = 0;
    int mTheme = 0;
    boolean mCancelable = true;
    boolean mShowsDialog = true;
    int mBackStackId = -1;
    OutsideClickDialog mDialog;
    boolean mViewDestroyed;
    boolean mDismissed;
    boolean mShownByMe;

    private boolean mCanceledOnTouchOutside = true;

    private Animator mContentInAnimator;
    private Animator mBackgroundInAnimator;
    private Animator mContentOutAnimator;
    private Animator mBackgroundOutAnimator;

    public DialogFragment() {
    }

    public void setStyle(int style, @StyleRes int theme) {
        this.mStyle = style;
        if (this.mStyle == 2 || this.mStyle == 3) {
            this.mTheme = 16973913;
        }

        if (theme != 0) {
            this.mTheme = theme;
        }

    }

    public void show(FragmentManager manager, String tag) {
        this.mDismissed = false;
        this.mShownByMe = true;
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commit();
    }

    public int show(FragmentTransaction transaction, String tag) {
        this.mDismissed = false;
        this.mShownByMe = true;
        transaction.add(this, tag);
        this.mViewDestroyed = false;
        this.mBackStackId = transaction.commit();
        return this.mBackStackId;
    }

    public void showNow(FragmentManager manager, String tag) {
        this.mDismissed = false;
        this.mShownByMe = true;
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitNow();
    }

    public void dismiss() {
        initContentOutAnimator(getView());
        if (mContentOutAnimator != null) {
            if (!mContentOutAnimator.isRunning()) {
                mContentOutAnimator.start();
            }
        } else {
            dismissInternal(false);
        }
    }

    public void dismissAllowingStateLoss() {
        this.dismissInternal(true);
    }

    public void dismissWithoutAnim() {
        this.dismissInternal(false);
    }

    void dismissInternal(boolean allowStateLoss) {
        if (!this.mDismissed) {
            this.mDismissed = true;
            this.mShownByMe = false;
            if (this.mDialog != null) {
                this.mDialog.dismiss();
            }

            this.mViewDestroyed = true;
            if (this.mBackStackId >= 0) {
                this.getFragmentManager().popBackStack(this.mBackStackId, 1);
                this.mBackStackId = -1;
            } else {
                FragmentTransaction ft = this.getFragmentManager().beginTransaction();
                ft.remove(this);
                if (allowStateLoss) {
                    ft.commitAllowingStateLoss();
                } else {
                    ft.commit();
                }
            }

        }
    }

    public Dialog getDialog() {
        return this.mDialog;
    }

    @StyleRes
    public int getTheme() {
        return this.mTheme;
    }

    public void setCancelable(boolean cancelable) {
        this.mCancelable = cancelable;
        Log.d("DialogFragment", "cancelable=" + cancelable);
        if (this.mDialog != null) {
            this.mDialog.setCancelable(cancelable);
        }
    }

    public boolean isCancelable() {
        Log.d("DialogFragment", "mCancelable=" + mCancelable);
        return mCancelable;
    }

    public void setCanceledOnTouchOutside(boolean mCanceledOnTouchOutside) {
        this.mCanceledOnTouchOutside = mCanceledOnTouchOutside;
    }

    public boolean isCanceledOnTouchOutside() {
        return mCanceledOnTouchOutside;
    }

    public void setShowsDialog(boolean showsDialog) {
        this.mShowsDialog = showsDialog;
    }

    public boolean getShowsDialog() {
        return this.mShowsDialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!this.mShownByMe) {
            this.mDismissed = false;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (!this.mShownByMe && !this.mDismissed) {
            this.mDismissed = true;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.mShowsDialog = this.mContainerId == 0;
        this.mShowsDialog = true;
        if (savedInstanceState != null) {
            this.mStyle = savedInstanceState.getInt(SAVED_STYLE, 0);
            this.mTheme = savedInstanceState.getInt(SAVED_THEME, 0);
            this.mCancelable = savedInstanceState.getBoolean(SAVED_CANCELABLE, true);
            this.mShowsDialog = savedInstanceState.getBoolean(SAVED_SHOWS_DIALOG, this.mShowsDialog);
            this.mBackStackId = savedInstanceState.getInt(SAVED_BACK_STACK_ID, -1);
        }

    }

    @Override
    @NonNull
    public LayoutInflater onGetLayoutInflater(@Nullable Bundle savedInstanceState) {
        if (!this.mShowsDialog) {
            return super.onGetLayoutInflater(savedInstanceState);
        } else {
            this.mDialog = this.onCreateDialog(savedInstanceState);
            this.mDialog.setOnTouchOutsideListener(new OutsideClickDialog.OnTouchOutsideListener() {
                @Override
                public void onTouchOutside() {
                    if (mCanceledOnTouchOutside) {
                        mDialog.setOnTouchOutsideListener(null);
                        dismiss();
                    }
                }
            });
            this.setupDialog(this.mDialog, this.mStyle);
            return (LayoutInflater)this.mDialog.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public void setupDialog(Dialog dialog, int style) {
        switch(style) {
            case 3:
                dialog.getWindow().addFlags(24);
            case 1:
            case 2:
                dialog.requestWindowFeature(1);
            default:
        }
    }

    private void initContentInAnimator(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mContentInAnimator = AnimHelper.createCircularRevealInAnim(view);
        } else {
            mContentInAnimator = AnimHelper.createZoomInAnim(view);
        }
        mContentInAnimator.setInterpolator(new DecelerateInterpolator());
    }

    private void initContentOutAnimator(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mContentOutAnimator = AnimHelper.createCircularRevealOutAnim(view);
        } else {
            mContentOutAnimator = AnimHelper.createZoomOutAnim(view);
        }
        mContentOutAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) { }

            @Override
            public void onAnimationEnd(Animator animation) {
                dismissInternal(false);
            }

            @Override
            public void onAnimationCancel(Animator animation) { }

            @Override
            public void onAnimationRepeat(Animator animation) { }
        });
        mContentOutAnimator.setInterpolator(new DecelerateInterpolator());
    }

    @NonNull
    public OutsideClickDialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        Toast.makeText(getContext(), "onCreateDialog", Toast.LENGTH_SHORT).show();
        return new OutsideClickDialog(this.getActivity(), this.getTheme());
    }

    @Override
    public void onCancel(DialogInterface dialog) {
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (!this.mViewDestroyed) {
            this.dismissInternal(true);
        }
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (view.getViewTreeObserver().isAlive()) {
                    view.getViewTreeObserver().removeOnPreDrawListener(this);
                }
//                int x = view.getMeasuredWidth();
//                int y = view.getMeasuredHeight();
//                int r = (int) Math.sqrt(Math.pow(x / 2, 2) + Math.pow(y / 2, 2));
//                Animator animator = ViewAnimationUtils.createCircularReveal(view, x / 2, y / 2, 0, r);
//                animator.setInterpolator(new DecelerateInterpolator());
//                animator.start();
                initContentInAnimator(view);
                if (mContentInAnimator != null) {
                    mContentInAnimator.start();
                }
                return true;
            }
        });
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            //如果isCancelable()是false 则会屏蔽物理返回键
//            dialog.setCancelable(isCancelable());
            //如果isCancelableOutside()为false 点击屏幕外Dialog不会消失；反之会消失
//            dialog.setCanceledOnTouchOutside(isCancelableOutside());
            //如果isCancelable()设置的是false 会屏蔽物理返回键
            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    boolean flag = keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN;
                    if (flag && mCancelable) {
//                        initContentOutAnimator(view);
                        dismiss();
                    }
                    return flag;
                }
            });
        }
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (this.mShowsDialog) {
            View view = this.getView();
            if (view != null) {
                if (view.getParent() != null) {
                    throw new IllegalStateException("DialogFragment can not be attached to a container view");
                }

                this.mDialog.setContentView(view);
            }

            Activity activity = this.getActivity();
            if (activity != null) {
                this.mDialog.setOwnerActivity(activity);
            }

            this.mDialog.setCancelable(this.mCancelable);
            this.mDialog.setOnCancelListener(this);
            this.mDialog.setOnDismissListener(this);
            if (savedInstanceState != null) {
                Bundle dialogState = savedInstanceState.getBundle("android:savedDialogState");
                if (dialogState != null) {
                    this.mDialog.onRestoreInstanceState(dialogState);
                }
            }

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (this.mDialog != null) {
            this.mViewDestroyed = false;
            this.mDialog.show();
        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (this.mDialog != null) {
            Bundle dialogState = this.mDialog.onSaveInstanceState();
            outState.putBundle("android:savedDialogState", dialogState);
        }

        if (this.mStyle != 0) {
            outState.putInt(SAVED_STYLE, this.mStyle);
        }

        if (this.mTheme != 0) {
            outState.putInt(SAVED_THEME, this.mTheme);
        }

        if (!this.mCancelable) {
            outState.putBoolean(SAVED_CANCELABLE, this.mCancelable);
        }

        if (!this.mShowsDialog) {
            outState.putBoolean(SAVED_SHOWS_DIALOG, this.mShowsDialog);
        }

        if (this.mBackStackId != -1) {
            outState.putInt(SAVED_BACK_STACK_ID, this.mBackStackId);
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (this.mDialog != null) {
            this.mDialog.hide();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (this.mDialog != null) {
            this.mViewDestroyed = true;
            this.mDialog.dismiss();
            this.mDialog = null;
        }

    }
}
