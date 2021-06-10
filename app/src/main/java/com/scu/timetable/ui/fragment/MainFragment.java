package com.scu.timetable.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.zagum.expandicon.ExpandIconView;
import com.scu.timetable.R;
import com.scu.timetable.bean.ScuSubject;
import com.scu.timetable.bean.SemesterInfo;
import com.scu.timetable.ui.activity.LoginActivity;
import com.scu.timetable.ui.fragment.base.SkinChangeFragment;
import com.scu.timetable.ui.fragment.dialog.RefreshDialog;
import com.scu.timetable.ui.fragment.dialog.SubjectDetailDialog2;
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

    private TimetableView timetableView;
    private WeekView mWeekView;
    private ExpandIconView expandIconView;
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
            initTimetableView();
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
            int currentWeek = TimetableHelper.getCurrentWeek();

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

        ImageView settings = view.findViewById(R.id.settins);
        settings.setOnClickListener(this);
        LinearLayout layout = view.findViewById(R.id.id_layout);
        layout.setOnClickListener(this);

        expandIconView = view.findViewById(R.id.expand_icon);
        mWeekView = view.findViewById(R.id.id_weekview);
        mTvTitle = findViewById(R.id.tv_title);
        initTimetableView();

        initData();
    }

    private void initTimetableView() {
        //设置周次选择属性
        mWeekView.curWeek(currentWeek)
                .hideLeftLayout()
                .callback(week -> {
                    mTvTitle.setText("第" + week + "周");
                    timetableView.setCurrentWeek(week);
                    timetableView.removeAllViews();
                    timetableView.requestLayout();
                })
                .isShow(false)//设置隐藏，默认显示
                .showView();
    }

    private void initData() {
        mTvTitle.setText("第" + currentWeek + "周");

        List<ScuSubject> scuSubjects = TimetableHelper.getSubjects(context);
        mWeekView.data(scuSubjects).showView();

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
        timetableView.setScheduleList(scuSubjects);
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
                                mWeekView.curWeek(currentWeek).updateView();

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
            if (mWeekView.isShowing()) {
                mWeekView.isShow(false);
                mTvTitle.setText("第" + currentWeek + "周");
                expandIconView.switchState();
                timetableView.setCurrentWeek(TimetableHelper.getCurrentWeek());
                timetableView.removeAllViews();
                timetableView.requestLayout();
            } else {
                mWeekView.isShow(true);
                expandIconView.switchState();
            }
        } else if (id == R.id.settins) {
            showMenu(view);
        }
    }

}
