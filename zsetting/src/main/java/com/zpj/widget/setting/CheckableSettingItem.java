package com.zpj.widget.setting;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewStub;

import com.zpj.widget.switcher.BaseSwitcher;
import com.zpj.widget.switcher.OnCheckedChangeListener;

public abstract class CheckableSettingItem extends ZSettingItem {

    protected BaseSwitcher switcher;

    private boolean isChecked;

    private OnCheckableItemClickListener listener;

    CheckableSettingItem(Context context) {
        this(context, null);
    }

    CheckableSettingItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    CheckableSettingItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onInflate(ViewStub stub, View inflated) {
        super.onInflate(stub, inflated);
        if (stub == vsRightContainer && inflated instanceof BaseSwitcher) {
            switcher = (BaseSwitcher) inflated;
            switcher.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick();
                }
            });
        }
    }

    @Override
    public void onItemClick() {
        setChecked(!isChecked);
        if (listener != null) {
            listener.onItemClick(this);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (switcher != null) {
            switcher.setEnabled(enabled);
        }
    }

    public void setOnItemClickListener(OnCheckableItemClickListener listener) {
        this.listener = listener;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        setChecked(isChecked, true);
    }

    public void setChecked(boolean isChecked, boolean withAnimation) {
        this.isChecked = isChecked;
        switcher.setChecked(isChecked, withAnimation);
    }
}

