package com.example.energydream;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ApproveBusinessActivity extends Fragment{

    private RecyclerView m_recycler_view;
    private RecyclerView.Adapter m_adapter;
    private RecyclerView.LayoutManager m_layout_manager;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_approve_business, container, false);
        Context context = view.getContext();

        m_recycler_view = (RecyclerView)view.findViewById(R.id.my_recycler_view);
        m_recycler_view.setHasFixedSize(true);//옵션

        m_adapter = new RecyclerAdapter_approve(NavActivity.notApprove_list); //스트링 배열 데이터 인자로
        m_recycler_view.setAdapter(m_adapter);

        //Linear layout manager 사용
        m_layout_manager = new LinearLayoutManager(context);
        m_recycler_view.setLayoutManager(m_layout_manager);

        //어답터 세팅
        return view;
    }
}