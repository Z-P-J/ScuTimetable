package com.scu.timetable.utils;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;

/**
 * @author 25714
 */
public class CaptchaFetcher {

    private static final RequestOptions OPTIONS = new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true);

    private static final String LINK = "http://202.115.47.141/img/captcha.jpg?";

    private static final String REFERER = "http://202.115.47.141/login";

    private CaptchaFetcher () {

    }

    private static GlideUrl getUrl(String cookie) {
        return new GlideUrl(
                LINK + Math.floor(Math.random() * 100),
                new LazyHeaders.Builder()
                        .addHeader("Cookie", cookie)
                        .addHeader("Referer", REFERER)
                        .addHeader("User-Agent", UAHelper.UA)
                        .build()
        );
    }

    public static void fetchcaptcha(String cookie, ImageView imageView) {
        Glide.with(imageView.getContext())
                .load(getUrl(cookie))
                .apply(OPTIONS)
                .into(imageView);
    }

}
