package com.scu.timetable.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scu.timetable.R;
import com.scu.timetable.bean.ScuSubject;

import java.util.ArrayList;
import java.util.List;

/**
 * 周次选择栏自定义View.
 * 每一项均为PerWeekView<br/>
 */
public class WeekView extends LinearLayout implements ViewTreeObserver.OnGlobalLayoutListener {

    private static final String TAG = "WeekView";
    LayoutInflater mInflate;

    //周次的容器
    LinearLayout container;

    private HorizontalScrollView scrollView;

    //跟布局
    LinearLayout root;

    //左侧按钮
    LinearLayout leftlayout;

    //数据
    private List<ScuSubject> dataSource;

    //布局保存
    private List<LinearLayout> layouts;
    private List<TextView> textViews;

    //当前周
    private int curWeek = 1;
    private int preIndex = 1;

    //多少项
    private int itemCount = 20;

    private OnWeekItemClickedListener onWeekItemClickedListener;
    private OnWeekLeftClickedListener onWeekLeftClickedListener;

    public WeekView(Context context) {
        this(context, null);
    }

    /**
     * 获取Item点击监听
     *
     * @return
     */
    public OnWeekItemClickedListener onWeekItemClickedListener() {
        return onWeekItemClickedListener;
    }

    /**
     * 设置Item点击监听
     *
     * @param onWeekItemClickedListener
     * @return
     */
    public WeekView callback(OnWeekItemClickedListener onWeekItemClickedListener) {
        this.onWeekItemClickedListener = onWeekItemClickedListener;
        return this;
    }

    /**
     * 获取左侧按钮点击监听
     *
     * @return
     */
    public OnWeekLeftClickedListener onWeekLeftClickedListener() {
        return onWeekLeftClickedListener;
    }

    /**
     * 设置左侧按钮点击监听
     *
     * @param onWeekLeftClickedListener
     * @return
     */
    public WeekView callback(OnWeekLeftClickedListener onWeekLeftClickedListener) {
        this.onWeekLeftClickedListener = onWeekLeftClickedListener;
        return this;
    }

    /**
     * 设置当前周
     *
     * @param curWeek
     * @return
     */
    public WeekView curWeek(int curWeek) {
        if (curWeek < 1) {
            curWeek = 1;
        }
        this.curWeek = curWeek;
        return this;
    }

    /**
     * 设置项数
     *
     * @param count
     * @return
     */
    public WeekView itemCount(int count) {
        if (count <= 0) {
            return this;
        }
        this.itemCount = count;
        return this;
    }

    public int itemCount() {
        return itemCount;
    }

    /**
     * 设置数据源
     *
     * @param scheduleList
     * @return
     */
    public WeekView data(List<ScuSubject> scheduleList) {
        if (scheduleList == null) {
            return null;
        }
        this.dataSource = scheduleList;
        return this;
    }

    /**
     * 获取数据源
     *
     * @return
     */
    public List<ScuSubject> dataSource() {
        if (dataSource == null) {
            dataSource = new ArrayList<>();
        }
        return dataSource;
    }

