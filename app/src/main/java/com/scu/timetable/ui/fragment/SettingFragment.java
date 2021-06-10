package com.scu.timetable.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import com.deadline.statebutton.StateButton;
import com.scu.timetable.R;
import com.scu.timetable.ui.fragment.base.SkinChangeFragment;
import com.scu.timetable.ui.fragment.dialog.MoreInfoDialog;
import com.scu.timetable.ui.widget.DetailLayout;
import com.scu.timetable.ui.widget.UpdateSettingItem;
import com.scu.timetable.utils.EventBus;
import com.scu.timetable.utils.SuperLinkUtil;
import com.scu.timetable.utils.TimetableHelper;
import com.scu.timetable.utils.TimetableWidgtHelper;
import com.scu.timetable.utils.UpdateUtil;
import com.zpj.fragmentation.dialog.IDialog;
import com.zpj.fragmentation.dialog.ZDialog;
import com.zpj.fragmentation.dialog.impl.AlertDialogFragment;
import com.zpj.toast.ZToast;
import com.zpj.utils.AnimatorUtils;
import com.zpj.utils.AppUtils;
import com.zpj.utils.PrefsHelper;
import com.zpj.widget.setting.CheckableSettingItem;
import com.zpj.widget.setting.CommonSettingItem;
import com.zpj.widget.setting.OnCheckableItemClickListener;
import com.zpj.widget.setting.OnCommonItemClickListener;
import com.zpj.widget.setting.SwitchSettingItem;

