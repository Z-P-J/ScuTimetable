package com.scu.timetable.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.zpj.http.ZHttp;
import com.zpj.http.core.HttpObserver;
import com.zpj.http.core.IHttp;
import com.zpj.utils.PrefsHelper;

import io.reactivex.ObservableEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Z-P-J
 */
public final class CaptchaFetcher {

    private static final String LINK = "img/captcha.jpg?";

    private static final String REFERER = "http://202.115.47.141/login";

    private CaptchaFetcher() {

    }

    public static void fetchCaptcha(String cookie, ImageView imageView) {
        ZHttp.get(LINK + Math.floor(Math.random() * 100))
                .cookie(cookie)
                .referer(REFERER)
                .userAgent(TimetableHelper.UA)
                .ignoreHttpErrors(true)
                .execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap((HttpObserver.OnFlatMapListener<IHttp.Response, Bitmap>) (data, emitter) -> {
                    emitter.onNext(BitmapFactory.decodeStream(data.bodyStream()));
                    emitter.onComplete();
                })
                .onSuccess(imageView::setImageBitmap)
                .subscribe();
    }

    public static void fetchCaptcha(ImageView imageView) {
        fetchCaptcha(PrefsHelper.with().getString("cookie", ""), imageView);
    }

}
