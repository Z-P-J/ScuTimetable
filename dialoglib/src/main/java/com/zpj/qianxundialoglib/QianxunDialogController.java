package com.zpj.qianxundialoglib;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zpj.dialoglib.R;

import java.lang.ref.WeakReference;

/**
 * Created by mq on 2018/9/1 上午10:58
 * mqcoder90@gmail.com
 */

public class QianxunDialogController {

    private int layoutRes;
    private int dialogWidth;
    private int dialogHeight;
    private float dimAmount = 0.2f;
    private int gravity = Gravity.CENTER;
    private boolean isCancelableOutside = true;
    private boolean cancelable;
    private int animRes;
    private View dialogView;
    private IDialog.OnClickListener mPositiveButtonListener;
    private IDialog.OnClickListener mNegativeButtonListener;
    private WeakReference<IDialog> mDialog;
    private String titleStr;//默认标题
    private CharSequence contentStr;//默认内容
    private String positiveStr;//右边按钮文字
    private String negativeStr;//左边按钮文字
    private boolean showBtnLeft, showBtnRight;
    int titleTextColor;
    int contentTextColor;
    int positiveStrColor;
    int negativeStrColor;


    private Button btnOk, btnCancel;

    QianxunDialogController(IDialog dialog) {
        mDialog = new WeakReference<>(dialog);
    }

    int getAnimRes() {
        return animRes;
    }

    int getLayoutRes() {
        return layoutRes;
    }

    void setLayoutRes(int layoutRes) {
        this.layoutRes = layoutRes;
    }

    int getDialogWidth() {
        return dialogWidth;
    }

    int getDialogHeight() {
        return dialogHeight;
    }

    float getDimAmount() {
        return dimAmount;
    }

    public int getGravity() {
        return gravity;
    }

    boolean isCancelableOutside() {
        return isCancelableOutside;
    }

    boolean isCancelable() {
        return cancelable;
    }

    private void setDialogView(View dialogView) {
        this.dialogView = dialogView;
    }

    View getDialogView() {
        return dialogView;
    }

    void setChildView(View view) {
        setDialogView(view);
        dealDefaultDialog(mPositiveButtonListener, mNegativeButtonListener, titleStr,
                contentStr, showBtnLeft, negativeStr, showBtnRight, positiveStr, titleTextColor, contentTextColor, positiveStrColor, negativeStrColor);
    }

    private void dealDefaultDialog(IDialog.OnClickListener positiveBtnListener, IDialog.OnClickListener negativeBtnListener, String titleStr, CharSequence contentStr,
                                   boolean showBtnLeft, String negativeStr, boolean showBtnRight, String positiveStr, int titleTextColor, int contentTextColor, int positiveStrColor, int negativeStrColor) {
        if (dialogView == null) {
            return;
        }
        this.mNegativeButtonListener = negativeBtnListener;
        this.mPositiveButtonListener = positiveBtnListener;
        btnOk = dialogView.findViewById(R.id.btn_ok);
        btnCancel = dialogView.findViewById(R.id.btn_cancel);
        if (btnOk != null && !TextUtils.isEmpty(positiveStr)) {
            btnOk.setVisibility(showBtnRight ? View.VISIBLE : View.GONE);
            btnOk.setText(positiveStr);
            btnOk.setOnClickListener(mButtonHandler);
            btnOk.setTextColor(positiveStrColor);
        }
        if (btnCancel != null) {
            btnCancel.setVisibility(showBtnLeft ? View.VISIBLE : View.GONE);
            btnCancel.setText(negativeStr);
            btnCancel.setOnClickListener(mButtonHandler);
            btnCancel.setTextColor(negativeStrColor);
        }
        TextView tvTitle = dialogView.findViewById(R.id.dialog_title);
        TextView tvContent = dialogView.findViewById(R.id.dialog_content);
        if (tvTitle != null) {
            tvTitle.setVisibility(TextUtils.isEmpty(titleStr) ? View.GONE : View.VISIBLE);
            tvTitle.setText(titleStr);
            tvTitle.setTextColor(titleTextColor);
        }
        if (tvContent != null) {
            tvContent.setVisibility(TextUtils.isEmpty(contentStr) ? View.GONE : View.VISIBLE);
            tvContent.setText(contentStr);
            tvContent.setTextColor(contentTextColor);
        }

    }

    private final View.OnClickListener mButtonHandler = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == btnCancel) {
                if (mDialog.get() == null) {
                    return;
                }
                if (mNegativeButtonListener != null) {
                    mNegativeButtonListener.onClick(mDialog.get());
                }
            } else if (view == btnOk) {
                if (mDialog.get() == null) {
                    return;
                }
                if (mPositiveButtonListener != null) {
                    mPositiveButtonListener.onClick(mDialog.get());
                }
            }
        }
    };

    public static class QianxunParams {
        FragmentManager fragmentManager;
        int layoutRes;
        int dialogWidth;
        int dialogHeight;
        float dimAmount = 0.2f;
        public int gravity = Gravity.CENTER;
        boolean isCancelableOutside = true;
        boolean cancelable = false;
        View dialogView;
        Context context;
        IDialog.OnClickListener positiveBtnListener;
        IDialog.OnClickListener negativeBtnListener;
        //默认标题
        String titleStr;
        int titleTextColor = Color.BLACK;
        //默认内容
        CharSequence contentStr;
        int contentTextColor = Color.BLACK;
        //右边按钮文字
        String positiveStr;
        int positiveStrColor = Color.BLACK;
        //左边按钮文字
        String negativeStr;
        int negativeStrColor = Color.BLACK;
        boolean showBtnLeft = true, showBtnRight = true;
        //Dialog动画style
        int animRes;

        void apply(QianxunDialogController controller) {
            controller.dimAmount = dimAmount;
            controller.gravity = gravity;
            controller.isCancelableOutside = isCancelableOutside;
            controller.cancelable = cancelable;
            controller.animRes = animRes;
            controller.titleStr = titleStr;
            controller.contentStr = contentStr;
            controller.positiveStr = positiveStr;
            controller.negativeStr = negativeStr;
            controller.showBtnLeft = showBtnLeft;
            controller.showBtnRight = showBtnRight;
            controller.mPositiveButtonListener = positiveBtnListener;
            controller.mNegativeButtonListener = negativeBtnListener;
            controller.titleTextColor = titleTextColor;
            controller.contentTextColor = contentTextColor;
            controller.negativeStrColor = negativeStrColor;
            controller.positiveStrColor = positiveStrColor;
            if (layoutRes > 0) {
                controller.setLayoutRes(layoutRes);
            } else if (dialogView != null) {
                controller.dialogView = dialogView;
            } else {
                throw new IllegalArgumentException("Dialog View can't be null");
            }
            if (dialogWidth > 0) {
                controller.dialogWidth = dialogWidth;
            }
            if (dialogHeight > 0) {
                controller.dialogHeight = dialogHeight;
            }
        }

    }

}
