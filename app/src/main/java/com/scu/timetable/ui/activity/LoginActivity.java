package com.scu.timetable.ui.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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
import com.scu.timetable.ui.fragment.LoginFragment;
import com.scu.timetable.ui.popup.MoreInfoPopup;
import com.scu.timetable.utils.CaptchaFetcher;
import com.scu.timetable.utils.EncryptionUtils;
import com.scu.timetable.utils.LoginUtil;
import com.scu.timetable.utils.TimetableHelper;
import com.zpj.fragmentation.SupportActivity;
import com.zpj.fragmentation.dialog.impl.AlertDialogFragment;
import com.zpj.utils.AnimatorUtils;
import com.zpj.utils.PrefsHelper;
import com.zpj.utils.StatusBarUtils;

import org.json.JSONObject;

/**
 * @author Z-P-J
 * @date 2019
 */
public final class LoginActivity extends SupportActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        long start = System.currentTimeMillis();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LoginFragment fragment = findFragment(LoginFragment.class);
        if (fragment == null) {
            fragment = new LoginFragment();
        }
        loadRootFragment(R.id.content, fragment);

    }

}
