package com.zpj.rxbus;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.text.TextUtils;
import android.view.View;

import com.zpj.rxlife.LifecycleTransformer;
import com.zpj.rxlife.RxLife;

import java.util.Map;
import java.util.WeakHashMap;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.functions.Functions;
import io.reactivex.schedulers.Schedulers;

public class RxObserver<T> {

    private final Object o;
    private final String key;
    private final Observable<T> observable;
    private Scheduler subscribeScheduler = Schedulers.io();
    private Scheduler observeScheduler = AndroidSchedulers.mainThread();
//    private LifecycleTransformer<T> composer;
    private final WeakHashMap<Object, LifecycleTransformer<T>> composerMap = new WeakHashMap<>();

    private RxObserver(Object o, Class<T> type) {
        this(o, null, type);
    }

    private RxObserver(Object o, String key, Observable<T> observable) {
        this.o = o;
        this.key = key == null ? null : key.trim();
        this.observable = observable;
    }

    private RxObserver(Object o, String key, Class<T> type) {
        this.o = o;
        this.key = key == null ? null : key.trim();
//        this.key = key.trim();
//        observable = RxBus2.get()
//                .toObservable(type);
        if (TextUtils.isEmpty(key)) {
            observable = RxBus.get()
                    .toObservable(type);
        } else {
            observable = RxBus.get()
                    .toObservable(key, type);
        }
    }

    public static <T> RxObserver<T> with(@NonNull Object o, @NonNull Class<T> type) {
        return new RxObserver<>(o, type);
    }

    public static RxObserver<String> with(@NonNull Object o, @NonNull String key) {
        return new RxObserver<>(o, key, RxBus.get().toObservable(key));
    }

    public static <T> RxObserver<T> with(@NonNull Object o, @NonNull String key, @NonNull Class<T> type) {
        return new RxObserver<>(o, key, type);
    }

    public static <S, T> RxPairObserver<S, T> with(@NonNull Object o, @NonNull String key, @NonNull Class<S> type1, @NonNull Class<T> type2) {
        return new RxPairObserver<S, T>(new RxObserver<>(o, key, RxBus.get().toObservable(key, type1, type2)));
    }

    public static <T> RxObserver<T> withSticky(@NonNull Object o, @NonNull Class<T> type) {
        return new RxObserver<>(o, null, RxBus.get().toObservableSticky(type));
    }

    public static RxObserver<String> withSticky(@NonNull Object o, @NonNull String key) {
        return new RxObserver<>(o, key, RxBus.get().toObservableSticky(key));
    }

    public static <T> RxObserver<T> withSticky(@NonNull Object o, @NonNull String key, @NonNull Class<T> type) {
        return new RxObserver<>(o, key, RxBus.get().toObservableSticky(key, type));
    }

    public static <T> T removeStickyEvent(Class<T> eventType) {
        return RxBus.get().removeStickyEvent(eventType);
    }

    public static Object removeStickyEvent(String key) {
        return RxBus.get().removeStickyEvent(key);
    }

    public static <T> T removeStickyEvent(String key, Class<T> type) {
        return RxBus.get().removeStickyEvent(key, type);
    }

    public static void removeAllStickyEvents() {
        RxBus.get().removeAllStickyEvents();
    }

    public RxObserver<T> subscribeOn(Scheduler scheduler) {
        this.subscribeScheduler = scheduler;
        return this;
    }

    public RxObserver<T> observeOn(Scheduler scheduler) {
        this.observeScheduler = scheduler;
        return this;
    }

//    public RxObserver<T> compose(ObservableTransformer<? super T, ? extends T> composer) {
//        this.composer = composer;
//        return this;
//    }

    public RxObserver<T> bindTag(String tag) {
        composerMap.put(tag, RxLife.bindTag(tag));
        return this;
    }

    public void removeByTag(String tag) {
        RxLife.removeTag(tag);
    }

    public RxObserver<T> bindView(View view) {
        composerMap.put(view, RxLife.bindView(view));
        return this;
    }

    public RxObserver<T> bindToLife(LifecycleOwner lifecycleOwner) {
        composerMap.put(lifecycleOwner, RxLife.bindLifeOwner(lifecycleOwner));
        return this;
    }

    public RxObserver<T> bindToLife(LifecycleOwner lifecycleOwner, Lifecycle.Event event) {
        composerMap.put(lifecycleOwner, RxLife.bindLifeOwner(lifecycleOwner, event));
        return this;
    }

    public void subscribe(Consumer<T> next) {
//        RxBus2.get().addSubscription(o, wrapObservable().subscribe(next));
        subscribe(next, Functions.ON_ERROR_MISSING);
    }

    public void subscribe(Consumer<T> next, Consumer<Throwable> error) {
        if (subscribeScheduler == null) {
            subscribeScheduler = Schedulers.io();
        }
        if (observeScheduler == null) {
            observeScheduler = AndroidSchedulers.mainThread();
        }

        Observable<T> observable = this.observable
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler);

        if (!composerMap.containsKey(o)) {
            if (o instanceof LifecycleOwner) {
                composerMap.put(o, RxLife.bindLifeOwner((LifecycleOwner) o));
            } else if (o instanceof View) {
                composerMap.put(o, RxLife.bindView((View) o));
            } else if (o instanceof Activity) {
                composerMap.put(o, RxLife.bindActivity((Activity) o));
            }
        }

        if (!composerMap.isEmpty()) {
            for (Map.Entry<Object, LifecycleTransformer<T>> entry : composerMap.entrySet()) {
                observable = observable.compose(entry.getValue());
            }
        }

//        if (composerMap.isEmpty() && o instanceof LifecycleOwner) {
//            observable = this.observable.compose(RxLife.bindLifeOwner((LifecycleOwner) o));
//        } else if (!composerMap.isEmpty()) {
//            for (Map.Entry<Object, LifecycleTransformer<T>> entry : composerMap.entrySet()) {
//                observable = observable.compose(entry.getValue());
//            }
//        }

        Disposable disposable = observable.subscribe(next, error);
        if (composerMap.isEmpty()) {
            RxBus.get().addSubscription(o, disposable);
        }
//        if (!(composer instanceof LifecycleTransformer)) {
//            RxBus.get().addSubscription(o, disposable);
//        }
    }

    public static void unSubscribe(Object o) {
        RxBus.get().unSubscribe(o);
    }

}