package com.scu.timetable.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.zpj.http.ZHttp;
import com.zpj.http.core.Connection;
import com.zpj.utils.PrefsHelper;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Z-P-J
 */
public final class CaptchaFetcher {

    private static final String LINK = "http://202.115.47.141/img/captcha.jpg?";

    private static final String REFERER = "http://202.115.47.141/login";

    private CaptchaFetcher() {

    }

    public static void fetchCaptcha(String cookie, ImageView imageView) {
        Observable.create(
                (ObservableOnSubscribe<Bitmap>) emitter -> {
                    Connection.Response response = ZHttp.get(LINK + Math.floor(Math.random() * 100))
                            .validateTLSCertificates(true)
                            .cookie(cookie)
                            .referer(REFERER)
                            .userAgent(TimetableHelper.UA)
                            .ignoreContentType(true)
                            .ignoreHttpErrors(true)
                            .syncExecute();
                    emitter.onNext(BitmapFactory.decodeStream(response.bodyStream()));
                    emitter.onComplete();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(imageView::setImageBitmap)
                .subscribe();
    }

    public static void fetchCaptcha(ImageView imageView) {
        fetchCaptcha(PrefsHelper.with().getString("cookie", ""), imageView);
    }

}
