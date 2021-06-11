package com.scu.timetable.ui.fragment;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scu.timetable.R;
import com.scu.timetable.bean.ScuSubject;
import com.scu.timetable.bean.SemesterInfo;
import com.scu.timetable.ui.activity.LoginActivity;
import com.scu.timetable.ui.fragment.base.SkinChangeFragment;
import com.scu.timetable.ui.fragment.dialog.RefreshDialog;
import com.scu.timetable.ui.fragment.dialog.SubjectDetailDialog2;
import com.scu.timetable.ui.widget.SolidArrowView;
import com.scu.timetable.ui.widget.TimetableView;
import com.scu.timetable.ui.widget.WeekView;
import com.scu.timetable.utils.EventBus;
import com.scu.timetable.utils.TimetableHelper;
import com.zpj.fragmentation.dialog.impl.AttachListDialogFragment;
import com.zpj.fragmentation.dialog.impl.BottomDragSelectDialogFragment;
import com.zpj.utils.PrefsHelper;

import java.util.List;

/**
 * @author Z-P-J
 */
public final class MainFragment extends SkinChangeFragment implements View.OnClickListener {

    private final static String TAG = "MainFragment";

    private LinearLayout mContaienr;
    private TimetableView timetableView;
    private WeekView mWeekView;
    private SolidArrowView mArrowView;
    private TextView mTvTitle;

    private int currentWeek;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    protected boolean supportSwipeBack() {
        return false;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.onRefresh(this, s -> {
            initData();
        });
        EventBus.onUpdateSetting(this, s -> {
            if (!TimetableHelper.isVisitorMode() && !PrefsHelper.with().getBoolean("logined", false)) {
                startActivity(new Intent(getContext(), LoginActivity.class));
                _mActivity.finish();
                return;
            }
            boolean sundayIsFirstDay = TimetableHelper.sundayIsFirstDay();
            boolean showWeekends = TimetableHelper.isShowWeekends();
            boolean showNotCurWeek = TimetableHelper.isShowNotCurWeek();
            boolean showTime = TimetableHelper.isShowTime();
            currentWeek = TimetableHelper.getCurrentWeek();

            mTvTitle.setText("第" + currentWeek + "周");
            if (mWeekView != null) {
                mWeekView.curWeek(currentWeek).updateView();
            }

            timetableView.setSundayIsFirstDay(sundayIsFirstDay);
            timetableView.setShowWeekends(showWeekends);
            timetableView.setCurrentWeek(currentWeek);
            timetableView.setShowNotCurrentWeek(showNotCurWeek);
            timetableView.setShowTime(showTime);
            timetableView.removeAllViews();
            timetableView.requestLayout();
        });
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {

        currentWeek = TimetableHelper.getCurrentWeek();

        mContaienr = findViewById(R.id.ll_container);

        ImageView settings = view.findViewById(R.id.settins);
        settings.setOnClickListener(this);
        LinearLayout layout = view.findViewById(R.id.id_layout);
        layout.setOnClickListener(this);

        mArrowView = findViewById(R.id.arrow_view);
        mTvTitle = findViewById(R.id.tv_title);

        initData();
    }

    private List<ScuSubject> mSubjects;

    private void initData() {
        mTvTitle.setText("第" + currentWeek + "周");

        mSubjects = TimetableHelper.getSubjects(context);
        if (mWeekView != null) {
            mWeekView.data(mSubjects).showView();
        }

        timetableView = findViewById(R.id.timetable_view);
        timetableView.setOnItemClickListener((view, subject) -> {
            new SubjectDetailDialog2()
                    .setSubject(subject)
                    .show(context);
        });
        timetableView.setSundayIsFirstDay(TimetableHelper.sundayIsFirstDay());
        timetableView.setShowWeekends(TimetableHelper.isShowWeekends());
        timetableView.setShowNotCurrentWeek(TimetableHelper.isShowNotCurWeek());
        timetableView.setShowTime(TimetableHelper.isShowTime());
        timetableView.setCurrentWeek(currentWeek);
        timetableView.setScheduleList(mSubjects);
    }

    public void showMenu(View view) {
        new AttachListDialogFragment<String>()
                .addItems("修改当前周", "切换学期", "刷新课表", "一键评教", "设置")
                .addIconIds(
                        R.drawable.ic_lock_black_24dp,
                        R.drawable.ic_event_note_black_24dp,
                        R.drawable.ic_refresh_black_24dp,
                        R.drawable.ic_refresh_black_24dp,
                        R.drawable.ic_settings_black_24dp
                )
                .setOnSelectListener((dialog, position, text) -> {
                    switch (position) {
                        case 0:
                            TimetableHelper.openChangeCurrentWeekDialog(getContext(), (dialog1, pos, item) -> {
                                currentWeek = pos + 1;
                                TimetableHelper.setCurrentWeek(currentWeek);
                                if (mWeekView != null) {
                                    mWeekView.curWeek(currentWeek).updateView();
                                }

                                mTvTitle.setText("第" + currentWeek + "周");

                                timetableView.setCurrentWeek(currentWeek);
                                timetableView.removeAllViews();
                                timetableView.requestLayout();
                            });
                            break;
                        case 1:
                            showChooseSemesterDialog();
                            break;
                        case 2:
                            new RefreshDialog().show(context);
                            break;
                        case 3:
                            start(new EvaluationFragment());
                            break;
                        case 4:
                            showSettingDialogFragment();
                            break;
                        default:
                            break;
                    }
                    dialog.dismiss();
                })
                .setAttachView(view)
                .show(context);
    }

    private void showChooseSemesterDialog() {
        List<SemesterInfo> list = TimetableHelper.getSemesterList(getContext());
        int selected = -1;
        for (int i = 0; i < list.size(); i++) {
            if (TimetableHelper.getCurrentSemesterCode().equals(list.get(i).getSemesterCode())) {
                selected = i;
                break;
            }
        }
        new BottomDragSelectDialogFragment<SemesterInfo>()
                .setSelected(selected)
                .onBindTitle((titleView, item, position) -> titleView.setText(item.getSemesterName()))
                .onSingleSelect((dialog, position, item) -> {
                    if (TimetableHelper.getCurrentSemesterCode().equals(item.getSemesterCode())) {
                        return;
                    }
                    TimetableHelper.setCurrentSemester(item.getSemesterCode(), item.getSemesterName());
                    initData();
                })
                .setData(list)
                .setTitle("切换学期")
                .show(context);
    }

    private void showSettingDialogFragment() {
        SettingFragment settingFragment = new SettingFragment();
        start(settingFragment);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.id_layout) {
            mArrowView.switchState();
            if (mWeekView == null) {
                mWeekView = new WeekView(getContext());
                mContaienr.addView(mWeekView, 1);
                mWeekView.curWeek(currentWeek)
                        .data(mSubjects)
                        .hideLeftLayout()
                        .callback(week -> {
                            mTvTitle.setText("第" + week + "周");
                            timetableView.setCurrentWeek(week);
                            timetableView.removeAllViews();
                            timetableView.requestLayout();
                        })
                        .isShow(true)
                        .showView();
            } else {
                mContaienr.removeView(mWeekView);
                mWeekView = null;

                mTvTitle.setText("第" + currentWeek + "周");
                timetableView.setCurrentWeek(TimetableHelper.getCurrentWeek());
                timetableView.removeAllViews();
                timetableView.requestLayout();
            }
        } else if (id == R.id.settins) {
            showMenu(view);
        }
    }

}
