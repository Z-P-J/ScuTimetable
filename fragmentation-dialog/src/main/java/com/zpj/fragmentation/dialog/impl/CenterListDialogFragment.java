package com.zpj.fragmentation.dialog.impl;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zpj.fragmentation.dialog.R;
import com.zpj.fragmentation.dialog.base.CenterDialogFragment;
import com.zpj.fragmentation.dialog.interfaces.OnSelectListener;
import com.zpj.fragmentation.dialog.utils.DialogThemeUtils;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;

import java.util.ArrayList;
import java.util.List;

public class CenterListDialogFragment<T> extends CenterDialogFragment
        implements IEasy.OnBindViewHolderListener<T>,
        IEasy.OnItemClickListener<T> {

    protected final List<T> data = new ArrayList<>();

    protected String title;
    protected TextView tvTitle;

    protected OnSelectListener<T> selectListener;

    protected int bindItemLayoutId = R.layout._dialog_item_text;

    private IEasy.OnBindViewHolderListener<T> onBindViewHolderListener;
    private IEasy.OnItemClickListener<T> onItemClickListener;

    protected int getItemRes() {
        return bindItemLayoutId;
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout._dialog_layout_center_impl_list;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);

        tvTitle = findViewById(R.id.tv_title);

        if (tvTitle != null) {
            tvTitle.setTextColor(DialogThemeUtils.getMajorTextColor(context));
            if (TextUtils.isEmpty(title)) {
                tvTitle.setVisibility(View.GONE);
                findViewById(R.id._dialog_view_divider).setVisibility(View.GONE);
            } else {
                tvTitle.setText(title);
            }
        }

        FrameLayout flContainer = findViewById(R.id._fl_container);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) flContainer.getLayoutParams();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.weight = 0;

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        initRecyclerView(recyclerView, data);

        recyclerView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        recyclerView.getViewTreeObserver()
                                .removeOnGlobalLayoutListener(this);
                        CenterListDialogFragment.super.doShowAnimation();
                    }
                });
    }

    protected void initRecyclerView(RecyclerView recyclerView, List<T> list) {
        new EasyRecyclerView<T>(recyclerView)
                .setData(list)
                .setItemRes(getItemRes())
                .onBindViewHolder(this)
                .onItemClick(this)
                .build();
    }

    @Override
    public void doShowAnimation() {

    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<T> list, int position, List<Object> payloads) {
        if (onBindViewHolderListener != null) {
            onBindViewHolderListener.onBindViewHolder(holder, list, position, payloads);
        }
    }

    @Override
    public void onClick(EasyViewHolder holder, View view, T item) {
        if (onItemClickListener != null) {
            onItemClickListener.onClick(holder, view, item);
        }
    }

    public CenterListDialogFragment<T> setItemRes(int itemRes) {
        this.bindItemLayoutId = itemRes;
        return this;
    }

    public CenterListDialogFragment<T> setItemLayoutRes(int itemLayoutId) {
        this.bindItemLayoutId = itemLayoutId;
        return this;
    }

    public CenterListDialogFragment<T> setTitle(String title) {
        this.title = title;
        return this;
    }

    public CenterListDialogFragment<T> setData(List<T> data) {
        this.data.clear();
        this.data.addAll(data);
        return this;
    }

    public CenterListDialogFragment<T> addData(List<T> data) {
        this.data.clear();
        this.data.addAll(data);
        return this;
    }


    public CenterListDialogFragment<T> setOnSelectListener(OnSelectListener<T> selectListener) {
        this.selectListener = selectListener;
        return this;
    }

    public CenterListDialogFragment<T> setOnBindViewHolderListener(IEasy.OnBindViewHolderListener<T> onBindViewHolderListener) {
        this.onBindViewHolderListener = onBindViewHolderListener;
        return this;
    }

    public CenterListDialogFragment<T> setOnItemClickListener(IEasy.OnItemClickListener<T> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        return this;
    }
}
