package com.zpj.rxbus;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;

import io.reactivex.Scheduler;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.functions.Functions;

public interface IObserver<T extends IObserver, C> {

//    default T subscribeOn(Scheduler scheduler) {
//        return (T) this;
//    }
//
//    default T observeOn(Scheduler scheduler) {
//        return (T) this;
//    }
//
//    default T bindToLife(LifecycleOwner lifecycleOwner) {
//        return (T) this;
//    }
//
//    default T bindToLife(LifecycleOwner lifecycleOwner, Lifecycle.Event event) {
//        return (T) this;
//    }
//
//    default T bindOnDestroy(LifecycleOwner lifecycleOwner) {
//        return bindToLife(lifecycleOwner, Lifecycle.Event.ON_DESTROY);
//    }

    T subscribeOn(Scheduler scheduler);

    T observeOn(Scheduler scheduler);

    T bindToLife(LifecycleOwner lifecycleOwner);

    T bindToLife(LifecycleOwner lifecycleOwner, Lifecycle.Event event);

//    T bindOnDestroy(LifecycleOwner lifecycleOwner);

    default T bindOnDestroy(LifecycleOwner lifecycleOwner) {
        return bindToLife(lifecycleOwner, Lifecycle.Event.ON_DESTROY);
    }

    default void subscribe(C next) {
        subscribe(next, Functions.ON_ERROR_MISSING);
    }

    void subscribe(C next, Consumer<Throwable> error);

}
