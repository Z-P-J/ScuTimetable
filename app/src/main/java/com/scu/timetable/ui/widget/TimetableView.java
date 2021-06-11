package com.scu.timetable.ui.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.OverScroller;
import android.widget.TextView;

import com.scu.timetable.R;
import com.scu.timetable.bean.ScuSubject;
import com.scu.timetable.utils.DateUtil;
import com.scu.timetable.utils.TimetableHelper;
import com.zpj.utils.ColorUtils;
import com.zpj.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimetableView extends FrameLayout {

    private static final String TAG = "TimetableView";

    private final List<ScuSubject> mScheduleList = new ArrayList<>();
    private int[][] mOccupy;

    private final ColorPool mColorPool = new ColorPool();
    private VelocityTracker mVelocityTracker;
    private final int mMaxVelocity;
    private final ViewFlinger mViewFlinger;

    private int mChildCount;

    private int mLastDeltaTop = 0;
    private float mDownX;
    private float mDownY;
    private float mLastY;
    private boolean isMove;


    private float mSubjectHeight;
    private float mSubjectSpace;
    private float mSubjectCornerRadius = 16;
    private int mSlideWidth;
    private int mHeaderHeight;

    private boolean mShowWeekends = true;
    private int mColomun = 7;
    private boolean mShowNotCurWeek = true;
    private boolean mSundayIsFirstDay;
    private boolean mShowTime = true;
    private int mCurrWeek = 17;
    private OnItemClickListener mOnItemClickListener;


    public TimetableView(Context context) {
        this(context, null);
    }

    public TimetableView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimetableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mSubjectSpace = 3 * context.getResources().getDisplayMetrics().density;
//        mColorPool = new ScheduleColorPool(context);
        mSubjectHeight = 64 * getContext().getResources().getDisplayMetrics().density;
        mSlideWidth = (int) (36 * getContext().getResources().getDisplayMetrics().density);
        mHeaderHeight = mSlideWidth;
        mMaxVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        mViewFlinger = new ViewFlinger(context);
    }

    public void setScheduleList(List<ScuSubject> list) {
        this.mScheduleList.clear();
        this.mScheduleList.addAll(list);
        mChildCount = mScheduleList.size();

        mOccupy = new int[7][12];

        for (ScuSubject subject : mScheduleList) {
            int x = subject.getDay() - 1;
            int max = 0;
            for (int i = 0; i < subject.getStep(); i++) {
                int y = subject.getStart() - 1 + i;
                int count = ++mOccupy[x][y];
                mOccupy[x][y] = count;
                max = Math.max(max, count);
            }
            mOccupy[x][subject.getStart() - 1] = max;
            subject.setIndex(--max);
        }

        initChildren(true);
        mLastDeltaTop = 0;
        requestLayout();
    }

    public void setSubjectHeight(float mSubjectHeight) {
        this.mSubjectHeight = mSubjectHeight;
    }

    public void setSubjectSpace(float mSpace) {
        this.mSubjectSpace = mSpace;
    }

    public void setSubjectCornerRadius(float mSubjectCornerRadius) {
        this.mSubjectCornerRadius = mSubjectCornerRadius;
    }

    public void setHeaderHeight(int mHeaderHeight) {
        this.mHeaderHeight = mHeaderHeight;
    }

    public void setSlideWidth(int mSlideWidth) {
        this.mSlideWidth = mSlideWidth;
    }

    public void setSundayIsFirstDay(boolean mSundayIsFirstDay) {
        this.mSundayIsFirstDay = mSundayIsFirstDay;
    }

    public void setShowWeekends(boolean mShowWeekends) {
        this.mShowWeekends = mShowWeekends;
        mColomun = mShowWeekends ? 7 : 5;
    }

    public void setShowNotCurrentWeek(boolean mShowNotCurWeek) {
        this.mShowNotCurWeek = mShowNotCurWeek;
    }

    public void setShowTime(boolean mShowTime) {
        this.mShowTime = mShowTime;
    }

    public void setCurrentWeek(int mCurrWeek) {
        this.mCurrWeek = mCurrWeek;
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

//    private List<ScuSubject> getColorReflect(List<ScuSubject> schedules) {
//        if (schedules == null || schedules.size() == 0) {
//            return schedules;
//        }
//
//        // 保存课程名、颜色的对应关系
//        Map<String, Integer> colorMap = new HashMap<>();
//        int colorCount = 1;
//
//        //开始转换
//        for (int i = 0; i < schedules.size(); i++) {
//            ScuSubject scuSubject = schedules.get(i);
//            //计算课程颜色
//            int color;
//            if (colorMap.containsKey(scuSubject.getCourseName())) {
//                color = colorMap.get(scuSubject.getCourseName());
//            } else {
//                colorMap.put(scuSubject.getCourseName(), colorCount);
//                color = colorCount;
//                colorCount++;
//            }
//            scuSubject.setColorRandom(color);
//        }
//
//        return schedules;
//    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        initChildren(false);
        layoutChildren(mLastDeltaTop, false);
    }

    private void layoutChildren(int deltaTop, boolean isScroll) {
        mLastDeltaTop = deltaTop;
        float childWidth = (float) (getWidth() - mSlideWidth) / mColomun;
        float childHeight = mSubjectHeight;
//        int widthSpec = MeasureSpec.makeMeasureSpec((int) (childWidth - mSubjectSpace), MeasureSpec.EXACTLY);
        int count = 0;
        for (int i = 0; i < mScheduleList.size(); i++) {
            ScuSubject schedule = mScheduleList.get(i);

            int day = schedule.getDay();
            if (mShowWeekends) {
                if (!mSundayIsFirstDay) { // getSundayIsFirstDay
                    day -= 1;
                    if (day == 0) {
                        day = 7;
                    }
                }
            } else if (day == 1 || day == 7) {
                continue;
            } else {
                day--;
            }
            count++;

            int n = mOccupy[schedule.getDay() - 1][schedule.getStart() - 1];


            int step = schedule.getStep();
            int height = (int) (step * childHeight - mSubjectSpace);
            View child = getChildAt(i);

            if (mShowNotCurWeek) {
                child.setVisibility(VISIBLE);
            } else if (schedule.isThisWeek(mCurrWeek)) {
                child.setVisibility(VISIBLE);
            } else {
                child.setVisibility(INVISIBLE);
            }

            int heightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            float width = childWidth / n - mSubjectSpace;
            int widthSpec = MeasureSpec.makeMeasureSpec((int) width, MeasureSpec.EXACTLY);
            child.measure(widthSpec, heightSpec);

            int childTop = (int) ((schedule.getStart() - 1) * childHeight + mSubjectSpace / 2f + deltaTop) + mHeaderHeight;
//            int childLeft = (int) (mSlideWidth + (--day) * childWidth + mSubjectSpace / 2f);
//            int childRight = (int) (childLeft + childWidth - mSubjectSpace);
            int childLeft = (int) (mSlideWidth + (--day) * childWidth + mSubjectSpace / 2f + childWidth / n * schedule.getIndex());
            int childRight = (int) (childLeft + width);
            int childBottom = (int) (childTop + height);

            child.layout(childLeft, childTop, childRight, childBottom);
        }
        mChildCount = count;
        int slideWidthSpec = MeasureSpec.makeMeasureSpec(mSlideWidth, MeasureSpec.EXACTLY);
        int slideHeightSpec = MeasureSpec.makeMeasureSpec((int) mSubjectHeight, MeasureSpec.EXACTLY);
        for (int i = 0; i < 12; i++) {
            View child = getChildAt(mChildCount + i);
            TextView timeTextView = child.findViewById(R.id.item_slide_time);
            TextView tvEndTime = child.findViewById(R.id.tv_end_time);
            timeTextView.setVisibility(mShowTime ? VISIBLE : GONE);
            tvEndTime.setVisibility(mShowTime ? VISIBLE : GONE);
            child.measure(slideWidthSpec, slideHeightSpec);
            int slideTop = (int) (i * mSubjectHeight + deltaTop) + mHeaderHeight;

            child.layout(0, slideTop, mSlideWidth, (int) (slideTop + mSubjectHeight));
        }

        if (isScroll) {
            return;
        }

        int space = (int) mSubjectSpace;
        int headerHeight = mHeaderHeight - space;
        int headerHeightSpec = MeasureSpec.makeMeasureSpec(headerHeight, MeasureSpec.EXACTLY);
        int widthSpec = MeasureSpec.makeMeasureSpec((int) childWidth, MeasureSpec.EXACTLY);
        for (int i = 0; i < mColomun; i++) {
            View child = getChildAt(mChildCount + 12 + i);
            child.measure(widthSpec, headerHeightSpec);
            int headerLeft = (int) (i * childWidth) + mSlideWidth;

            child.layout(headerLeft, 0, (int) (headerLeft + childWidth), headerHeight);
        }

        View monthView = getChildAt(getChildCount() - 1);
        monthView.measure(slideWidthSpec, headerHeightSpec);
        monthView.layout(0, 0, mSlideWidth, headerHeight);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                }
                mVelocityTracker.addMovement(ev);
                mViewFlinger.stop();
                mDownX = ev.getRawX();
                mDownY = ev.getRawY();
                mLastY = mDownY;
                isMove = false;
                return false;
            case MotionEvent.ACTION_MOVE:
                float deltaX = ev.getRawX() - mDownX;
                float deltaY = ev.getRawY() - mDownY;
                if (isMove || Math.abs(deltaY / deltaX) > 1f) {
                    isMove = true;
                    return true;
                }
                return false;
            case MotionEvent.ACTION_UP:
                if (isMove) {
                    return true;
                }
                break;
        }
