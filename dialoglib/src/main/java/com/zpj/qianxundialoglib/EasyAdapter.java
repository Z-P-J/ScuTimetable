package com.zpj.qianxundialoglib;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import java.util.List;

public class EasyAdapter<T> extends RecyclerView.Adapter<EasyAdapter.ViewHolder> {

    private List<T> list;

    private int itemRes;

    private EasyAdapterCallback<T> easyAdapterCallback;

    public interface EasyAdapterCallback<T> {

        ViewHolder onCreateViewHolder(List<T> list, View itemView, int i);

        void onBindViewHolder(List<T> list, View itemView, int i);

//        int onGetItemCount();

    }

//    public void setEasyAdapterCallback(EasyAdapterCallback<T> easyAdapterCallback) {
//        this.easyAdapterCallback = easyAdapterCallback;
//    }

    public EasyAdapter(List<T> list, int itemRes, EasyAdapterCallback<T> easyAdapterCallback) {
        this.list = list;
        this.itemRes = itemRes;
        this.easyAdapterCallback = easyAdapterCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(itemRes, viewGroup, false);
        if (easyAdapterCallback != null) {
            easyAdapterCallback.onCreateViewHolder(list, view, i);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        if (easyAdapterCallback != null) {
            easyAdapterCallback.onBindViewHolder(list, viewHolder.itemView, i);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

//    public void setItemRes(int res) {
//
//    }
}
