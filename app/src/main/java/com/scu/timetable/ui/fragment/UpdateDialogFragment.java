/*
 * Copyright (C) 2018 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.scu.timetable.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scu.timetable.R;
import com.scu.timetable.model.UpdateBean;
import com.scu.timetable.ui.view.NumberProgressBar;
import com.zpj.qianxundialoglib.base.DialogFragment;
import com.zpj.qxdownloader.QianXun;
import com.zpj.qxdownloader.core.DownloadMission;
import com.zpj.qxdownloader.util.FileUtil;

import java.io.File;

/**
 * 版本更新提示器【DialogFragment实现】
 *
 * @author xuexiang
 * @since 2018/7/2 上午11:40
 */
public class UpdateDialogFragment extends DialogFragment implements View.OnClickListener {
    public final static String KEY_UPDATE_ENTITY = "key_update_entity";
    public final static String KEY_UPDATE_PROMPT_ENTITY = "key_update_prompt_entity";

    public final static int REQUEST_CODE_REQUEST_PERMISSIONS = 111;

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
    private TextView mTvUpdateInfo;
    /**
     * 版本更新
     */
    private Button mBtnUpdate;
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
    private LinearLayout mLlClose;
    private ImageView mIvClose;

    private UpdateBean bean;

    private DownloadMission mission;

    /**
     * 获取更新提示
     *
     * @return
     */
    public static UpdateDialogFragment newInstance(@NonNull UpdateBean bean) {
        UpdateDialogFragment fragment = new UpdateDialogFragment();
//        Bundle args = new Bundle();
        fragment.setUpdateBean(bean);
//                .setArguments(args);
        return fragment;
    }

    /**
     * 设置更新代理
     *
     * @param bean
     * @return
     */
    public UpdateDialogFragment setUpdateBean(UpdateBean bean) {
        this.bean = bean;
        return this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.XUpdate_Fragment_Dialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        initDialog();
    }

