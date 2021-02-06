package com.scu.timetable.utils;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.scu.timetable.bean.SemesterInfo;
import com.zpj.http.ZHttp;
import com.zpj.http.core.HttpObserver;
import com.zpj.http.core.IHttp;
import com.zpj.http.parser.DocumentParser;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.utils.CipherUtils;
import com.zpj.utils.PrefsHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Z-P-J
 * @date 2019/5/16 10:26
 */
public final class LoginUtil {

    public static String getUserName() {
        return EncryptionUtils.decryptByAES(PrefsHelper.with().getString("user_name", ""));
    }

    public static String getPassword() {
        return EncryptionUtils.decryptByAES(PrefsHelper.with().getString("password", ""));
    }

    public static void saveAccount(String userName, String password) {
        PrefsHelper.with().putString("user_name", EncryptionUtils.encryptByAES(userName));
        PrefsHelper.with().putString("password", EncryptionUtils.encryptByAES(password));
    }

    public void onGetCookie(String cookie) {
//        PrefsHelper.with().putString("cookie", cookie);
        if (loginCallback != null) {
            loginCallback.onGetCookie(cookie);
        }
    }

//    public void onLoginSuccess() {
//        if (loginCallback != null) {
//            loginCallback.onLoginSuccess();
//        }
//    }

    public void onLoginFailed() {
        if (loginCallback != null) {
            loginCallback.onLoginFailed();
        }
    }

    public void onLoginError(String errorMsg) {
        if (loginCallback != null) {
            if (errorMsg.contains("badCredentials")) {
                Log.d("重新登录", "重新登录");
            } else if (errorMsg.contains("badCaptcha")) {
                Log.d("验证码错误", "验证码错误");
            }
            loginCallback.onLoginError(errorMsg);
        }
    }

//    public void onGetTimetable(JSONObject jsonObject) {
////        if (loginCallback != null) {
////            loginCallback.onGetTimetable(jsonObject);
////        }
//        Observable.create(
//                emitter -> {
//                    TimetableHelper.writeToJson(jsonObject);
//                    emitter.onComplete();
//                })
//                .subscribeOn(Schedulers.io())
//                .subscribe();
//    }

    public void onGetTimetableFinished() {
        if (loginCallback != null) {
            loginCallback.onGetTimetableFinished();
        }
    }

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

//        void onGetTimetable(JSONObject jsonObject);

        void onGetTimetableFinished();

//        void onGetSemesters(String json);
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
//            PrefsHelper.with().putString("cookie", cookie);
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
        }
