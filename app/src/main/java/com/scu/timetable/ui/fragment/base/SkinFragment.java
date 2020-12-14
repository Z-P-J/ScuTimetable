package com.scu.timetable.ui.fragment.base;

import com.zpj.fragmentation.BaseFragment;

public abstract class SkinFragment extends BaseFragment {

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        darkStatusBar();
    }
}
