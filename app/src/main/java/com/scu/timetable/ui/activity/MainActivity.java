package com.scu.timetable.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;

import com.felix.atoast.library.AToast;
import com.scu.timetable.R;
import com.scu.timetable.events.UpdateEvent;
import com.scu.timetable.model.UpdateBean;
import com.scu.timetable.service.AlarmService;
import com.scu.timetable.ui.fragment.MainFragment;
import com.scu.timetable.ui.fragment.UpdateDialogFragment;
import com.scu.timetable.utils.TimetableHelper;
import com.scu.timetable.utils.UpdateUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import me.yokeyword.eventbusactivityscope.EventBusActivityScope;
import me.yokeyword.fragmentation.SupportActivity;

/**
 * @author Z-P-J
 */
public final class MainActivity extends SupportActivity {

    private long firstTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);

        AlarmService.start(this);

        MainFragment mainFragment = findFragment(MainFragment.class);
        if (mainFragment == null) {
            mainFragment = new MainFragment();
        }
        loadRootFragment(R.id.content, mainFragment);

        UpdateUtil.newInstance().checkUpdate(this);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateEvent(UpdateEvent event) {
        if (event.getUpdateBean() != null) {
            AToast.normal("开始更新！");
//        http://tt.shouji.com.cn/wap/down/soft?id=1555815
            UpdateDialogFragment.newInstance(event.getUpdateBean()).show(getSupportFragmentManager());
        } else if (event.isLatestVersion()) {
            AToast.normal("软件已是最新版");
        } else if (TextUtils.isEmpty(event.getErrorMsg())) {
            AToast.error("检查更新出错 errorMsg:" + event.getErrorMsg());
        }
    }
}
