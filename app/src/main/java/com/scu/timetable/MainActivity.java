package com.scu.timetable;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scu.timetable.model.MySubject;
import com.scu.timetable.ui.activity.ActivityCollector;
import com.scu.timetable.ui.activity.BaseActivity;
import com.scu.timetable.ui.fragment.SettingsDialogFragment;
import com.scu.timetable.utils.AnimatorUtil;
import com.scu.timetable.utils.CaptchaFetcher;
import com.scu.timetable.utils.StatusBarUtil;
import com.scu.timetable.utils.TimetableHelper;
import com.scu.timetable.utils.content.SPHelper;
import com.zhuangfei.timetable.TimetableView;
import com.zhuangfei.timetable.listener.ISchedule;
import com.zhuangfei.timetable.listener.IWeekView;
import com.zhuangfei.timetable.listener.OnSlideBuildAdapter;
import com.zhuangfei.timetable.model.Schedule;
import com.zhuangfei.timetable.view.WeekView;
import com.zpj.popupmenuview.CustomPopupMenuView;
import com.zpj.popupmenuview.OptionMenuView;
import com.zpj.qianxundialoglib.IDialog;
import com.zpj.qianxundialoglib.QianxunDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 25714
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {

    private Drawable expandMoreDrawable;
    private Drawable expandLessDrawable;

    //控件
    TimetableView mTimetableView;
    WeekView mWeekView;

    LinearLayout layout;
    TextView titleTextView;
    List<MySubject> mySubjects = new ArrayList<>();

    //记录切换的周次，不一定是当前周
    int target = -1;

    private long firstTime = 0;

    private int currentWeek;

    private CardView test;

    private int testX, testY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_func);

        expandMoreDrawable = getResources().getDrawable(R.drawable.ic_expand_more_white_24dp);
        expandLessDrawable = getResources().getDrawable(R.drawable.ic_expand_less_white_24dp);

        currentWeek = TimetableHelper.getCurrentWeek();

        test = findViewById(R.id.test);

        ImageView settings = findViewById(R.id.settins);
        settings.setOnClickListener(this);
        titleTextView = findViewById(R.id.id_title);
        toggleTitle(true);
        layout = findViewById(R.id.id_layout);
        layout.setOnClickListener(this);
        initTimetableView();

        requestData();
    }

    private void toggleTitle(boolean tag) {
//        int resId = tag ? R.drawable.ic_expand_more_white_24dp : R.drawable.ic_expand_less_white_24dp;
//
//        Drawable expand = getResources().getDrawable(resId);
//        expand.setBounds(1, 1, 10, 10);
        titleTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, tag ? expandMoreDrawable : expandLessDrawable, null);

    }

    /**
     * 2秒后刷新界面，模拟网络请求
     */
    private void requestData() {
        mySubjects = TimetableHelper.getSubjects(this);
        mWeekView.source(mySubjects).showView();
        mTimetableView.source(mySubjects)
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
        //获取控件
        mWeekView = findViewById(R.id.id_weekview);
        mTimetableView = findViewById(R.id.id_timetableView);

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
                        testX = location[0] + width / 2;
                        testY = location[1] + height / 2 - StatusBarUtil.getStatusBarHeight(MainActivity.this);
//                        testX = left + width / 2;
//                        testY = top + height / 2;
                        Toast.makeText(MainActivity.this, testX + "  " + testY, Toast.LENGTH_SHORT).show();
                        MySubject mySubject = (MySubject) scheduleList.get(0).getScheduleEnable();
//                        AnimatorUtil.circleAnimator(test, testX, testY, 500);
                        display(scheduleList);

                        showSubjectPopupView(view, mySubject);
                    }
                })
                .callback(new ISchedule.OnItemLongClickListener() {
                    @Override
                    public void onLongClick(View v, int day, int start) {
                        Toast.makeText(MainActivity.this,
                                "长按:周" + day + ",第" + start + "节",
                                Toast.LENGTH_SHORT).show();
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
        CustomPopupMenuView.with(this, R.layout.layout_menu)
                .setOrientation(LinearLayout.VERTICAL)
                .setBackgroundAlpha(MainActivity.this, 0.8f, 300)
                .setPopupViewBackgroundColor(Color.parseColor("#eeffffff"))
//                .setAnimationTranslationShow(CustomPopupMenuView.DIRECTION_X, 350, 100, 0)
//                .setAnimationTranslationShow(CustomPopupMenuView.DIRECTION_Y, 350, -100, 0)
                .setAnimationAlphaShow(350, 0.0f, 1.0f)
//                .setAnimationAlphaDismiss(350, 1.0f, 0.0f)
                .initViews(
                        1,
                        (popupMenuView, itemView, position) -> {

                            FrameLayout toolsFrameLayout = itemView.findViewById(R.id.tools);
                            OptionMenuView tools = new OptionMenuView(MainActivity.this);
                            tools.setOrientation(LinearLayout.VERTICAL);
                            tools.inflate(R.menu.menu_tools, new MenuBuilder(MainActivity.this));
                            tools.setOnOptionMenuClickListener((index, menu) -> {
                                if (!menu.isEnable()) {
                                    if (index == 0) {
                                        Toast.makeText(this, "智能显示周末已开启！", Toast.LENGTH_SHORT).show();
                                    }
                                    return false;
                                }
                                popupMenuView.dismiss();
                                switch (index) {
                                    case 0:
                                        mTimetableView.isShowWeekends(!menu.isChecked()).updateView();
                                        TimetableHelper.toggleShowWeekends();
                                        break;
                                    case 1:
                                        toggleTime(!menu.isChecked());
                                        TimetableHelper.toggleShowTime();
                                        break;
                                    case 2:
                                        mTimetableView.isShowNotCurWeek(!menu.isChecked()).updateView();
                                        TimetableHelper.toggleShowNotCurWeek();
                                        break;
                                    case 3:
                                        onWeekLeftLayoutClicked();
                                        break;
                                    case 4:
                                        showRefreshDialog();
                                        break;
                                    case 5:
                                        showSettingDialogFragment();
                                        break;
                                    default:

                                        break;
                                }
                                return true;
                            });
                            toolsFrameLayout.addView(tools);

                            if (TimetableHelper.isSmartShowWeekends()) {
                                tools.setEnabled(0, false);
                            }
                            tools.setChecked(0, mTimetableView.isShowWeekends());
                            tools.setChecked(1, TimetableHelper.isShowTime());
                            tools.setChecked(2, TimetableHelper.isShowNotCurWeek());

                        })
                .show(view);
    }

    private void showSubjectPopupView(View view, MySubject mySubject) {
        CustomPopupMenuView.with(MainActivity.this, R.layout.layout_subject_detail)
                .setOrientation(LinearLayout.VERTICAL)
//                .setBackgroundAlpha(MainActivity.this, 0.9f)
                .setPopupViewBackgroundColor(Color.parseColor("#eeffffff"))
                .setAnimationTranslationShow(CustomPopupMenuView.DIRECTION_X, 350, 100, 0)
                .setAnimationTranslationShow(CustomPopupMenuView.DIRECTION_Y, 350, -100, 0)
                .setAnimationAlphaShow(350, 0.0f, 1.0f)
//                .setAnimationAlphaDismiss(350, 1.0f, 0.0f)
                .initViews(
                        1,
                        (popupMenuView, itemView, position) -> {
                            TextView courseName = itemView.findViewById(R.id.course_name);
                            TextView teacherName = itemView.findViewById(R.id.teacher_name);
                            TextView classRoom = itemView.findViewById(R.id.class_room);
                            TextView classTime = itemView.findViewById(R.id.class_time);
//                            TextView courseSequenceNum = itemView.findViewById(R.id.course_sequence_num);
//                            TextView courseNum = itemView.findViewById(R.id.course_num);

                            courseName.setText(mySubject.getCourseName());
                            teacherName.setText(mySubject.getTeacher());
                            classRoom.setText(mySubject.getRoom());
                            String sessions = "第" + mySubject.getStart() + " - " + mySubject.getEnd() + "节";
                            classTime.setText(mySubject.getWeekDescription() + "\n" + sessions);

//                            courseSequenceNum.setText(mySubject.getCoureSequenceNumber());
//                            courseNum.setText(mySubject.getCoureNumber());

                            ImageView note = itemView.findViewById(R.id.subject_note);
                            note.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    popupMenuView.dismiss();
                                    showSubjectNote();
                                }
                            });
                        })
                .show(view);
    }

    private void showSubjectNote() {
        QianxunDialog.with(MainActivity.this)
                .setDialogView(R.layout.layout_subject_note)
                .setBuildChildListener(new IDialog.OnBuildListener() {
                    @Override
                    public void onBuildChildView(IDialog dialog, View view, int layoutRes) {
                        ImageView btnClose = view.findViewById(R.id.btn_close);
                        ImageView btnSave = view.findViewById(R.id.btn_save);
                        AppCompatEditText editText = view.findViewById(R.id.edit_text);
                        editText.setText("哈哈哈哈或或或或或或或或或或或或或或或或或或或或或或或或或或或或或");
                        btnClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        btnSave.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //todo 保存备注
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .show();
    }

    private void showRefreshDialog() {
        QianxunDialog.with(MainActivity.this)
                .setDialogView(R.layout.layout_refresh)
                .setBuildChildListener(new IDialog.OnBuildListener() {
                    @Override
                    public void onBuildChildView(IDialog dialog, View view, int layoutRes) {
                        ImageView imgCatpcha = view.findViewById(R.id.img_captcha);
                        CaptchaFetcher.fetchcaptcha(imgCatpcha);
                        ImageView btnClose = view.findViewById(R.id.btn_close);
                        btnClose.setOnClickListener(v -> dialog.dismiss());
                        TextView changeCatpcha = view.findViewById(R.id.change_captcha);
                        changeCatpcha.setOnClickListener(v -> CaptchaFetcher.fetchcaptcha(imgCatpcha));
                        TextView btnRefresh = view.findViewById(R.id.btn_refresh);
                        btnRefresh.setOnClickListener(v -> {
                            //todo refresh
                            Toast.makeText(MainActivity.this, "todo refresh", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        });
                    }
                })
                .show();
    }

    private void showSettingDialogFragment() {
        SettingsDialogFragment dialogFragment = new SettingsDialogFragment();
        dialogFragment.setOnDismissListener(new SettingsDialogFragment.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Toast.makeText(MainActivity.this, "SettingsDialogFragment", Toast.LENGTH_SHORT).show();
                boolean sundayIsFirstDay = TimetableHelper.sundayIsFirstDay();
                boolean showWeekends = TimetableHelper.isShowWeekendsOrin();
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
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        dialogFragment.show(fragmentTransaction, "setting");
    }

    private void toggleTime(boolean showTime) {
        if (showTime) {
            OnSlideBuildAdapter listener = (OnSlideBuildAdapter) mTimetableView.onSlideBuildListener();
            listener.setTimes(TimetableHelper.TIMES_1)
                    .setTimeTextColor(Color.BLACK);
            mTimetableView.updateSlideView();
        } else {
            mTimetableView.callback((ISchedule.OnSlideBuildListener) null);
            mTimetableView.updateSlideView();
        }
    }

    /**
     * 更新一下，防止因程序在后台时间过长（超过一天）而导致的日期或高亮不准确问题。
     */
    @Override
    protected void onStart() {
        super.onStart();
        mTimetableView.onDateBuildListener()
                .onHighLight();
    }

    /**
     * 周次选择布局的左侧被点击时回调<br/>
     * 对话框修改当前周次
     */
    protected void onWeekLeftLayoutClicked() {
//        final String[] items = new String[20];
//        int itemCount = mWeekView.itemCount();
//        for (int i = 0; i < itemCount; i++) {
//            items[i] = "第" + (i + 1) + "周";
//        }
//        target = -1;
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("设置当前周");
//        builder.setSingleChoiceItems(items, mTimetableView.curWeek() - 1,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        target = i;
//                    }
//                });
//        builder.setPositiveButton("设置为当前周", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if (target != -1) {
//                    mWeekView.curWeek(target + 1).updateView();
//                    mTimetableView.changeWeekForce(target + 1);
//                    TimetableHelper.setCurrentWeek(target + 1);
//                }
//            }
//        });
//        builder.setNegativeButton("取消", null);
//        builder.create().show();

        TimetableHelper.openChangeCurrentWeekDialog(this, new DialogInterface.OnClickListener() {
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
        Toast.makeText(this, str.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        int id =view.getId();
        if (id == R.id.id_layout) {//如果周次选择已经显示了，那么将它隐藏，更新课程、日期
            //否则，显示
            if (mWeekView.isShowing()) {
                hideWeekView();
            } else {
                showWeekView();
            }
        }  else if (id == R.id.settins) {
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
        Toast.makeText(this, "cur=" + cur, Toast.LENGTH_SHORT).show();
        mTimetableView.onDateBuildListener()
                .onUpdateDate(cur, cur, mTimetableView.getSundayIsFirstDay(), mTimetableView.isShowWeekends());
        mTimetableView.changeWeekOnly(cur);
    }

    public void showWeekView() {
        toggleTitle(false);
        mWeekView.isShow(true);
//        titleTextView.setTextColor(Color.RED);
    }

    @Override
    public void onBackPressed() {
        if (test.getVisibility() == View.VISIBLE) {
            AnimatorUtil.circleAnimator(test, testX, testY, 500);
            return;
        }
        if (System.currentTimeMillis() - firstTime > 2000) {
            Toast.makeText(this, "再次点击退出！", Toast.LENGTH_SHORT).show();
            firstTime = System.currentTimeMillis();
        } else {
            ActivityCollector.finishAll();
        }
    }
}
