package com.scu.timetable.utils;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.scu.timetable.model.SemesterBean;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
        void onGetTimetable(JSONObject jsonObject);
        void onGetTimetableFinished();
        void onGetSemesters(String json);
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
                JSONObject json = (JSONObject) msg.obj;
                loginCallback.onGetTimetable(json);
            }
        } else if (msg.what == 6) {
            if (loginCallback != null) {
                String json = (String) msg.obj;
                loginCallback.onGetSemesters(json);
            }
        } else if (msg.what == 7) {
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
        ExecutorHelper.submit(() -> {
            try {
                securityCheck(captcha);
            } catch (IOException e) {
                e.printStackTrace();
                sendMessage(-1, e.getMessage());
            }
        });
    }

    private Connection.Response securityCheck(final String captcha) throws IOException {
        String userName = SPHelper.getString("user_name", "");
        String password = Md5Utils.md5Encrypt(SPHelper.getString("password", ""));
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
                .followRedirects(true)
                .header("Cookie", cookie)
                .userAgent(TimetableHelper.UA)
                .header("Referer", "http://202.115.47.141/login")
                .data("j_username", userName)
                .data("j_password", password)
                .data("j_captcha", captcha)
                .data("_spring_security_remember_me", "on")
                .ignoreHttpErrors(true)
                .ignoreContentType(true)
                .execute();
        Log.d("securityCheck", "location=" + response.header("location"));
        Log.d("securityCheck", "body=" + response.body());
        if (response.statusCode() != 200 || response.body().contains("badCredentials") || response.body().contains("欢迎登录四川大学教务管理系统")) {
            sendMessage(3, null);
            return null;
        } else {
            Log.d("securityCheck", "set-cookie=" + response.header("Set-Cookie"));
            sendMessage(4, null);
            return response;
        }
    }

    private List<SemesterBean> getSemesters() throws IOException, JSONException {
        List<SemesterBean> semesterBeanList = new ArrayList<>();
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
            SemesterBean semester = new SemesterBean();
            semester.setSemesterName(semesterName);
            semester.setSemesterCode(semesterCode);
            semesterBeanList.add(semester);
            jsonObject.put("name", semesterName);
            jsonObject.put("code", semesterCode);
            jsonArray.put(jsonObject);
        }
        sendMessage(6, jsonArray.toString());
        TimetableHelper.setCurrentSemester(currentSemesterCode, currentSenesterName);
        return semesterBeanList;
    }

    private void getTimetable(final String currentSemesterCode) throws Exception {
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
            JSONObject jsonObject = new JSONObject(json);
            jsonObject.put("semester_code", currentSemesterCode);
            sendMessage(5, jsonObject);
        }
    }

    public static void main(String[] args) {
        try {
            int currentWeek;
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DATE);
            System.out.println("year=" + year + " month=" + month + " day=" + day );
            Document doc = Jsoup.connect(String.format(Locale.CHINA, "http://jwc.scu.edu.cn/scdx/xl%d.html", year))
                    .userAgent(TimetableHelper.UA)
                    .ignoreContentType(true)
                    .get();
            String firstDay = "";
            String monthStr = "";
            if (month == 1 || month == 2 || month == 3) {
                firstDay = doc.select("table").get(1).select("tr").get(2).select("td").get(3).text().trim();
                int first = Integer.parseInt(firstDay);
                String info = doc.select("table").get(1).select("tr").get(2).select("td").get(10).select("p").get(1).select("strong").text();
                System.out.println("info1=" + info);
                if (first < 10) {
                    firstDay = "0" + firstDay;
                }

                if (info.contains("3月") || first <= 10) {
                    //3
                    monthStr = "-03-";
                } else {
                    //2
                    monthStr = "-02-";
                }

            } else if (month == 7 || month == 8 || month == 9) {
                firstDay = doc.select("table").get(0).select("tr").get(2).select("td").get(3).text().trim();
                int first = Integer.parseInt(firstDay);
                String info = doc.select("table").get(0).select("tr").get(2).select("td").get(10).select("p").get(1).select("strong").text();
                System.out.println("info2=" + info);
                if (first < 10) {
                    firstDay = "0" + firstDay;
                }
                if (info.contains("9月") || first <= 10) {
                    //9
                    monthStr = "-09-";
                } else {
                    //8
                    monthStr = "-08-";
                }
            }
            if (monthStr.equals("")) {
                System.out.println("empty");
                currentWeek = 1;
            } else {
                System.out.println(year + monthStr + firstDay);
                currentWeek = -DateUtil.computeWeek(DateUtil.parse("2019-08-30"), DateUtil.parse(year + monthStr + firstDay));
            }
            System.out.println("currentWeek=" + currentWeek);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getCurrentWeek(Connection.Response response) {
        Document document = Jsoup.parse(response.body());
        Elements elements = document.select("li");
        String text = elements.select(".light-red").get(0).select("a").get(0).text();
        Log.d("text", "text=" + text);
        int currentWeek;
        if (text.contains("假期")) {
            try {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1;
                int day = calendar.get(Calendar.DATE);
                System.out.println("year=" + year + " month=" + month + " day=" + day );
                Document doc = Jsoup.connect(String.format(Locale.CHINA, "http://jwc.scu.edu.cn/scdx/xl%d.html", year))
                        .userAgent(TimetableHelper.UA)
                        .ignoreContentType(true)
                        .get();
                String firstDay = "";
                String monthStr = "";
                if (month == 1 || month == 2 || month == 3) {
                    firstDay = doc.select("table").get(1).select("tr").get(2).select("td").get(3).text().trim();
                    int first = Integer.parseInt(firstDay);
                    String info = doc.select("table").get(1).select("tr").get(2).select("td").get(10).select("p").get(1).select("strong").text();
                    System.out.println("info1=" + info);
                    if (first < 10) {
                        firstDay = "0" + firstDay;
                    }
                    if (info.contains("3月") || first <= 10) {
                        //3
                        monthStr = "-03-";
                    } else {
                        //2
                        monthStr = "-02-";
                    }
                } else if (month == 7 || month == 8 || month == 9) {
                    firstDay = doc.select("table").get(0).select("tr").get(2).select("td").get(3).text().trim();
                    int first = Integer.parseInt(firstDay);
                    String info = doc.select("table").get(0).select("tr").get(2).select("td").get(10).select("p").get(1).select("strong").text();
                    System.out.println("info2=" + info);
                    if (first < 10) {
                        firstDay = "0" + firstDay;
                    }
                    if (info.contains("9月") || first <= 10) {
                        //9
                        monthStr = "-09-";
                    } else {
                        //8
                        monthStr = "-08-";
                    }
                }
                if (monthStr.equals("")) {
                    System.out.println("empty");
                    currentWeek = 1;
                } else {
                    System.out.println(year + monthStr + firstDay);
                    currentWeek = -DateUtil.computeWeek(new Date(), DateUtil.parse(year + monthStr + firstDay));
                }
                System.out.println("currentWeek=" + currentWeek);
            } catch (IOException e) {
                e.printStackTrace();
                currentWeek = 1;
            }
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
        ExecutorHelper.submit(() -> {
            try {
                Connection.Response response = securityCheck(captcha);
                if (response != null) {
                    getCurrentWeek(response);
                    for (SemesterBean semester : getSemesters()) {
                        getTimetable(semester.getSemesterCode());
                    }
                    sendMessage(7, null);
//                        String currentSemesterCode = getSemesters();
//                        getTimetable(currentSemesterCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendMessage(-1, e.getMessage());
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
//                        getCurrentWeek(response);
                        getTimetable(semesterCode);
                        sendMessage(7, null);
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
        ExecutorHelper.submit(() -> {
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
        });
    }

}
