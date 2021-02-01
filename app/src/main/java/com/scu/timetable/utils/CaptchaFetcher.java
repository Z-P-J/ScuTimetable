package com.scu.timetable.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.zpj.http.ZHttp;
import com.zpj.http.core.Connection;
import com.zpj.http.core.IHttp;
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
        ZHttp.get(LINK + Math.floor(Math.random() * 100))
                .cookie(cookie)
                .referer(REFERER)
                .userAgent(TimetableHelper.UA)
                .ignoreHttpErrors(true)
                .execute()
                .onSuccess(new IHttp.OnSuccessListener<Connection.Response>() {
                    @Override
                    public void onSuccess(Connection.Response data) throws Exception {
                        imageView.setImageBitmap(BitmapFactory.decodeStream(data.bodyStream()));
                    }
                })
                .subscribe();
    }

    public static void fetchCaptcha(ImageView imageView) {
        fetchCaptcha(PrefsHelper.with().getString("cookie", ""), imageView);
    }

}
