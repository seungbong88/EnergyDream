package com.example.energydream;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

public class EnrollDonationActivity extends Fragment {

    //Donation 생성자 변수
    boolean is_donation;
    String donation_name;
    int donation_goal;
    String donation_IMG; //image 파일이름 저장
    String donation_contents;
    String donation_goal_date;
    String donation_company;

    Uri filepath; //파일 불러와서 주소 저장
    String filename;
    //Donation를 bus로 쓰게씀다
    EditText text_donation_company;
    EditText text_donation_busname;
    EditText edit_donation_busContents;
    EditText edit_donation_mileage;
    TextView tx_donation_date;
    Button btn_donation_img;
    Button btn_select_date;
    Button btn_donation_enroll;
    ImageView img_donation;


    Calendar calendar_donation;
    private int donation_year;
    private int donation_month;
    private int donation_day;
    static final int Donation_DATE_DIALOG_ID = 0;
    public EnrollDonationActivity(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup Donation_rootView = (ViewGroup)inflater.inflate(R.layout.activity_enroll_donation, container, false);
        Donation_initView(Donation_rootView);

        calendar_donation = Calendar.getInstance();
        donation_year = calendar_donation.get(Calendar.YEAR);
        donation_month = calendar_donation.get(Calendar.MONTH);
        donation_day = calendar_donation.get(Calendar.DAY_OF_MONTH);

        btn_select_date.setOnClickListener(new View.OnClickListener() {
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

                        donation_goal_date = str_year + " 년 " + str_month + " 월 " + str_day + " 일 ";
                        tx_donation_date.setText(donation_goal_date);

                    }
                }, calendar_donation.get(Calendar.YEAR), calendar_donation.get(Calendar.MONTH), calendar_donation.get(Calendar.DATE));
                dialog.show();


            }
        });

        return Donation_rootView;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && resultCode == getActivity().RESULT_OK){
            filepath = data.getData();
            try {
                Bitmap Donation_bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filepath);
                img_donation.setImageBitmap(Donation_bitmap);
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
            NavActivity.s_reference = storage.getReferenceFromUrl("gs://e-tmi-43dd5.appspot.com").child("businessIMG/"+filename);
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

    private void Donation_initView(final View view){
        text_donation_company = view.findViewById(R.id.enroll_donation_com);
        text_donation_busname = view.findViewById(R.id.enroll_donation_name);
        btn_donation_img = view.findViewById(R.id.enroll_donation_img);
        btn_donation_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectGallery();
            }
        });
        edit_donation_busContents = view.findViewById(R.id.enroll_donation_contents);
        edit_donation_mileage = view.findViewById(R.id.enroll_donation_goal_milage);
        tx_donation_date = view.findViewById(R.id.enroll_donation_goal_date);
        btn_select_date = view.findViewById(R.id.enroll_donation_date);
        img_donation = view.findViewById(R.id.enroll_donation_img_value);



        btn_donation_enroll = view.findViewById(R.id.enroll_donation);
        btn_donation_enroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(text_donation_busname.getText().toString().length() == 0
                        || text_donation_company.getText().toString().length()==0
                        || edit_donation_busContents.getText().toString().length() == 0
                        || edit_donation_mileage.getText().toString().length() == 0  || filepath==null
                        || tx_donation_date.getText().toString().length() == 0){
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
        is_donation = false;
        donation_IMG = filename;
        donation_name = text_donation_busname.getText().toString();
        donation_goal = Integer.parseInt(edit_donation_mileage.getText().toString());
        donation_contents = edit_donation_busContents.getText().toString();
        donation_goal_date = tx_donation_date.getText().toString();
        donation_company = text_donation_company.getText().toString();

        Business newBusiness = new Business(is_donation, donation_name, donation_company,
                donation_goal, donation_IMG, donation_contents, donation_goal_date);

        NavActivity.login_comp_user.addBusiness(newBusiness);
        NavActivity.m_reference
                .child("CompanyMembers")
                .child(NavActivity.login_comp_user.getCor_num())
                .setValue(NavActivity.login_comp_user);
    }
}
