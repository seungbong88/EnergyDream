package com.example.energydream;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.energydream.Model.Business;
import com.example.energydream.Model.CompanyMember;
import com.example.energydream.R;
import com.example.energydream.RecyclerAdapter_myCompPage;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/*
    사업자 마이페이지
    기능 : 해당 사업자가 올린 사업내역 확인 가능
*/
public class MyCompPageActivity extends Fragment {

    private ArrayList<Business> business_list;
    private RecyclerView m_recycler_view;
    private RecyclerView.Adapter m_adapter;
    private RecyclerView.LayoutManager m_layout_manager;

    public MyCompPageActivity() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.activity_mycompanypage, container, false);

        m_recycler_view = (RecyclerView)rootView.findViewById(R.id.compPage_recycler_view);
        m_recycler_view.setHasFixedSize(true);
        m_layout_manager = new LinearLayoutManager(getContext());
        m_recycler_view.setLayoutManager(m_layout_manager);

        m_adapter = new RecyclerAdapter_myCompPage(true);
        m_recycler_view.setAdapter(m_adapter);


        return rootView;
    }
    
}
