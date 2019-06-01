package com.scu.timetable.utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.scu.timetable.utils.content.SPHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

    public interface LoginCallback {
        void onGetCookie(String cookie);
        void onLoginSuccess();
        void onLoginFailed();
//        void onLoginError(Throwable e);
        void onLoginError(String errorMsg);
        void onGetTimetable(String json);
        void onGetSemesters(JSONArray jsonArray);
    }

    private final Handler handler = new MyHandler(this);

    private LoginCallback loginCallback;

    private LoginUtil() {

    }

    public static LoginUtil with() {
        return new LoginUtil();
    }

    public LoginUtil setLoginCallback(LoginCallback loginCallback) {
        this.loginCallback = loginCallback;
        return this;
    }

    public void postDelayed(Runnable runnable, long delay) {
        handler.postDelayed(runnable, delay);
    }

    public void start() {

    }

    private void handleMessage(Message msg) {
        if (msg.what == -1) {
            if (loginCallback != null) {
                String errorMsg = (String) msg.obj;
                if (errorMsg.contains("badCredentials")) {
                    Log.d("重新登录", "重新登录");
                } else if (errorMsg.contains("badCaptcha")) {
                    Log.d("验证码错误", "验证码错误");
                }
                loginCallback.onLoginError(errorMsg);
            }
        } else if (msg.what == 2) {
            String cookie = (String) msg.obj;
            SPHelper.putString("cookie", cookie);
            if (loginCallback != null) {
                loginCallback.onGetCookie(cookie);
            }
        } else if (msg.what == 3) {
            if (loginCallback != null) {
                loginCallback.onLoginFailed();
            }
        } else if (msg.what == 4) {
            if (loginCallback != null) {
                loginCallback.onLoginSuccess();
            }
        } else if (msg.what == 5) {
            if (loginCallback != null) {
                String json = (String) msg.obj;
                loginCallback.onGetTimetable(json);
            }
        } else if (msg.what == 6) {
            if (loginCallback != null) {
                JSONArray jsonArray = (JSONArray) msg.obj;
                loginCallback.onGetSemesters(jsonArray);
            }
        }
    }

    private void sendMessage(int what, Object obj) {
        Message msg = new Message();
        msg.what = what;
        msg.obj = obj;
        handler.sendMessage(msg);
    }

    public void checkCaptcha(final String captcha) {
        ExecutorHelper.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    securityCheck(captcha);
                } catch (IOException e) {
                    e.printStackTrace();
                    sendMessage(-1, e.getMessage());
                }
            }
        });
    }

    private Connection.Response securityCheck(final String captcha) throws IOException {
        String userName = SPHelper.getString("user_name", "");
        String password = SPHelper.getString("password", "");
        if (userName.isEmpty() || password.isEmpty()) {
            sendMessage(-1, "You have to log in first.");
            return null;
        }
        final String cookie = SPHelper.getString("cookie", "");
        if (cookie.isEmpty()) {
            sendMessage(-1, "You have to get the cookie first.");
            return null;
        }
        Connection.Response response = Jsoup.connect("http://202.115.47.141/j_spring_security_check")
                .method(Connection.Method.POST)
                .header("Cookie", cookie)
                .userAgent(TimetableHelper.UA)
                .header("Referer", "http://202.115.47.141/login")
                .data("j_username", userName)
                .data("j_password", password)
                .data("j_captcha", captcha)
                .execute();
        if (response.statusCode() != 200 || response.body().contains("badCredentials")) {
            sendMessage(3, null);
            return null;
        } else {
            sendMessage(4, null);
            return response;
        }
    }

    private String getSemesters() throws IOException, JSONException {
        Document document = Jsoup.connect("http://zhjw.scu.edu.cn/student/courseSelect/calendarSemesterCurriculum/index")
                .header("cookie", SPHelper.getString("cookie", ""))
                .header("Referer", "http://zhjw.scu.edu.cn/")
                .get();
        Elements elements = document.getElementById("planCode").select("option");
        JSONArray jsonArray = new JSONArray();
        String currentSemesterCode = "2018-2019-2-1";
        String currentSenesterName  = "2018-2019学年春(当前)";
        for (Element element : elements) {
            JSONObject jsonObject = new JSONObject();
            String semesterName = element.text().replaceAll("#", "").trim();
            String semesterCode = element.val();
            if (semesterName.contains("当前")) {
                currentSemesterCode = semesterCode;
                currentSenesterName = semesterName;
            }
            jsonObject.put("name", semesterName);
            jsonObject.put("code", semesterCode);
            jsonArray.put(jsonObject);
        }
        sendMessage(6, jsonArray);
        TimetableHelper.setCurrentSemester(currentSemesterCode, currentSenesterName);
        return currentSemesterCode;
    }

    private void getTimetable(final String currentSemesterCode) throws IOException {
        Connection.Response response = Jsoup.connect("http://202.115.47.141/student/courseSelect/thisSemesterCurriculum/ajaxStudentSchedule/callback")
                .method(Connection.Method.POST)
                .userAgent(TimetableHelper.UA)
                .ignoreContentType(true)
                .header("Cookie", SPHelper.getString("cookie", ""))
                .header("Referer", "http://202.115.47.141/student/courseSelect/calendarSemesterCurriculum/index")
                .data("planCode", currentSemesterCode)
                .execute();

        Log.d("课程信息", "" + response.body());

        String json = response.body().trim();
        if (json.startsWith("{\"allUnits\"")) {
            sendMessage(5, json);
        }
    }

    private void getCurrentWeek(Connection.Response response) {
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
    }

    public void login(final String captcha) {
        Log.d("captcha", "captcha=" + captcha);
        ExecutorHelper.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection.Response response = securityCheck(captcha);
                    if (response != null) {
                        getCurrentWeek(response);
                        String currentSemesterCode = getSemesters();
                        getTimetable(currentSemesterCode);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    sendMessage(-1, e.getMessage());
                }
            }
        });
    }

    public void login(final String captcha, final String semesterCode) {
        Log.d("captcha", "captcha=" + captcha);
        ExecutorHelper.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection.Response response = securityCheck(captcha);
                    if (response != null) {
                        getCurrentWeek(response);
                        getTimetable(semesterCode);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    sendMessage(-1, e.getMessage());
                }
            }
        });
    }

    public void login(final String userName, final String password, final String captcha) {
        SPHelper.putString("user_name", userName);
        SPHelper.putString("password", password);
        login(captcha);
    }

    public void getCookie() {
        ExecutorHelper.submit(new Runnable() {
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
                    String cookie = response.header("Set-Cookie");
                    Log.d("cookie", "cookie=" + cookie);
                    sendMessage(2, cookie);
                } catch (Exception e) {
                    e.printStackTrace();
                    sendMessage(-1, e.getMessage());
                }
            }
        });
    }

}
