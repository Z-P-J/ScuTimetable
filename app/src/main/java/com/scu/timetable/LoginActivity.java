package com.scu.timetable;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.scu.timetable.utils.CaptchaFetcher;
import com.scu.timetable.utils.DateUtil;
import com.scu.timetable.utils.SubjectUtil;
import com.scu.timetable.utils.UAHelper;
import com.scu.timetable.utils.UIHelper;
import com.scu.timetable.utils.content.SPHelper;
import com.scu.timetable.view.LoadingDialogFragment;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.Date;

public class LoginActivity extends BaseActivity {

    private EditText userName;
    private EditText password;
    private EditText captcha;
    private ImageView captchaImg;
//    private String[] jieshu = {"第一节", "第二节", "第三节", "第四节", "", "第五节", "第六节", "第七节", "第八节", "第九节", "", "第十节", "第十一节", "第十二节", "第十三节"};
//    private String[] shijian = {"8:15-9:00", "9:10-9:55", "10:15-11:00", "11:10-11:55", "11:55-1:50", "13:50-14:35", "14:45-15:30", "15:40-16:25", "16:50-17:35", "17:45-18:30", "18:30-19:20", "19:20-20:05", "20:15-21:00", "21:10-21:55", "22:05-22:50"};

    private String cookie;

    private LoadingDialogFragment loadingDialogFragment = new LoadingDialogFragment();

//    public static void main(String[] args) {
//        Date startDate = DateUtil.parse("2019-05-01");
//        Date endDate = DateUtil.parse("2019-05-02");
//
//        int weeks = DateUtil.countTwoDayWeek(startDate, endDate);
////        System.out.println("currentDate=" + DateUtil.currentDate());
//        System.out.println("weeks=" + weeks);
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        StatusBarUtil.setStatusBarColor(this, Color.TRANSPARENT);
        Toast.makeText(this, "" + SubjectUtil.hasJsonFile(this), Toast.LENGTH_SHORT).show();
        if (SPHelper.getBoolean("denglu", false) && SubjectUtil.hasJsonFile(this)) {
            String date = SPHelper.getString("current_date", "");
            if (!date.isEmpty()) {
                Date oldDate = DateUtil.parse(date);
                int weeks = DateUtil.computeWeek(oldDate, new Date());
                int currentWeek = SPHelper.getInt("currrent_weak", 1);
                SPHelper.putInt("currrent_weak", currentWeek + weeks);
            }
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        } else {
            initView();
        }
    }

    public void initView() {
        final Button login = findViewById(R.id.login);
        userName = findViewById(R.id.user_name);
        password = findViewById(R.id.password);
        captcha = findViewById(R.id.captcha);
        captchaImg = findViewById(R.id.img_captcha);
        captchaImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(cookie)) {
                    CaptchaFetcher.fetchcaptcha(cookie, captchaImg);
                }
            }
        });
        //http://202.115.47.141/img/captcha.jpg?60

        userName.setText(SPHelper.getString("user_name", ""));
        this.password.setText(SPHelper.getString("password", ""));

//        userName.setText("2017141461383");
//        this.password.setText("095711");
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userName.getText().toString().isEmpty() || password.getText().toString().isEmpty() || captcha.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "请填写好账号信息。", Toast.LENGTH_SHORT).show();
                    UIHelper.openLoadingDialogFragment(LoginActivity.this, "登录中...");
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            UIHelper.closeLoadingDialogFragment();
                        }
                    }, 5000);
                } else {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    Bundle bundle = new Bundle();
                    bundle.putString("msg", "登录中...");
                    loadingDialogFragment.setArguments(bundle);
                    loadingDialogFragment.show(ft, "loading");
                    login();
                }
            }
        });
        getCookie();
    }

    private void getCookie() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection.Response response = Jsoup.connect("http://202.115.47.141/login")
                            .followRedirects(false)
                            .userAgent(UAHelper.UA)
                            .ignoreContentType(true)
                            .execute();
                    Log.d("body=", "" + response.body());
                    Log.d("headers", response.headers().toString());
//                    Set-Cookie=JSESSIONID=bcaJAyI5zQLik_Df2jtQw
                    cookie = response.header("Set-Cookie");
                    Log.d("cookie", "cookie=" + cookie);
                    Message msg = new Message();
                    msg.what = 2;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.obj = e.getMessage();
                    msg.what = 1;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }

    private void login() {
        new Thread() {
            @Override
            public void run() {
                try {
                    Connection.Response response = Jsoup.connect("http://202.115.47.141/j_spring_security_check")
                            .method(Connection.Method.POST)
                            .header("Cookie", cookie)
                            .userAgent(UAHelper.UA)
                            .header("Referer", "http://202.115.47.141/login")
                            .data("j_username", userName.getText().toString())
                            .data("j_password", password.getText().toString())
                            .data("j_captcha", captcha.getText().toString())
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
                        SPHelper.putInt("currrent_weak", currentWeek);
                        SPHelper.putString("currrent_data", DateUtil.currentDate());

                        //通过以下链接可获取当前学期code
                        //http://202.115.47.141/main/academicInfo

                        //http://202.115.47.141/student/courseSelect/thisSemesterCurriculum/ajaxStudentSchedule/callback

                        response = Jsoup.connect("http://202.115.47.141/student/courseSelect/thisSemesterCurriculum/ajaxStudentSchedule/callback")
                                .method(Connection.Method.POST)
                                .userAgent(UAHelper.UA)
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
                }
            }
        }.start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                String sessionId1 = (String) msg.obj;
                Toast.makeText(LoginActivity.this, "erroe=" + sessionId1, Toast.LENGTH_SHORT).show();
            } else if (msg.what == 2) {
                Toast.makeText(LoginActivity.this, "cookie=" + cookie, Toast.LENGTH_SHORT).show();
                SPHelper.putString("cookie", cookie);
                CaptchaFetcher.fetchcaptcha(cookie, captchaImg);
            } else if (msg.what == 3) {
                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 4) {
                Toast.makeText(LoginActivity.this, "登录成功!获取课表信息中。。。", Toast.LENGTH_SHORT).show();
                loadingDialogFragment.updateMsg("登录成功!获取课表数据中...");
                SPHelper.putString("user_name", userName.getText().toString());
                SPHelper.putString("password", password.getText().toString());
            } else if (msg.what == 5) {
                String json = (String) msg.obj;
                try {
                    SubjectUtil.writeToJson(LoginActivity.this, json);
                    SPHelper.putBoolean("denglu", true);
                    loadingDialogFragment.dismiss();
                    loadingDialogFragment = null;
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    loadingDialogFragment.updateMsg("出错了！ " + e.getMessage());
                }
            }
        }
    };
}
