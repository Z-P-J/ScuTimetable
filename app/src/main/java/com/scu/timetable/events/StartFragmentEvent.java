package com.scu.timetable.events;

import com.zpj.fragmentation.SupportFragment;

public class StartFragmentEvent extends BaseEvent {

    private final SupportFragment fragment;

    public StartFragmentEvent(SupportFragment fragment) {
        this.fragment = fragment;
    }

    public SupportFragment getFragment() {
        return fragment;
    }
}
