package com.example.energydream;

import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.energydream.Model.Business;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import static java.lang.Math.abs;

public class DetailActivity extends Fragment {

    Business detail_business;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    View view;
    TextView tx_detail_title;
    TextView tx_detail_company;
    ImageView img_detail;
    TextView tx_detail_mileage;
    TextView tx_detail_percent;
    ProgressBar progress_detail;
    TextView tx_detail_goalDate;
    TextView tx_detail_goal;
    TextView tx_detail_contents;
    TextView tx_detail_dday;
    Button btn_detail_godonation;

    RelativeLayout navTop;
    int position;
    int before_fragment_id; // DetailFragment로 넘어오기 전의 프래그먼트 id
    Boolean isVenture;
    /* 기부리스트 상세 페이지 */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_detail, container, false);

        Bundle bundle = getArguments();
        if(bundle != null){
            isVenture = bundle.getBoolean("isVenture");
            position = Integer.parseInt(bundle.getString("index"));
            before_fragment_id = Integer.parseInt(bundle.getString("fragment"));

            if(before_fragment_id == NavActivity.FRAGMENT_DONATION_LIST) {
                if (NavActivity.donation_list.size() > 0) {
                    detail_business = NavActivity.donation_list.get(position);
                    isVenture = false;
                }
            }else{
                if (NavActivity.venture_list.size() > 0) {
                    detail_business = NavActivity.venture_list.get(position);
                    isVenture = true;
                }
            }

        }

        init(view);
        setDetail(detail_business);

        return view;
    }
    void init(final View view){
        navTop = view.findViewById(R.id.navTop);
        tx_detail_title = view.findViewById(R.id.detail_title);
        tx_detail_company = view.findViewById(R.id.detail_company);
        img_detail = view.findViewById(R.id.detail_img);
        tx_detail_mileage = view.findViewById(R.id.detail_mileage);
        tx_detail_percent = view.findViewById(R.id.detail_percent);
        progress_detail=view.findViewById(R.id.detail_progress);
        tx_detail_goalDate = view.findViewById(R.id.detail_goalDate);
        tx_detail_goal = view.findViewById(R.id.detail_goal);
        tx_detail_contents = view.findViewById(R.id.detail_contents);
        tx_detail_dday = view.findViewById(R.id.detail_dday);
        btn_detail_godonation = view.findViewById(R.id.detail_godonation);
    }
    void setDetail(Business det_business){
        tx_detail_title.setText(det_business.getBusinessName());
        tx_detail_company.setText(det_business.getCompanyName());
        StorageReference sReference = storage.getReference().child("businessIMG/").child(det_business.getBusinessIMG());
        sReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(getContext()).load(uri.toString()).into(img_detail);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                img_detail.setImageResource(R.drawable.ic_launcher_background);
            }
        });
        int get_dday = RecyclerAdapter_venture.calc_Dday(det_business.getGoalDate());
        if(get_dday < 0) tx_detail_dday.setText("D + " + abs(get_dday));
        else tx_detail_dday.setText("D - " + get_dday);
        tx_detail_mileage.setText(det_business.getMileage()+"");
        int test = det_business.getMileage()*100/det_business.getBusinessGoal();
        tx_detail_percent.setText( test+ " %");
        progress_detail.setProgress(test, true);
        if(test==0) progress_detail.setProgress(0);
        //progress_detail.setProgress(test);
        tx_detail_goalDate.setText(det_business.getGoalDate());
        tx_detail_goal.setText(det_business.getBusinessGoal()+"");

        if(isVenture){
            progress_detail.getProgressDrawable().setColorFilter(0xfff3acb1, PorterDuff.Mode.SRC_IN);
            btn_detail_godonation.setBackground(getResources().getDrawable(R.drawable.design_venture_button));
            btn_detail_godonation.setText("응원하기");
            tx_detail_contents.setText(det_business.getBusinessContents());
        }else{
            btn_detail_godonation.setBackground(getResources().getDrawable(R.drawable.design_main_button));
            btn_detail_godonation.setText("기부하기");
            tx_detail_contents.setText(det_business.getBusinessContents()+"\n\n※위 사업은 중부발전과 함께 진행되고 있습니다.");
        }

        btn_detail_godonation.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String business_id = detail_business.getBusinessID();
                if(NavActivity.login_user == null){
                    /*Dialog로 띄우주고, 확인버튼 누를 시 로그인페이지로 이동*/
                    Toast.makeText(getContext(), "로그인 시 이용할 수 있습니다", Toast.LENGTH_LONG ).show();
                }else {
                    // Bundle에 사업id 저장 후 프래그먼트 전환
                    Bundle bundle = new Bundle();
                    bundle.putString("id", business_id);
                    bundle.putString("index", position + "");   // testing
                    bundle.putBoolean("isVenture", isVenture);
                    NavActivity.getDonationFragment().setArguments(bundle);
                    NavActivity.changeFragment(NavActivity.FRAGMENT_DONATION);
                }
            }
        });
    }
}