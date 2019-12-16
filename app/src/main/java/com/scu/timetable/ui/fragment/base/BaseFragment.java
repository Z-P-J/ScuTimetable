package com.scu.timetable.ui.fragment.base;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scu.timetable.R;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import me.yokeyword.fragmentation_swipeback.SwipeBackFragment;

public abstract class BaseFragment extends SwipeBackFragment {

    @SuppressLint("ResourceType")
    @Nullable
    @Override
    public final View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        if (getLayoutId() > 0) {
            view = inflater.inflate(getLayoutId(), container, false);
//            CommonTitleBar titleBar = view.findViewById(R.id.title_bar);
//            if (titleBar != null && titleBar.getLeftImageButton() != null) {
//                titleBar.getLeftImageButton().setOnClickListener(v -> {
////                    if (getActivity() != null) {
////                        getActivity().onBackPressed();
////                    }
//                    pop();
//                });
//                if (titleBar.getCenterTextView() != null) {
//                    titleBar.getCenterTextView().setText(getToolbarTitle());
//                }
//            }
            initView(view, savedInstanceState);
        } else {
            view = super.onCreateView(inflater, container, savedInstanceState);
        }
        if (view != null && supportSwipeBack()) {
            return attachToSwipeBack(view);
        } else {
            return view;
        }
    }

    @LayoutRes
    protected abstract int getLayoutId();

    protected abstract void initView(View view, @Nullable Bundle savedInstanceState);

    protected boolean supportSwipeBack() {
        return true;
    }

    protected String getToolbarTitle() {
        return "标题";
    }

}
