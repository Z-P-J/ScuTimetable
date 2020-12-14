package com.scu.timetable.ui.fragment;

import android.Manifest;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.felix.atoast.library.AToast;
import com.lxj.xpermission.PermissionConstants;
import com.lxj.xpermission.XPermission;
import com.scu.timetable.R;
import com.scu.timetable.ui.activity.MainActivity;
import com.scu.timetable.ui.fragment.base.SkinFragment;
import com.scu.timetable.ui.fragment.dialog.MoreInfoDialog;
import com.scu.timetable.utils.CaptchaFetcher;
import com.scu.timetable.utils.EncryptionUtils;
import com.scu.timetable.utils.LoginUtil;
import com.scu.timetable.utils.TimetableHelper;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.fragmentation.dialog.impl.AlertDialogFragment;
import com.zpj.utils.AnimatorUtils;
import com.zpj.utils.PrefsHelper;
import com.zpj.utils.StatusBarUtils;

import org.json.JSONObject;

/**
 * @author Z-P-J
 * @date 2019
 */
public final class LoginFragment extends SkinFragment
        implements View.OnClickListener, LoginUtil.LoginCallback {

    private EditText userName;
    private EditText password;
    private EditText captcha;
    private ImageView captchaImg;
    private String cookie;

    private View progress;

    private TextView msgText;

    private RelativeLayout middleLayout;

    private LinearLayout visitorLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {

        TimetableHelper.closeVisitorMode();
        postDelayed(this::showRequestPermissionPopup, 500);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void updateWidget(boolean isLogined) {
        //发送广播。更新桌面插件
        Intent intent = new Intent();
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        intent.putExtra("islogined", isLogined);
        context.sendBroadcast(intent);
    }

    public void initView() {
        middleLayout = findViewById(R.id.layout_middle);

//        middleLayout.setVisibility(View.VISIBLE);
        AnimatorUtils.showViewAnimator(middleLayout, 1000);

        TextView mBtnLogin = findViewById(R.id.main_btn_login);
        mBtnLogin.setOnClickListener(this);

        visitorLayout = findViewById(R.id.layout_visitor);
        visitorLayout.setVisibility(View.VISIBLE);
        TextView visitorLogin = findViewById(R.id.visitor_mode);
        //下划线
        visitorLogin.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        //抗锯齿
        visitorLogin.getPaint().setAntiAlias(true);
        visitorLogin.setOnClickListener(this);
        ImageView btnInfo = findViewById(R.id.btn_info);
        btnInfo.setOnClickListener(this);


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

        userName.setText(EncryptionUtils.decryptByAES(PrefsHelper.with().getString("user_name", "")));
        password.setText(EncryptionUtils.decryptByAES(PrefsHelper.with().getString("password", "")));

    }

    private void onError() {
        AnimatorUtils.hideViewAnimator(progress, 500, new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) { }

            @Override
            public void onAnimationEnd(Animator animation) {
                progress.setVisibility(View.GONE);
                AnimatorUtils.showViewAnimator(middleLayout, 500);
                visitorLayout.setVisibility(View.VISIBLE);
                captcha.setText("");
                CaptchaFetcher.fetchCaptcha(cookie, captchaImg);
            }

            @Override
            public void onAnimationCancel(Animator animation) { }

            @Override
            public void onAnimationRepeat(Animator animation) { }
        });
    }

    private void login() {
        if (progress.getVisibility() == View.VISIBLE) {
            return;
        }
        if (userName.getText().toString().isEmpty() || password.getText().toString().isEmpty() || captcha.getText().toString().isEmpty()) {
            AToast.normal("请输入正确的信息");
        } else {
            AnimatorUtils.hideViewAnimator(middleLayout, 500, new AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) { }

                @Override
                public void onAnimationEnd(Animator animation) {
                    progress.setVisibility(View.VISIBLE);
                    AnimatorUtils.shakeAnimator(progress, 1000);
                    middleLayout.setVisibility(View.INVISIBLE);
                    visitorLayout.setVisibility(View.GONE);
                    msgText.setText("登录中...");
//                        login();
                    LoginUtil.with()
                            .setLoginCallback(LoginFragment.this)
                            .login(
                                    userName.getText().toString(),
                                    password.getText().toString(),
                                    captcha.getText().toString()
                            );
                }

                @Override
                public void onAnimationCancel(Animator animation) { }

                @Override
                public void onAnimationRepeat(Animator animation) { }
            });
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.change_captcha || id == R.id.img_captcha) {
            if (!TextUtils.isEmpty(cookie)) {
                CaptchaFetcher.fetchCaptcha(cookie, captchaImg);
            }
        } else if (id == R.id.main_btn_login) {
            login();
        } else if (id == R.id.visitor_mode){
            if (TimetableHelper.startVisitorMode(context)) {
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
                //发送广播。更新桌面插件
                updateWidget(true);
                _mActivity.finish();
            }
        } else if (id == R.id.btn_info) {
            new MoreInfoDialog()
                    .setTitle("关于游客模式")
                    .setContent("在该模式下会显示软件内置的一个课程表，并且在该模式下有些功能不能使用！")
                    .show(LoginFragment.this);

        }
    }

    @Override
    public void onGetCookie(String cookie) {
        this.cookie = cookie;
//        Toast.makeText(LoginActivity.this, "cookie=" + cookie, Toast.LENGTH_SHORT).show();
        CaptchaFetcher.fetchCaptcha(cookie, captchaImg);
    }

    @Override
    public void onLoginSuccess() {
//        Toast.makeText(LoginActivity.this, "登录成功!获取课表信息中。。。", Toast.LENGTH_SHORT).show();
        msgText.setText(R.string.text_getting_data);
        PrefsHelper.with().putString("user_name", EncryptionUtils.encryptByAES(userName.getText().toString()));
        PrefsHelper.with().putString("password", EncryptionUtils.encryptByAES(password.getText().toString()));
    }

    @Override
    public void onLoginFailed() {
        AToast.normal(R.string.text_login_failed);
        onError();
    }

    @Override
    public void onLoginError(String errorMsg) {
        AToast.normal("错误信息:" + errorMsg);
        onError();
    }

    @Override
    public void onGetTimetable(JSONObject jsonObject) {
        try {
            TimetableHelper.writeToJson(context, jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            onError();
        }
    }

    @Override
    public void onGetTimetableFinished() {
        msgText.setText(R.string.text_getting_data_successfully);
        PrefsHelper.with().putBoolean("logined", true);
        Intent intent = new Intent(context, MainActivity.class);
        startActivity(intent);
        //发送广播。更新桌面插件
        updateWidget(true);
        _mActivity.finish();
    }

    @Override
    public void onGetSemesters(String json) {
        try {
            TimetableHelper.writeSemesterFile(context, json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showRequestPermissionPopup() {
        if (hasStoragePermissions(context)) {
            requestPermission();
        } else {
            new AlertDialogFragment()
                    .setTitle(R.string.title_permission)
                    .setContent(getString(R.string.content_permission))
                    .setPositiveButton(R.string.text_apply, dialog -> requestPermission())
                    .setNegativeButton(R.string.text_decline, dialog -> ActivityCompat.finishAfterTransition(_mActivity))
                    .show(this);
        }
    }

    private boolean hasStoragePermissions(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        XPermission.create(context, PermissionConstants.STORAGE)
                .callback(new XPermission.SimpleCallback() {
                    @Override
                    public void onGranted() {
                        if (TimetableHelper.isLogined(context)) {
                            postDelayed(() -> {
                                updateWidget(true);
                                startActivity(new Intent(context, MainActivity.class));
                                _mActivity.finish();
                            }, 500);
                        } else {
                            Window window = _mActivity.getWindow();
                            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                            StatusBarUtils.transparentStatusBar(window);
                            updateWidget(false);
                            initView();
                            LoginUtil.with()
                                    .setLoginCallback(LoginFragment.this)
                                    .getCookie();
                        }
                    }

                    @Override
                    public void onDenied() {
                        showRequestPermissionPopup();
                    }
                }).request();
    }

}
