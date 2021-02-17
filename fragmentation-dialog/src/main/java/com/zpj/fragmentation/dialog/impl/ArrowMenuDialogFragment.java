package com.zpj.fragmentation.dialog.impl;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.Nullable;
import android.support.v7.view.menu.MenuBuilder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.zpj.fragmentation.dialog.base.ArrowDialogFragment;
import com.zpj.fragmentation.dialog.model.OptionMenu;
import com.zpj.fragmentation.dialog.widget.OptionMenuView;
import com.zpj.fragmentation.dialog.widget.PopHorizontalScrollView;
import com.zpj.fragmentation.dialog.widget.PopVerticalScrollView;
import com.zpj.utils.ContextUtils;
import com.zpj.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArrowMenuDialogFragment extends ArrowDialogFragment {

    private int menuRes = 0;

    private final List<OptionMenu> optionMenus = new ArrayList<>();

    private int mOrientation = LinearLayout.VERTICAL;

    private OnItemClickListener onItemClickListener;

    @Override
    protected int getContentLayoutId() {
        return 0;
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);

        OptionMenuView mOptionMenuView = new OptionMenuView(context);
        if (mOrientation == LinearLayout.VERTICAL) {
            mOptionMenuView.setMinimumWidth((int) (ScreenUtils.getScreenWidth(context) / 2.5));
        }
        mOptionMenuView.setOrientation(mOrientation);
        mOptionMenuView.setOnOptionMenuClickListener(new OptionMenuView.OnOptionMenuClickListener() {
            @Override
            public boolean onOptionMenuClick(int position, OptionMenu menu) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position, menu);
                }
                dismiss();
                return true;
            }
        });

        ViewGroup scrollView = getScrollView(mOptionMenuView.getOrientation());
        scrollView.addView(mOptionMenuView);
        mPopLayout.addView(scrollView);


        if (menuRes > 0) {
            mOptionMenuView.inflate(menuRes, new MenuBuilder(context));
        } else {
            mOptionMenuView.setOptionMenus(optionMenus);
        }


    }

    private ViewGroup getScrollView(int orientation) {
        ViewGroup scrollView;
        if (orientation == LinearLayout.HORIZONTAL) {
            scrollView = new PopHorizontalScrollView(getContext());
            scrollView.setHorizontalScrollBarEnabled(false);
            scrollView.setVerticalScrollBarEnabled(false);
        } else {
            scrollView = new PopVerticalScrollView(getContext());
            scrollView.setHorizontalScrollBarEnabled(false);
            scrollView.setVerticalScrollBarEnabled(false);
        }
        return scrollView;
    }



    //----------------------------------setter----------------------------------

    public ArrowMenuDialogFragment setMenuRes(int menuRes) {
        this.menuRes = menuRes;
        return this;
    }

    public ArrowMenuDialogFragment setOptionMenus(List<OptionMenu> optionMenus) {
        this.optionMenus.addAll(optionMenus);
        return this;
    }

    public ArrowMenuDialogFragment setOptionMenus(@ArrayRes int strArrayIds) {
        return setOptionMenus(ContextUtils.getApplicationContext().getResources().getStringArray(strArrayIds));
    }

    public ArrowMenuDialogFragment setOptionMenus(String ... titles) {
        this.optionMenus.clear();
        for (String title : titles) {
            addOptionMenus(title);
        }
        return this;
    }

    public ArrowMenuDialogFragment addOptionMenus(OptionMenu optionMenu) {
        this.optionMenus.add(optionMenu);
        return this;
    }

    public ArrowMenuDialogFragment addOptionMenus(String title) {
        this.optionMenus.add(new OptionMenu(title));
        return this;
    }

    public ArrowMenuDialogFragment addOptionMenus(String...titles) {
        for (String title : titles) {
            addOptionMenus(title);
        }
        return this;
    }

    public ArrowMenuDialogFragment addOptionMenuIf(boolean flag, String item) {
        if (flag) {
            addOptionMenus(item);
        }
        return this;
    }

    public ArrowMenuDialogFragment setOrientation(int orientation) {
        this.mOrientation = orientation;
        return this;
    }

    public ArrowMenuDialogFragment setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        return this;
    }

}
