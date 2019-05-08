package com.scu.timetable.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.scu.timetable.R;
import com.wang.avi.AVLoadingIndicatorView;

public class LoadingDialogFragment extends DialogFragment {

    private static final String[] INDICATOR_NAMES = {
            "BallPulseIndicator", "BallGridPulseIndicato", "BallClipRotateIndicator", "BallClipRotatePulseIndicator",
            "SquareSpinIndicator", "BallClipRotateMultipleIndicator", "BallPulseRiseIndicator", "BallRotateIndicator",
            "CubeTransitionIndicator", "BallZigZagIndicator", "BallZigZagDeflectIndicator", "BallTrianglePathIndicator",
            "BallScaleIndicator", "LineScaleIndicator", "LineScalePartyIndicator", "BallScaleMultipleIndicator",
            "BallPulseSyncIndicator", "BallBeatIndicator", "LineScalePulseOutIndicator", "LineScalePulseOutRapidIndicator",
            "BallScaleRippleIndicator", "BallScaleRippleMultipleIndicator", "BallSpinFadeLoaderIndicator", "LineSpinFadeLoaderIndicator",
            "TriangleSkewSpinIndicator", "PacmanIndicator", "BallGridBeatIndicator", "SemiCircleSpinIndicator",
    };

    private TextView loadingText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View view = inflater.inflate(R.layout.layout_dialog_loading, null);
        loadingText = view.findViewById(R.id.id_tv_loading_dialog_text);
        AVLoadingIndicatorView avLoadingIndicatorView = view.findViewById(R.id.AVLoadingIndicatorView);
        Bundle bundle = getArguments();
        if (bundle != null) {
            loadingText.setText(bundle.getString("msg"));
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("action.closeLoadingDialogFragment");
        getActivity().getApplicationContext().registerReceiver(broadcastReceiver, intentFilter);
        int i = (int) (Math.random() * 28);
        avLoadingIndicatorView.setIndicator(INDICATOR_NAMES[i]);
        avLoadingIndicatorView.setIndicatorColor(Color.parseColor("#2196f3"));
        avLoadingIndicatorView.smoothToShow();

        getDialog().setOnKeyListener((dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                return true;
            }
            return false;
        });
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().setCancelable(false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Window dialogWindow = getDialog().getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        lp.width = (int) (dm.widthPixels * 0.6);
        lp.height = (int) (dm.heightPixels * 0.2);
        dialogWindow.setAttributes(lp);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().getApplicationContext().unregisterReceiver(broadcastReceiver);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("action.closeLoadingDialogFragment".equals(intent.getAction())) {
                getDialog().dismiss();
            }
        }
    };

    public void updateMsg(String msg) {
        loadingText.setText(msg);
    }


}
