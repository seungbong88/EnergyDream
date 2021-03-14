package com.example.energydream;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.energydream.Model.Business;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

import static java.lang.Math.abs;

/**
 * Created by 이명남 on 2018-11-12.
 */

public class RecyclerAdapter_venture  extends RecyclerView.Adapter<RecyclerAdapter_venture.ViewHolder>{

    FirebaseStorage storage = FirebaseStorage.getInstance();
    private ArrayList<Business> m_list;
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        LinearLayout linearLayout;
        ImageView img_m_view;
        TextView tx_title;
        TextView tx_host;
        ProgressBar progress;
        TextView tx_percent;
        TextView tx_fund;
        TextView tx_b_dday;
        Button btn_donation;

        ImageView img_success;
        ImageView img_fail;

        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            img_m_view = itemView.findViewById(R.id.image);
            tx_title = itemView.findViewById(R.id.title);
            tx_host=itemView.findViewById(R.id.host);
            progress = itemView.findViewById(R.id.progress);
            tx_percent = itemView.findViewById(R.id.percent);
            tx_fund = itemView.findViewById(R.id.fund);
            btn_donation = itemView.findViewById(R.id.donation);
            context = itemView.getContext();
            linearLayout = itemView.findViewById(R.id.linear_venture_list);
            tx_b_dday = itemView.findViewById(R.id.business_dday);

            img_success = itemView.findViewById(R.id.img_success);
            img_fail = itemView.findViewById(R.id.img_fail);
        }

        public void onClick(View v){
            Bundle args = new Bundle();
            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION){
                // 현재 프래그먼트 스택에 저장
                NavActivity.stack_fragment.push(NavActivity.manager.findFragmentById(R.id.content_fragment_layout));

                args.putBoolean("isVenture", true);
                args.putString("index", position+"");
                args.putString("fragment", NavActivity.FRAGMENT_VENTURE_LIST + "");

                NavActivity.getDetailFragment().setArguments(args);
                NavActivity.changeFragment(NavActivity.FRAGMENT_DETAIL);
            }
        }
    }

    public RecyclerAdapter_venture(ArrayList<Business> items){
        m_list = items;
    }

    @Override
    public RecyclerAdapter_venture.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_venture, parent, false);
        RecyclerAdapter_venture.ViewHolder vh = new RecyclerAdapter_venture.ViewHolder(v);

        return vh;
    }


    @Override
    public void onBindViewHolder(final RecyclerAdapter_venture.ViewHolder holder, final int position) {

        StorageReference sReference = storage.getReference().child("businessIMG/").child(m_list.get(position).getBusinessIMG());
        sReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(context).load(uri.toString()).into(holder.img_m_view);
            }
        });

        holder.tx_title.setText(m_list.get(position).getBusinessName());
        holder.tx_host.setText(m_list.get(position).getCompanyName());
        holder.progress.setProgress((m_list.get(position).getMileage()*100/m_list.get(position).getBusinessGoal()));
        holder.tx_percent.setText(m_list.get(position).getMileage()*100/m_list.get(position).getBusinessGoal()+ "%");
        holder.tx_fund.setText(m_list.get(position).getMileage()+"개");
        holder.img_fail.setVisibility(View.INVISIBLE);      // 초기화 반드시 필요
        holder.img_success.setVisibility(View.INVISIBLE);
        int get_tx_b_dday = calc_Dday(m_list.get(position).getGoalDate());
        if(get_tx_b_dday < 0) holder.tx_b_dday.setText("D + " + abs(get_tx_b_dday));
        else holder.tx_b_dday.setText("D - " + get_tx_b_dday);

        // 사업 종료 여부 확인
        String termi_date = m_list.get(position).getGoalDate().trim();
        if(calc_Dday(termi_date) < 0 ){
            if(isSuccess(position)) // 목표 마일리지 모금 성공
                holder.img_success.setVisibility(View.VISIBLE);
            else          // 목표 마일리지 모금 실패
                holder.img_fail.setVisibility(View.VISIBLE);
        }

        holder.btn_donation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String business_id = m_list.get(position).getBusinessID();

                if(NavActivity.login_user == null){
                    Toast.makeText(context, "로그인 시 이용할 수 있습니다", Toast.LENGTH_LONG ).show();
                }else {
                    // 현재 프래그먼트 스택에 저장
                    NavActivity.stack_fragment.push(NavActivity.manager.findFragmentById(R.id.content_fragment_layout));

                    // Bundle에 사업id 저장 후 프래그먼트 전환
                    Bundle bundle = new Bundle();
                    bundle.putString("index", position + "");
                    bundle.putBoolean("isVenture", true);

                    NavActivity.getDonationFragment().setArguments(bundle);
                    NavActivity.changeFragment(NavActivity.FRAGMENT_DONATION);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return m_list.size();
    }

    private boolean isSuccess(int index){
        if(m_list.get(index).getMileage() >= m_list.get(index).getBusinessGoal())
            return true;   // 모금 실패
        else
            return false;    // 모금 성공
    }

    // yyyy년 MM월 dd일 값과 현재날짜와의 차이값 계산(D-day)
    protected static int calc_Dday(String goal_day){
        int d_day = 0;

        //Calendar today = Calendar.getInstance();
        Calendar goday = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        String goal[] = goal_day.split(" ");
        int goal_year = Integer.parseInt(goal[0]);
        int goal_mon = Integer.parseInt(goal[2]);
        int goal_date = Integer.parseInt(goal[4]);
        goday.set(goal_year,goal_mon, goal_date);

        long goal_dday = goday.getTimeInMillis() / (24*60*60*1000);
        long to_dday   = today.getTimeInMillis() / (24*60*60*1000);

        d_day = (int)(goal_dday - to_dday);

        return d_day;
    }
}