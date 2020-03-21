package com.scu.timetable.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zpj.popup.ZPopup;
import com.scu.timetable.R;
import com.scu.timetable.events.RefreshEvent;
import com.scu.timetable.model.ScuSubject;
import com.scu.timetable.model.SemesterInfo;
import com.scu.timetable.ui.activity.LoginActivity;
import com.scu.timetable.ui.popup.RefreshPopup;
import com.scu.timetable.ui.popup.SubjectDetailPopup;
import com.scu.timetable.utils.TimetableHelper;
import com.zhuangfei.timetable.TimetableView;
import com.zhuangfei.timetable.listener.ISchedule;
import com.zhuangfei.timetable.listener.IWeekView;
import com.zhuangfei.timetable.listener.OnSlideBuildAdapter;
import com.zhuangfei.timetable.model.Schedule;
import com.zhuangfei.timetable.view.WeekView;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.utils.PrefsHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Z-P-J
 */
public final class MainFragment extends BaseFragment implements View.OnClickListener {

    private final static String TAG = "MainFragment";

    private Drawable expandMoreDrawable;
    private Drawable expandLessDrawable;

    //控件
    private TimetableView mTimetableView;
    private WeekView mWeekView;

    private LinearLayout layout;
    private TextView titleTextView;
    private List<ScuSubject> scuSubjects = new ArrayList<>();

    //记录切换的周次，不一定是当前周
    private int target = -1;

    private long firstTime = 0;

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

        expandMoreDrawable = getResources().getDrawable(R.drawable.ic_expand_more_white_24dp);
        expandLessDrawable = getResources().getDrawable(R.drawable.ic_expand_less_white_24dp);

        currentWeek = TimetableHelper.getCurrentWeek();

        ImageView settings = view.findViewById(R.id.settins);
        settings.setOnClickListener(this);
        titleTextView = view.findViewById(R.id.id_title);
        toggleTitle(true);
        layout = view.findViewById(R.id.id_layout);
        layout.setOnClickListener(this);
        //获取控件
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

    private void toggleTitle(boolean tag) {
        titleTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, tag ? expandMoreDrawable : expandLessDrawable, null);
    }

    private void initData() {
        scuSubjects = TimetableHelper.getSubjects(getContext());

        mWeekView.source(scuSubjects).showView();
        mTimetableView.source(scuSubjects)
                .isShowWeekends(TimetableHelper.isShowWeekends())
                .isShowNotCurWeek(TimetableHelper.isShowNotCurWeek())
//                .callback(new ISchedule.OnScrollViewBuildListener() {
//                    @Override
//                    public View getScrollView(LayoutInflater mInflate) {
//                        return mInflate.inflate(R.layout.custom_myscrollview, null, false);
//                    }
//                })
                .showView();
        toggleTime(TimetableHelper.isShowTime());
        modifySlideBgColor(Color.TRANSPARENT);
    }

    protected void modifySlideBgColor(int color) {
        OnSlideBuildAdapter listener = (OnSlideBuildAdapter) mTimetableView.onSlideBuildListener();
        listener.setBackground(color);
        listener.setTextColor(Color.GRAY);
        listener.setTimeTextColor(Color.GRAY);
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
        ZPopup.attachList(view)
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
                .setOnSelectListener((position, text) -> {
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
                })
                .show();
    }

    private void showSubjectPopupView(final View view, final ScuSubject scuSubject) {
        new SubjectDetailPopup(context, scuSubject).show();
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
        ZPopup.bottomList(context, SemesterInfo.class)
                .setData(list)
                .setTitle("切换学期")
                .setCheckedPosition(selected)
                .setOnSelectListener((popup, position, item) -> {
                    if (TimetableHelper.getCurrentSemesterCode().equals(item.getSemesterCode())) {
                        return;
                    }
                    TimetableHelper.setCurrentSemester(item.getSemesterCode(), item.getSemesterName());
                    initTimetableView();
                    initData();
                    postDelayed(popup::dismiss, 50);
                })
                .show();
    }

    private void showRefreshDialog() {
        try {
            ZPopup.custom(context, RefreshPopup.class).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showSettingDialogFragment() {
        SettingFragment settingFragment = new SettingFragment();
        settingFragment.setOnDismissListener(new SettingFragment.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (!TimetableHelper.isVisitorMode() && !PrefsHelper.with().getBoolean("logined", false)) {
                    startActivity(new Intent(getContext(), LoginActivity.class));
                    getActivity().finish();
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
//        SettingsDialogFragment dialogFragment = new SettingsDialogFragment();
//        dialogFragment.setOnDismissListener();
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//        dialogFragment.show(fragmentTransaction, "setting");
    }

    private void toggleTime(boolean showTime) {
        OnSlideBuildAdapter listener = (OnSlideBuildAdapter) mTimetableView.onSlideBuildListener();
        listener.setTimes(showTime ? TimetableHelper.TIMES_1 : null);
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
        toggleTitle(true);
//        titleTextView.setTextColor(Color.WHITE);
        int cur = mTimetableView.curWeek();
        mTimetableView.onDateBuildListener()
                .onUpdateDate(cur, cur, mTimetableView.getSundayIsFirstDay(), mTimetableView.isShowWeekends());
        mTimetableView.changeWeekOnly(cur);
    }

    public void showWeekView() {
        toggleTitle(false);
        mWeekView.isShow(true);
    }

    @Subscribe
    public void onRefreshEvent(RefreshEvent event) {
        initTimetableView();
        initData();
    }

}
