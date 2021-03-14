package com.example.energydream;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.energydream.Model.Business;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Calendar;

public class EnrollBusinessActivity extends Fragment{
    //Business 생성자 변수
    boolean is_business;
    String business_name;
    int business_goal;
    String business_IMG; //image 파일이름 저장
    String business_contents;
    String goal_date;


    Uri filepath; //파일 불러와서 주소 저장
    String filename;

    //business를 bus로 쓰게씀다
    EditText edit_busName;
    EditText edit_bus_contents;
    EditText edit_mileage;
    TextView text_goal_date;
    Button btn_bus_img;
    Button select_date;
    Button btn_enroll;
    ImageView img_business;


    Calendar calendar;
    private int m_year;
    private int m_month;
    private int m_day;
    static final int DATE_DIALOG_ID = 0;
    public EnrollBusinessActivity(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_enroll_business, container, false);
        initView(view);
        calendar = Calendar.getInstance();
        m_year = calendar.get(Calendar.YEAR);
        m_month = calendar.get(Calendar.MONTH);
        m_day = calendar.get(Calendar.DAY_OF_MONTH);

        select_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(getActivity(),android.R.style.Theme_Holo_Dialog, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                        String str_year = year + "";
                        String str_month = (month+1) + "";
                        if(str_month.length() == 1) str_month = "0" + str_month;
                        String str_day = date + "";
                        if(str_day.length() == 1) str_day = "0" + str_day;

                        goal_date = str_year + " 년 " + str_month + " 월 " + str_day + " 일 ";
                        text_goal_date.setText(goal_date);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
                dialog.show();


            }
        });

        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && resultCode == getActivity().RESULT_OK){
            filepath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filepath);
                img_business.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void selectGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 0);
    }
    private void uploadFile(){
        if(filepath!= null){
            filename = (int)(Math.random()*1000+1)+".png";
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("업로드중");
            progressDialog.show();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            NavActivity.s_reference = storage.getReferenceFromUrl("gs://e-tmi-43dd5.appspot.com").child("business_IMG/"+filename);
            NavActivity.s_reference.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss(); //업로드 진행 Dialog 상자 닫기
                }
            })
                    //실패시
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                        }
                    })
                    //진행중
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests") //이걸 넣어 줘야 아랫줄에 에러가 사라진다. 넌 누구냐?
                                    double progress = (100 * taskSnapshot.getBytesTransferred()) /  taskSnapshot.getTotalByteCount();
                            //dialog에 진행률을 퍼센트로 출력해 준다
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
                        }
                    });
        } else {

        }
    }

    private void initView(final View view){
        edit_busName = view.findViewById(R.id.business_name);
        btn_bus_img = view.findViewById(R.id.business_img);
        btn_bus_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectGallery();
            }
        });
        edit_bus_contents = view.findViewById(R.id.business_contents);
        edit_mileage = view.findViewById(R.id.goal_milage);
        text_goal_date = view.findViewById(R.id.goal_date);
        select_date = view.findViewById(R.id.business_date);
        img_business = view.findViewById(R.id.business_img_value);


        btn_enroll = view.findViewById(R.id.enroll);
        btn_enroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edit_busName.getText().toString().length() == 0
                        || edit_bus_contents.getText().toString().length() == 0
                        || edit_mileage.getText().toString().length() == 0  || filepath==null
                        || text_goal_date.getText().toString().length() == 0){
                    Toast.makeText(getContext(), "정보를 모두 기입해주세요.", Toast.LENGTH_LONG).show();
                } else {
                    uploadFile();
                    enroll();
                    Toast.makeText(getContext(), "등록되었습니다.", Toast.LENGTH_LONG).show();
                    NavActivity.changeFragment(NavActivity.FRAGMENT_MAIN);
                }

            }
        });


    }

    private void enroll(){

        /* 소셜/일반기부 구분하는 switch 만들기 */
        is_business = true;
        business_IMG = filename;
        business_name = edit_busName.getText().toString();
        business_goal = Integer.parseInt(edit_mileage.getText().toString());
        business_contents = edit_bus_contents.getText().toString();
        goal_date = text_goal_date.getText().toString();


        Business newBusiness = new Business(is_business, business_name, NavActivity.login_comp_user.getCor_name(),
                business_goal, business_IMG, business_contents, goal_date);

        NavActivity.login_comp_user.addBusiness(newBusiness);
        NavActivity.m_reference
                .child("CompanyMembers")
                .child(NavActivity.login_comp_user.getCor_num())
                .setValue(NavActivity.login_comp_user);
    }

}