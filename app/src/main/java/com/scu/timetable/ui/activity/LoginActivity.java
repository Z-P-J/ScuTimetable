package com.scu.timetable.ui.activity;

import android.os.Bundle;

import com.scu.timetable.R;
import com.scu.timetable.ui.fragment.LoginFragment;
import com.zpj.fragmentation.SupportActivity;

/**
 * @author Z-P-J
 * @date 2019
 */
public final class LoginActivity extends BaseActivity {

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
