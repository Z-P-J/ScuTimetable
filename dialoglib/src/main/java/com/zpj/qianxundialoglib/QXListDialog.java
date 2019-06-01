package com.zpj.qianxundialoglib;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import com.zpj.dialoglib.R;

import java.util.List;

/**
 * @author Z-P-J
 * @date 2019/6/1 16:25
 */
public class QXListDialog<T> {

    private Context context;

    private final int layoutRes = R.layout.layout_dialog_list;

    private QianxunDialog dialog;

    private EasyRecyclerView<T> easyRecyclerView;

    private List<T> list;

    private EasyAdapter.EasyAdapterCallback<T> callback;

    private RecyclerView.LayoutManager layoutManager;

    private int gravity = Gravity.CENTER;

    @LayoutRes
    private int itemRes;


    public QXListDialog(Context context) {
        this.context = context;
        View view = LayoutInflater.from(context).inflate(layoutRes, null, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        easyRecyclerView = new EasyRecyclerView<T>(recyclerView);
        dialog = QianxunDialog.with(context).setDialogView(view);
    }

//    public static QXListDialog with(Context context) {
//        return new QXListDialog(context);
//    }

    public QXListDialog<T> setItemRes(@LayoutRes int res) {
        this.itemRes = res;
        return this;
    }

    public QXListDialog<T> setItemList(List<T> list) {
        this.list = list;
        return this;
    }

    public QXListDialog<T> setEasyAdapterCallback(EasyAdapter.EasyAdapterCallback<T> easyAdapterCallback) {
        callback = easyAdapterCallback;
        return this;
    }

    public QXListDialog<T> setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
        return this;
    }

    public QXListDialog<T> setGravity(int gravity) {
        this.gravity = gravity;
        return this;
    }

    public void show() {
        easyRecyclerView.setList(list)
                .setItemRes(itemRes)
                .setLayoutManager(layoutManager == null ? new LinearLayoutManager(context) : layoutManager)
                .setEasyAdapterCallback(callback)
                .build();
        dialog.setGravity(gravity).show();
    }

    public void dismiss() {
        dialog.dismiss();
    }

}
