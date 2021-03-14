package com.example.energydream;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.energydream.Model.CompanyMember;
import com.example.energydream.Model.Member;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AdminStatisticsActivity extends Fragment {
    int total_mileage=0;
    int present_mileage=0;

    ArrayList<CompanyMember> company;
    private RecyclerView m_recycler_view;
    private RecyclerView.Adapter m_adapter;
    private RecyclerView.LayoutManager m_layout_manager;

    TextView txAvaliable;
    TextView txTotal;
    public AdminStatisticsActivity() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.activity_admin_statistics, container, false);

        init(rootView);
        availableDonation();
        Context context = rootView.getContext();

        m_recycler_view = (RecyclerView) rootView.findViewById(R.id.admin_list_view);
        m_recycler_view.setHasFixedSize(true);    //옵션

        wattComparator wc = new wattComparator();
        Collections.sort(NavActivity.company_list, wc);

        m_adapter = new RecyclerAdapter_statistic(NavActivity.company_list); //스트링 배열 데이터 인자로
        m_recycler_view.setAdapter(m_adapter);

        //Linear layout manager 사용
        m_layout_manager = new LinearLayoutManager(context);
        m_recycler_view.setLayoutManager(m_layout_manager);

        return rootView;
    }
    public void init(View view){
        txAvaliable = view.findViewById(R.id.availableMileage);
        txTotal = view.findViewById(R.id.totalMielage);
    }

    private void availableDonation(){
        NavActivity.m_reference.child("Members").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 기업의 사업자 아이디(business name) 탐색
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Member member = snapshot.getValue(Member.class);
                    total_mileage+= member.getTotal_mileage();
                    present_mileage+=member.getMileage();
                }
                txTotal.setText(total_mileage+"");
                txAvaliable.setText(present_mileage+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    class wattComparator implements Comparator<CompanyMember>{
        @Override
        public int compare(CompanyMember c1, CompanyMember c2) {
            int com1 = c1.getWatt();
            int com2 = c2.getWatt();

            if(com1>com2) return -1;
            else if(com1<com2) return 1;
            else return 0;

        }
    }
}