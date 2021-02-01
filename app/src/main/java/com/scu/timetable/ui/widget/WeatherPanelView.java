package com.scu.timetable.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.felix.atoast.library.AToast;
import com.scu.timetable.R;
import com.scu.timetable.bean.ScuSubject;
import com.scu.timetable.bean.SemesterInfo;
import com.scu.timetable.utils.TimetableHelper;
import com.xw.repo.supl.ISlidingUpPanel;
import com.xw.repo.supl.SlidingUpPanelLayout;
import com.zhuangfei.timetable.TimetableView;
import com.zhuangfei.timetable.listener.ISchedule;
import com.zhuangfei.timetable.listener.OnSlideBuildAdapter;
import com.zhuangfei.timetable.model.Schedule;
import com.zpj.utils.StatusBarUtils;

import java.util.List;

public class WeatherPanelView extends BaseWeatherPanelView
        implements View.OnClickListener {

    private View mContentLayout;
    private View mMenuLayout;
    private TimetableView mTimetableView;
    private ImageView mCollapseImg;
    private ImageView mSettingsImg;

    private View mCollapseLayout;
    private TextView mCityTextCollapse;

    private int mBgColor = -1;

    private int currentWeek;

    public WeatherPanelView(Context context) {
        this(context, null);
    }

    public WeatherPanelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeatherPanelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.content_weather_panel_view, this, true);
        mContentLayout = findViewById(R.id.panel_content_layout);
        mMenuLayout = findViewById(R.id.panel_menu_layout);
        mTimetableView = findViewById(R.id.view_timetable);
        mCollapseImg = findViewById(R.id.panel_collapse_img);
        mSettingsImg = findViewById(R.id.panel_settings_img);
        mCollapseLayout = findViewById(R.id.panel_collapse_layout);
        mCityTextCollapse = findViewById(R.id.panel_city_text_collapse);
        mCollapseImg.setOnClickListener(this);
        mSettingsImg.setOnClickListener(this);

        int statusBarHeight = StatusBarUtils.getStatusBarHeight();
        mMenuLayout.setPadding(0, statusBarHeight, 0, 0);
        mTimetableView.setPadding(0, statusBarHeight, 0, 0);

        currentWeek = TimetableHelper.getCurrentWeek();

        mTimetableView.curWeek(currentWeek)
                .curTerm("大二下学期")
                .setSundayIsFirstDay(TimetableHelper.sundayIsFirstDay())
                .callback(new ISchedule.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, List<Schedule> scheduleList) {
                        ScuSubject scuSubject = (ScuSubject) scheduleList.get(0).getScheduleEnable();
                        AToast.normal(scuSubject.getCourseName());
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
//                        titleTextView.setText("第" + curWeek + "周");
                    }
                })
                .showView();

        checkVisibilityOfViews();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.panel_collapse_img:
                if (mCollapseImg.getAlpha() == 1) {
                    ((SlidingUpPanelLayout) getParent()).collapsePanel();
                }

                break;
            case R.id.panel_settings_img:
                if (mSettingsImg.getAlpha() >= 1) {
                    Toast.makeText(getContext(), "settings", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    @Override
    public void setSlideState(@SlidingUpPanelLayout.SlideState int slideState) {
        super.setSlideState(slideState);

        checkVisibilityOfViews();
    }

    @Override
    public void onSliding(@NonNull ISlidingUpPanel panel, int top, int dy, float slidedProgress) {
        super.onSliding(panel, top, dy, slidedProgress);

        if (dy < 0) { // 向上
            float radius = getRadius();
            if (radius > 0 && MAX_RADIUS >= top) {
                setRadius(top);
            }

            float alpha = mCollapseLayout.getAlpha();
            if (alpha > 0f && top < 200) {
                alpha += dy / 200.0f;
                mCollapseLayout.setAlpha(alpha < 0 ? 0 : alpha); // 逐隐
            }

            alpha = mMenuLayout.getAlpha();
            if (alpha < 1f && top < 100) {
                alpha -= dy / 100.0f;
                mMenuLayout.setAlpha(alpha > 1 ? 1 : alpha); // 逐显
            }

            alpha = mTimetableView.getAlpha();
            if (alpha < 1f) {
                alpha -= dy / 1000.0f;
                mTimetableView.setAlpha(alpha > 1 ? 1 : alpha); // 逐显
            }
        } else { // 向下
            float radius = getRadius();
            if (radius < MAX_RADIUS) {
                radius += top;
                setRadius(radius > MAX_RADIUS ? MAX_RADIUS : radius);
            }

            float alpha = mCollapseLayout.getAlpha();
            if (alpha < 1f) {
                alpha += dy / 800.0f;
                mCollapseLayout.setAlpha(alpha > 1 ? 1 : alpha); // 逐显
            }

            alpha = mMenuLayout.getAlpha();
            if (alpha > 0f) {
                alpha -= dy / 100.0f;
                mMenuLayout.setAlpha(alpha < 0 ? 0 : alpha); // 逐隐
            }

            alpha = mTimetableView.getAlpha();
            if (alpha > 0f) {
                alpha -= dy / 1000.0f;
                mTimetableView.setAlpha(alpha < 0 ? 0 : alpha); // 逐隐
            }
        }
    }

    @Override
    public void setSemesterInfo(SemesterInfo semesterInfo) {
        mCityTextCollapse.setText(semesterInfo.getSemesterName());

        mTimetableView.source(TimetableHelper.getSubjects(getContext(), semesterInfo))
                .cornerAll(16)
                .isShowWeekends(TimetableHelper.isShowWeekends())
                .isShowNotCurWeek(TimetableHelper.isShowNotCurWeek())
                .showView();

        OnSlideBuildAdapter listener = (OnSlideBuildAdapter) mTimetableView.onSlideBuildListener();
        listener.setBackground(Color.TRANSPARENT);
        listener.setTextColor(Color.BLACK);
        listener.setTimeTextColor(getResources().getColor(R.color.black_transparent_50));
        mTimetableView.updateSlideView();


        checkVisibilityOfViews();
    }

    private void checkVisibilityOfViews() {
        if (mBgColor == -1) {
            mBgColor = Color.argb(255, (int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
        }
        mContentLayout.setBackgroundColor(mBgColor);

        if (mSlideState == SlidingUpPanelLayout.COLLAPSED) {
            setRadius(MAX_RADIUS);

            mMenuLayout.setAlpha(0f);
            mTimetableView.setAlpha(0f);
            mCollapseLayout.setAlpha(1f);
        } else if (mSlideState == SlidingUpPanelLayout.EXPANDED) {
            setRadius(0);

            mMenuLayout.setAlpha(1f);
            mTimetableView.setAlpha(1f);
            mCollapseLayout.setAlpha(0f);
        }
    }
}