//        else if (msg.what == 5) {
//            if (loginCallback != null) {
//                JSONObject json = (JSONObject) msg.obj;
//                loginCallback.onGetTimetable(json);
//            }
//        }
//        else if (msg.what == 6) {
//            if (loginCallback != null) {
//                String json = (String) msg.obj;
//                loginCallback.onGetSemesters(json);
//            }
//        }
        else if (msg.what == 7) {
            if (loginCallback != null) {
                loginCallback.onGetTimetableFinished();
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
        new HttpObserver<>(
                (ObservableOnSubscribe<IHttp.Response>) emitter -> {
                    securityCheck(captcha).close();
                    emitter.onComplete();
                })
                .onError(e -> onLoginError(e.getMessage()))
                .subscribe();
    }

    private IHttp.Response securityCheck(final String captcha) throws Exception {
        String userName = EncryptionUtils.decryptByAES(PrefsHelper.with().getString("user_name", ""));
        String password = CipherUtils.md5Encrypt(EncryptionUtils.decryptByAES(PrefsHelper.with().getString("password", "")));
        if (userName.isEmpty() || password.isEmpty()) {
            throw new Exception("You have to login first.");
        }
//        final String cookie = PrefsHelper.with().getString("cookie", "");
//        if (cookie.isEmpty()) {
//            throw new Exception("You have to get the cookie first.");
//        }

        IHttp.Response response = ZHttp.post("j_spring_security_check")
                .referer("http://zhjw.scu.edu.cn/login")
                .data("j_username", userName)
                .data("j_password", password)
                .data("j_captcha", captcha)
                .data("_spring_security_remember_me", "on")
                .syncExecute();
        Log.d("securityCheck", "location=" + response.header("location"));
        Log.d("securityCheck", "body=" + response.body());
        Log.d("securityCheck", "statusCode=" + response.statusCode());
        if (response.statusCode() != 200 || response.body().contains("badCredentials")
                || response.body().contains("欢迎登录四川大学教务管理系统")) {
            sendMessage(3, null);
            response.close();
            return null;
        } else {
            Log.d("securityCheck", "set-cookie=" + response.header("Set-Cookie"));
            String setCookie = response.header("Set-Cookie");
            Log.d("securityCheck", "cookie2=" + response.cookieStr());
            if (!TextUtils.isEmpty(setCookie)) {
//                sendMessage(2, cookie + "; " + setCookie);
                sendMessage(2, response.cookieStr());
            }
            Log.d("securityCheck", "cookie2=" + response.cookies().toString());
            sendMessage(4, null);
            return response;
        }
    }

    private List<SemesterInfo> getSemesters() throws Exception {
        List<SemesterInfo> semesterInfoList = new ArrayList<>();
        Document document = ZHttp.get("student/courseSelect/calendarSemesterCurriculum/index")
//                .header("cookie", PrefsHelper.with().getString("cookie", ""))
                .referer("http://zhjw.scu.edu.cn/")
                .syncToHtml();
        Elements elements = document.getElementById("planCode").select("option");
        JSONArray jsonArray = new JSONArray();
        String currentSemesterCode = "2018-2019-2-1";
        String currentSenesterName = "2018-2019学年春(当前)";
        for (Element element : elements) {
            JSONObject jsonObject = new JSONObject();
            SemesterInfo semester = new SemesterInfo();
            String semesterName = element.text().replaceAll("#", "").trim();
            String semesterCode = element.val();
            if (semesterName.contains("当前")) {
                currentSemesterCode = semesterCode;
                currentSenesterName = semesterName;
                jsonObject.put("is_current", true);
                semester.setCurrent(true);
            } else {
                jsonObject.put("is_current", false);
                semester.setCurrent(false);
            }

            semester.setSemesterName(semesterName);
            semester.setSemesterCode(semesterCode);
            semesterInfoList.add(semester);
            jsonObject.put("name", semesterName);
            jsonObject.put("code", semesterCode);
            jsonArray.put(jsonObject);
        }
//        onGetSemesters(jsonArray.toString());
        TimetableHelper.writeSemesterFile(jsonArray.toString());
        TimetableHelper.setCurrentSemester(currentSemesterCode, currentSenesterName);
        return semesterInfoList;
    }

    private JSONObject getTimetable(final String currentSemesterCode) throws Exception {
//        http://zhjw.scu.edu.cn/student/courseSelect/thisSemesterCurriculum/ajaxStudentSchedule/past/callback
        // 当前课表
        // http://zhjw.scu.edu.cn/student/courseSelect/thisSemesterCurriculum/ajaxStudentSchedule/curr/callback
        JSONObject jsonObject = ZHttp.post("student/courseSelect/thisSemesterCurriculum/ajaxStudentSchedule/past/callback")
//                .cookie(PrefsHelper.with().getString("cookie", ""))
//                .referer("http://zhjw.scu.edu.cn/student/courseSelect/thisSemesterCurriculum/index")
                .referer("http://zhjw.scu.edu.cn/student/courseSelect/calendarSemesterCurriculum/index")
                .data("planCode", currentSemesterCode)
                .syncToJsonObject();

        jsonObject.put("semester_code", currentSemesterCode);
        return jsonObject;
    }

    private void getCurrentWeek(Document document) {
//        Document document = ZHttp.parse(response.body());
//        Elements elements = document.select("li");
        Log.d("getCurrentWeek", "light-red=" + document.selectFirst("li.light-red"));
        Log.d("getCurrentWeek", "a=" + document.selectFirst("li.light-red").selectFirst("a"));
        String text = document.selectFirst("li.light-red").selectFirst("a").text();
        Log.d("getCurrentWeek", "text=" + text);
        int currentWeek;
        if (text.contains("假期")) {
//            try {
//                Calendar calendar = Calendar.getInstance();
//                int year = calendar.get(Calendar.YEAR);
//                int month = calendar.get(Calendar.MONTH) + 1;
//                int day = calendar.get(Calendar.DATE);
//                System.out.println("year=" + year + " month=" + month + " day=" + day);
////                Document doc = ZHttp.get(String.format(Locale.CHINA, "http://jwc.scu.edu.cn/scdx/xl%d.html", year))
////                        .userAgent(TimetableHelper.UA)
////                        .ignoreContentType(true)
////                        .syncToHtml();
//                Document doc = ZHttp.get("http://jwc.scu.edu.cn/cdxl.htm").syncToHtml();
//                String url = "http://jwc.scu.edu.cn/" + doc.selectFirst("ul.list-d-list").selectFirst("li").selectFirst("a").attr("href");
//                Log.d("getCurrentWeek", "url=" + url);
//                doc = ZHttp.get(url).syncToHtml();
//
//
//                String firstDay = "";
//                String monthStr = "";
//                if (month == 1 || month == 2 || month == 3) {
//                    firstDay = doc.select("table").get(1).select("tr").get(2).select("td").get(3).text().trim();
//                    int first = Integer.parseInt(firstDay);
//                    String info = doc.select("table").get(1).select("tr").get(2).select("td").get(10).select("p").get(1).select("strong").text();
//                    System.out.println("info1=" + info);
//                    if (first < 10) {
//                        firstDay = "0" + firstDay;
//                    }
//                    if (info.contains("3月") || first <= 10) {
//                        //3
//                        monthStr = "-03-";
//                    } else {
//                        //2
//                        monthStr = "-02-";
//                    }
//                } else if (month == 7 || month == 8 || month == 9) {
//                    firstDay = doc.select("table").get(0).select("tr").get(2).select("td").get(3).text().trim();
//                    int first = Integer.parseInt(firstDay);
//                    String info = doc.select("table").get(0).select("tr").get(2).select("td").get(10).select("p").get(1).select("strong").text();
//                    System.out.println("info2=" + info);
//                    if (first < 10) {
//                        firstDay = "0" + firstDay;
//                    }
//                    if (info.contains("9月") || first <= 10) {
//                        //9
//                        monthStr = "-09-";
//                    } else {
//                        //8
//                        monthStr = "-08-";
//                    }
//                }
//                if (monthStr.equals("")) {
//                    System.out.println("empty");
//                    currentWeek = 1;
//                } else {
//                    System.out.println(year + monthStr + firstDay);
//                    currentWeek = -DateUtil.computeWeek(new Date(), DateUtil.parse(year + monthStr + firstDay));
//                }
//                System.out.println("currentWeek=" + currentWeek);
//            } catch (Exception e) {
//                e.printStackTrace();
//                currentWeek = 1;
//            }
            currentWeek = 1;
        } else {
            text = text.substring(text.indexOf("第"));
            text = text.substring(1, text.indexOf("周"));
            currentWeek = Integer.parseInt(text);
        }
        Log.d("text", "text=" + text);
        TimetableHelper.setCurrentWeek(currentWeek);
        TimetableHelper.setCurrentDate(DateUtil.currentDate());
    }

    private void login(final String captcha) {
        Log.d("captcha", "captcha=" + captcha);
        new HttpObserver<>(
                (ObservableOnSubscribe<JSONObject>) emitter -> {
                    IHttp.Response res = securityCheck(captcha);
//                    emitter.onNext(securityCheck(captcha));
                    getCurrentWeek(DocumentParser.parse(res.body()));
                    res.close();
                    for (SemesterInfo semester : getSemesters()) {
                        if (semester.isCurrent()) {
                            JSONObject jsonObject = getTimetable(semester.getSemesterCode());
                            TimetableHelper.writeToJson(jsonObject);
                        } else {
                            Observable.create(
                                    e -> {
                                        JSONObject jsonObject = getTimetable(semester.getSemesterCode());
                                        TimetableHelper.writeToJson(jsonObject);
                                        e.onComplete();
                                    })
                                    .subscribeOn(Schedulers.io())
                                    .subscribe();
                        }
                    }
                    emitter.onComplete();
                })
//                .onSuccess(this::onGetTimetable)
                .onError(throwable -> onLoginError(throwable.getMessage()))
                .onComplete(this::onGetTimetableFinished)
                .subscribe();
    }

    public void login(final String captcha, final String semesterCode) {
        Log.d("captcha", "captcha=" + captcha);
        new HttpObserver<>(
                (ObservableOnSubscribe<JSONObject>) emitter -> {
                    IHttp.Response res = securityCheck(captcha);
                    res.close();
//                    emitter.onNext(getTimetable(semesterCode));
                    JSONObject jsonObject = getTimetable(semesterCode);
                    TimetableHelper.writeToJson(jsonObject);
                    emitter.onComplete();
                })
//                .onSuccess(this::onGetTimetable)
                .onError(throwable -> onLoginError(throwable.getMessage()))
                .onComplete(this::onGetTimetableFinished)
                .subscribe();
    }

    public void login(final String userName, final String password, final String captcha) {
        saveAccount(userName, password);
        login(captcha);
    }

    public void getCookie() {
        ZHttp.get("login")
                .onRedirect((redirectCount, redirectUrl) -> false)
                .userAgent(TimetableHelper.UA)
                .ignoreContentType(true)
                .execute()
                .onSuccess(response -> {
//                    Log.d("body=", "" + response.body());
                    Log.d("headers", response.headers().toString());
                    String cookie = response.header("Set-Cookie");
                    Log.d("cookie", "cookie1=" + cookie);
                    Log.d("cookie", "cookie2=" + response.cookieStr());
                    onGetCookie(cookie);
                })
                .onError(e -> onLoginError(e.getMessage()))
                .subscribe();
    }

}
