package com.win16.recycleview;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Created by REXZOU on 11/2/2015.
 */
public class MyViewHolder extends RecyclerView.ViewHolder{

    static int count = 0;
    public TextView mTextView;

    public MyViewHolder(View itemView) {
        super(itemView);
        Log.d("MyViewHolder", "CREATE COUNT:" + count++);
        mTextView = (TextView) itemView.findViewById(android.R.id.text1);
    }
}
