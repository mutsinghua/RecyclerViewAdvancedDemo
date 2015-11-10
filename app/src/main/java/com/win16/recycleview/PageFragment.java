package com.win16.recycleview;

import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by REXZOU on 11/10/2015.
 */
public class PageFragment extends Fragment {


    public static PageFragment newInstance() {
        PageFragment fragment = new PageFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(view.getContext());
        manager.setRecycleChildrenOnDetach(true);
        recyclerView.setLayoutManager(manager);
        List<PackageInfo> packageInfos = view.getContext().getPackageManager().getInstalledPackages(0);
        recyclerView.setAdapter(new MyAdapter(packageInfos));
        recyclerView.setRecycledViewPool(myPool);
        return view;
    }

    static RecyclerView.RecycledViewPool myPool = new RecyclerView.RecycledViewPool();
    static{
        myPool.setMaxRecycledViews(0, 10);
    }

}