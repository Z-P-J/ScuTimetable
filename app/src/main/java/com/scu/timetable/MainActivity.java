package com.scu.timetable;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scu.timetable.model.MySubject;
import com.scu.timetable.utils.SubjectUtil;
import com.scu.timetable.utils.content.SPHelper;
import com.zhuangfei.timetable.TimetableView;
import com.zhuangfei.timetable.listener.ISchedule;
import com.zhuangfei.timetable.listener.IWeekView;
import com.zhuangfei.timetable.model.Schedule;
import com.zhuangfei.timetable.view.WeekView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_func);

        currentWeek = SPHelper.getInt("currrent_weak", 1);

        titleTextView = findViewById(R.id.id_title);
        layout = findViewById(R.id.id_layout);
        layout.setOnClickListener(this);
        initTimetableView();

        requestData();
    }

    /**
     * 2秒后刷新界面，模拟网络请求
     */
    private void requestData() {
        mySubjects = SubjectUtil.getSubjects(this);
        mWeekView.source(mySubjects).showView();
        mTimetableView.source(mySubjects)
                .isShowWeekends(false)
                .callback(new ISchedule.OnScrollViewBuildListener() {
                    @Override
                    public View getScrollView(LayoutInflater mInflate) {
                        return mInflate.inflate(R.layout.custom_myscrollview, null, false);
                    }
                })
                .showView();
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
                .callback(new IWeekView.OnWeekItemClickedListener() {
                    @Override
                    public void onWeekClicked(int week) {
                        int cur = mTimetableView.curWeek();
                        //更新切换后的日期，从当前周cur->切换的周week
                        mTimetableView.onDateBuildListener()
                                .onUpdateDate(cur, week);
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
                .curTerm("大三下学期")
                .callback(new ISchedule.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, List<Schedule> scheduleList) {
                        display(scheduleList);
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
        final String[] items = new String[20];
        int itemCount = mWeekView.itemCount();
        for (int i = 0; i < itemCount; i++) {
            items[i] = "第" + (i + 1) + "周";
        }
        target = -1;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设置当前周");
        builder.setSingleChoiceItems(items, mTimetableView.curWeek() - 1,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        target = i;
                    }
                });
        builder.setPositiveButton("设置为当前周", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (target != -1) {
                    mWeekView.curWeek(target + 1).updateView();
                    mTimetableView.changeWeekForce(target + 1);
                }
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create().show();
    }

    /**
     * 显示内容
     *
     * @param beans
     */
    protected void display(List<Schedule> beans) {
        String str = "";
        for (Schedule bean : beans) {
            str += bean.getName() + "," + bean.getWeekList().toString() + "," + bean.getStart() + "," + bean.getStep() + "\n";
        }
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.id_layout) {//如果周次选择已经显示了，那么将它隐藏，更新课程、日期
            //否则，显示
            if (mWeekView.isShowing()) {
                hideWeekView();
            } else {
                showWeekView();
            }
        }
    }

    /**
     * 隐藏周次选择，此时需要将课表的日期恢复到本周并将课表切换到当前周
     */
    public void hideWeekView() {
        mWeekView.isShow(false);
        titleTextView.setTextColor(Color.WHITE);
        int cur = mTimetableView.curWeek();
        Toast.makeText(this, "cur=" + cur, Toast.LENGTH_SHORT).show();
        mTimetableView.onDateBuildListener()
                .onUpdateDate(cur, cur);
        mTimetableView.changeWeekOnly(cur);
    }

    public void showWeekView() {
        mWeekView.isShow(true);
        titleTextView.setTextColor(Color.RED);
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - firstTime > 2000) {
            Toast.makeText(this, "再次点击退出！", Toast.LENGTH_SHORT).show();
            firstTime = System.currentTimeMillis();
        } else {
            ActivityCollector.finishAll();
        }
    }
}
