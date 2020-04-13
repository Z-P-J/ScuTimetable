package com.zpj.popup.impl;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.zpj.popup.R;
import com.zpj.popup.XPopup;
import com.zpj.popup.core.BasePopup;
import com.zpj.popup.core.BasePopupView;
import com.zpj.popup.core.CenterPopup;
import com.zpj.popup.core.CenterPopupView;
import com.zpj.popup.interfaces.OnCancelListener;
import com.zpj.popup.interfaces.OnConfirmListener;
import com.zpj.popup.util.XPopupUtils;

public class AlertPopup extends AbstractAlertPopup<AlertPopup> {

    public AlertPopup(@NonNull Context context) {
        super(context);
    }

}