    public WeekView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInflate = LayoutInflater.from(context);
        initView();
    }

    private void initView() {
        mInflate.inflate(R.layout.layout_weekview, this);
        container = findViewById(R.id.id_weekview_container);
        scrollView = findViewById(R.id.scroll_view);
        root = findViewById(R.id.id_root);
        leftlayout = findViewById(R.id.id_weekview_leftlayout);
//        root.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    /**
     * 初次构建时调用，显示周次选择布局
     */
    public WeekView showView() {
        if (curWeek < 1) {
            curWeek(1);
        }
        if (curWeek > itemCount()) {
            curWeek = itemCount;
        }

        container.removeAllViews();
        layouts = new ArrayList<>();
        textViews = new ArrayList<>();

        leftlayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onWeekLeftClickedListener().onWeekLeftClicked();
            }
        });

        for (int i = 1; i <= itemCount; i++) {
            final int tmp = i;
            View view = mInflate.inflate(R.layout.item_weekview, null);
            final LinearLayout perLayout = view.findViewById(R.id.id_perweekview_layout);
            TextView weekText = view.findViewById(R.id.id_weektext);
            TextView bottomText = view.findViewById(R.id.id_weektext_bottom);

            weekText.setText("第" + i + "周");
            if (i == curWeek) {
                bottomText.setText("(本周)");
                perLayout.setBackground(getContext().getResources().getDrawable(R.drawable.weekview_thisweek));
            } else {
                perLayout.setBackgroundColor(Color.WHITE);
            }
            PerWeekView perWeekView = view.findViewById(R.id.id_perweekview);
            perWeekView.setData(dataSource(), i);
            perLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    resetBackground();
                    preIndex = tmp;
                    if (tmp != curWeek) {
                        Drawable bg = getContext().getResources().getDrawable(R.drawable.weekview_thisweek);
                        bg.setAlpha(0x80);
                        perLayout.setBackground(bg);
                    }
                    onWeekItemClickedListener().onWeekClicked(tmp);
                }
            });


            layouts.add(perLayout);
            textViews.add(bottomText);
            container.addView(view);
        }
        if (curWeek > 0 && curWeek <= layouts.size()) {
            layouts.get(curWeek - 1).setBackground(getContext().getResources().getDrawable(R.drawable.weekview_current));
        }
        return this;
    }

    /**
     * 当前周被改变后可以调用该方式修正一下底部的文本
     *
     * @return
     */
    public WeekView updateView() {
        if (layouts == null || layouts.size() == 0) {
            return this;
        }
        if (textViews == null || textViews.size() == 0) {
            return this;
        }

        for (int i = 0; i < layouts.size(); i++) {
            if (curWeek - 1 == i) {
                textViews.get(i).setText("(本周)");
            } else {
                textViews.get(i).setText("");
            }
//            layouts.get(i).setBackgroundColor(getContext().getResources().getColor(R.color.app_course_chooseweek_bg));
        }

        if (curWeek > 0 && curWeek <= layouts.size()) {
            layouts.get(curWeek - 1).setBackground(getContext().getResources().getDrawable(R.drawable.weekview_current));
        }
        return this;
    }

    /**
     * 重置背景色
     */
    public void resetBackground() {
        if (preIndex <= layouts.size()) {
            layouts.get(preIndex - 1).setBackgroundColor(Color.WHITE);
        }
        if (curWeek <= layouts.size()) {
            layouts.get(curWeek - 1).setBackground(getContext().getResources().getDrawable(R.drawable.weekview_current));
        }
    }

    /**
     * 隐藏左侧设置当前周的控件
     */
    public WeekView hideLeftLayout() {
        leftlayout.setVisibility(View.GONE);
        return this;
    }

    /**
     * 设置控件的可见性
     *
     * @param isShow true:显示，false:隐藏
     */
    public WeekView isShow(boolean isShow) {
        if (isShow) {
            root.getViewTreeObserver().addOnGlobalLayoutListener(this);
            root.setVisibility(VISIBLE);
//            View itemView = container.getChildAt(curWeek - 1);
//            int itemWidth = itemView.getWidth();
//            int scrollViewWidth = scrollView.getMeasuredWidth();
//            Log.d("itemWidth", "itemWidth=" + itemWidth);
//            Log.d("scrollViewWidth", "scrollViewWidth=" + scrollViewWidth);
//            scrollView.smoothScrollTo(itemView.getLeft() - (scrollViewWidth / 2 - itemWidth / 2), 0);
        } else {
            root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            root.setVisibility(GONE);
            if (layouts != null) {
                resetBackground();
            }
        }
        return this;
    }

    /**
     * 判断该控件是否显示
     *
     * @return
     */
    public boolean isShowing() {
        return root.getVisibility() != GONE;
    }

    @Override
    public void onGlobalLayout() {
//        Toast.makeText(getContext(), "onGlobalLayout", Toast.LENGTH_SHORT).show();
        View itemView = container.getChildAt(curWeek - 1);
        int itemWidth = itemView.getWidth();
        int scrollViewWidth = scrollView.getMeasuredWidth();
        Log.d("itemWidth", "itemWidth=" + itemWidth);
        Log.d("scrollViewWidth", "scrollViewWidth=" + scrollViewWidth);
        scrollView.smoothScrollTo(itemView.getLeft() - (scrollViewWidth / 2 - itemWidth / 2), 0);
        root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    /**
     * WeekView的Item点击监听器
     */
    public interface OnWeekItemClickedListener{
        /**
         * 当Item被点击时回调
         * @param week 选择的周次
         */
        void onWeekClicked(int week);
    }

    /**
     * WeekView的左侧（设置当前周）的点击监听器
     */
    public interface OnWeekLeftClickedListener{
        /**
         * 当"设置当前周"按钮被点击时回调
         */
        void onWeekLeftClicked();
    }

}
