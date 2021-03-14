package com.example.energydream.Model;


import android.util.Log;

// 대기전력 객체
public class StandyPower {

    private String id;
    private boolean Exist;        // 대기전력 발생 여부
    private boolean Calc;         // 대기전력 계산 여부
    private long start_time;        // 대기전력 차단 시작 시간 (전기 차단 시간)
    private long end_time;          // 대기전력 차단 종료 시간 (다시 켠 시간)
    private double unit_power;         // 단위시간 당 대기전력량
    private int save_power;         // 절약한 대기전력량



    public StandyPower() {
        id = "-1";
        Exist = false;
        Calc = false;
        start_time = end_time = 0;
        unit_power = save_power = 0;
    }

    public double calcSavePower(double power){

        power = power * 0.001;
        Log.d("1세이브",Double.toString(power));
        Log.d("세이브시간",Long.toString((end_time - start_time)/60000));
        Log.d("세이브 량",Double.toString(((end_time - start_time)/60000) * power));

        // 절약한 전기량 계산
        return ((end_time - start_time)/60000) * power;
    }

    public void setId(String res) { this.id = res; }
    public void setStart_time() {
        this.start_time = System.currentTimeMillis();
    }
    public void setEnd_time() {
        this.end_time =  System.currentTimeMillis();//(long)1545655668830.0;

    }
    public void setUnit_power(int unit_power) {
        this.unit_power = unit_power;
    }
    public void setExist(boolean isExist) {
        this.Exist = isExist;
    }
    public void setCalc(boolean isCalc) {this.Calc = isCalc; }
    public void setSave_power(int save_power) { this.save_power = save_power; }
    // getter

    public boolean isCalc() { return Calc; }
    public boolean isExist() {
        return Exist;
    }
    public long getStart_time() {
        return start_time;
    }
    public long getEnd_time() {
        return end_time;
    }
    public double getUnit_power() {
        return unit_power;
    }
    public int getSave_power() {
        return save_power;
    }
    public String getId() {
        return id;
    }

}