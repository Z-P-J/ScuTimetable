package com.scu.timetable.ui.activity;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.scu.timetable.R;
import com.scu.timetable.service.AlarmService;
import com.scu.timetable.ui.fragment.MainFragment;
import com.scu.timetable.ui.fragment.dialog.UpdateDialog;
import com.scu.timetable.utils.TimetableHelper;
import com.scu.timetable.utils.UpdateUtil;
import com.zpj.fragmentation.anim.DefaultHorizontalAnimator;
import com.zpj.fragmentation.anim.DefaultVerticalAnimator;
import com.zpj.fragmentation.anim.FragmentAnimator;
import com.zpj.toast.ZToast;
import com.zpj.utils.StatusBarUtils;

/**
 * @author Z-P-J
 */
public final class MainActivity extends BaseActivity {

    private long firstTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StatusBarUtils.transparentStatusBar(this);

        AlarmService.start(this);

        MainFragment mainFragment = findFragment(MainFragment.class);
        if (mainFragment == null) {
            mainFragment = new MainFragment();
            loadRootFragment(R.id.content, mainFragment);
        }


        UpdateUtil.with(this)
                .setOnErrorListener(throwable -> ZToast.error("检查更新出错 errorMsg:" + throwable.getMessage()))
                .setOnUpdateCheckedListener((info, isLastedVersion) -> {
                    if (info != null) {
                        ZToast.normal("开始更新！");
                        new UpdateDialog()
                                .setUpdateInfo(info)
                                .show(MainActivity.this);
                    } else if (isLastedVersion) {
                        ZToast.normal("软件已是最新版");
                    }
                })
                .checkUpdate();
    }

    @Override
    public void onBackPressedSupport() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            pop();
            return;
        }
        if (TimetableHelper.isVisitorMode()) {
            super.onBackPressedSupport();
            finish();
            return;
        }
        if (System.currentTimeMillis() - firstTime > 2000) {
            ZToast.normal("再次点击退出！");
            firstTime = System.currentTimeMillis();
        } else {
            TimetableHelper.closeVisitorMode();
            ActivityCompat.finishAfterTransition(this);
        }
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
//        return new DefaultVerticalAnimator();
        return new DefaultHorizontalAnimator();
    }

}
