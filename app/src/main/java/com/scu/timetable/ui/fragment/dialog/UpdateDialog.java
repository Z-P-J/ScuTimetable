package com.scu.timetable.ui.fragment.dialog;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.deadline.statebutton.StateButton;
import com.scu.timetable.R;
import com.scu.timetable.bean.UpdateInfo;
import com.scu.timetable.ui.widget.DetailLayout;
import com.scu.timetable.ui.widget.NumberProgressBar;
import com.zpj.downloader.BaseMission;
import com.zpj.downloader.DownloadMission;
import com.zpj.downloader.ZDownloader;
import com.zpj.downloader.constant.Error;
import com.zpj.fragmentation.dialog.base.CardDialogFragment;
import com.zpj.toast.ZToast;
import com.zpj.utils.PrefsHelper;
import com.zpj.utils.ScreenUtils;

/**
 * @author Z-P-J
 */
public class UpdateDialog extends CardDialogFragment
        implements View.OnClickListener {

    private TextView mTvTitle;
    private ImageView mIvClose;

    private StateButton mBtnUpdate;
    private StateButton mTvIgnore;
    private StateButton mBtnBackgroundUpdate;

    private NumberProgressBar mNumberProgressBar;

    private LinearLayout llContainer;

    private UpdateInfo updateInfo;

    private DownloadMission mission;

    public UpdateDialog() {
        setCancelable(false);
//        setTransparentBackground(true);
        setMarginHorizontal((int) (ScreenUtils.getScreenWidth() * 0.08f));
        int marginVertical = (int) (ScreenUtils.getScreenHeight() * 0.16f);
        setMarginTop(marginVertical);
        setMarginBottom(marginVertical);
//        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public UpdateDialog setUpdateInfo(UpdateInfo updateInfo) {
        this.updateInfo = updateInfo;
        return this;
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_dialog_update;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        ZDownloader.clearAll();

        mTvTitle = findViewById(R.id.tv_title);
        mIvClose = findViewById(R.id.iv_close);

        mTvIgnore = findViewById(R.id.btn_ignore);
        mBtnUpdate = findViewById(R.id.btn_update);
        mBtnBackgroundUpdate = findViewById(R.id.btn_background_update);

        mNumberProgressBar = findViewById(R.id.npb_progress);

        llContainer = findViewById(R.id.ll_container);

        TextView tvContent = findViewById(R.id.tv_update);
        tvContent.setText("最新版本：" + updateInfo.getVersionName() + "\n软件大小："
                + updateInfo.getFileSize() + "\n更新时间：" + updateInfo.getUpdateTime()
                + "\n" + updateInfo.getUpdateContent());

        mBtnUpdate.setOnClickListener(this);
        mBtnBackgroundUpdate.setOnClickListener(this);
        mIvClose.setOnClickListener(this);
        mTvIgnore.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_update) {
            mission = ZDownloader.download(updateInfo.getDownloadUrl())
                    .addListener(missionListener);
            mission.start();
        } else if (i == R.id.btn_background_update) {
            ZToast.normal("TODO 后台更新");
//            dismiss();
        } else if (i == R.id.iv_close) {
//            ZToast.normal("关闭");
            dismiss();
        } else if (i == R.id.btn_ignore) {
//            ZToast.normal("忽略更新");
            PrefsHelper.with().putString("ignore_version", updateInfo.getVersionName());
            dismiss();
        }
    }






    private final DownloadMission.MissionListener missionListener = new DownloadMission.MissionListener() {
        @Override
        public void onInit() {
            Log.d("onInit", "onInit");
        }

        @Override
        public void onStart() {
            Log.d("onStart", "onStart");
            if (isSupportVisible()) {
                mNumberProgressBar.setVisibility(View.VISIBLE);
                mNumberProgressBar.setProgress(0);
                mNumberProgressBar.setMax(100);
                llContainer.setVisibility(View.GONE);
                mBtnBackgroundUpdate.setVisibility(View.VISIBLE);
//                mBtnUpdate.setVisibility(View.GONE);
//                mBtnBackgroundUpdate.setVisibility(View.VISIBLE);
//                if (mPromptEntity.isSupportBackgroundUpdate()) {
//                    mBtnBackgroundUpdate.setVisibility(View.VISIBLE);
//                } else {
//                    mBtnBackgroundUpdate.setVisibility(View.GONE);
//                }
            }
        }

        @Override
        public void onPause() {
            Log.d("onPause", "onPause");
        }

        @Override
        public void onWaiting() {
            Log.d("onWaiting", "onWaiting");
        }

        @Override
        public void onRetry() {
            Log.d("onRetry", "onRetry");
        }

        @Override
        public void onProgress(BaseMission.ProgressInfo update) {
            Log.d("progress", "progress=" + update.getProgress());
            if (isSupportVisible()) {
                mNumberProgressBar.setProgress(Math.round(update.getProgress()));
            }
        }

        @Override
        public void onFinish() {
            Log.d("onFinish", "onFinish");
            if (isSupportVisible()) {
                mBtnBackgroundUpdate.setVisibility(View.GONE);
                dismiss();
            }
            mission.openFile(getContext());
        }

        @Override
        public void onError(Error e) {
            Log.d("errCode", "errCode=" + e);
            if (isSupportVisible()) {
                ZToast.normal(e.getErrorMsg());
                dismiss();
            }
        }

        @Override
        public void onDelete() {

        }

        @Override
        public void onClear() {

        }
    };
}
