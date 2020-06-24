package com.scu.timetable.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.deadline.statebutton.StateButton;
import com.felix.atoast.library.AToast;
import com.scu.timetable.R;
import com.scu.timetable.ui.popup.MoreInfoPopup;
import com.scu.timetable.ui.widget.DetailLayout;
import com.scu.timetable.utils.SuperLinkUtil;
import com.scu.timetable.utils.TimetableHelper;
import com.scu.timetable.utils.TimetableWidgtHelper;
import com.scu.timetable.utils.UpdateUtil;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.popup.ZPopup;
import com.zpj.utils.PrefsHelper;
import com.zpj.widget.setting.CheckableSettingItem;
import com.zpj.widget.setting.CommonSettingItem;
import com.zpj.widget.setting.OnCheckableItemClickListener;
import com.zpj.widget.setting.OnCommonItemClickListener;
import com.zpj.widget.setting.SwitchSettingItem;

public class SettingFragment extends BaseFragment
        implements View.OnClickListener,
        OnCheckableItemClickListener,
        OnCommonItemClickListener {

    private OnDismissListener onDismissListener;

    private SwitchSettingItem itemShowWeekends;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_settings;
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {

        SwitchSettingItem smartShowWeekends = view.findViewById(R.id.item_smart_show_weekends);
        smartShowWeekends.setChecked(TimetableHelper.isSmartShowWeekends());
        smartShowWeekends.setOnItemClickListener(this);

        SwitchSettingItem itemMondayIsFirstDay = view.findViewById(R.id.item_monday_is_first_day);
        itemMondayIsFirstDay.setChecked(!TimetableHelper.sundayIsFirstDay());
        itemMondayIsFirstDay.setOnItemClickListener(this);

        itemShowWeekends = view.findViewById(R.id.item_show_weekends);
        itemShowWeekends.setChecked(TimetableHelper.isShowWeekendsOrin());
        if (TimetableHelper.isSmartShowWeekends()) {
            itemShowWeekends.setEnabled(false);
        }
        itemShowWeekends.setOnItemClickListener(this);

        SwitchSettingItem itemShowNonThisWeek = view.findViewById(R.id.item_show_non_this_week);
        itemShowNonThisWeek.setChecked(TimetableHelper.isShowNotCurWeek());
        itemShowNonThisWeek.setOnItemClickListener(this);

        SwitchSettingItem itemShowTime = view.findViewById(R.id.item_show_time);
        itemShowTime.setChecked(TimetableHelper.isShowTime());
        itemShowTime.setOnItemClickListener(this);

        SwitchSettingItem itemSpeech = view.findViewById(R.id.item_speech);
        itemSpeech.setChecked(TimetableHelper.isSpeech());
        itemSpeech.setOnItemClickListener(this);

        CommonSettingItem itemChangeCurrentWeek = view.findViewById(R.id.item_change_current_week);
        itemChangeCurrentWeek.setOnItemClickListener(this);

        CommonSettingItem itemNotification = view.findViewById(R.id.item_notification);
        itemNotification.setOnItemClickListener(this);

        SwitchSettingItem itemWidgetSmartShowWeekends = view.findViewById(R.id.item_widget_smart_show_weekends);
        itemWidgetSmartShowWeekends.setChecked(TimetableWidgtHelper.isSmartShowWeekends());
        itemWidgetSmartShowWeekends.setOnItemClickListener(this);

        SwitchSettingItem itemWidgetTransparentMode = view.findViewById(R.id.item_widget_transparent_mode);
        itemWidgetTransparentMode.setChecked(TimetableWidgtHelper.isTransparentMode());
        itemWidgetTransparentMode.setOnItemClickListener(this);

        StateButton btnLogout = view.findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(this);

        DetailLayout appVersion = view.findViewById(R.id.app_version);
        appVersion.setContent("V" + UpdateUtil.getVersionName(getContext()));

        String link2 = "https://github.com/Z-P-J/ScuTimetable";
        DetailLayout linkOpenSource = view.findViewById(R.id.link_open_source);
        SuperLinkUtil.setSuperLink(linkOpenSource.getContentTextView(), link2, link2);

        String link = "https://github.com/Z-P-J";
        DetailLayout linkGithub = view.findViewById(R.id.link_github);
        SuperLinkUtil.setSuperLink(linkGithub.getContentTextView(), link, link);

        DetailLayout linkSjly = view.findViewById(R.id.link_sjly);
        SuperLinkUtil.setSuperLink(linkSjly.getContentTextView(), "天蓝蓝的", "https://www.shouji.com.cn/user/5544802/home.html");
    }

    @Override
    public void onDestroyView() {
        if (onDismissListener != null) {
            onDismissListener.onDismiss(null);
        }
        super.onDestroyView();
    }

    private void showInfoPopupView(View view, final String title, final String content) {
        new MoreInfoPopup(context)
                .setTitle(title)
                .setContent(content)
                .show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_logout) {
            ZPopup.alert(context)
                    .setTitle("注销登录！")
                    .setContent("注销后需重新登录才能查看课表，确认注销？")
                    .setConfirmButton(popup -> {
                        if (TimetableHelper.isVisitorMode()) {
                            AToast.normal("您当前正处于游客模式，无法注销登录！");
                            return;
                        }
                        PrefsHelper.with().putBoolean("logined", false);
                        pop();
                    })
                    .show();
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

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
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
                AToast.normal("关闭智能显示周末后启用！");
            } else {
                TimetableHelper.toggleShowWeekends();
            }
        } else if (id == R.id.item_show_time) {
            TimetableHelper.toggleShowTime();
        } else if (id == R.id.item_speech) {
            TimetableHelper.toggleSpeech();
        } else if (id == R.id.item_widget_smart_show_weekends) {
            TimetableWidgtHelper.toggleSmartShowWeekends(getContext());
        } else if (id == R.id.item_widget_transparent_mode) {
            TimetableWidgtHelper.toggleTransparentMode(getContext());
        }
    }

    @Override
    public void onItemClick(CommonSettingItem item) {
        int id = item.getId();
        if (id == R.id.item_change_current_week) {
            TimetableHelper.openChangeCurrentWeekDialog(getContext(), null);
        } else if (id == R.id.item_notification) {
            goSetting();
        }
    }

    public interface OnDismissListener {
        void onDismiss(DialogInterface dialog);
    }

}
