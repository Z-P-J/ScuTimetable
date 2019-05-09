//package com.scu.timetable.utils;
//
//import android.app.Dialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.res.ColorStateList;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.support.v4.app.FragmentTransaction;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.scu.timetable.BaseActivity;
//import com.scu.timetable.R;
//import com.scu.timetable.utils.content.SPHelper;
//import com.scu.timetable.view.LoadingDialogFragment;
//import com.wang.avi.AVLoadingIndicatorView;
//
//public class UIHelper {
////    private static Dialog mLoadingDialog;
//    private static LoadingDialogFragment loadingDialogFragment;
//
//    public static void updateLoadingMsg(String msg) {
//        loadingDialogFragment.updateMsg(msg);
//    }
//
//    public static void openLoadingDialogFragment(BaseActivity activity, String msg){
//        loadingDialogFragment = new LoadingDialogFragment();
//        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
//        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
////        FragmentManager fragmentManager = activity.getSupportFragmentManager();
//        Bundle bundle = new Bundle();
//        bundle.putString("msg", msg);
//        loadingDialogFragment.setArguments(bundle);
//        loadingDialogFragment.show(ft, "loading");
//    }
//
////    public static void openLoadingDialogFragment(Context context, String msg) {
////        View view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_loading, null);
////        loadingText = view.findViewById(R.id.id_tv_loading_dialog_text);
////        AVLoadingIndicatorView avLoadingIndicatorView = view.findViewById(R.id.AVLoadingIndicatorView);
////        loadingText.setText(msg);
////        mLoadingDialog = new Dialog(context, R.style.loading_dialog_style);
////        mLoadingDialog.setCancelable(false);
////        mLoadingDialog.setContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
////                LinearLayout.LayoutParams.MATCH_PARENT));
////
////        mLoadingDialog.show();
////        avLoadingIndicatorView.smoothToShow();
////        mLoadingDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
////            @Override
////            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
////                if (keyCode == KeyEvent.KEYCODE_BACK) {
//////                    mLoadingDialog.hide();
////                    return true;
////                }
////                return false;
////            }
////        });
////    }
//
////    public static void updateLoadingDialogFrament(String newMsg){
////        try{
////            TextView loadingText = loadingDialogFragment.getDialog().getWindow().findViewById(R.id.id_tv_loading_dialog_text);
////            loadingText.setText(oldMsg + newMsg);
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////    }
//
//    public static void closeLoadingDialogFragment(){
//        if (loadingDialogFragment != null) {
//            loadingDialogFragment.dismiss();
//        }
//    }
//
//}
