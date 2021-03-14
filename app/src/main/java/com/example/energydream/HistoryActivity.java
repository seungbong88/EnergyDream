package com.example.energydream;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.energydream.Model.Member;

import java.util.ArrayList;

public class HistoryActivity extends Fragment {

    TextView tx_user_name;
    TextView tx_user_mileage;
    ListView listview = null;

    ArrayList<Member.Donation_user> donation_users;

    public HistoryActivity(){
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_history, container, false);


        donation_users = new ArrayList<>();
        donation_users = NavActivity.login_user.getDonationList();

        tx_user_name = (TextView)view.findViewById(R.id.tx_user_name);
        tx_user_mileage = (TextView)view.findViewById(R.id.userBal);

        tx_user_name.setText(NavActivity.login_user.getName());
        tx_user_mileage.setText(NavActivity.login_user.getMileage() + "");

        listview = (ListView)view.findViewById(R.id.history_list);
        HistoryAdapter adapter = new HistoryAdapter(donation_users);
        listview.setAdapter(adapter);

        return view;
    }
}
