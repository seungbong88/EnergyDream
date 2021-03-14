package com.example.energydream.Model;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

// Serailizagle 구현 : 액티비티 간 객체 전달을 위해서 구현해야 하는 부분
public class Member implements Serializable {

    String email;
    String pw;
    String name;
    String region;

    int total_mileage;  // 전체 마일리지 (현재 마일리지 + 기부 마일리지)
    int mileage;        // 현재 마일리지
    double power;         // 절약전기량
    int money;          // 절약 금액
    ArrayList<Donation_user> donationList;

    public static class Donation_user implements Serializable{
        String company_name;
        String business_name;
        int dona_mileage;
        boolean is_venture;
        String date;

        public Donation_user(){}
        public Donation_user(String comp_name, String busi_name, int mile, boolean isVenture, String date){
            company_name = comp_name;
            business_name = busi_name;
            dona_mileage = mile;
            is_venture = isVenture;
            this.date = date;

        }

        public void add_mileage(int mileage) {this.dona_mileage += mileage;}

        // getter
        public String getBusiness_name(){return business_name;}
        public String getCompany() {return company_name;}
        public int getDona_mileage() {return dona_mileage;}
        public boolean isIs_venture() { return is_venture; }
        public String getDate() {return date;}
        // setter
        public void setCompany_name(String company_name) { this.company_name = company_name; }
        public void setBusiness_name(String business_name) { this.business_name = business_name; }
        public void setDona_mileage(int dona_mileage) {  this.dona_mileage = dona_mileage;}
        public void setIs_venture(boolean is_venture) { this.is_venture = is_venture;}
        public void setDate(String date) {  this.date = date;}


    }

    public Member() {
        if(donationList == null)
            donationList = new ArrayList<>();
    }

    public Member(String email, String pw, String name, String region) {
        this.email = email;
        this.pw = pw;
        this.name = name;
        this.region = region;

        if(donationList == null)
            donationList = new ArrayList<>();
    }

    public void savePower(double elec) {
        this.power += elec;
    }

    public void addMoney(int money){
        this.money += money;
    }

    public void addMileage(int mileage){
        this.mileage += mileage;
        this.total_mileage += mileage;
    }

    // 기부내역 추가
    public void donate(String comp_name, String busi_name, int mileage, boolean isVenture, String date){

        if(donationList == null)
            donationList = new ArrayList<>();

        donationList.add(new Donation_user(comp_name, busi_name, mileage, isVenture, date));
        this.mileage -= mileage;

        Log.v("리스트님 들어가세여", donationList.get(0).getBusiness_name()+"");
    }

    // Getter
    public String getEmail() {
        return email;
    }
    public String getPw() {
        return pw;
    }
    public String getName() {
        return name;
    }
    public String getRegion() {
        return region;
    }
    public int getTotal_mileage() {
        return total_mileage;
    }
    public int getMileage() {
        return mileage;
    }
    public double getPower() {
        return power;
    }
    public int getMoney() {
        return money;
    }
    public ArrayList<Donation_user> getDonationList() {
        return donationList;
    }

    // Setter
    public void setEmail(String email) {this.email = email;}
    public void setPw(String pw) { this.pw = pw; }
    public void setName(String name) { this.name = name; }
    public void setRegion(String region) { this.region = region; }
    public void setTotal_mileage(int total_mileage) { this.total_mileage = total_mileage; }
    public void setMileage(int mileage) { this.mileage = mileage; }
    public void setPower(double power) { this.power = power; }
    public void setMoney(int money) { this.money = money; }
    public void setDonationList(ArrayList<Donation_user> donationList) { this.donationList = donationList;}
}