//        return super.onInterceptTouchEvent(ev);
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent event=" + MotionEvent.actionToString(event.getAction()));
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float y = event.getRawY();
                float dy = y - mLastY;
                mLastY = y;

                scrollBy(dy);
                break;
            case MotionEvent.ACTION_UP:

                mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
                float velocityY = mVelocityTracker.getYVelocity();
                mViewFlinger.fling(velocityY);

                mVelocityTracker.clear();
                mVelocityTracker.recycle();
                mVelocityTracker = null;

                break;
        }
        return true;
    }

    private void scrollBy(float dy) {
        layoutChildren((int) (getChildAt(mScheduleList.size()).getTop() - mHeaderHeight + dy), true);
    }

    private void initChildren(boolean force) {
        if (force || getChildCount() - (12 + mColomun + 1) != mScheduleList.size()) {
            removeAllViews();
            for (int i = 0; i < mScheduleList.size(); i++) {
                ScuSubject subject = mScheduleList.get(i);
                if (!mShowWeekends && (subject.getDay() == 1 || subject.getDay() == 7)) {
                    continue;
                }
                addView(onCreateSubjectView(subject));
            }
            mChildCount = getChildCount();
            for (int i = 0; i < TimetableHelper.TIMES_1.length; i++) {
                String start = TimetableHelper.TIMES_1[i];
                String end = TimetableHelper.TIMES_END_1[i];
                View child = onCreateTimeSlideView(i, start, end);
                addView(child);
            }
            String[] dateArray = getStringArray();
            List<String> weekDates = DateUtil.getDateStringFromWeek(mCurrWeek, mCurrWeek, mSundayIsFirstDay);

            //获取周几，1->7
            Calendar now = Calendar.getInstance();
            //一周第一天是否为星期天
            boolean isFirstSunday = (now.getFirstDayOfWeek() == Calendar.SUNDAY);

            int weekDay = now.get(Calendar.DAY_OF_WEEK);
            if (!mShowWeekends || !mSundayIsFirstDay) {
                weekDay = weekDay - 1;
                if (weekDay == 0) {
                    weekDay = 7;
                }
            }
            int delta = (!mShowWeekends && mSundayIsFirstDay ? 2 : 1);
            for (int i = 0; i < mColomun; i++) {
                View child = onCreateDateHeaderView(dateArray[i], weekDates.get(0), weekDates.get(i + delta), (i + 1) == weekDay);
                addView(child);
            }


            TextView weekMonth = new TextView(getContext());
            weekMonth.getPaint().setFakeBoldText(true);
            weekMonth.setGravity(Gravity.CENTER);
            weekMonth.setTextSize(12);
            weekMonth.setText(weekDates.get(0) + "\n月");
            weekMonth.setTextColor(Color.BLACK);
            weekMonth.setBackgroundColor(Color.WHITE);
            addView(weekMonth);

        }
    }

    public String[] getStringArray() {
        if (mSundayIsFirstDay && mShowWeekends) {
            return new String[]{"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        }
        return new String[]{"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
    }

    public View onCreateSubjectView(ScuSubject subject) {
        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(mSubjectCornerRadius);

        boolean isThisWeek = subject.isThisWeek(mCurrWeek);


        int bgColor;
        int textColor;

        if (isThisWeek) {
//            bgColor = mColorPool.getColorAutoWithAlpha(subject.getColorRandom(), 1);
            bgColor = mColorPool.getColor(subject.getCourseName());
            textColor = ColorUtils.darkenColor(bgColor);
            gd.setAlpha(0x20);
        } else {
            bgColor = Color.parseColor("#EEEEEE");
            textColor = Color.WHITE;
            gd.setAlpha(255);
        }

        gd.setColor(bgColor);

        TextView textView = new TextView(getContext());
        if (mOccupy[subject.getDay() - 1][subject.getStart() - 1] > 1) {
            textView.setTextSize(10);
        } else {
            textView.setTextSize(12);
        }
        textView.setGravity(Gravity.CENTER);
//        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_timetable, null, false);
//        TextView textView = view.findViewById(R.id.id_course_item_course);
        textView.setText(subject.getCourseName() + "@" + subject.getRoom());
        textView.setTextColor(textColor);
        textView.setBackground(gd);
        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, subject);
                }
            }
        });
        return textView;
    }

    public View onCreateTimeSlideView(int index, String start, String end) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_slide_time, null, false);
        TextView numberTextView = view.findViewById(R.id.item_slide_number);
        TextView timeTextView = view.findViewById(R.id.item_slide_time);
        TextView tvEndTime = view.findViewById(R.id.tv_end_time);
        numberTextView.setText("" + (index + 1));
        timeTextView.setText(start);
        tvEndTime.setText(end);
        return view;
    }

    public View onCreateDateHeaderView(String week, String month, String day, boolean isActive) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.item_dateview, null, false);
        TextView dayTextView = v.findViewById(R.id.id_week_day);
        dayTextView.setTextColor(Color.GRAY);
        dayTextView.setText(week);

        TextView monthTextView = v.findViewById(R.id.id_week_date);
        monthTextView.setTextColor(Color.GRAY);
        monthTextView.setText(month + "/" + day);

        if (isActive) {
            Resources resources = getResources();
            dayTextView.setTextColor(resources.getColor(R.color.colorPrimary));
            monthTextView.setTextColor(resources.getColor(R.color.colorPrimary));
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(Color.parseColor("#f8f8f8"));
            drawable.setCornerRadius(ScreenUtils.dp2px(4));
            v.setBackground(drawable);
        } else {
            v.setBackgroundColor(Color.WHITE);
        }

        return v;
    }


    private class ViewFlinger implements Runnable {

        private final OverScroller mScroller;
        private int mLastFlingY = 0;

        private ValueAnimator animator;

        public ViewFlinger(Context context) {
            mScroller = new OverScroller(context, t -> {
                t -= 1.0f;
                return t * t * t * t * t + 1.0f;
            });
        }

        @Override
        public void run() {
            if (mScroller.computeScrollOffset()) {
                final int y = mScroller.getCurrY();
                int dy = y - mLastFlingY;
                mLastFlingY = y;

                // 第一个view越界回弹
                View firstView = getChildAt(mChildCount);
                View lastView = getChildAt(mChildCount + 11);
                if (firstView.getTop() > mHeaderHeight) {
                    startSpringAnimation(mHeaderHeight - firstView.getTop());
                    return;
                } else if (lastView.getBottom() < getHeight()) {
                    startSpringAnimation(getHeight() - lastView.getBottom());
                    return;
                }
                scrollBy(dy);

                postOnAnimation();
            }
        }

        private void startSpringAnimation(int dy) {
            stop();
            animator = ValueAnimator.ofFloat(0, 1f);
            animator.setInterpolator(new FastOutSlowInInterpolator());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                float last = 0;

                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float percent = (float) (valueAnimator.getAnimatedValue());
                    float temp = percent * dy;
                    float delta = temp - last;
                    last = temp;
                    scrollBy(delta);
                }
            });
            animator.setDuration(300);
            animator.start();
        }

        public void stop() {
            if (animator != null) {
                animator.cancel();
                animator = null;
            }
            removeCallbacks(this);
            mScroller.forceFinished(true);
        }

        public void fling(float velocityY) {
            mScroller.fling(0, mScroller.getCurrY(), 0, (int) velocityY, 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
            postOnAnimation();
        }

        private void postOnAnimation() {
            removeCallbacks(this);
            ViewCompat.postOnAnimation(TimetableView.this, this);
        }

    }

    public interface OnItemClickListener {
        void onItemClick(View view, ScuSubject subject);
    }

}
