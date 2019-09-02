package com.scu.timetable;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.view.menu.MenuBuilder;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.felix.atoast.library.AToast;
import com.scu.timetable.model.ScuSubject;
import com.scu.timetable.model.SemesterBean;
import com.scu.timetable.model.UpdateBean;
import com.scu.timetable.service.NotificationService;
import com.scu.timetable.ui.activity.ActivityCollector;
import com.scu.timetable.ui.activity.BaseActivity;
import com.scu.timetable.ui.fragment.DetailDialogFragment;
import com.scu.timetable.ui.fragment.EvaluationDialogFragment;
import com.scu.timetable.ui.fragment.SettingsDialogFragment;
import com.scu.timetable.ui.fragment.UpdateDialogFragment;
import com.scu.timetable.ui.widget.DetailLayout;
import com.scu.timetable.utils.UpdateUtil;
import com.scu.timetable.utils.CaptchaFetcher;
import com.scu.timetable.utils.LoginUtil;
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
import com.zpj.qianxundialoglib.EasyAdapter;
import com.zpj.qianxundialoglib.IDialog;
import com.zpj.qianxundialoglib.QXListDialog;
import com.zpj.qianxundialoglib.QianxunDialog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Z-P-J
 */
public final class MainActivity extends BaseActivity implements View.OnClickListener, UpdateUtil.UpdateCallback {

    private Drawable expandMoreDrawable;
    private Drawable expandLessDrawable;

    //控件
    TimetableView mTimetableView;
    WeekView mWeekView;

    LinearLayout layout;
    TextView titleTextView;
    List<ScuSubject> scuSubjects = new ArrayList<>();

    //记录切换的周次，不一定是当前周
    int target = -1;

    private long firstTime = 0;

    private int currentWeek;

    private int testX, testY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_func);

        NotificationService.start(this);

        expandMoreDrawable = getResources().getDrawable(R.drawable.ic_expand_more_white_24dp);
        expandLessDrawable = getResources().getDrawable(R.drawable.ic_expand_less_white_24dp);

        currentWeek = TimetableHelper.getCurrentWeek();

        ImageView settings = findViewById(R.id.settins);
        settings.setOnClickListener(this);
        titleTextView = findViewById(R.id.id_title);
        toggleTitle(true);
        layout = findViewById(R.id.id_layout);
        layout.setOnClickListener(this);
        initTimetableView();

        initData();
        UpdateUtil.with(this).checkUpdate(this);
