package com.scu.timetable.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.deadline.statebutton.StateButton;
import com.felix.atoast.library.AToast;
import com.leon.lib.settingview.LSettingItem;
import com.scu.timetable.R;
import com.scu.timetable.ui.fragment.base.BaseFragment;
import com.scu.timetable.ui.widget.DetailLayout;
import com.scu.timetable.utils.FastBlur;
import com.scu.timetable.utils.TextUtil;
import com.scu.timetable.utils.TimetableHelper;
import com.scu.timetable.utils.TimetableWidgtHelper;
import com.scu.timetable.utils.UpdateUtil;
import com.scu.timetable.utils.content.SPHelper;
import com.zpj.popupmenuview.CustomPopupMenuView;
import com.zpj.zdialog.ZAlertDialog;
import com.zpj.zdialog.base.IDialog;

public class SettingFragment extends BaseFragment implements View.OnClickListener, LSettingItem.OnLSettingItemClick {

    private OnDismissListener onDismissListener;

    private LSettingItem itemShowWeekends;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_settings;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        ImageView btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);

        LSettingItem smartShowWeekends = view.findViewById(R.id.item_smart_show_weekends);
        smartShowWeekends.setChecked(TimetableHelper.isSmartShowWeekends());
        smartShowWeekends.setmOnLSettingItemClick(this);
        smartShowWeekends.setmOnBtnInfoClick(v -> showInfoPopupView(v,
                "关于智能显示周末",
                "开启该功能后将只会在当前时间为周末时才显示周末的课程，当前时间不为周末时则隐藏周末的课程。该选项启用后将不能使用“显示周末”选项。")
        );

        LSettingItem itemMondayIsFirstDay = view.findViewById(R.id.item_monday_is_first_day);
        itemMondayIsFirstDay.setChecked(!TimetableHelper.sundayIsFirstDay());
        itemMondayIsFirstDay.setmOnLSettingItemClick(this);
        itemMondayIsFirstDay.setmOnBtnInfoClick(v -> showInfoPopupView(v,
                "关于设置星期一为周一",
                "默认星期天为周一。不排除有些人喜欢将星期一作为周一，所以增加设置星期一为周一的选项。")
        );

        itemShowWeekends = view.findViewById(R.id.item_show_weekends);
        itemShowWeekends.setChecked(TimetableHelper.isShowWeekendsOrin());
        if (TimetableHelper.isSmartShowWeekends()) {
            itemShowWeekends.setEnable(false);
        }
        itemShowWeekends.setmOnLSettingItemClick(this);
        itemShowWeekends.setmOnBtnInfoClick(v -> showInfoPopupView(v,
                "关于显示周末",
                "显示周末。")
        );

        LSettingItem itemShowNonThisWeek = view.findViewById(R.id.item_show_non_this_week);
        itemShowNonThisWeek.setChecked(TimetableHelper.isShowNotCurWeek());
        itemShowNonThisWeek.setmOnLSettingItemClick(this);
        itemShowNonThisWeek.setmOnBtnInfoClick(v -> showInfoPopupView(v,
                "关于显示非本周课程",
                "开启该选项将显示不在本周上课的课程。")
        );

        LSettingItem itemShowTime = view.findViewById(R.id.item_show_time);
        itemShowTime.setChecked(TimetableHelper.isShowTime());
        itemShowTime.setmOnLSettingItemClick(this);
        itemShowTime.setmOnBtnInfoClick(v -> showInfoPopupView(v,
                "关于显示节次时间",
                "开启该选项将在侧边栏显示该节课的上课时间。")
        );

        LSettingItem itemChangeCurrentWeek = view.findViewById(R.id.item_change_current_week);
        itemChangeCurrentWeek.setmOnLSettingItemClick(this);

        LSettingItem itemNotification = view.findViewById(R.id.item_notification);
        itemNotification.setmOnLSettingItemClick(this);

        LSettingItem itemWidgetSmartShowWeekends = view.findViewById(R.id.item_widget_smart_show_weekends);
        itemWidgetSmartShowWeekends.setChecked(TimetableWidgtHelper.isSmartShowWeekends());
        itemWidgetSmartShowWeekends.setmOnLSettingItemClick(this);
        itemWidgetSmartShowWeekends.setmOnBtnInfoClick(v -> showInfoPopupView(v,
                "关于桌面插件的智能显示周末",
                "桌面插件默认开启智能显示周末")
        );

        LSettingItem itemWidgetTransparentMode = view.findViewById(R.id.item_widget_transparent_mode);
        itemWidgetTransparentMode.setChecked(TimetableWidgtHelper.isTransparentMode());
        itemWidgetTransparentMode.setmOnLSettingItemClick(this);
        itemWidgetTransparentMode.setmOnBtnInfoClick(v -> showInfoPopupView(v,
                "关于透明模式",
                "炫酷的透明模式，并且将充分利用桌面空间来显示您的课程")
        );

        StateButton btnLogout = view.findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(this);

        DetailLayout appVersion = view.findViewById(R.id.app_version);
        appVersion.setContent("V" + UpdateUtil.getVersionName(getContext()));

        String link2 = "https://github.com/Z-P-J/ScuTimetable";
        DetailLayout linkOpenSource = view.findViewById(R.id.link_open_source);
        TextUtil.setSuperlink(linkOpenSource.getContentTextView(), link2, link2);

        String link = "https://github.com/Z-P-J";
        DetailLayout linkGithub = view.findViewById(R.id.link_github);
        TextUtil.setSuperlink(linkGithub.getContentTextView(), link, link);

        DetailLayout linkSjly = view.findViewById(R.id.link_sjly);
        TextUtil.setSuperlink(linkSjly.getContentTextView(), "我好像在哪儿见过您", "https://www.shouji.com.cn/user/5544802/home.html");
    }

    @Override
    public void onDestroyView() {
        if (onDismissListener != null) {
            onDismissListener.onDismiss(null);
        }
        super.onDestroyView();
    }

    private void initBackground(View view) {
        LinearLayout linearLayout = view.findViewById(R.id.container);
        Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.background_login);

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(mBitmap,
                mBitmap.getWidth() / 4,
                mBitmap.getHeight() / 4,
                false);
        Bitmap blurBitmap = FastBlur.doBlur(scaledBitmap, 20, true);

        linearLayout.setBackground(new BitmapDrawable(null, blurBitmap));
    }

    private void showInfoPopupView(View view, final String title, final String content) {
        CustomPopupMenuView.with(getContext(), R.layout.layout_text)
                .setOrientation(LinearLayout.VERTICAL)
                .setBackgroundAlpha(getActivity(), 0.8f, 500)
                .setPopupViewBackgroundColor(Color.parseColor("#eeffffff"))
//                        .setAnimationTranslationShow(EasyDialog.DIRECTION_X, 350, 100, 0)
//                        .setAnimationTranslationShow(EasyDialog.DIRECTION_Y, 350, -100, 0)
                .setAnimationAlphaShow(350, 0.0f, 1.0f)
                .setAnimationAlphaDismiss(350, 1.0f, 0.0f)
                .initViews(
                        1,
                        (popupMenuView, itemView, position) -> {
                            TextView titleView = itemView.findViewById(R.id.title);
                            titleView.setText(title);
                            TextView contentView = itemView.findViewById(R.id.content);

                            StringBuilder content2 = new StringBuilder(content);
                            if (title.length() >= content2.length()) {
                                for (int i = 0; i < title.length() * 4; i++) {
                                    content2.append(" ");
                                }
                            }
                            contentView.setText(content2.toString());
                            ImageView btnClose = itemView.findViewById(R.id.btn_close);
                            btnClose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    popupMenuView.dismiss();

                                }
                            });
                        })
                .show(view);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_back) {
            pop();
        } else if (id == R.id.btn_logout){
            ZAlertDialog.with(getContext())
                    .setTitle("注销登录！")
                    .setTitleTextColor(Color.RED)
                    .setContent("注销后需重新登录才能查看课表，确认注销？")
                    .setPositiveButton(new IDialog.OnClickListener() {
                        @Override
                        public void onClick(IDialog dialog) {
                            if (TimetableHelper.isVisitorMode()) {
                                AToast.normal("您当前正处于游客模式，无法注销登录！");
                                return;
                            }
                            SPHelper.putBoolean("logined", false);
                            pop();
                        }
                    })
                    .setPositiveButtonTextColor(Color.RED)
                    .show();
        }
    }

    @Override
    public void click(View view, boolean isChecked) {
        int id = view.getId();
        if (id == R.id.item_smart_show_weekends) {
            TimetableHelper.toggleSmartShowWeekends();
            itemShowWeekends.setEnable(!TimetableHelper.isSmartShowWeekends());
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
        } else if (id == R.id.item_change_current_week) {
            TimetableHelper.openChangeCurrentWeekDialog(getContext(), null);
        } else if (id == R.id.item_notification) {
            goSetting();
        }else if (id == R.id.item_widget_smart_show_weekends) {
            TimetableWidgtHelper.toggleSmartShowWeekends(getContext());
        } else if (id == R.id.item_widget_transparent_mode) {
            TimetableWidgtHelper.toggleTransparentMode(getContext());
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

    public interface OnDismissListener {
        void onDismiss(DialogInterface dialog);
    }

}
