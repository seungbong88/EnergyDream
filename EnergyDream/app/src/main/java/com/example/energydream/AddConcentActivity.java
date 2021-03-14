package com.example.energydream;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.energydream.Model.StandyPower;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

/*
     QR코드를 인식해서 콘센트 고유 번호만 저장
*/
public class AddConcentActivity extends Fragment {

    ImageButton btn_add;
    TextView txt;
    IntentIntegrator scanner;


    public AddConcentActivity(){ }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_add_concent, container, false);

        btn_add = (ImageButton)view.findViewById(R.id.btn_add);
        txt = view.findViewById(R.id.text);
        scanner = new IntentIntegrator(getActivity());
       // if(NavActivity.m_reference.child("standy_power")!=null)
         //   txt.setText("콘센트가 이미 등록되어있습니다.");
       // else{
            txt.setText("콘센트를 등록해주세요.");
        //}
        btn_add.setOnClickListener(addConcent); //콘센트 추가 버튼
        return view;
    }


    //은솔 수정
    View.OnClickListener addConcent = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            getstandy_power();
        }
    };
    //뿌뿌

    private void getstandy_power(){
        NavActivity.m_reference.child("standy_power").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                NavActivity.standy_power = dataSnapshot.getValue(StandyPower.class);

                // DB에 존재하지 않으면 QR코드 인식기 시행
                if(NavActivity.standy_power == null){
                    scanner.forSupportFragment(NavActivity.manager.findFragmentById(R.id.content_fragment_layout))
                            .setOrientationLocked(false)
                            .setPrompt("바코드를 콘센트의 큐알코드에 비춰주세요")
                            .initiateScan();
                }
                else{
                    txt.setText("콘센트가 이미 등록되어있습니다.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //콘센트 등록
    private void addstandy_power(String res){
        NavActivity.m_reference.child("standy_power").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                NavActivity.standy_power = dataSnapshot.getValue(StandyPower.class);

                // DB에 standy_power 객체가 존재하지 않으면 새로 객체를 만들어 저장한다.
                if(NavActivity.standy_power == null){
                    NavActivity.standy_power = new StandyPower();
                    NavActivity.standy_power.setId(res);
                    NavActivity.m_reference.child("standy_power").setValue(NavActivity.standy_power);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    
    //Getting the scan results
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(getContext(), "취소!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "스캔완료!", Toast.LENGTH_SHORT).show();
                try {
                    JSONObject obj = new JSONObject(result.getContents());

                    if(obj != null) {
                        String res = null;
                        
                        try {
                            res = obj.getString("concentID");
                        }catch (JSONException e){
                            e.printStackTrace();
                        }

                        //파이어베이스 넣는 작업 시작
                        if (res != null) {
                            addstandy_power(res);
                            NavActivity.nav_add_concent.setVisible(false);
                            NavActivity.changeFragment(NavActivity.FRAGMENT_MAIN);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), result.getContents(), Toast.LENGTH_LONG).show();
                }
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
