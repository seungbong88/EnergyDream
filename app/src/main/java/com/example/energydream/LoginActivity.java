package com.example.energydream;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.energydream.Model.Business;
import com.example.energydream.Model.CompanyMember;
import com.example.energydream.Model.Member;
import com.example.energydream.Service.MyService;
import com.example.energydream.Service.ServiceConfig;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    FirebaseUser m_user;                 // 로그인 사용자 정보(email-pw)
    Member login_user;                  // 로그인 한 개인 사용자
    CompanyMember login_comp_user;      // 로그인 한 기업 사용자
    ArrayList<Business> business_list;   // 로그인한 기업의 사업 리스트

    FirebaseAuth m_auth;
    DatabaseReference m_reference;
    String input_email, input_pw;
    boolean is_company;

    EditText text_email;
    EditText text_pw;
    Button btn_user;
    Button btn_companyUser;
    Button btn_login;
    TextView tx_signup;
    TextView tx_signup_company;

    ServiceConfig serviceConfig;    // 서비스 활성화 확인


    public LoginActivity(){

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences a =getSharedPreferences("a", MODE_PRIVATE);
        int firstviewShow = a.getInt("First", 0);

        if(firstviewShow == 0){
            Intent intent = new Intent(LoginActivity.this, TutorialActivity.class);
            startActivity(intent);
        }

        // NavActivity로 보낼 객체들
        m_user = FirebaseAuth.getInstance().getCurrentUser();
        business_list = new ArrayList<>();

        text_email = (EditText)findViewById(R.id.insert_id);
        text_pw = (EditText)findViewById(R.id.insert_passwd);
        btn_login = (Button)findViewById(R.id.sign_in);
        tx_signup = (TextView)findViewById(R.id.sign_up);
        tx_signup_company = (TextView)findViewById(R.id.sign_up_company);
        btn_user = (Button)findViewById(R.id.btn_user);
        btn_companyUser = (Button)findViewById(R.id.btn_company_user);

        serviceConfig = new ServiceConfig(this);
        m_reference = FirebaseDatabase.getInstance().getReference();
        m_auth = FirebaseAuth.getInstance();
        is_company = false;

        // 개인로그인 / 기업로그인 스위치 버튼
        btn_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(is_company){
                    is_company = false;
                    text_email.setHint("이메일");
                    text_email.setText("");
                    text_pw.setText("");
                    // 회원가입 텍스트 변경
                    tx_signup.setVisibility(View.VISIBLE);
                    tx_signup_company.setVisibility(View.INVISIBLE);
                    // 개인회원/기업회원 버튼 설정
                    btn_user.setTextColor(Color.WHITE);
                    btn_user.setBackgroundResource(R.drawable.design_login_botton_2);
                    btn_companyUser.setTextColor(Color.BLACK);
                    btn_companyUser.setBackgroundResource(R.drawable.design_login_botton_1);
                }
            }
        });
        btn_companyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!is_company){
                    is_company = true;
                    text_email.setHint("사업자번호");
                    text_email.setText("");
                    text_pw.setText("");
                    tx_signup.setVisibility(View.INVISIBLE);
                    tx_signup_company.setVisibility(View.VISIBLE);
                    btn_companyUser.setTextColor(Color.WHITE);
                    btn_companyUser.setBackgroundResource(R.drawable.design_login_botton_1_1);
                    btn_user.setTextColor(Color.BLACK);
                    btn_user.setBackgroundResource(R.drawable.design_login_botton_2_1);
                }
            }
        });

        // 개인 회원가입으로 이동
        tx_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
            }
        });

        // 기업 회원가입으로 이동
        tx_signup_company.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignupCompanyActivity.class);
                startActivity(intent);
            }
        });

        // 로그인 버튼
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(is_company){
                    login_company();    // 기업 로그인
                }else {
                    login_personal();   // 개인 로그인
                }
            }
        });

    }

    private void login_company(){
        // 기업 로그인 이메일 : 사업자번호@company.com
        input_email = text_email.getText().toString() + "@company.com";
        input_pw = text_pw.getText().toString();

        // 이메일, 비밀번호 중 빈칸이 있는 경우
        if(input_email.length() <= 0 || input_pw.length() <= 0) {
            Toast.makeText(getApplicationContext(), "로그인 정보를 입력해주세요", Toast.LENGTH_LONG).show();
        }// 제대로 입력된 경우
        else{
            m_auth.signInWithEmailAndPassword(input_email, input_pw)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // email-pw 가 일치할 경우 로그인 성공
                                m_user = m_auth.getCurrentUser();

                                // 로그인 유저 객체 생성
                                m_reference.child("CompanyMembers").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                            CompanyMember compMember = snapshot.getValue(CompanyMember.class);

                                            String company_email = compMember.getCor_num() + "@company.com";
                                            if (company_email.equals(m_user.getEmail())) {
                                                login_comp_user = compMember;

                                                addCompBusiList(); // 해당 사업자의 사업리스트 저장

                                            } else {
                                                Log.d("[TAG]", m_user.getEmail()+"");
                                                // Firebase에 데이터에 문제가 생긴 경우
                                                Log.d("[TAG]", "Database Problem occur in LoginActivity");
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            } else {
                                Toast.makeText(getApplicationContext(), "로그인 정보가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    private void login_personal(){

        input_email = text_email.getText().toString();
        input_pw = text_pw.getText().toString();

        // 이메일, 비밀번호 중 빈칸이 있는 경우
        if(input_email.length() == 0 || input_pw.length() == 0) {
            Toast.makeText(getApplicationContext(), "로그인 정보를 입력해주세요", Toast.LENGTH_LONG).show();
        }// 제대로 입력된 경우
        else{
            m_auth.signInWithEmailAndPassword(input_email, input_pw)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // email-pw 가 일치할 경우 로그인 성공
                                m_user = m_auth.getCurrentUser();

                                // 로그인 유저 객체 생성 (수정)
                                m_reference.child("Members").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                            Member member = snapshot.getValue(Member.class);

                                            if (member.getEmail().equals(m_user.getEmail())) {
                                                login_user = member;

                                                //만약 서비스가 돌아가고 있지않은 상태라면
                                                if (!serviceConfig.getServiceState()) {
                                                    //Toast.makeText(getContext(), NavActivity.login_user.getEmail() + "호오오잇 서비스돌려요~.", Toast.LENGTH_LONG).show();
                                                    startService(new Intent(getApplicationContext(), MyService.class));
                                                }

                                                // 로그인 후 메인페이지로 이동
                                                changeNavActivity();

                                            } else {
                                                // Firebase에 데이터에 문제가 생긴 경우
                                                Log.d("[TAG]", "Database Problem occur in LoginActivity");
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(getApplicationContext(), "Database Error", Toast.LENGTH_LONG).show();
                                    }
                                });

                            } else {
                                Toast.makeText(getApplicationContext(), "로그인 정보가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

        }
    }

    // 해당 사업자의 사업리스트 저장
    private void addCompBusiList(){

        // 사업리시트가 없다면 초기화
        if(login_comp_user.getBusinessList() == null)
            login_comp_user.setBusinessList(new ArrayList<Business>());

        business_list = new ArrayList<>();
        String com_user_id = login_comp_user.getCor_num();

        m_reference.child("CompanyMembers").child(com_user_id).child("business_list")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // 데이터 변경 시,
                        // 변경된 데이터를 새롭게 리스트에 저장
                        if(business_list != null) {
                            business_list.clear();
                            ArrayList<Business> cur_busiList = (ArrayList<Business>) dataSnapshot.getValue();
                            if(cur_busiList != null) {
                                for (int i = 0; i < cur_busiList.size(); i++) {
                                    Business cur_busi = dataSnapshot.child(i + "").getValue(Business.class);
                                    business_list.add(cur_busi);
                                }
                            }
                        }

                        // 데이터 저장 후 메인페이지로 이동
                        changeNavActivity();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void changeNavActivity(){
        Intent intent = new Intent(getApplicationContext(), NavActivity.class);

        // NavActivity 로 객체들 전달
        intent.putExtra("Member", login_user);
        intent.putExtra("CompanyMember", login_comp_user);
        intent.putExtra("business_list", business_list);

        startActivity(intent);


        // 초기화
        login_user = null;
        login_comp_user = null;
        input_email = input_pw = "";
        text_pw.setText("");
        text_email.setText("");

    }


}
