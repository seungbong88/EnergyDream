package com.example.energydream;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.energydream.Model.Member;

import java.util.ArrayList;

/**
 * Created by 이명남 on 2018-11-09.
 */

public class HistoryAdapter extends BaseAdapter {
    LayoutInflater inflater = null;

    ArrayList<Member.Donation_user> m_data;

    public HistoryAdapter(ArrayList<Member.Donation_user> m_data){
        this.m_data = m_data;
    }
    @Override
    public int getCount() {
        return m_data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            final Context context = parent.getContext();

            if (inflater == null) {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            convertView = inflater.inflate(R.layout.history_item, parent, false);
        }

        TextView tx_busi_name = (TextView) convertView.findViewById(R.id.history_busi_name);
        TextView tx_comp_name = (TextView) convertView.findViewById(R.id.history_comp_name);
        TextView tx_coin = (TextView) convertView.findViewById(R.id.history_coin);
        TextView tx_date = (TextView) convertView.findViewById(R.id.history_date);

        tx_busi_name.setText(m_data.get(position).getBusiness_name());
        Log.v("히스토리", ""+m_data.get(0).getBusiness_name()+"");
        tx_comp_name.setText(m_data.get(position).getCompany());
        tx_coin.setText(m_data.get(position).getDona_mileage() + "");
        tx_date.setText(m_data.get(position).getDate());

        return convertView;
    }

}

class ItemData{

    public String comp_name; // 기업명
    public String busi_name; // 사업명
    public String date;     // 기부날짜
    public int coin;        // 기부금액

    public ItemData(String c_name, String b_date, String date, int coin){
        this.comp_name = c_name;
        this.busi_name = b_date;
        this.date = date;
        this.coin = coin;
    }

}