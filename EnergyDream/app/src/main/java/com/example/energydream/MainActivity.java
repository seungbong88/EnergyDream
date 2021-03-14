package com.example.energydream;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.energydream.CustomDialog.MileageDialog;
import com.example.energydream.Model.StandyPower;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends Fragment {


    ImageButton btn_standy_power;
    ImageButton btn_concent_on;
    ViewGroup root_view;
    TextView tx_check;
    TextView tx_venture;
    public static TextView tx_watt;
    public static TextView tx_save;
    Context context;
    private boolean standy_check; //대기전력 발생 여부
    private DatabaseReference main_reference;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle tx_savedInstanceState) {
        if(NavActivity.login_user!=null) root_view = (ViewGroup)inflater.inflate(R.layout.activity_main, container, false);
        else if(NavActivity.login_comp_user!=null) root_view = (ViewGroup)inflater.inflate(R.layout.activity_main2, container, false);
        btn_standy_power = (ImageButton)root_view.findViewById(R.id.btn_checkisExist);
        btn_concent_on = (ImageButton)root_view.findViewById(R.id.btn_elecOn);
        tx_check = root_view.findViewById(R.id.checkText); //대기전력 발생/안전 텍스트
        tx_venture = root_view.findViewById(R.id.venture_text);
        context = getContext();

        if(NavActivity.login_comp_user!=null) tx_venture.setText(NavActivity.login_comp_user.getCor_name() + "님\n기업회원으로 로그인 하셨습니다.");
        else{

        //tx_watt, 모은 전력량 표시
        tx_watt = root_view.findViewById(R.id.collect_watt);
        tx_save = root_view.findViewById(R.id.save_power);

        main_reference = FirebaseDatabase.getInstance().getReference();
        checkStandyPower_isExist();
        checkConcentUsage();

        if(NavActivity.login_user!=null){
            tx_watt.setText(NavActivity.login_user.getMileage() + "");
            tx_save.setText(String.format("%.2f",NavActivity.login_user.getPower())+"W/H");

        }

        standy_check = false;
        Log.d("띠용 시작","ㅇ");

//        //대기전력 차단 버튼
        btn_standy_power.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startStandyBtnAction();
            }
        });

        //콘센트 On 버튼
        btn_concent_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startConcentBtnAction();
            }
        });

        }
        return root_view;
    }

    public void startConcentBtnAction(){

        main_reference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int op = dataSnapshot.child("operation").getValue(Integer.class);
                boolean usage =dataSnapshot.child("usage").getValue(Boolean.class);
                StandyPower standyPower = dataSnapshot.child("StandyPower").getValue(StandyPower.class);

                if( standyPower != null) {
                    //사용중이 아닐때만 킨다.
                    if (usage == false) {
                        boolean isCalc = standyPower.isCalc();

                        if (op == 0) {
                            //계산함수 돌리기
                            if (isCalc) {
                                Log.d("계산할꿰!", "뀨");
                                energySaving();
                            }

                            main_reference.child("operation").setValue(1);

                        } else {
                            Toast.makeText(getContext(), "다른 작업을 처리하고 있습니다.", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        //사용중이 아닐 때

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void startStandyBtnAction(){

        main_reference.child("operation").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int op = dataSnapshot.getValue(Integer.class);

                if(standy_check==true){

                    if(op == 0){
                        //대기전력 지금 차단하러 가겠습니다!!!!!!! 가즈아!!!!!!!!1
                        Intent gointent = new Intent(getContext(),ArActionActivity.class);
                        startActivityForResult(gointent,3000);

                    }else if(op == 2){
                        Toast.makeText(getContext(), "작업을 처리하고 있습니다.", Toast.LENGTH_LONG).show();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void checkConcentUsage(){
        main_reference.child("usage").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean usage = dataSnapshot.getValue(boolean.class);

                if(usage){
                    ConcentBtnNotActive concentBtnNotActive = new ConcentBtnNotActive();
                    concentBtnNotActive.start();
                }else{
                    ConcentBtnActive concentBtnActive = new ConcentBtnActive();
                    concentBtnActive.start();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //은솔은솔 -> 대기전력 차단 버튼 활성화/비활성화
    public void checkStandyPower_isExist(){

        main_reference.child("StandyPower").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                StandyPower check_standyPower = dataSnapshot.getValue(StandyPower.class);

                if(check_standyPower!=null) {
                    //대기전력 발생여부
                    boolean check_IsStandyPower = check_standyPower.isExist();

                    //True이면 버튼 울고있는모양
                    if(check_IsStandyPower){
                        //UI 활성화 -> 노란색 콘센트

                        Log.d(".....","띠용1");
                        standy_check = true;
                        StandyBtnActive standyBtnActive = new StandyBtnActive();
                        standyBtnActive.start();


                    }else {
                        //False면 하얀색 콘센트
                        Log.d(".....","띠용2");

                        standy_check = false;
                        ChangeOP(); //operation 값 변경
                        StandyBtnNotActive standyBtnNotActive = new StandyBtnNotActive();
                        standyBtnNotActive.start();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void energySaving(){
        //파이어베이스 값 셋팅
        //isCalc = true시 총 와트를 계산한다.
        NavActivity.m_reference.child("StandyPower").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                StandyPower standyPower = dataSnapshot.getValue(StandyPower.class);

                if(standyPower!=null) {

                    standyPower.setEnd_time();
                    double tx_savedPower = standyPower.calcSavePower(standyPower.getUnit_power());
                    tx_savedPower = Math.round((tx_savedPower*1000)/1000.0);
                    int ceil_tx_savePower = (int)Math.ceil(tx_savedPower);
                    int length = 0;

                    if(ceil_tx_savePower != 0)
                        length = (int)(Math.log10(ceil_tx_savePower)+1);
                    else
                        length = 0;


                    String res = ceil_tx_savePower + "자릿수: " + length;

                    Log.d("세이브 : " ,res);
                    Log.d("세이브 에너지",Double.toString(tx_savedPower));
                    int mileage = length * 5;
                    Log.d("세이브마일리지:", Integer.toString(mileage));


                    if(NavActivity.login_user != null) {
                        Log.d("세이브에너지 체크","1");
                        NavActivity.login_user.savePower(tx_savedPower);
                        NavActivity.login_user.addMileage(mileage);


                        Log.d("세이브에너지 체크","2");
                        NavActivity.m_reference.child("Members").child(NavActivity.login_user.getName())
                                .setValue(NavActivity.login_user);
                        Log.d("세이브에너지 체크","3");
                        MileageDialog mileageDialog = new MileageDialog(context, tx_savedPower);
                        mileageDialog.show();

                    }else{
                    }
                    //Standy Power 초기화
                    NavActivity.m_reference.child("StandyPower").child("calc").setValue(false);
                    NavActivity.m_reference.child("StandyPower").child("start_time").setValue(0);
                    NavActivity.m_reference.child("StandyPower").child("end_time").setValue(0);
                    NavActivity.m_reference.child("StandyPower").child("unit_power").setValue(0);
                    NavActivity.m_reference.child("StandyPower").child("tx_save_power").setValue(0);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    public void ChangeOP(){

        main_reference.child("operation").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int op = dataSnapshot.getValue(Integer.class);
                //차단이벤트 완료 됬으니 연산값 변경
                if(op == 2)
                    main_reference.child("operation").setValue(0);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //대기전력차단버튼 Active
    private class StandyBtnActive extends Thread{
        @Override
        public void run(){
            if(getActivity()!=null) {
                btn_standy_power.setClickable(true);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btn_standy_power.setImageResource(R.drawable.standypowerexist);
                        tx_check.setText("대기전력 발생");
                        tx_check.setTextColor(Color.rgb(253,213,99));
                    }
                });
            }
        }
    }

    //대기전력차단버튼 비활성화
    private class StandyBtnNotActive extends Thread{
        @Override
        public void run() {
            if(getActivity()!=null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btn_standy_power.setImageResource(R.drawable.standypoweroff);
                        btn_standy_power.setClickable(false);

                        tx_check.setText("대기전력 안심");
                        tx_check.setTextColor(Color.parseColor("#70bdbdbd"));
                    }
                });
            }
        }
    }

    //콘센트 ON 버튼 활성화
    private class ConcentBtnActive extends Thread{
        @Override
        public void run() {

            if(getActivity()!=null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btn_concent_on.setImageResource(R.drawable.on);
                    }
                });
            }
        }
    }
    //콘센트ON 버튼 비활성화
    private class ConcentBtnNotActive extends Thread{
        @Override
        public void run() {

            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btn_concent_on.setImageResource(R.drawable.off);
                    }
                });
            }
        }
    }

}