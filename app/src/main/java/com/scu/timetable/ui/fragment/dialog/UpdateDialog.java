package com.scu.timetable.ui.fragment.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.zpj.fragmentation.dialog.base.CenterDialogFragment;
import com.zpj.toast.ZToast;
import com.zpj.utils.PrefsHelper;

/**
 * @author Z-P-J
 */
public class UpdateDialog extends CenterDialogFragment
        implements View.OnClickListener {

    //======顶部========//
    /**
     * 顶部图片
     */
    private ImageView mIvTop;
    /**
     * 标题
     */
    private TextView mTvTitle;
    //======更新内容========//
    /**
     * 版本更新内容
     */
    private DetailLayout appVersion;
    private DetailLayout appSize;
    private DetailLayout appUpdateTime;
    private DetailLayout appUpdateInfo;
    /**
     * 版本更新
     */
    private StateButton mBtnUpdate;
    /**
     * 后台更新
     */
    private Button mBtnBackgroundUpdate;
    /**
     * 忽略版本
     */
    private TextView mTvIgnore;
    /**
     * 进度条
     */
    private NumberProgressBar mNumberProgressBar;
    //======底部========//
    /**
     * 底部关闭
     */
    private ImageView mIvClose;

    private UpdateInfo updateInfo;

    private DownloadMission mission;

    public UpdateDialog() {
        setCancelable(false);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public UpdateDialog setUpdateInfo(UpdateInfo updateInfo) {
        this.updateInfo = updateInfo;
        return this;
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.layout_dialog_update;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        ZDownloader.clearAll();

        //顶部图片
        mIvTop = findViewById(R.id.iv_top);
        //标题
        mTvTitle = findViewById(R.id.tv_title);
        //提示内容
        appVersion = findViewById(R.id.app_version);
        appSize = findViewById(R.id.app_size);
        appUpdateTime = findViewById(R.id.app_update_time);
        appUpdateInfo = findViewById(R.id.app_update_content);
        //更新按钮
        mBtnUpdate = findViewById(R.id.btn_update);
        //后台更新按钮
        mBtnBackgroundUpdate = findViewById(R.id.btn_background_update);
        //忽略
        mTvIgnore = findViewById(R.id.tv_ignore);
        //进度条
        mNumberProgressBar = findViewById(R.id.npb_progress);

        //关闭按钮
        mIvClose = findViewById(R.id.iv_close);

        setDialogTheme(Color.parseColor("#FFE94339"), R.drawable.xupdate_bg_app_top);
        initUpdateInfo();
        initListeners();
    }

    private void setDialogTheme(int color, int topResId) {
        mIvTop.setImageResource(topResId);
//        mBtnUpdate.setBackgroundColor(color);
        mBtnBackgroundUpdate.setBackgroundColor(color);
//        mBtnUpdate.setBackgroundDrawable(DrawableUtils.getDrawable(UpdateUtils.dip2px(4, getActivity()), color));
//        mBtnBackgroundUpdate.setBackgroundDrawable(DrawableUtils.getDrawable(UpdateUtils.dip2px(4, getActivity()), color));
        mNumberProgressBar.setProgressTextColor(color);
        mNumberProgressBar.setReachedBarColor(color);
        //随背景颜色变化
        mBtnUpdate.setTextColor(Color.WHITE);
    }

    private void initUpdateInfo() {
        //弹出对话框
        final String newVersion = updateInfo.getVersionName();
        appVersion.setContent(newVersion);
        appSize.setContent(updateInfo.getFileSize());
        appUpdateTime.setContent(updateInfo.getUpdateTime());
        appUpdateInfo.setContent(updateInfo.getUpdateContent());
        mTvTitle.setText(String.format("是否升级到%s版本？", newVersion));

        mTvIgnore.setVisibility(View.VISIBLE);
    }

    private void initListeners() {
        mBtnUpdate.setOnClickListener(this);
        mBtnBackgroundUpdate.setOnClickListener(this);
        mIvClose.setOnClickListener(this);
        mTvIgnore.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        //点击版本升级按钮【下载apk】
        if (i == R.id.btn_update) {
            //权限判断是否有访问外部存储空间权限
//            int flag = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
//            if (flag != PackageManager.PERMISSION_GRANTED) {
//                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_REQUEST_PERMISSIONS);
//            } else {
//                installApp();
//            }
            mission = ZDownloader.download(updateInfo.getDownloadUrl())
                    .addListener(missionListener)
                    .start();
//            mission = DownloadMission.create(updateInfo.getDownloadUrl(), null, MissionConfig.with());
//            mission.addListener(missionListener);
//            mission.start();
        } else if (i == R.id.btn_background_update) {
            //点击后台更新按钮
            ZToast.normal("后台更新");
            dismiss();
        } else if (i == R.id.iv_close) {
            //点击关闭按钮
            ZToast.normal("关闭");
            dismiss();
        } else if (i == R.id.tv_ignore) {
            //点击忽略按钮
            ZToast.normal("忽略更新");
            PrefsHelper.with().putString("ignore_version", appVersion.getContentTextView().getText().toString());
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
                mBtnUpdate.setVisibility(View.GONE);
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
