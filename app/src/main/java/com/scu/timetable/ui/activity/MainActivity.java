package com.scu.timetable.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.felix.atoast.library.AToast;
import com.scu.timetable.R;
import com.scu.timetable.model.ScuSubject;
import com.scu.timetable.model.SemesterBean;
import com.scu.timetable.model.UpdateBean;
import com.scu.timetable.service.AlarmService;
import com.scu.timetable.ui.activity.ActivityCollector;
import com.scu.timetable.ui.fragment.DetailFragment;
import com.scu.timetable.ui.fragment.EvaluationFragment;
import com.scu.timetable.ui.fragment.MainFragment;
import com.scu.timetable.ui.fragment.SettingFragment;
import com.scu.timetable.ui.fragment.UpdateDialogFragment;
import com.scu.timetable.ui.widget.DetailLayout;
import com.scu.timetable.utils.CaptchaFetcher;
import com.scu.timetable.utils.LoginUtil;
import com.scu.timetable.utils.StatusBarUtil;
import com.scu.timetable.utils.TimetableHelper;
import com.scu.timetable.utils.UpdateUtil;
import com.scu.timetable.utils.content.SPHelper;
import com.zhuangfei.timetable.TimetableView;
import com.zhuangfei.timetable.listener.ISchedule;
import com.zhuangfei.timetable.listener.IWeekView;
import com.zhuangfei.timetable.listener.OnSlideBuildAdapter;
import com.zhuangfei.timetable.model.Schedule;
import com.zhuangfei.timetable.view.WeekView;
import com.zpj.popupmenuview.CustomPopupMenuView;
import com.zpj.popupmenuview.OptionMenuView;
import com.zpj.zdialog.ZDialog;
import com.zpj.zdialog.ZListDialog;
import com.zpj.zdialog.base.IDialog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.yokeyword.fragmentation.SupportActivity;

/**
 * @author Z-P-J
 */
public final class MainActivity extends SupportActivity implements UpdateUtil.UpdateCallback {

    private long firstTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AlarmService.start(this);

        MainFragment mainFragment = findFragment(MainFragment.class);
        if (mainFragment == null) {
            mainFragment = new MainFragment();
        }
        loadRootFragment(R.id.content, mainFragment);

        UpdateUtil.with(this).checkUpdate(this);
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
            AToast.normal("再次点击退出！");
            firstTime = System.currentTimeMillis();
        } else {
            TimetableHelper.closeVisitorMode();
            finish();
        }
    }

    @Override
    public void onError(String errMsg) {
        AToast.normal("检查更新出错！");
    }

    @Override
    public void onGetLatestVersion(UpdateBean bean) {
        AToast.normal("开始更新！");
//        http://tt.shouji.com.cn/wap/down/soft?id=1555815
        UpdateDialogFragment.newInstance(bean).show(getSupportFragmentManager());
    }

    @Override
    public void isLatestVersion() {
        AToast.normal("软件已是最新版！");
    }
}
