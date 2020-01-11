package com.scu.timetable.utils;

import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.scu.timetable.utils.content.SPHelper;

import java.io.IOException;

/**
 * @author Z-P-J
 */
public final class CaptchaFetcher {

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
                        .addHeader("User-Agent", TimetableHelper.UA)
                        .build()
        );
    }

    public static void fetchCaptcha(String cookie, ImageView imageView) {
//        ExecutorHelper.submit(() -> {
//            try {
//                Connection.Response response = Jsoup.connect("http://zhjw.scu.edu.cn/logout")
//                        .followRedirects(false)
//                        .header("cookie", cookie)
//                        .userAgent(TimetableHelper.UA)
//                        .ignoreContentType(true)
//                        .ignoreHttpErrors(true)
//                        .execute();
//                Log.d("fetchCaptcha", "/logout body=" + response.body());
//                response = Jsoup.connect("http://202.115.47.141/login")
//                        .followRedirects(false)
//                        .userAgent(TimetableHelper.UA)
//                        .ignoreContentType(true)
//                        .execute();
//                String cookie2 = response.header("Set-Cookie");
//                Log.d("fetchCaptcha", "login body=" + response.body());
//                Log.d("fetchCaptcha", "cookie=" + cookie2);
//                imageView.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        SPHelper.putString("cookie", cookie2);
//                        Glide.with(imageView.getContext())
//                                .load(getUrl(cookie2))
//                                .apply(OPTIONS)
//                                .into(imageView);
//                    }
//                });
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
        Glide.with(imageView.getContext())
                .load(getUrl(cookie))
                .apply(OPTIONS)
                .into(imageView);
    }

    public static void fetchCaptcha(ImageView imageView) {
        fetchCaptcha(SPHelper.getString("cookie", ""), imageView);
//        Glide.with(imageView.getContext())
//                .load(getUrl(SPHelper.getString("cookie", "")))
//                .apply(OPTIONS)
//                .into(imageView);
    }

}
