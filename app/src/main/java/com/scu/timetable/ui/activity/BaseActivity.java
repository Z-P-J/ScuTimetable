package com.scu.timetable.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.scu.timetable.utils.EventBus;
import com.zpj.fragmentation.SupportActivity;
import com.zpj.fragmentation.SupportFragment;
import com.zpj.fragmentation.dialog.IDialog;
import com.zpj.fragmentation.dialog.impl.LoadingDialogFragment;
import com.zpj.rxbus.RxObserver;

import io.reactivex.functions.Consumer;

public class BaseActivity extends SupportActivity {

    private LoadingDialogFragment loadingDialogFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EventSender.onStartFragmentEvent(this, this::start);
        RxObserver.with(this, SupportFragment.class)
                .bindToLife(this)
                .subscribe(this::start);

        EventBus.onShowLoading(this, (title, isUpdate) -> {
            if (loadingDialogFragment != null) {
                if (isUpdate) {
                    loadingDialogFragment.setTitle(title);
                    return;
                }
                loadingDialogFragment.dismiss();
            }
            loadingDialogFragment = null;
            loadingDialogFragment = new LoadingDialogFragment().setTitle(title);
            loadingDialogFragment.show(BaseActivity.this);
        });

        EventBus.onHideLoading(this, new Consumer<IDialog.OnDismissListener>() {
            @Override
            public void accept(IDialog.OnDismissListener listener) throws Exception {
                if (loadingDialogFragment != null) {
                    loadingDialogFragment.setOnDismissListener(listener);
                    loadingDialogFragment.dismiss();
                    loadingDialogFragment = null;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (loadingDialogFragment != null) {
            loadingDialogFragment.dismiss();
            loadingDialogFragment = null;
        }
        super.onDestroy();
    }
}
