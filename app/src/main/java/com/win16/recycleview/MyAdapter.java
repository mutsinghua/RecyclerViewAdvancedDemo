package com.win16.recycleview;

import android.content.pm.PackageInfo;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by REXZOU on 11/2/2015.
 */
public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private List<PackageInfo> lists;

    public MyAdapter(List<PackageInfo> lists) {
        this.lists = lists;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new MyViewHolder(layoutInflater.inflate(android.R.layout.simple_list_item_1,null));



    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.mTextView.setText(lists.get(position).packageName);

    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }
}