//        AlarmReceiver.startAlarm(this);
    }

    private void toggleTitle(boolean tag) {
        titleTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, tag ? expandMoreDrawable : expandLessDrawable, null);
    }

    private void initData() {
        scuSubjects = TimetableHelper.getSubjects(this);

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
        CustomPopupMenuView.with(this, R.layout.layout_menu)
                .setOrientation(LinearLayout.VERTICAL)
                .setBackgroundAlpha(MainActivity.this, 0.9f, 500)
                .setPopupViewBackgroundColor(Color.parseColor("#eeffffff"))
//                .setAnimationTranslationShow(CustomPopupMenuView.DIRECTION_X, 350, 100, 0)
//                .setAnimationTranslationShow(CustomPopupMenuView.DIRECTION_Y, 350, -100, 0)
//                .setAnimationAlphaShow(350, 0.0f, 1.0f)
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
                                        AToast.normal("请关闭智能显示周末后再试！");
                                    }
                                    return false;
                                }
                                popupMenuView.dismiss();
                                switch (index) {
                                    case 0:
                                        mTimetableView.isShowWeekends(menu.isChecked()).updateView();
                                        TimetableHelper.toggleShowWeekends();
                                        break;
                                    case 1:
                                        toggleTime(menu.isChecked());
                                        TimetableHelper.toggleShowTime();
                                        break;
                                    case 2:
                                        mTimetableView.isShowNotCurWeek(menu.isChecked()).updateView();
                                        TimetableHelper.toggleShowNotCurWeek();
                                        break;
                                    case 3:
                                        onWeekLeftLayoutClicked();
                                        break;
                                    case 4:
                                        showChooseSemesterDialog();
                                        break;
                                    case 5:
                                        showRefreshDialog();
                                        break;
                                    case 6:
                                        EvaluationDialogFragment dialogFragment = new EvaluationDialogFragment();
                                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                                        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                                        dialogFragment.show(fragmentTransaction, "evaluation");
                                        break;
                                    case 7:
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

    private void showSubjectPopupView(final View view, final ScuSubject scuSubject) {
        CustomPopupMenuView.with(MainActivity.this, R.layout.layout_subject_detail)
                .setOrientation(LinearLayout.VERTICAL)
//                .setBackgroundAlpha(MainActivity.this, 0.9f)
                .setPopupViewBackgroundColor(Color.parseColor("#eeffffff"))
//                .setAnimationTranslationShow(CustomPopupMenuView.DIRECTION_X, 350, 100, 0)
//                .setAnimationTranslationShow(CustomPopupMenuView.DIRECTION_Y, 350, -100, 0)
//                .setAnimationAlphaShow(350, 0.0f, 1.0f)
//                .setAnimationAlphaDismiss(350, 1.0f, 0.0f)
                .initViews(
                        1,
                        (popupMenuView, itemView, position) -> {
                            TextView courseName = itemView.findViewById(R.id.course_name);
                            DetailLayout teacherName = itemView.findViewById(R.id.teacher_name);
                            DetailLayout classRoom = itemView.findViewById(R.id.class_room);
                            DetailLayout classTime = itemView.findViewById(R.id.class_time);
//                            TextView courseSequenceNum = itemView.findViewById(R.id.course_sequence_num);
//                            TextView courseNum = itemView.findViewById(R.id.course_num);

                            courseName.setText(scuSubject.getCourseName());
                            teacherName.setContent(scuSubject.getTeacher());
                            classRoom.setContent(scuSubject.getRoom());
                            classTime.setContent(scuSubject.getClassTime());

                            if (!TextUtils.isEmpty(scuSubject.getNote())) {
                                DetailLayout noteLayout = itemView.findViewById(R.id.layout_note);
                                noteLayout.setVisibility(View.VISIBLE);
                                noteLayout.setContent(scuSubject.getNote());
                            }

                            ImageView note = itemView.findViewById(R.id.subject_note);
                            note.setOnClickListener(v -> {
                                popupMenuView.dismiss();
                                showSubjectNote(scuSubject);
                            });
                            ImageView more = itemView.findViewById(R.id.subject_more);
                            more.setOnClickListener(v -> {
                                popupMenuView.dismiss();
                                DetailDialogFragment dialogFragment = new DetailDialogFragment();
                                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                                dialogFragment.setCallback(new DetailDialogFragment.Callback() {
                                    @Override
                                    public ScuSubject fechSubject() {
                                        return scuSubject;
                                    }
                                });
                                dialogFragment.show(fragmentTransaction, "detail");
                            });

                            ImageView alarm = itemView.findViewById(R.id.subject_alarm);
                            alarm.setOnClickListener(v -> {
                                //todo alarm
                                AToast.normal("提醒功能未实现！");
                            });
                        })
                .show(view, -1);
    }

    private void showSubjectNote(final ScuSubject subject) {
        QianxunDialog.with(MainActivity.this)
                .setDialogView(R.layout.layout_subject_note)
                .setBuildChildListener(new IDialog.OnBuildListener() {
                    @Override
                    public void onBuildChildView(IDialog dialog, View view, int layoutRes) {
                        TextView noteTitle = view.findViewById(R.id.note_title);
                        ImageView btnClose = view.findViewById(R.id.btn_close);
                        ImageView btnSave = view.findViewById(R.id.btn_save);
                        EditText editText = view.findViewById(R.id.edit_text);

                        noteTitle.setText(subject.getCourseName() + "的备注");
                        editText.setText(subject.getNote());
                        editText.setSelection(subject.getNote().length());
                        btnClose.setOnClickListener(v -> dialog.dismiss());
                        btnSave.setOnClickListener(v -> {
                            String note = editText.getText().toString();
                            if (subject.getNote().isEmpty() && editText.getText().toString().isEmpty()) {
                                AToast.normal("请输入备注！");
                                return;
                            }
                            if (TimetableHelper.saveNote(MainActivity.this, subject, note)) {
                                dialog.dismiss();
                                AToast.normal("保存成功！");
                                subject.setNote(note);
                            } else {
                                AToast.normal("保存失败，请重试！");
                            }
                        });
                    }
                })
                .show();
    }

    private void showChooseSemesterDialog() {
        QXListDialog<SemesterBean> dialog = new QXListDialog<>(this);
        dialog.setItemList(TimetableHelper.getSemesterList(this))
                .setItemRes(R.layout.layout_semester_item)
                .setGravity(Gravity.BOTTOM)
                .setEasyAdapterCallback(new EasyAdapter.EasyAdapterCallback<SemesterBean>() {
                    @Override
                    public EasyAdapter.ViewHolder onCreateViewHolder(List<SemesterBean> list, View itemView, int i) {
                        return null;
                    }

                    @Override
                    public void onBindViewHolder(List<SemesterBean> list, View itemView, int i) {
                        TextView textView = itemView.findViewById(R.id.text_view);
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (TimetableHelper.getCurrentSemesterCode().equals(list.get(i).getSemesterCode())) {
                                    return;
                                }
//                                Toast.makeText(MainActivity.this, "请输入验证码刷新课表！", Toast.LENGTH_SHORT).show();
                                TimetableHelper.setCurrentSemester(list.get(i).getSemesterCode(), list.get(i).getSemesterName());
                                initTimetableView();
                                initData();
                                dialog.dismiss();
                            }
                        });
                        textView.setText(list.get(i).getSemesterName());
                        if (TimetableHelper.getCurrentSemesterCode().equals(list.get(i).getSemesterCode())) {
                            textView.setTextColor(Color.BLACK);
                        }
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
                        LinearLayout container = view.findViewById(R.id.container);
                        LinearLayout statusLayout = view.findViewById(R.id.layout_status);
                        TextView loadingDialogText = view.findViewById(R.id.loading_dialog_text);

                        ImageView imgCatpcha = view.findViewById(R.id.img_captcha);
                        CaptchaFetcher.fetchCaptcha(imgCatpcha);
                        ImageView btnClose = view.findViewById(R.id.btn_close);
                        btnClose.setOnClickListener(v -> dialog.dismiss());
                        TextView changeCatpcha = view.findViewById(R.id.change_captcha);
                        changeCatpcha.setOnClickListener(v -> CaptchaFetcher.fetchCaptcha(imgCatpcha));
                        TextView btnRefresh = view.findViewById(R.id.btn_refresh);
                        EditText captchaEdit = view.findViewById(R.id.captcha);

                        btnRefresh.setOnClickListener(v -> {
                            String captcha = captchaEdit.getText().toString();
                            if (TextUtils.isEmpty(captcha)) {
                                AToast.normal("验证码为空！");
                                return;
                            }
                            if (TimetableHelper.isVisitorMode()) {
                                AToast.normal("您当前正处于游客模式，无法刷新课表！");
                                return;
                            }
                            if (dialog instanceof QianxunDialog) {
                                QianxunDialog qianxunDialog = ((QianxunDialog) dialog);
                                qianxunDialog.setCancelable(false);
                                qianxunDialog.setCanceledOnTouchOutside(false);
                            }
                            statusLayout.setVisibility(View.VISIBLE);
                            LoginUtil.with()
                                    .setLoginCallback(new LoginUtil.LoginCallback() {

                                        private void onError() {
                                            loadingDialogText.setText("登录失败！");
                                            statusLayout.setVisibility(View.GONE);
                                            AToast.normal("登录失败，请重试！");
                                            CaptchaFetcher.fetchCaptcha(imgCatpcha);
                                            captchaEdit.setText("");
                                            if (dialog instanceof QianxunDialog) {
                                                QianxunDialog qianxunDialog = ((QianxunDialog) dialog);
                                                qianxunDialog.setCancelable(true);
                                                qianxunDialog.setCanceledOnTouchOutside(true);
                                            }
                                        }

                                        @Override
                                        public void onGetCookie(String cookie) { }

                                        @Override
                                        public void onLoginSuccess() {
                                            loadingDialogText.setText("登录成功!获取课表信息中。。。");
                                        }

                                        @Override
                                        public void onLoginFailed() {
                                            onError();
                                        }

                                        @Override
                                        public void onLoginError(String errorMsg) {
                                            onError();
                                        }

                                        @Override
                                        public void onGetTimetable(JSONObject jsonObject) {
                                            try {
                                                TimetableHelper.writeToJson(MainActivity.this, jsonObject);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onGetTimetableFinished() {
                                            AToast.normal("刷新课表成功！");
                                            dialog.dismiss();
                                            initTimetableView();
                                            initData();
                                        }

                                        @Override
                                        public void onGetSemesters(String json) {
                                            try {
                                                TimetableHelper.writeSemesterFile(MainActivity.this, json);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    })
                                    .login(captcha, TimetableHelper.getCurrentSemesterCode());
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
                if (!TimetableHelper.isVisitorMode() && !SPHelper.getBoolean("logined", false)) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
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
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        dialogFragment.show(fragmentTransaction, "setting");
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

    @Override
    public void onBackPressed() {
        if (TimetableHelper.isVisitorMode()) {
            super.onBackPressed();
            finish();
            return;
        }
        if (System.currentTimeMillis() - firstTime > 2000) {
            AToast.normal("再次点击退出！");
            firstTime = System.currentTimeMillis();
        } else {
            TimetableHelper.closeVisitorMode();
            ActivityCollector.finishAll();
        }
    }

    @Override
    public void onError(String errMsg) {
        AToast.normal("检查更新出错！");
    }

    @Override
    public void onGetLatestVersion(UpdateBean bean) {
        AToast.normal("开始更新！");
//        http://tt.shouji.com.cn/wap/down/soft?id=1555815
        UpdateDialogFragment.newInstance(bean).show(getSupportFragmentManager());
    }

    @Override
    public void isLatestVersion() {
        AToast.normal("软件已是最新版！");
    }
}
