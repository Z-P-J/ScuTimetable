package com.scu.timetable;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scu.timetable.ui.activity.BaseHandlerActivity;
import com.scu.timetable.utils.AnimatorUtil;
import com.scu.timetable.utils.CaptchaFetcher;
import com.scu.timetable.utils.DateUtil;
import com.scu.timetable.utils.TimetableHelper;
import com.scu.timetable.utils.content.SPHelper;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * @author 25714
 */
public final class LoginActivity extends BaseHandlerActivity implements View.OnClickListener {

    private EditText userName;
    private EditText password;
    private EditText captcha;
    private ImageView captchaImg;
//    private String[] shijian = {"8:15-9:00", "9:10-9:55", "10:15-11:00", "11:10-11:55", "11:55-1:50", "13:50-14:35", "14:45-15:30", "15:40-16:25", "16:50-17:35", "17:45-18:30", "18:30-19:20", "19:20-20:05", "20:15-21:00", "21:10-21:55", "22:05-22:50"};

    private String cookie;

    private View progress;

    private TextView msgText;

    private RelativeLayout middleLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (TimetableHelper.isLogined(LoginActivity.this)) {
                    updateWidget(true);
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    updateWidget(false);
                    initView();
                    getCookie();
                }
            }
        }, 1000);

    }

    private void updateWidget(boolean isLogined) {
        //发送广播。更新桌面插件
        Intent intent = new Intent();
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        intent.putExtra("islogined", isLogined);
        getApplicationContext().sendBroadcast(intent);
    }

    public void initView() {
        middleLayout = findViewById(R.id.layout_middle);

//        middleLayout.setVisibility(View.VISIBLE);
        AnimatorUtil.showViewAnimator(middleLayout, 1000);

        TextView mBtnLogin = findViewById(R.id.main_btn_login);
        mBtnLogin.setOnClickListener(this);

        progress = findViewById(R.id.layout_progress);
        msgText = findViewById(R.id.id_tv_loading_dialog_text);
        userName = findViewById(R.id.user_name);
        password = findViewById(R.id.password);
        captcha = findViewById(R.id.captcha);
        captchaImg = findViewById(R.id.img_captcha);
        captchaImg.setOnClickListener(this);

        TextView changeCaptcha = findViewById(R.id.change_captcha);
        changeCaptcha.setOnClickListener(this);

        //http://202.115.47.141/img/captcha.jpg?60

        userName.setText(SPHelper.getString("user_name", ""));
        password.setText(SPHelper.getString("password", ""));

    }

    private void getCookie() {
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
                    cookie = response.header("Set-Cookie");
                    Log.d("cookie", "cookie=" + cookie);
                    Message msg = new Message();
                    msg.what = 2;
                    sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.obj = e.getMessage();
                    msg.what = -1;
                    sendMessage(msg);
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
                            .userAgent(TimetableHelper.UA)
                            .header("Referer", "http://202.115.47.141/login")
                            .data("j_username", userName.getText().toString())
                            .data("j_password", password.getText().toString())
                            .data("j_captcha", captcha.getText().toString())
                            .ignoreHttpErrors(true)
//                            .followRedirects(false)
                            .execute();
                    Log.d("response.statusCode()=", "" + response.statusCode());
                    Log.d("body=", "" + response.body());
                    if (response.statusCode() != 200 || response.body().contains("badCredentials")) {
                        Message msg = new Message();
                        msg.what = 3;
                        sendMessage(msg);
                    } else {
                        Message msg = new Message();
                        msg.what = 4;
                        sendMessage(msg);
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
                            sendMessage(msg);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.obj = e.getMessage();
                    msg.what = -1;
                    sendMessage(msg);
                }
            }
        }.start();
    }

    private void onError() {
        AnimatorUtil.hideViewAnimator(progress, 500, new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) { }

            @Override
            public void onAnimationEnd(Animator animation) {
                progress.setVisibility(View.GONE);
                AnimatorUtil.showViewAnimator(middleLayout, 500);
                captcha.setText("");
                CaptchaFetcher.fetchcaptcha(cookie, captchaImg);
            }

            @Override
            public void onAnimationCancel(Animator animation) { }

            @Override
            public void onAnimationRepeat(Animator animation) { }
        });
    }

    @Override
    protected void handleMessage(Message msg) {
        if (msg.what == -1) {
            String errorMsg = (String) msg.obj;
            Toast.makeText(this, "错误信息：" + errorMsg, Toast.LENGTH_SHORT).show();
            onError();
        } else if (msg.what == 2) {
            Toast.makeText(LoginActivity.this, "cookie=" + cookie, Toast.LENGTH_SHORT).show();
            SPHelper.putString("cookie", cookie);
            CaptchaFetcher.fetchcaptcha(cookie, captchaImg);
        } else if (msg.what == 3) {
            Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
            onError();
        } else if (msg.what == 4) {
            Toast.makeText(LoginActivity.this, "登录成功!获取课表信息中。。。", Toast.LENGTH_SHORT).show();
            msgText.setText("获取课表信息中...");
            SPHelper.putString("user_name", userName.getText().toString());
            SPHelper.putString("password", password.getText().toString());
        } else if (msg.what == 5) {
            String json = (String) msg.obj;
            try {
                TimetableHelper.writeToJson(LoginActivity.this, json);
                SPHelper.putBoolean("logined", true);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                //发送广播。更新桌面插件
                updateWidget(true);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
                onError();
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.change_captcha || id == R.id.img_captcha) {
            if (!TextUtils.isEmpty(cookie)) {
                CaptchaFetcher.fetchcaptcha(cookie, captchaImg);
            }
        } else if (id == R.id.main_btn_login) {
            if (progress.getVisibility() == View.VISIBLE) {
                return;
            }
            if (userName.getText().toString().isEmpty() || password.getText().toString().isEmpty() || captcha.getText().toString().isEmpty()) {
                Toast.makeText(LoginActivity.this, "请输入正确的信息", Toast.LENGTH_SHORT).show();
            } else {
                SPHelper.putString("user_name", userName.getText().toString());
                SPHelper.putString("password", password.getText().toString());
                AnimatorUtil.hideViewAnimator(middleLayout, 500, new AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        progress.setVisibility(View.VISIBLE);
                        AnimatorUtil.shakeAnimator(progress, 1000);
                        middleLayout.setVisibility(View.INVISIBLE);
                        msgText.setText("登录中...");
                        login();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

            }
        }
    }
}
