package com.example.energydream;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.energydream.Model.Business;
import com.example.energydream.Model.CompanyMember;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DonationActivityDialog extends DialogFragment {

    TextView text_type;
    TextView text_name;
    TextView text_mileage;
    TextView text1;//벤처 설정때문에 넣은 가라 텍스트뷰
    TextView text2;
    Button btn_ok;
    Button btn_no;

    Business business;
    int mileage;
    String company_num;
    int index;
    boolean isVenture;
    String date;
    public DonationActivityDialog() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.activity_donation_dialog, container, false);
        text_type = (TextView)rootView.findViewById(R.id.type);
        text_name = (TextView)rootView.findViewById(R.id.name);
        text_mileage = (TextView)rootView.findViewById(R.id.mileage_value);
        btn_ok = (Button)rootView.findViewById(R.id.yes);
        btn_no = (Button)rootView.findViewById(R.id.no);


        text1 = rootView.findViewById(R.id.text_1);
        text2 = rootView.findViewById(R.id.mileage);

        Bundle bundle = getArguments();
        index = Integer.parseInt(bundle.getString("index"));
        mileage = Integer.parseInt(bundle.getString("mileage"));
        isVenture = bundle.getBoolean("isVenture");

        //Log.v("check_venture", ""+ business.isVenture());


        String type = (isVenture) ? "벤처기업후원" : "에너지기부";
        if(isVenture){
            business = NavActivity.venture_list.get(index);
            btn_ok.setBackground(getResources().getDrawable(R.drawable.design_venture_button));
            btn_ok.setText("응원하기");
            btn_no.setBackground(getResources().getDrawable(R.drawable.design_venture_button));
            text1.setText("위와 같이 응원하시겠습니까?");
            text2.setText("응원 마일리지 : ");
        }
        else{
            business = NavActivity.donation_list.get(index);
            btn_ok.setBackground(getResources().getDrawable(R.drawable.design_dialog_button));
            btn_ok.setText("기부하기");
            btn_no.setBackground(getResources().getDrawable(R.drawable.design_dialog_button));
            text1.setText("위와 같이 기부하시겠습니까?");
            text2.setText("기부 마일리지 : ");
        }
        text_type.setText(type);
        text_name.setText(business.getBusinessName());
        text_mileage.setText(mileage+"");


        if(isVenture){
        }

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int mile = Integer.parseInt(text_mileage.getText().toString());
                if(mile > 0) {
                    SimpleDateFormat Dateformat = new SimpleDateFormat("MM-dd HH:mm");
                    long time = System.currentTimeMillis();
                    date = Dateformat.format(new Date(time));

                    db_commit();
                    Toast.makeText(getContext(), "기부되었습니다.", Toast.LENGTH_LONG).show();

                    if(isVenture)
                        NavActivity.changeFragment(NavActivity.FRAGMENT_VENTURE_LIST);
                    else
                        NavActivity.changeFragment(NavActivity.FRAGMENT_DONATION_LIST);

                    dismiss();
                }else{
                    Toast.makeText(getContext(), "기부할 마일리지를 입력해주세요.", Toast.LENGTH_LONG).show();
                }
            }
        });
        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return  rootView;
    }

    private void db_commit(){

        // 사업/사용자 마일리지 추가 후 DB 업데이트
        business.addMileage(mileage);

        NavActivity.m_reference.child("CompanyMembers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 기업의 사업자 아이디(business name) 탐색
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    CompanyMember cm = snapshot.getValue(CompanyMember.class);
                    cm.sumWatt(mileage);
                    if(cm.getBusinessList() != null) {
                        for (int i = 0; i < cm.getBusinessList().size(); i++) {
                            if (cm.getBusinessList().get(i).getBusinessID().equals(business.getBusinessID())) {
                                company_num = cm.getCor_num();
                                String company_name = cm.getCor_name();

                                // 1) 사용자 마일리지 차감 및 기부내역 추가
                                NavActivity.login_user.donate(company_name, business.getBusinessName(), mileage, isVenture, date);
                                NavActivity.updateUser();


                                // 2) 사업 마일리지 추가
                                NavActivity.m_reference.child("CompanyMembers")
                                        .child(company_num)
                                        .child("businessList")
                                        .child(i + "")
                                        .setValue(business);

                                NavActivity.m_reference.child("CompanyMembers")
                                        .child(company_num)
                                        .setValue(cm);

                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}
