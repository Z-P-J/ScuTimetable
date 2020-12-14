package com.scu.timetable.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.scu.timetable.R;
import com.scu.timetable.model.SemesterInfo;
import com.scu.timetable.ui.fragment.base.SkinFragment;
import com.scu.timetable.ui.view.BaseWeatherPanelView;
import com.scu.timetable.ui.view.WeatherPanelView;
import com.scu.timetable.utils.TimetableHelper;
import com.xw.repo.supl.ISlidingUpPanel;
import com.xw.repo.supl.SlidingUpPanelLayout;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

import static com.xw.repo.supl.SlidingUpPanelLayout.COLLAPSED;
import static com.xw.repo.supl.SlidingUpPanelLayout.EXPANDED;
import static com.xw.repo.supl.SlidingUpPanelLayout.HIDDEN;

public class TestFragment extends SkinFragment {

    private final List<SemesterInfo> mSemesterList = new ArrayList<>();

    private SlidingUpPanelLayout mSlidingUpPanelLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_test;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        mSlidingUpPanelLayout = findViewById(R.id.sliding_up_panel_layout);

        mSlidingUpPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListenerAdapter() {
            @Override
            public void onPanelExpanded(ISlidingUpPanel panel) {
//                if (panel instanceof BaseWeatherPanelView) {
//                    int count = mSlidingUpPanelLayout.getChildCount();
//                    // 如果被展开的Panel不是距离屏幕顶部最近（floor值最大）那个，做如下处理，再被收起时已是距屏幕顶部最近
//                    if (((BaseWeatherPanelView) panel).getFloor() != count - 1) {
//
//                        mSlidingUpPanelLayout.removeView(panel.getPanelView());
//                        mSlidingUpPanelLayout.addView(panel.getPanelView(), 1);
//
//                        for (int i = 1; i < count; i++) {
//                            BaseWeatherPanelView child = (BaseWeatherPanelView) mSlidingUpPanelLayout.getChildAt(i);
//                            child.setFloor(count - i);
//                        }
//                        mSlidingUpPanelLayout.requestLayout();
//                    }
//                }

                int count = mSlidingUpPanelLayout.getChildCount();
                for (int i = 1; i < count; i++) {
                    ISlidingUpPanel panel2 = (ISlidingUpPanel) mSlidingUpPanelLayout.getChildAt(i);
                    if (panel2 == panel) {
                        panel2.getPanelView().setEnabled(false);
                    } else {
                        panel2.setSlideState(HIDDEN);
                        panel2.getPanelView().setEnabled(true);
                    }
                }
            }

            @Override
            public void onPanelCollapsed(ISlidingUpPanel panel) {
                int count = mSlidingUpPanelLayout.getChildCount();
                for (int i = 1; i < count; i++) {
                    panel = (ISlidingUpPanel) mSlidingUpPanelLayout.getChildAt(i);
                    panel.setSlideState(COLLAPSED);
                    panel.getPanelView().setEnabled(true);
                }
            }
        });

        loadData();

    }

    private void loadData() {
        mSemesterList.clear();

        mSemesterList.addAll(TimetableHelper.getSemesterList(context));

        int selected = -1;
        for (int i = 0; i < mSemesterList.size(); i++) {
            if (TimetableHelper.getCurrentSemesterCode().equals(mSemesterList.get(i).getSemesterCode())) {
                selected = i;
                break;
            }
        }

        final int selectedPosition = selected;

        mSlidingUpPanelLayout.setAdapter(new SlidingUpPanelLayout.Adapter() {

            private final int mSize = mSemesterList.size();

            @Override
            public int getItemCount() {
                return mSize;
            }

            @NonNull
            @Override
            public ISlidingUpPanel onCreateSlidingPanel(int position) {
                WeatherPanelView panel = new WeatherPanelView(context);
                panel.setFloor(mSize - position);
                panel.setPanelHeight(mSize == 1 ? ScreenUtils.dp2pxInt(context, 100) : ScreenUtils.dp2pxInt(context, 64));
                if (position == selectedPosition) {
                    panel.setSlideState(EXPANDED);
                    panel.setEnabled(false);
                } else {
                    panel.setSlideState(HIDDEN);
                    panel.setEnabled(true);
                }

                return panel;
            }

            @Override
            public void onBindView(final ISlidingUpPanel panel, int position) {
                if (mSize == 0)
                    return;

                BaseWeatherPanelView BasePanel = (BaseWeatherPanelView) panel;
                BasePanel.setSemesterInfo(mSemesterList.get(position));
                BasePanel.setClickable(true);
                BasePanel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (panel.getSlideState() != EXPANDED) {
                            mSlidingUpPanelLayout.expandPanel();
                        } else {
                            mSlidingUpPanelLayout.collapsePanel();
                        }
                    }
                });
            }
        });
    }

}
