package com.zpj.rxbus;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;

import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public class RxPairObserver<S, T> implements IObserver<RxPairObserver<S, T>, RxPairObserver.PairConsumer<S, T>> {

    private final RxObserver<RxSubscriber.PairMessage> observer;

    RxPairObserver(RxObserver<RxSubscriber.PairMessage> observer) {
        this.observer = observer;
    }

//    public static <S, T> RxPairObserver<S, T> with(@NonNull Object o, @NonNull String key, @NonNull Class<S> type1, @NonNull Class<T> type2) {
//        return new RxPairObserver<>(RxObserver.with(o, key, type1, type2));
//    }

    @Override
    public RxPairObserver<S, T> subscribeOn(Scheduler scheduler) {
        observer.subscribeOn(scheduler);
        return this;
    }

    @Override
    public RxPairObserver<S, T> observeOn(Scheduler scheduler) {
        observer.observeOn(scheduler);
        return this;
    }

    @Override
    public RxPairObserver<S, T> bindToLife(LifecycleOwner lifecycleOwner) {
        observer.bindToLife(lifecycleOwner);
        return this;
    }

    @Override
    public RxPairObserver<S, T> bindToLife(LifecycleOwner lifecycleOwner, Lifecycle.Event event) {
        observer.bindToLife(lifecycleOwner, event);
        return this;
    }

    @Override
    public void subscribe(PairConsumer<S, T> next, Consumer<Throwable> error) {
        observer.subscribe(
                new Consumer<RxSubscriber.PairMessage>() {
                    @Override
                    public void accept(RxSubscriber.PairMessage msg) throws Exception {
                        next.accept((S) msg.getS(), (T) msg.getT());
                    }
                }, error);
    }

    public interface PairConsumer<S, T> {
        void accept(S s, T t) throws Exception;
    }


}