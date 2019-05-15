package com.zpj.qianxundialoglib;

import android.content.Context;

/**
 * @author Z-P-J
 * @date 2019/5/15 23:10
 */
public class QXAlertDialog {

    private Context context;

    private String title;

    private String content;

    private String negativBtnStr;

    private String positiveBtnStr;

    IDialog.OnClickListener positiveBtnListener;
    IDialog.OnClickListener negativeBtnListener;

    private QXAlertDialog(Context context) {
        this.context = context;
    }

    public static QXAlertDialog with(Context context) {
        return new QXAlertDialog(context);
    }

    public QXAlertDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public QXAlertDialog setContent(String content) {
        this.content = content;
        return this;
    }

    public QXAlertDialog setNegativeButton(IDialog.OnClickListener onclickListener) {
        return setNegativeButton("取消", onclickListener);
    }

    public QXAlertDialog setNegativeButton(String btnStr, IDialog.OnClickListener onclickListener) {
        this.negativBtnStr = btnStr;
        this.negativeBtnListener = onclickListener;
        return this;
    }

    public QXAlertDialog setPositiveButton(IDialog.OnClickListener onclickListener) {
        return setPositiveButton("确定", onclickListener);
    }

    public QXAlertDialog setPositiveButton(String btnStr, IDialog.OnClickListener onclickListener) {
        this.positiveBtnStr = btnStr;
        this.positiveBtnListener = onclickListener;
        return this;
    }

}
