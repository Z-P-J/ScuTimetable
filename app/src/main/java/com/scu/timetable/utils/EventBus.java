package com.scu.timetable.utils;

import com.scu.timetable.model.EvaluationInfo;
import com.zpj.fragmentation.dialog.IDialog;
import com.zpj.http.core.Connection;
import com.zpj.rxbus.RxObserver;
import com.zpj.rxbus.RxPairObserver;
import com.zpj.rxbus.RxSubscriber;

import io.reactivex.functions.Consumer;

public class EventBus {

//    private static final String EVENT_EVALUATION = "evaluation_event";
    private static final String EVENT_REFRESH = "refresh_event";
    private static final String EVENT_UPDATE_SETTING = "update_setting_event";
    private static final String EVENT_SHOW_LOADING = "show_loading_event";
    private static final String EVENT_HIDE_LOADING = "hide_loading_event";

//    public static void sendEvaluationEvent(Object o, EvaluationInfo info, Connection connection) {
//        RxSubscriber.post(EVENT_EVALUATION, info, connection);
//    }
//
//    public static void onEvaluationEvent(Object o, RxPairObserver.PairConsumer<EvaluationInfo, Connection> next) {
//        RxObserver.with(o, EVENT_EVALUATION, EvaluationInfo.class, Connection.class)
//                .subscribe(next);
//    }

    public static void sendRefreshEvent() {
        RxSubscriber.post(EVENT_REFRESH);
    }

    public static void onRefresh(Object o, Consumer<String> next) {
        RxObserver.with(o, EVENT_REFRESH)
                .subscribe(next);
    }

    public static void sendUpdateSettingEvent() {
        RxSubscriber.post(EVENT_UPDATE_SETTING);
    }

    public static void unSubscribeUpdateSettingEvent() {
        RxObserver.unSubscribe(EVENT_UPDATE_SETTING);
    }

    public static void onUpdateSetting(Object o, Consumer<String> next) {
        RxObserver.with(o, EVENT_UPDATE_SETTING)
                .subscribe(next);
    }

    public static void showLoading(String text) {
        showLoading(text, false);
    }

    public static void showLoading(String text, boolean isUpdate) {
        RxSubscriber.post(EVENT_SHOW_LOADING, text, isUpdate);
    }

    public static void onShowLoading(Object o, RxPairObserver.PairConsumer<String, Boolean> next) {
        RxObserver.with(o, EVENT_SHOW_LOADING, String.class, Boolean.class)
                .subscribe(next);
    }

    public static void hideLoading(IDialog.OnDismissListener listener) {
        RxSubscriber.post(EVENT_HIDE_LOADING, listener);
    }

    public static void hideLoading(IDialog.OnDismissListener listener, long delay) {
        RxSubscriber.post(EVENT_HIDE_LOADING, listener, delay);
    }

    public static void onHideLoading(Object o, Consumer<IDialog.OnDismissListener> next) {
        RxObserver.with(o, EVENT_HIDE_LOADING, IDialog.OnDismissListener.class)
                .subscribe(next);
    }

//    public static void onStartFragmentEvent(Object o, Consumer<SupportFragment> next) {
//        RxObserver.with(o, SupportFragment.class)
//                .subscribe(next);
//    }

}
