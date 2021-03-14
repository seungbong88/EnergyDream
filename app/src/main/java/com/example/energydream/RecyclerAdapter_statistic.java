package com.example.energydream;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.energydream.Model.Business;
import com.example.energydream.Model.CompanyMember;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by 이명남 on 2018-11-12.
 */

public class RecyclerAdapter_statistic extends RecyclerView.Adapter<RecyclerAdapter_statistic.ViewHolder>{

    FirebaseStorage storage = FirebaseStorage.getInstance();
    private ArrayList<CompanyMember> m_list;
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView img_view;
        TextView tx_name;
        TextView tx_id;
        TextView tx_count;
        TextView tx_watt;

        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            img_view  = itemView.findViewById(R.id.bus_img);
            tx_name  = itemView.findViewById(R.id.bus_name);
            tx_id    = itemView.findViewById(R.id.bus_id);
            tx_count = itemView.findViewById(R.id.bus_count);
            tx_watt  = itemView.findViewById(R.id.bus_watt);
        }

    }

    public RecyclerAdapter_statistic(ArrayList<CompanyMember> items){
        m_list = items;
    }

    @Override
    public RecyclerAdapter_statistic.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_admin_statistics_page, parent, false);
        RecyclerAdapter_statistic.ViewHolder vh = new RecyclerAdapter_statistic.ViewHolder(v);

        return vh;
    }


    @Override
    public void onBindViewHolder(final RecyclerAdapter_statistic.ViewHolder holder, final int position) {

        StorageReference sReference = storage.getReference().child("userIMG/").child("user (1).png");
        sReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(context).load(uri.toString()).into(holder.img_view);
            }
        });

        holder.tx_name.setText(m_list.get(position).getCor_name());
        holder.tx_id.setText(m_list.get(position).getCor_num());
        if(m_list.get(position).getBusinessList() == null) holder.tx_count.setText("0");
        else holder.tx_count.setText(m_list.get(position).getBusinessList().size()+"");
        holder.tx_watt.setText(getMileageCount(m_list.get(position))+"");

    }

    @Override
    public int getItemCount() {
        return m_list.size();
    }
    public void sortCompany(){

    }
    public int getMileageCount(CompanyMember cm){
        int totalMileage=0;
        ArrayList<Business> cmBusiness = cm.getBusinessList();
        if(cmBusiness!=null)
            for(int i=0; i<cmBusiness.size(); i++){
                totalMileage+=cmBusiness.get(i).getMileage();
            }
        return  totalMileage;
    }


}