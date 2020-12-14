package com.scu.timetable.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.zagum.expandicon.ExpandIconView;
import com.scu.timetable.R;
import com.scu.timetable.events.RefreshEvent;
import com.scu.timetable.model.ScuSubject;
import com.scu.timetable.model.SemesterInfo;
import com.scu.timetable.ui.activity.LoginActivity;
import com.scu.timetable.ui.fragment.base.SkinFragment;
import com.scu.timetable.ui.fragment.dialog.RefreshDialog;
import com.scu.timetable.ui.fragment.dialog.SubjectDetailDialog2;
import com.scu.timetable.utils.TimetableHelper;
import com.zhuangfei.timetable.TimetableView;
import com.zhuangfei.timetable.listener.ISchedule;
import com.zhuangfei.timetable.listener.IWeekView;
import com.zhuangfei.timetable.listener.OnSlideBuildAdapter;
import com.zhuangfei.timetable.model.Schedule;
import com.zhuangfei.timetable.view.WeekView;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.fragmentation.dialog.impl.AttachListDialogFragment;
import com.zpj.fragmentation.dialog.impl.BottomListDialogFragment;
import com.zpj.utils.PrefsHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Z-P-J
 */
public final class MainFragment extends SkinFragment implements View.OnClickListener {

    private final static String TAG = "MainFragment";

    //控件
    private TimetableView mTimetableView;
    private WeekView mWeekView;
    private ExpandIconView expandIconView;

