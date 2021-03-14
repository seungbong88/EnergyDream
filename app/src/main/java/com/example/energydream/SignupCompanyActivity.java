package com.example.energydream;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.energydream.Model.CompanyMember;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupCompanyActivity extends AppCompatActivity {


    FirebaseAuth m_auth;
    DatabaseReference m_reference;

    CompanyMember cor_member;
    String cor_num;
    String pw;
    String name;

    EditText edit_corNum;
    EditText edit_pw;
    EditText edit_pw_check;
    EditText edit_name;
    Button btn_signup;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_company);

        edit_corNum = (EditText)findViewById(R.id.join_company_id);
        edit_pw = (EditText)findViewById(R.id.join_company_pw);
        edit_pw_check = (EditText)findViewById(R.id.join_company_pw_check);
        edit_name = (EditText)findViewById(R.id.join_company_name);
        btn_signup = (Button)findViewById(R.id.btn_join_company);

        m_auth = FirebaseAuth.getInstance();
        m_reference = FirebaseDatabase.getInstance().getReference();

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 비어있는 칸이 있다면 가입되지 않음
                if(edit_corNum.getText().length() == 0  || edit_pw.getText().length() == 0
                        || edit_pw_check.getText().length() == 0  || edit_name.getText().length() == 0){
                    Toast.makeText(getApplicationContext(), "정보를 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else if(!edit_pw.getText().toString().equals(edit_pw_check.getText().toString())){
                    Toast.makeText(getApplicationContext(), "비밀번호가 동일하지 않습니다..", Toast.LENGTH_SHORT).show();
                }else {
                    signup();
                }
            }
        });

    }

    private void signup(){
        cor_num = edit_corNum.getText().toString();
        pw = edit_pw.getText().toString();
        name = edit_name.getText().toString();

        String cor_email = cor_num + "@company.com";    // 기업 이메일 : 사업자번호@company.com
        m_auth.createUserWithEmailAndPassword(cor_email, pw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // 성공적으로 회원가입이 되었을 때
                        if (task.isSuccessful()) {
                            //mUser = m_auth.getCurrentUser();
                            cor_member = new CompanyMember(cor_num, name, pw, null, 0);
                            m_reference.child("CompanyMembers").child(cor_num).setValue(cor_member);

                            finish();
                            Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다.", Toast.LENGTH_LONG).show();
                        } else {  // 회원가입에 실패했을 경우
                            Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}
