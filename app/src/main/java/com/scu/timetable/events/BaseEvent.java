package com.scu.timetable.events;

import org.greenrobot.eventbus.EventBus;

public class BaseEvent {

    public void post() {
        EventBus.getDefault().post(this);
    }

}
