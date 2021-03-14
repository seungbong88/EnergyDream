package com.example.energydream;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.energydream.Model.Member;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    String[] city;
    Spinner spinner;
    String email;
    String pw;
    String pw_check;
    String name;
    String region;

    EditText edit_email;
    EditText edit_pw;
    EditText edit_pw_check;
    EditText edit_name;
    Button btn_signup;

    FirebaseAuth m_auth;
    DatabaseReference m_reference;

    public SignupActivity(){

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initView();

        m_auth = FirebaseAuth.getInstance();
        m_reference = FirebaseDatabase.getInstance().getReference();

        spinner.setPrompt("시도를 선택하세요");
        spinner.setOnItemSelectedListener(this);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, city);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });

    }

    private void initView(){
        spinner = findViewById(R.id.join_city);
        edit_email = findViewById(R.id.join_email);
        edit_pw = findViewById(R.id.join_pw);
        edit_pw_check = findViewById(R.id.join_pw_check);
        edit_name = findViewById(R.id.join_name);
        btn_signup = findViewById(R.id.btn_join_inJoin);

        city = new String[]{"서울", "부산", "인천","대구", "광주", "대전", "울산", "세종", "경기",
                "강원", "경북", "경남", "충북", "충남", "전북", "전남", "제주"};
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private void signUp(){
        email = edit_email.getText().toString();
        pw = edit_pw.getText().toString();
        pw_check = edit_pw_check.getText().toString();
        name = edit_name.getText().toString();
        region = spinner.getSelectedItem().toString();

        if(!pw.equals(pw_check)){
            Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
        }else {

            m_auth.createUserWithEmailAndPassword(email, pw)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // 성공적으로 회원가입이 되었을 때
                            if (task.isSuccessful()) {
                                //mUser = m_auth.getCurrentUser();
                                Member signupMember = new Member(email, pw, name, region);
                                m_reference.child("Members").child(name).setValue(signupMember);

                                finish();
                                Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다.", Toast.LENGTH_LONG).show();

                            } else {  // 회원가입에 실패했을 경우
                                Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

}