    private LinearLayout layout;
    private TextView titleTextView;
    private List<ScuSubject> scuSubjects = new ArrayList<>();

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
    protected void initView(View view, @Nullable Bundle savedInstanceState) {

        EventBus.getDefault().register(this);

        currentWeek = TimetableHelper.getCurrentWeek();

        ImageView settings = view.findViewById(R.id.settins);
        settings.setOnClickListener(this);
        titleTextView = view.findViewById(R.id.id_title);
        layout = view.findViewById(R.id.id_layout);
        layout.setOnClickListener(this);
        //获取控件
        expandIconView = view.findViewById(R.id.expand_icon);
        mWeekView = view.findViewById(R.id.id_weekview);
        mTimetableView = view.findViewById(R.id.id_timetableView);
        initTimetableView();

        initData();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void initData() {
        long time1 = System.currentTimeMillis();
        scuSubjects = TimetableHelper.getSubjects(context);
        long time2 = System.currentTimeMillis();

        mWeekView.source(scuSubjects).showView();
        long time3 = System.currentTimeMillis();
        mTimetableView.source(scuSubjects)
                .cornerAll(16)
                .isShowWeekends(TimetableHelper.isShowWeekends())
                .isShowNotCurWeek(TimetableHelper.isShowNotCurWeek())
//                .callback(new ISchedule.OnScrollViewBuildListener() {
//                    @Override
//                    public View getScrollView(LayoutInflater mInflate) {
//                        return mInflate.inflate(R.layout.custom_myscrollview, null, false);
//                    }
//                })
                .showView();
        long time4 = System.currentTimeMillis();
        toggleTime(TimetableHelper.isShowTime());
        modifySlideBgColor(Color.TRANSPARENT);
        long time5 = System.currentTimeMillis();
        Log.d(TAG, "deltaTime1=" + (time2 - time1));
        Log.d(TAG, "deltaTime2=" + (time3 - time2));
        Log.d(TAG, "deltaTime3=" + (time4 - time3));
        Log.d(TAG, "deltaTime4=" + (time5 - time4));
    }

    protected void modifySlideBgColor(int color) {
        OnSlideBuildAdapter listener = (OnSlideBuildAdapter) mTimetableView.onSlideBuildListener();
        listener.setBackground(color);
        listener.setTextColor(Color.BLACK);
        listener.setTimeTextColor(getResources().getColor(R.color.black_transparent_50));
        mTimetableView.updateSlideView();
    }

    /**
     * 初始化课程控件
     */
    private void initTimetableView() {
        //设置周次选择属性
        mWeekView.curWeek(currentWeek)
                .hideLeftLayout()
                .callback(new IWeekView.OnWeekItemClickedListener() {
                    @Override
                    public void onWeekClicked(int week) {
                        int cur = mTimetableView.curWeek();
                        //更新切换后的日期，从当前周cur->切换的周week
                        mTimetableView.onDateBuildListener()
                                .onUpdateDate(cur, week, mTimetableView.getSundayIsFirstDay(), mTimetableView.isShowWeekends());
                        mTimetableView.changeWeekOnly(week);
                    }
                })
                .callback(new IWeekView.OnWeekLeftClickedListener() {
                    @Override
                    public void onWeekLeftClicked() {
                        onWeekLeftLayoutClicked();
                    }
                })
                .isShow(false)//设置隐藏，默认显示
                .showView();

        mTimetableView.curWeek(currentWeek)
                .curTerm("大二下学期")
                .setSundayIsFirstDay(TimetableHelper.sundayIsFirstDay())
                .callback(new ISchedule.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, List<Schedule> scheduleList) {
                        int height = view.getMeasuredHeight();
                        int width = view.getMeasuredWidth();
                        int[] location = new int[2];
                        view.getLocationOnScreen(location);
                        ScuSubject scuSubject = (ScuSubject) scheduleList.get(0).getScheduleEnable();
//                        AnimatorUtil.circleAnimator(test, testX, testY, 500);
                        display(scheduleList);
                        showSubjectPopupView(view, scuSubject);
                    }
                })
                .callback(new ISchedule.OnItemLongClickListener() {
                    @Override
                    public void onLongClick(View v, int day, int start) {
//                        Toast.makeText(MainActivity.this,
//                                "长按:周" + day + ",第" + start + "节",
//                                Toast.LENGTH_SHORT).show();
                    }
                })
                .callback(new ISchedule.OnWeekChangedListener() {
                    @Override
                    public void onWeekChanged(int curWeek) {
                        titleTextView.setText("第" + curWeek + "周");
                    }
                })
                .showView();
    }

    public void showMenu(View view) {
        new AttachListDialogFragment<String>()
                .addItems(
                        "修改当前周",
                        "切换学期",
                        "刷新课表",
                        "一键评教",
                        "设置"
                )
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
                            onWeekLeftLayoutClicked();
                            break;
                        case 1:
                            showChooseSemesterDialog();
                            break;
                        case 2:
                            showRefreshDialog();
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

    private void showSubjectPopupView(final View view, final ScuSubject scuSubject) {
        new SubjectDetailDialog2()
                .setSubject(scuSubject)
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
        new BottomListDialogFragment<SemesterInfo>()
                .setData(list)
                .setTitle("切换学期")
                .setCheckedPosition(selected)
                .setOnSelectListener((popup, position, item) -> {
                    if (TimetableHelper.getCurrentSemesterCode().equals(item.getSemesterCode())) {
                        return;
                    }
                    TimetableHelper.setCurrentSemester(item.getSemesterCode(), item.getSemesterName());
//                    ShowLoadingEvent.post("切换中");
                    popup.dismiss();
                })
                .setOnDismissListener(() -> {
                    initData();
//                    HideLoadingEvent.postEvent();
                })
                .show(context);
    }

    private void showRefreshDialog() {
        new RefreshDialog().show(context);
    }

    private void showSettingDialogFragment() {
        SettingFragment settingFragment = new SettingFragment();
        settingFragment.setOnDismissListener(new SettingFragment.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
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
                if (sundayIsFirstDay != mTimetableView.getSundayIsFirstDay()
                        || showWeekends != mTimetableView.isShowWeekends()
                        || showNotCurWeek != mTimetableView.isShowNotCurWeek()
                        || currentWeek != mTimetableView.curWeek()) {
                    mTimetableView.curWeek(currentWeek)
                            .setSundayIsFirstDay(sundayIsFirstDay)
                            .isShowWeekends(showWeekends)
                            .isShowNotCurWeek(showNotCurWeek)
                            .updateView();
                }
                toggleTime(showTime);
            }
        });
        start(settingFragment);
    }

    private void toggleTime(boolean showTime) {
        OnSlideBuildAdapter listener = (OnSlideBuildAdapter) mTimetableView.onSlideBuildListener();
        listener.setTimes(showTime ? TimetableHelper.TIMES_1 : null);
        listener.setEndTimes(showTime ? TimetableHelper.TIMES_END_1 : null);
        mTimetableView.updateSlideView();
    }

    /**
     * 更新一下，防止因程序在后台时间过长（超过一天）而导致的日期或高亮不准确问题。
     */
    @Override
    public void onStart() {
        super.onStart();
        mTimetableView.onDateBuildListener()
                .onHighLight();
    }

    /**
     * 周次选择布局的左侧被点击时回调<br/>
     * 对话框修改当前周次
     */
    protected void onWeekLeftLayoutClicked() {
        TimetableHelper.openChangeCurrentWeekDialog(getContext(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mWeekView.curWeek(which).updateView();
                mTimetableView.changeWeekForce(which);
                TimetableHelper.setCurrentWeek(which);
            }
        });
    }

    /**
     * 显示内容
     *
     * @param beans
     */
    protected void display(List<Schedule> beans) {
        StringBuilder str = new StringBuilder();
        for (Schedule bean : beans) {
            str.append(bean.getName()).append(",").append(bean.getWeekList().toString()).append(",").append(bean.getStart()).append(",").append(bean.getStep()).append("\n");
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        //如果周次选择已经显示了，那么将它隐藏，更新课程、日期
        if (id == R.id.id_layout) {
            //否则，显示
            if (mWeekView.isShowing()) {
                hideWeekView();
            } else {
                showWeekView();
            }
        } else if (id == R.id.settins) {
            showMenu(view);
        }
    }

    /**
     * 隐藏周次选择，此时需要将课表的日期恢复到本周并将课表切换到当前周
     */
    public void hideWeekView() {
        mWeekView.isShow(false);
        expandIconView.switchState();
//        titleTextView.setTextColor(Color.WHITE);
        int cur = mTimetableView.curWeek();
        mTimetableView.onDateBuildListener()
                .onUpdateDate(cur, cur, mTimetableView.getSundayIsFirstDay(), mTimetableView.isShowWeekends());
        mTimetableView.changeWeekOnly(cur);
    }

    public void showWeekView() {
        mWeekView.isShow(true);
        expandIconView.switchState();
    }

    @Subscribe
    public void onRefreshEvent(RefreshEvent event) {
        initTimetableView();
        initData();
    }

}
