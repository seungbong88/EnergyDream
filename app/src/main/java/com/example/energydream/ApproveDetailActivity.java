package com.example.energydream;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.energydream.Model.Business;
import com.example.energydream.Model.CompanyMember;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

/* 관리자로그인 -> 사업승인 detail 페이지 */
public class ApproveDetailActivity extends Fragment {

    FirebaseStorage storage;
    int index;
    boolean is_approve;    // approve 면 true, refuse 면 false;
    String company_name;
    Business business;

    TextView tx_busi_name;
    TextView tx_comp_name;
    TextView tx_termi_date;
    TextView tx_goal;
    TextView tx_contents;
    Button btn_approve;
    Button btn_refuse;
    ImageView image_view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.activity_approve_detail, container, false);
        tx_busi_name = (TextView)rootView.findViewById(R.id.approve_detail_business);
        tx_comp_name = (TextView)rootView.findViewById(R.id.approve_detail_company);
        tx_termi_date = (TextView)rootView.findViewById(R.id.approve_detail_goalDate);
        tx_goal = (TextView)rootView.findViewById(R.id.approve_detail_mileage);
        tx_contents = (TextView)rootView.findViewById(R.id.approve_detail_contents);
        btn_approve = (Button)rootView.findViewById(R.id.approve);
        btn_refuse = (Button)rootView.findViewById(R.id.refuse);
        image_view = (ImageView)rootView.findViewById(R.id.approve_detail_img);
        is_approve = true;
        storage = FirebaseStorage.getInstance();

        Bundle bundle = getArguments();
        if(bundle != null){
            index = bundle.getInt("index");
            business = NavActivity.notApprove_list.get(index);

            tx_busi_name.setText(business.getBusinessName());
            tx_comp_name.setText(business.getCompanyName());
            tx_termi_date.setText(business.getGoalDate());
            tx_goal.setText(business.getBusinessGoal() + "");
            tx_contents.setText(business.getBusinessContents());

        }else{
        }


        StorageReference sReference = storage.getReference().child("businessIMG/").child(business.getBusinessIMG());
        sReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(getContext()).load(uri.toString()).into(image_view);
            }
        });


        btn_approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                business = NavActivity.notApprove_list.get(index);

                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("사업을 승인하시겠습니까?")
                        .setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        business.setState(1);
                        is_approve = true;
                        db_commit();
                        //NavActivity.changeFragment(NavActivity.FRAGMENT_APPROVE);
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                AlertDialog alertDialog = alert.create();
                alertDialog.show();
            }
        });

        btn_refuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                   business = NavActivity.notApprove_list.get(index);

                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setMessage("사업을 거절하시겠습니까?")
                            .setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            is_approve =false;
                            business.setState(1);
                            db_commit();
                            //NavActivity.changeFragment(NavActivity.FRAGMENT_APPROVE);
                        }
                    }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    });
                    AlertDialog alertDialog = alert.create();
                    alertDialog.show();
                }
        });

        return rootView;
    }

    private void db_commit(){
        // 사업승인 후 DB 업데이트

        NavActivity.m_reference.child("CompanyMembers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 기업의 사업자 아이디(business name) 탐색
                int t=0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CompanyMember cm = snapshot.getValue(CompanyMember.class);
                    if(cm.getBusinessList()!=null) {
                        if (is_approve) {
                            for (int i = 0; i < cm.getBusinessList().size(); i++) {
                                if (cm.getBusinessList().get(i).getBusinessID().equals(business.getBusinessID())) {
                                    company_name = cm.getCor_num();
                                    //사업 상태 변경
                                    NavActivity.m_reference.child("CompanyMembers")
                                            .child(company_name)
                                            .child("businessList")
                                            .child(i + "")
                                            .setValue(business);

                                }
                            }
                        } else {
                            for (int i = 0; i < cm.getBusinessList().size(); i++) {
                                if (cm.getBusinessList().get(i).getBusinessID().equals(business.getBusinessID())) {
                                    company_name = cm.getCor_num();
                                    // 해당 사업자의 리스트 업데이트(사업삭제)
                                    deleteBusiness(cm, business.getBusinessID());
                                }
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

    void deleteBusiness(CompanyMember company, String busiID){

        CompanyMember updateCompany = company;
        for(int i=0; i<company.getBusinessList().size() ;i++)
            // 사업 삭제 후 DB 반영
            if(updateCompany.getBusinessList().get(i).getBusinessID().equals(busiID)){
                updateCompany.getBusinessList().remove(i);
                NavActivity.m_reference
                        .child("CompanyMembers")
                        .child(updateCompany.getCor_num())
                        .setValue(updateCompany);
            }

    }
}
