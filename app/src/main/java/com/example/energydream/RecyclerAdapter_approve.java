package com.example.energydream;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.energydream.Model.Business;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by 이명남 on 2018-12-09.
 */

public class RecyclerAdapter_approve  extends RecyclerView.Adapter<RecyclerAdapter_approve.ViewHolder>{

    FirebaseDatabase database;
    private ArrayList<Business> mList;
    private Context context;
    Business business;
    int gPosition;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView titletext;
        TextView hosttext;
        TextView goalDate;
        TextView goalMileage;

        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            titletext = itemView.findViewById(R.id.title);
            hosttext=itemView.findViewById(R.id.host);
            goalDate=itemView.findViewById(R.id.goal_date);
            goalMileage=itemView.findViewById(R.id.goal_milage);

            context = itemView.getContext();
        }
        public void onClick(View v){
            // Approve Detail 페이지로 이동
            Bundle bundle = new Bundle();
            bundle.putInt("index", getAdapterPosition());
            NavActivity.getApproveDtailFragment().setArguments(bundle);
            NavActivity.stack_fragment.push(NavActivity.manager.findFragmentById(R.id.content_fragment_layout));
            NavActivity.changeFragment(NavActivity.FRAGMENT_APPROVE_DETAIL);
        }
    }
    public RecyclerAdapter_approve(ArrayList<Business> items){
        mList = items;
    }
    @Override
    public RecyclerAdapter_approve.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_approve_business, parent, false);
        RecyclerAdapter_approve.ViewHolder vh = new RecyclerAdapter_approve.ViewHolder(v);

        return vh;
    }


    public void onBindViewHolder(final RecyclerAdapter_approve.ViewHolder holder, int position) {
        holder.titletext.setText(mList.get(position).getBusinessName());
        holder.hosttext.setText(mList.get(position).getCompanyName());
        holder.goalDate.setText(mList.get(position).getGoalDate());
        holder.goalMileage.setText(mList.get(position).getBusinessGoal()+"");
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


}