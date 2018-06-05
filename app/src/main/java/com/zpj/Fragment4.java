package com.zpj;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class Fragment4 extends BaseFragment {
    private View view;
    private RecyclerView recyclerView;
    private List<FragmentItem> fragmentItemList;
    private FragmentItemAdapter fragmentItemAdapter;
    private SharedPreferences sharedPreferences;
    private FragmentItem fragmentItem;
    private boolean isInit;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment4,container,false);
        recyclerView=(RecyclerView)view.findViewById(R.id.recycler_view_4);
        isInit=true;
        lazyLoadData();



        return view;
    }

    @Override
    public void lazyLoadData() {
        if (isVisible&&isInit){
            fragmentItemList=new ArrayList<>();
            sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getContext());
            LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            //recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            int m=4;
            for (int i=1;i<=15;i++){
                fragmentItem=new FragmentItem(sharedPreferences.getString(m+"_"+i,""),sharedPreferences.getString(i+"_jieshu",""),sharedPreferences.getString(i+"_shijian",""));
                fragmentItemList.add(fragmentItem);
            }

            //recyclerView=(RecyclerView) view.findViewById(R.id.recycler_view_4);

            fragmentItemAdapter=new FragmentItemAdapter(fragmentItemList);
            recyclerView.setAdapter(fragmentItemAdapter);
        }
    }
}
