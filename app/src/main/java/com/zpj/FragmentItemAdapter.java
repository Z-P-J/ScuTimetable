package com.zpj;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class FragmentItemAdapter extends RecyclerView.Adapter<FragmentItemAdapter.ViewHolder>{
    private List<FragmentItem> fragmentItemList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        TextView textView;
        TextView jieshu;
        TextView shijian;

        public ViewHolder(View view){
            super(view);
            itemView=view;
            textView=(TextView)view.findViewById(R.id.text_view);
            jieshu=(TextView)view.findViewById(R.id.text_jieshu);
            shijian=(TextView)view.findViewById(R.id.text_shijian);
        }
    }

    public FragmentItemAdapter(List<FragmentItem> fragmentItemList){
        this.fragmentItemList=fragmentItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.fragement_item,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FragmentItemAdapter.ViewHolder holder, int position) {
        holder.textView.setText(fragmentItemList.get(position).getKechengName());
        holder.jieshu.setText(fragmentItemList.get(position).getJieshu());
        holder.shijian.setText(fragmentItemList.get(position).getShijian());
    }

    @Override
    public int getItemCount() {
        return fragmentItemList.size();
    }
}
