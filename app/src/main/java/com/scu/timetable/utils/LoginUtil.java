package com.scu.timetable.utils;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.scu.timetable.LoginActivity;
import com.scu.timetable.MainActivity;
import com.scu.timetable.utils.content.SPHelper;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.lang.ref.WeakReference;

/**
 * @author Z-P-J
 * @date 2019/5/16 10:26
 */
public final class LoginUtil {

    private static final class MyHandler extends Handler {
        private final WeakReference<LoginUtil> reference;

        MyHandler(LoginUtil loginUtil) {
            this.reference = new WeakReference<>(loginUtil);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            LoginUtil loginUtil = reference.get();
            loginUtil.handleMessage(msg);
        }
    }

    public interface Callback {
        void onGetCookie(String cookie);
        void onLoginSuccess();
        void onLoginFailed();
//        void onLoginError(Throwable e);
        void onLoginError(String errorMsg);
        void onGetTimetable(String json);
    }

    private final Handler handler = new MyHandler(this);

    private Callback callback;

    private LoginUtil() {

    }

    public static LoginUtil with() {
        return new LoginUtil();
    }

    public LoginUtil setCallback(Callback callback) {
        this.callback = callback;
        return this;
    }

    public void postDelayed(Runnable runnable, long delay) {
        handler.postDelayed(runnable, delay);
    }

    public void start() {

    }

    private void handleMessage(Message msg) {
        if (msg.what == -1) {
            if (callback != null) {
                String errorMsg = (String) msg.obj;
                if (errorMsg.contains("badCredentials")) {
                    Log.d("重新登录", "重新登录");
                } else if (errorMsg.contains("badCaptcha")) {
                    Log.d("验证码错误", "验证码错误");
                }
                callback.onLoginError(errorMsg);
            }
        } else if (msg.what == 2) {
            String cookie = (String) msg.obj;
            SPHelper.putString("cookie", cookie);
            if (callback != null) {
                callback.onGetCookie(cookie);
            }
        } else if (msg.what == 3) {
            if (callback != null) {
                callback.onLoginFailed();
            }
        } else if (msg.what == 4) {
            if (callback != null) {
                callback.onLoginSuccess();
            }
        } else if (msg.what == 5) {
            if (callback != null) {
                String json = (String) msg.obj;
                callback.onGetTimetable(json);
            }
        }
    }

    public void login(final String captcha) {
        String userName = SPHelper.getString("user_name", "");
        String password = SPHelper.getString("password", "");
        if (userName.isEmpty() || password.isEmpty()) {
            Message msg = new Message();
            msg.obj = "You must has logined!";
            msg.what = -1;
            handler.sendMessage(msg);
        }
        Log.d("captcha", "captcha=" + captcha);
        login(userName, password, captcha);
    }

    public void login(final String userName, final String password, final String captcha) {
        final String cookie = SPHelper.getString("cookie", "");
        if (cookie.isEmpty()) {
            Message msg = new Message();
            msg.obj = "You must getCookie first!";
            msg.what = -1;
            handler.sendMessage(msg);
            return;
        }
        new Thread() {
            @Override
            public void run() {
                try {
                    Connection.Response response = Jsoup.connect("http://202.115.47.141/j_spring_security_check")
                            .method(Connection.Method.POST)
                            .header("Cookie", cookie)
                            .userAgent(TimetableHelper.UA)
                            .header("Referer", "http://202.115.47.141/login")
                            .data("j_username", userName)
                            .data("j_password", password)
                            .data("j_captcha", captcha)
//                            .ignoreHttpErrors(true)
//                            .followRedirects(false)
                            .execute();
                    Log.d("response.statusCode()=", "" + response.statusCode());
                    Log.d("body=", "" + response.body());
                    if (response.statusCode() != 200 || response.body().contains("badCredentials")) {
                        Message msg = new Message();
                        msg.what = 3;
                        handler.sendMessage(msg);
                    } else {
                        Message msg = new Message();
                        msg.what = 4;
                        handler.sendMessage(msg);
                        Document document = Jsoup.parse(response.body());
                        Elements elements = document.select("li");
                        String text = elements.select(".light-red").get(0).select("a").get(0).text();
                        Log.d("text", "text=" + text);
                        text = text.substring(text.indexOf("第"));
                        text = text.substring(1, text.indexOf("周"));
                        Log.d("text", "text=" + text);
                        int currentWeek = Integer.parseInt(text);
                        TimetableHelper.setCurrentWeek(currentWeek);
                        TimetableHelper.setCurrentDate(DateUtil.currentDate());

                        //通过以下链接可获取当前学期code
                        //http://202.115.47.141/main/academicInfo

                        //http://202.115.47.141/student/courseSelect/thisSemesterCurriculum/ajaxStudentSchedule/callback

                        response = Jsoup.connect("http://202.115.47.141/student/courseSelect/thisSemesterCurriculum/ajaxStudentSchedule/callback")
                                .method(Connection.Method.POST)
                                .userAgent(TimetableHelper.UA)
                                .ignoreContentType(true)
                                .header("Cookie", cookie)
                                .header("Referer", "http://202.115.47.141/student/courseSelect/calendarSemesterCurriculum/index")
                                .data("planCode", "2018-2019-2-1")
                                .execute();

                        Log.d("课程信息", "" + response.body());

                        String json = response.body().trim();
                        if (json.startsWith("{\"allUnits\"")) {
                            msg = new Message();
                            msg.obj = json;
                            msg.what = 5;
                            handler.sendMessage(msg);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.obj = e.getMessage();
                    msg.what = -1;
                    handler.sendMessage(msg);
                }
            }
        }.start();
    }

    public void getCookie() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection.Response response = Jsoup.connect("http://202.115.47.141/login")
                            .followRedirects(false)
                            .userAgent(TimetableHelper.UA)
                            .ignoreContentType(true)
                            .execute();
                    Log.d("body=", "" + response.body());
                    Log.d("headers", response.headers().toString());
//                    Set-Cookie=JSESSIONID=bcaJAyI5zQLik_Df2jtQw
                    String cookie = response.header("Set-Cookie");
                    Log.d("cookie", "cookie=" + cookie);
                    Message msg = new Message();
                    msg.what = 2;
                    msg.obj = cookie;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.obj = e.getMessage();
                    msg.what = -1;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }

}
