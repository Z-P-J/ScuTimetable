package com.scu.timetable.ui.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.scu.timetable.R;
import com.scu.timetable.ui.fragment.dialog.UpdateDialog;
import com.scu.timetable.utils.UpdateUtil;
import com.zpj.toast.ZToast;
import com.zpj.utils.AppUtils;
import com.zpj.utils.ContextUtils;
import com.zpj.widget.setting.CommonSettingItem;

public class UpdateSettingItem extends CommonSettingItem
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private TextView tvVersion;
    private ImageView ivNew;

    public UpdateSettingItem(Context context) {
        this(context, null);
    }

    public UpdateSettingItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UpdateSettingItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setInfoText("V" + AppUtils.getAppVersionName(context, context.getPackageName()));
    }

    @Override
    public void inflateRightContainer(ViewStub viewStub) {
        viewStub.setLayoutResource(R.layout.layout_update_item);
        View view = viewStub.inflate();
        tvVersion = view.findViewById(R.id.tv_version);
        ivNew = view.findViewById(R.id.iv_new);
        initItem(UpdateUtil.getPrefs().getSharedPreferences());
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        UpdateUtil.getPrefs().registerOnChangeListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        UpdateUtil.getPrefs().unregisterOnChangeListener(this);
    }

    @Override
    public void onItemClick() {
        if (UpdateUtil.hasChecked()) {
            if (UpdateUtil.hasUpdate()) {
                new UpdateDialog().show(getContext());
            } else {
                ZToast.normal("已是最新版");
            }
        } else {
            UpdateUtil.checkUpdate(ContextUtils.getActivity(getContext()));
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if ("has_checked".equals(key)) {
            initItem(sharedPreferences);
        }
    }

    private void initItem(SharedPreferences sharedPreferences) {
        boolean hasChecked = UpdateUtil.hasChecked();
        ivNew.setVisibility(GONE);
        tvVersion.setText("已是最新版");
        if (hasChecked) {
            if (sharedPreferences.getBoolean("has_update", false)) {
                ivNew.setVisibility(VISIBLE);
                tvVersion.setText("新版本V" + sharedPreferences.getString("version_name",
                        AppUtils.getAppVersionName(getContext(), getContext().getPackageName())));
            }
        }
    }

}

