package com.example.energydream;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.energydream.Model.Member;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import me.itangqi.waveloadingview.WaveLoadingView;

/*
    우리집 <-> 모든사람
    기부랭킹
    막대 그래프로 지역별 랭킹
*/
public class StatisticsActivity extends Fragment {

    Map<String, Integer> rank_donation;
    Map<String, Integer> rank_venture;

    ArrayList<BarEntry> entries;    // 데이터값 리스트
    ArrayList<String> labels;       // 지역 이름
    String city[];

    int donation_value[];   // 지역별 donation 총 합
    int venture_value[];    // 지역별 venture 총 합
    int donation[];
    int venture[];
    String donation_city[];
    String venture_city[];

    int rank_d=0;
    int rank_v=0;

    boolean is_load_DB; // DB 로드 여부
    boolean is_venture;// 벤처 / 에너지기부 구분 변수

    BarChart chart;     // 차트 layout
    Button btn_venture_chart;
    Button btn_donation_chart;
    TextView text_rank;

    WaveLoadingView mWaveLoadingView;


    public StatisticsActivity(){}
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_statistics, container, false);
        chart = view.findViewById(R.id.barchart);
        btn_venture_chart = (Button)view.findViewById(R.id.btn_venture_chart);
        btn_donation_chart = (Button)view.findViewById(R.id.btn_donation_chart);
        text_rank = view.findViewById(R.id.text_chart);
        waveSetting(view);
        city = new String[]{"서울", "부산", "인천","대구", "광주", "대전", "울산", "세종", "경기","강원", "경북", "경남", "충북", "충남", "전북", "전남", "제주"};
        donation_value = new int[17];  // 지역별 에너지 기부 통계
        venture_value = new int[17];   // 지역별 소셜벤처기업 응원 통계

        donation = new int[3];
        venture = new int[3];
        donation_city = new String[3];
        venture_city = new String[3];

        rank_donation = new HashMap<>();
        rank_venture = new HashMap<>();
        is_load_DB = false;
        is_venture = true;

        entries = new ArrayList<>();
        labels  = new ArrayList<>();
        // 지역별 기부내역 데이터 가져오기
        getRegionData();

        btn_venture_chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(is_load_DB && !is_venture){
                    is_venture = true;
                    setVentureChart();
                    btn_venture_chart.setTextColor(Color.WHITE);
                    btn_venture_chart.setBackgroundResource(R.drawable.design_statistic_botton_2);
                    btn_donation_chart.setTextColor(Color.BLACK);
                    btn_donation_chart.setBackgroundResource(R.drawable.design_statistic_botton_1);
                }
            }
        });

        btn_donation_chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                if(is_load_DB && is_venture) {
                    is_venture = false;
                    setDonationChart();
                    btn_venture_chart.setTextColor(Color.BLACK);
                    btn_venture_chart.setBackgroundResource(R.drawable.design_statistic_botton_2_1);
                    btn_donation_chart.setTextColor(Color.WHITE);
                    btn_donation_chart.setBackgroundResource(R.drawable.design_statistic_botton_1_1);
                }
            }
        });


        chart.getAxisLeft().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.setDescription("");
        chart.setBackgroundColor(0x00ffffff);
        chart.setVisibleXRange(0f,7f);
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setEnabled(false);
        chart.getXAxis().setEnabled(true);
        chart.getLegend().setEnabled(false);
        chart.getXAxis().setDrawLabels(true);
        chart.setDrawBarShadow(false);
        //don't click chart
        chart.setClickable(false);
        chart.setTouchEnabled(false);
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);
        chart.setScaleXEnabled(false);
        chart.setScaleYEnabled(false);
        chart.setPinchZoom(false);

        return view;
    }
    public void waveSetting(View view){
        mWaveLoadingView = view.findViewById(R.id.waveLoadingView);
        mWaveLoadingView.setShapeType(WaveLoadingView.ShapeType.CIRCLE);
        mWaveLoadingView.setWaveColor(0xfff9d949);
        mWaveLoadingView.setBorderColor(0xfff9d949);
        mWaveLoadingView.setCenterTitleColor(0xffffff);
        mWaveLoadingView.setProgressValue(40);
    }
    private void getRegionData(){
        NavActivity.m_reference.child("Members").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // DB 값 불러오기
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Member member = snapshot.getValue(Member.class);
                    getDataFromMember(member);
                }
                is_load_DB = true;
                // DB 데이터 저장 후 차트 설정하는 함수 호출
                getRanking();
                setVentureChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getDataFromMember(Member member){
        // 지역 index 번호 가져오기
        int region = getRegionNum(member.getRegion());

        if(region == -1){
            Toast.makeText(getContext(), "잘못된 지역값이 들어가 있습니다." ,Toast.LENGTH_LONG).show();
        }else{
            for(int i=0; i<member.getDonationList().size(); i++){
                // 벤처응원/에너지기부 구분해서 통계 값 저장
                if(member.getDonationList().get(i).isIs_venture())
                    venture_value[region] += member.getDonationList().get(i).getDona_mileage();
                else
                    donation_value[region] += member.getDonationList().get(i).getDona_mileage();

            }
        }

    }
    private void setVentureChart(){
        entries.clear();
        labels.clear();
        for(int i=0; i<3; i++) {
            entries.add(new BarEntry(venture[i], i));
            labels.add(venture_city[i]);
        }
        BarDataSet dataSet = new BarDataSet(entries, "지역별 마일리지");
        BarData data = new BarData(labels, dataSet);
        dataSet.setColor(0xff29145b);
        chart.setData(data);
        chart.invalidate();

        text_rank.setText("우리 지역의 벤처응원은 "+rank_v+"등 입니다.");
        //chart.invalidate();
        // chart.setDescription("전국 소셜벤쳐 기부 마일리지 순위");

    }

    private void setDonationChart(){
        entries.clear();
        labels.clear();
        for(int i=0; i<3; i++) {
            entries.add(new BarEntry(donation[i], i));
            labels.add(donation_city[i]);
        }
        BarDataSet dataset = new BarDataSet(entries, "지역별 마일리지");
        BarData data = new BarData(labels, dataset);
        chart.setData(data);
        chart.invalidate();
        //chart.setDescription("전국 기부 마일리지 순위");

        text_rank.setText("우리 지역의 에너지기부는 "+rank_d+"등 입니다.");
        dataset.setColor(0xff29145b);

    }
    private void getRanking(){
        for(int i=0; i<17; i++){
            rank_venture.put(city[i], venture_value[i]);
            rank_donation.put(city[i], donation_value[i]);
        }
        LinkedHashMap<String, Integer> sorted_venture = new LinkedHashMap<>();
        LinkedHashMap<String, Integer> sorted_donation = new LinkedHashMap<>();

        rank_venture.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x-> sorted_venture.put(x.getKey(), x.getValue()));
        rank_donation.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x-> sorted_donation.put(x.getKey(), x.getValue()));


        Collection k_d = sorted_donation.keySet();
        Collection k_v = sorted_venture.keySet();

        Iterator it_d = k_d.iterator();
        Iterator it_v = k_v.iterator();
        Log.v("자료확인_d",  sorted_donation+"");
        Log.v("자료확인_v",  sorted_venture+"");

        for(int i=0; i<17; i++){
            if(it_d.next().equals(NavActivity.login_user.getRegion())) rank_d=i+1;
            if(it_v.next().equals(NavActivity.login_user.getRegion())) rank_v=i+1;
        }

        it_d = k_d.iterator();
        it_v = k_v.iterator();
        for(int i=0; i<3; i++){
            donation_city[i] = ""+it_d.next();
            donation[i]=sorted_donation.get(donation_city[i]);
            venture_city[i] = ""+it_v.next();
            venture[i]=sorted_venture.get(venture_city[i]);
        }

    }

    // 지역 String 값으로 지역 고유 번호(배열 인덱스) 얻어오는 함수
    private int getRegionNum(String str_region){
        if(str_region.equals("서울")) return 0;

        else if(str_region.equals("부산")) return 1;
        else if(str_region.equals("인천")) return 2;
        else if(str_region.equals("대구")) return 3;
        else if(str_region.equals("광주")) return 4;
        else if(str_region.equals("대전")) return 5;
        else if(str_region.equals("울산")) return 6;
        else if(str_region.equals("세종")) return 7;
        else if(str_region.equals("경기")) return 8;
        else if(str_region.equals("강원")) return 9;
        else if(str_region.equals("경북")) return 10;
        else if(str_region.equals("경남")) return 11;
        else if(str_region.equals("충북")) return 12;
        else if(str_region.equals("충남")) return 13;
        else if(str_region.equals("전북")) return 14;
        else if(str_region.equals("전남")) return 15;
        else if(str_region.equals("제주")) return 16;
        else return -1; // 잘못된 지역값이 들어가 있는 경우
    }

}