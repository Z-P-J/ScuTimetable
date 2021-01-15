package com.scu.timetable.utils;

import com.zpj.fragmentation.dialog.IDialog;
import com.zpj.rxbus.RxBus;

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
        RxBus.post(EVENT_REFRESH);
    }

    public static void onRefresh(Object o, Consumer<String> next) {
        RxBus.observe(o, EVENT_REFRESH)
                .doOnNext(next)
                .subscribe();
    }

    public static void sendUpdateSettingEvent() {
        RxBus.post(EVENT_UPDATE_SETTING);
    }

    public static void unSubscribeUpdateSettingEvent() {
        RxBus.removeObservers(EVENT_UPDATE_SETTING);
    }

    public static void onUpdateSetting(Object o, Consumer<String> next) {
        RxBus.observe(o, EVENT_UPDATE_SETTING)
                .doOnNext(next)
                .subscribe();
    }

    public static void showLoading(String text) {
        showLoading(text, false);
    }

    public static void showLoading(String text, boolean isUpdate) {
        RxBus.post(EVENT_SHOW_LOADING, text, isUpdate);
    }

    public static void onShowLoading(Object o, RxBus.PairConsumer<String, Boolean> next) {
        RxBus.observe(o, EVENT_SHOW_LOADING, String.class, Boolean.class)
                .doOnNext(next)
                .subscribe();
    }

    public static void hideLoading(IDialog.OnDismissListener listener) {
        RxBus.post(EVENT_HIDE_LOADING, listener);
    }

    public static void hideLoading(IDialog.OnDismissListener listener, long delay) {
        RxBus.post(EVENT_HIDE_LOADING, listener, delay);
    }

    public static void onHideLoading(Object o, RxBus.SingleConsumer<IDialog.OnDismissListener> next) {
        RxBus.observe(o, EVENT_HIDE_LOADING, IDialog.OnDismissListener.class)
                .doOnNext(next)
                .subscribe();
    }

//    public static void onStartFragmentEvent(Object o, Consumer<SupportFragment> next) {
//        RxObserver.with(o, SupportFragment.class)
//                .subscribe(next);
//    }

}