    private void initDialog() {
//        getDialog().setCanceledOnTouchOutside(false);
//        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
//            @Override
//            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                //如果是强制更新的话，就禁用返回键
//                return keyCode == KeyEvent.KEYCODE_BACK;
//            }
//        });
//
//        Window window = getDialog().getWindow();
//        if (window != null) {
//            window.setGravity(Gravity.CENTER);
//            WindowManager.LayoutParams lp = window.getAttributes();
//            DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
//            lp.height = (int) (displayMetrics.heightPixels * 0.8f);
//            lp.width = (int) (displayMetrics.widthPixels * 0.8f);
//            window.setAttributes(lp);
//        }
        if (getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_dialog_update, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        setDialogTheme(Color.parseColor("#FFE94339"), R.drawable.xupdate_bg_app_top);
        initUpdateInfo();
        initListeners();
    }

    private void initView(View view) {
        //顶部图片
        mIvTop = view.findViewById(R.id.iv_top);
        //标题
        mTvTitle = view.findViewById(R.id.tv_title);
        //提示内容
        mTvUpdateInfo = view.findViewById(R.id.tv_update_info);
        //更新按钮
        mBtnUpdate = view.findViewById(R.id.btn_update);
        //后台更新按钮
        mBtnBackgroundUpdate = view.findViewById(R.id.btn_background_update);
        //忽略
        mTvIgnore = view.findViewById(R.id.tv_ignore);
        //进度条
        mNumberProgressBar = view.findViewById(R.id.npb_progress);

        //关闭按钮+线 的整个布局
        mLlClose = view.findViewById(R.id.ll_close);
        //关闭按钮
        mIvClose = view.findViewById(R.id.iv_close);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * 初始化更新信息
     */
    private void initUpdateInfo() {
        //弹出对话框
        final String newVersion = bean.getVersionName();
        String updateInfo = bean.getUpdateContent();
        //更新内容
        mTvUpdateInfo.setText(updateInfo);
        mTvTitle.setText(String.format("是否升级到%s版本？", newVersion));

        mTvIgnore.setVisibility(View.VISIBLE);
    }

    /**
     * 设置
     *
     * @param color    主色
     * @param topResId 图片
     */
    private void setDialogTheme(int color, int topResId) {
        mIvTop.setImageResource(topResId);
        mBtnUpdate.setBackgroundColor(color);
        mBtnBackgroundUpdate.setBackgroundColor(color);
//        mBtnUpdate.setBackgroundDrawable(DrawableUtils.getDrawable(UpdateUtils.dip2px(4, getActivity()), color));
//        mBtnBackgroundUpdate.setBackgroundDrawable(DrawableUtils.getDrawable(UpdateUtils.dip2px(4, getActivity()), color));
        mNumberProgressBar.setProgressTextColor(color);
        mNumberProgressBar.setReachedBarColor(color);
        //随背景颜色变化
        mBtnUpdate.setTextColor(Color.WHITE);
    }

    private void initListeners() {
        mBtnUpdate.setOnClickListener(this);
        mBtnBackgroundUpdate.setOnClickListener(this);
        mIvClose.setOnClickListener(this);
        mTvIgnore.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        //点击版本升级按钮【下载apk】
        if (i == R.id.btn_update) {
            //权限判断是否有访问外部存储空间权限
//            int flag = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
//            if (flag != PackageManager.PERMISSION_GRANTED) {
//                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_REQUEST_PERMISSIONS);
//            } else {
//                installApp();
//            }
            mission = QianXun.download("http://tt.shouji.com.cn/wap/down/soft?id=1555815");
            mission.addListener(missionListener);
        } else if (i == R.id.btn_background_update) {
            //点击后台更新按钮
            Toast.makeText(getContext(), "后台更新", Toast.LENGTH_SHORT).show();
            dismiss();
        } else if (i == R.id.iv_close) {
            //点击关闭按钮
            Toast.makeText(getContext(), "关闭", Toast.LENGTH_SHORT).show();
            dismiss();
        } else if (i == R.id.tv_ignore) {
            //点击忽略按钮
            Toast.makeText(getContext(), "忽略", Toast.LENGTH_SHORT).show();
            dismiss();
        }
    }

    private void installApp() {

    }

    DownloadMission.MissionListener missionListener = new DownloadMission.MissionListener() {
        @Override
        public void onInit() {
            Log.d("onInit", "onInit");
        }

        @Override
        public void onStart() {
            Log.d("onStart", "onStart");
            if (!UpdateDialogFragment.this.isRemoving()) {
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
        public void onProgress(long done, long total) {
            Log.d("progress", "progress=" + mission.getProgress());
            if (!UpdateDialogFragment.this.isRemoving()) {
                mNumberProgressBar.setProgress(Math.round(mission.getProgress()));
            }
        }

        @Override
        public void onFinish() {
            Log.d("onFinish", "onFinish");
            if (!UpdateDialogFragment.this.isRemoving()) {
                mBtnBackgroundUpdate.setVisibility(View.GONE);
                dismissAllowingStateLoss();
            }
            FileUtil.openFile(getContext(), mission.getFile());
        }

        @Override
        public void onError(int errCode) {
            Log.d("errCode", "errCode=" + errCode);
            if (!UpdateDialogFragment.this.isRemoving()) {
                dismissAllowingStateLoss();
            }
        }
    };

    /**
     * 显示安装的按钮
     */
    private void showInstallButton(final File apkFile) {
        mNumberProgressBar.setVisibility(View.GONE);
        mBtnUpdate.setText("安装");
        mBtnUpdate.setVisibility(View.VISIBLE);
        mBtnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onInstallApk(apkFile);
            }
        });
    }

    private void onInstallApk() {

    }

    private void onInstallApk(File apkFile) {

    }

    @Override
    public void show(FragmentManager manager, String tag) {
        if (manager.isDestroyed()) {
            return;
        }
        super.show(manager, tag);
    }

    /**
     * 显示更新提示
     *
     * @param manager
     */
    public void show(FragmentManager manager) {
        show(manager, "update_dialog");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}