public class SettingFragment extends SkinChangeFragment
        implements View.OnClickListener,
        OnCheckableItemClickListener,
        OnCommonItemClickListener {

    private SwitchSettingItem itemShowWeekends;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_settings;
    }

    @Override
    public int getToolbarTitleId() {
        return R.string.text_title_setting;
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        darkStatusBar();
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {

        SwitchSettingItem smartShowWeekends = findViewById(R.id.item_smart_show_weekends);
        smartShowWeekends.setChecked(TimetableHelper.isSmartShowWeekends());
        smartShowWeekends.setOnItemClickListener(this);

        SwitchSettingItem itemMondayIsFirstDay = findViewById(R.id.item_monday_is_first_day);
        itemMondayIsFirstDay.setChecked(!TimetableHelper.sundayIsFirstDay());
        itemMondayIsFirstDay.setOnItemClickListener(this);

        itemShowWeekends = findViewById(R.id.item_show_weekends);
        itemShowWeekends.setChecked(TimetableHelper.isShowWeekendsOrin());
        if (TimetableHelper.isSmartShowWeekends()) {
            itemShowWeekends.setEnabled(false);
        }
        itemShowWeekends.setOnItemClickListener(this);

        SwitchSettingItem itemShowNonThisWeek = findViewById(R.id.item_show_non_this_week);
        itemShowNonThisWeek.setChecked(TimetableHelper.isShowNotCurWeek());
        itemShowNonThisWeek.setOnItemClickListener(this);

        SwitchSettingItem itemShowTime = findViewById(R.id.item_show_time);
        itemShowTime.setChecked(TimetableHelper.isShowTime());
        itemShowTime.setOnItemClickListener(this);

        SwitchSettingItem itemSpeech = findViewById(R.id.item_speech);
        itemSpeech.setChecked(TimetableHelper.isSpeech());
        itemSpeech.setOnItemClickListener(this);

        CommonSettingItem itemChangeCurrentWeek = findViewById(R.id.item_change_current_week);
        itemChangeCurrentWeek.setOnItemClickListener(this);

        CommonSettingItem itemNotification = findViewById(R.id.item_notification);
        itemNotification.setOnItemClickListener(this);

        SwitchSettingItem itemWidgetSmartShowWeekends = findViewById(R.id.item_widget_smart_show_weekends);
        itemWidgetSmartShowWeekends.setChecked(TimetableWidgtHelper.isSmartShowWeekends());
        itemWidgetSmartShowWeekends.setOnItemClickListener(this);

        SwitchSettingItem itemWidgetTransparentMode = findViewById(R.id.item_widget_transparent_mode);
        itemWidgetTransparentMode.setChecked(TimetableWidgtHelper.isTransparentMode());
        itemWidgetTransparentMode.setOnItemClickListener(this);


        CommonSettingItem aboutMeItem = findViewById(R.id.item_about_me);
        aboutMeItem.setOnItemClickListener(this);

        CommonSettingItem openSourceItem = findViewById(R.id.item_open_source);
        openSourceItem.setOnItemClickListener(this);

//        CommonSettingItem checkUpdateItem = findViewById(R.id.item_version_name);
//        checkUpdateItem.setRightText("V" + AppUtils.getAppVersionName(context, context.getPackageName()));

        StateButton btnLogout = findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(this);

        LinearLayout llContainer = findViewById(R.id.ll_container);
        View[] views = new View[llContainer.getChildCount()];
        for (int i = 0; i < llContainer.getChildCount(); i++) {
            views[i] = llContainer.getChildAt(i);
        }
        AnimatorUtils.doDelayShowAnim(500, 50, views);
    }

    @Override
    public void onDestroyView() {
        EventBus.sendUpdateSettingEvent();
        super.onDestroyView();
    }

    private void showInfoPopupView(View view, final String title, final String content) {
        new MoreInfoDialog()
                .setTitle(title)
                .setContent(content)
                .show(context);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_logout) {
            ZDialog.alert()
                    .setTitle("注销登录！")
                    .setContent("注销后需重新登录才能查看课表，确认注销？")
                    .setPositiveButton((fragment, which) -> {
                        if (TimetableHelper.isVisitorMode()) {
                            ZToast.normal("您当前正处于游客模式，无法注销登录！");
                            return;
                        }
                        PrefsHelper.with().putBoolean("logined", false);
                        SettingFragment.this.pop();
                    })
                    .show(context);
        }
    }

    private void goSetting() {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= 26) {
            // android 8.0引导
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("android.provider.extra.APP_PACKAGE", getContext().getPackageName());
        } else if (Build.VERSION.SDK_INT >= 21) {
            // android 5.0-7.0
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", getContext().getPackageName());
            intent.putExtra("app_uid", getContext().getApplicationInfo().uid);
        } else {
            // 其他
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", getContext().getPackageName(), null));
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onItemClick(CheckableSettingItem item) {
        int id = item.getId();
        if (id == R.id.item_smart_show_weekends) {
            TimetableHelper.toggleSmartShowWeekends();
            itemShowWeekends.setEnabled(!TimetableHelper.isSmartShowWeekends());
        } else if (id == R.id.item_monday_is_first_day) {
            TimetableHelper.toggleSundayIsFirstDay();
        } else if (id == R.id.item_show_non_this_week) {
            TimetableHelper.toggleShowNotCurWeek();
        } else if (id == R.id.item_show_weekends) {
            if (TimetableHelper.isSmartShowWeekends()) {
                ZToast.normal("关闭智能显示周末后启用！");
            } else {
                TimetableHelper.toggleShowWeekends();
            }
        } else if (id == R.id.item_show_time) {
            TimetableHelper.toggleShowTime();
        } else if (id == R.id.item_speech) {
            TimetableHelper.toggleSpeech();
        } else if (id == R.id.item_widget_smart_show_weekends) {
            TimetableWidgtHelper.toggleSmartShowWeekends(context);
        } else if (id == R.id.item_widget_transparent_mode) {
            TimetableWidgtHelper.toggleTransparentMode(context);
        }
    }

    @Override
    public void onItemClick(CommonSettingItem item) {
        int id = item.getId();
        if (id == R.id.item_change_current_week) {
            TimetableHelper.openChangeCurrentWeekDialog(context, null);
        } else if (id == R.id.item_notification) {
            goSetting();
        } else if (id == R.id.item_about_me) {
            new AboutMeFragment()
                    .setOnDismissListener(this::darkStatusBar)
                    .show(context);
        } else if (id == R.id.item_open_source) {
            Uri uri = Uri.parse(item.getInfoText());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    public interface OnDismissListener {
        void onDismiss(DialogInterface dialog);
    }

}
