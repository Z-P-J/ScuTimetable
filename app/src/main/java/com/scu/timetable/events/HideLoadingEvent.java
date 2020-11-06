package com.scu.timetable.events;

import com.zpj.fragmentation.dialog.IDialog;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

/**
 * @author Z-P-J
 */
public class HideLoadingEvent extends BaseEvent {

    private IDialog.OnDismissListener onDismissListener;

    private HideLoadingEvent() {

    }

    public IDialog.OnDismissListener getOnDismissListener() {
        return onDismissListener;
    }

    public static void postEvent() {
        new HideLoadingEvent().post();
    }

    public static void post(IDialog.OnDismissListener onDismissListener) {
        HideLoadingEvent event = new HideLoadingEvent();
        event.onDismissListener = onDismissListener;
        event.post();
    }

    public static void post(long delay, IDialog.OnDismissListener onDismissListener) {
        Observable.timer(delay, TimeUnit.MILLISECONDS)
                .doOnComplete(() -> post(onDismissListener))
                .subscribe();
    }

    public static void postDelayed(long delay) {
        Observable.timer(delay, TimeUnit.MILLISECONDS)
                .doOnComplete(HideLoadingEvent::postEvent)
                .subscribe();
    }

}
