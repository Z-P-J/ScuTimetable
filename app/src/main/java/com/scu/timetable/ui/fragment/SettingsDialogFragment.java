package com.scu.timetable.ui.fragment;

import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.leon.lib.settingview.LSettingItem;
import com.scu.timetable.R;
import com.scu.timetable.ui.fragment.base.FullscreenDialogFragment;
import com.scu.timetable.utils.ApkUtil;
import com.scu.timetable.utils.FastBlur;
import com.scu.timetable.utils.TextUtil;
import com.scu.timetable.utils.TimetableHelper;
import com.zpj.popupmenuview.CustomPopupMenuView;

public class SettingsDialogFragment extends FullscreenDialogFragment implements View.OnClickListener, LSettingItem.OnLSettingItemClick {

    private FrameLayout background;

    private float currentAlpha = 0.0f;

    private OnDismissListener onDismissListener;

    private LSettingItem itemShowWeekends;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FrameLayout frameLayout = new FrameLayout(getContext());
        View view = inflater.inflate(R.layout.dialog_fragment_settings, null, false);
        frameLayout.addView(view);
        background = new FrameLayout(getContext());
        background.setBackgroundColor(Color.BLACK);
        background.setAlpha(currentAlpha);
        frameLayout.addView(background);
        initView(view);
        return frameLayout;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
        super.onDismiss(dialog);
    }

    private void initView(View view) {
//        initBackground(view);
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
                "默认星期天为周一。不排除有些人喜欢讲星期一作为周一，所以天骄设置星期一为周一的选项。")
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
        itemShowWeekends.setEnabled(false);

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

        TextView appVersion = view.findViewById(R.id.app_version);
        appVersion.setText("V" + ApkUtil.getVersionName(getContext()));

        String link2 = "https://github.com/Z-P-J/ScuTimetable";
        TextUtil.setSuperlink(view.findViewById(R.id.link_open_source), link2, link2);

        String link = "https://github.com/Z-P-J";
        TextUtil.setSuperlink(view.findViewById(R.id.link_github), link, link);

        TextUtil.setSuperlink(view.findViewById(R.id.link_sjly), "我好像在哪儿见过您", "https://www.shouji.com.cn/user/5544802/home.html");
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

    public void setBackgroudAlpha(float alpha) {
        ValueAnimator animator = ValueAnimator.ofFloat(currentAlpha, alpha);
        currentAlpha = alpha;
//        if (alpha > 0.0f) {
//            animator = ValueAnimator.ofFloat(0.0f, alpha);;
//        } else {
//            ValueAnimator.ofFloat(0.0f, alpha);
//        }
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                background.setAlpha(value);
            }
        });
        animator.start();
    }

    private void showInfoPopupView(View view, final String title, final String content) {

        CustomPopupMenuView.with(getContext(), R.layout.layout_text)
                .setOrientation(LinearLayout.VERTICAL)
//                .setBackgroundAlpha(getActivity(), 0.9f)
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
                .setOnPopupWindowDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        setBackgroudAlpha(0.0f);
                    }
                })
                .show(view);
        setBackgroudAlpha(0.1f);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_back) {
            dismiss();
        }
    }

    @Override
    public void click(View view, boolean isChecked) {
        int id = view.getId();
        if (id == R.id.item_smart_show_weekends) {
            TimetableHelper.toggleSmartShowWeekends();
        } else if (id == R.id.item_monday_is_first_day) {
            TimetableHelper.toggleSundayIsFirstDay();
        } else if (id == R.id.item_show_non_this_week) {
            TimetableHelper.toggleShowNotCurWeek();
        } else if (id == R.id.item_show_weekends) {
            if (TimetableHelper.isSmartShowWeekends()) {
                Toast.makeText(getContext(), "关闭智能显示周末后启用", Toast.LENGTH_SHORT).show();
            } else {
                TimetableHelper.toggleShowWeekends();
            }
        } else if (id == R.id.item_show_time) {
            TimetableHelper.toggleShowTime();
        } else if (id == R.id.item_change_current_week) {
            TimetableHelper.openChangeCurrentWeekDialog(getContext(), null);
        }

        Toast.makeText(getContext(), "" + isChecked, Toast.LENGTH_SHORT).show();
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    public interface OnDismissListener {
        void onDismiss(DialogInterface dialog);
    }

}
