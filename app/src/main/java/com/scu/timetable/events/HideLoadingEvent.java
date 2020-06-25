package com.scu.timetable.events;

public class HideLoadingEvent extends BaseEvent {

    public HideLoadingEvent() {

    }

    public static void postEvent() {
        new HideLoadingEvent().post();
    }

}
