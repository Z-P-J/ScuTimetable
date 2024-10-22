package com.scu.timetable.ui.fragment.base;

import com.zpj.fragmentation.SupportFragment;
import com.zpj.rxbus.RxBus;

public abstract class SkinChangeFragment extends BaseFragment {

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        darkStatusBar();
    }

    protected static void start(SupportFragment fragment) {
        RxBus.post(fragment);
    }

}
