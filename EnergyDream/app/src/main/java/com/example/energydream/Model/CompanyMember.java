package com.example.energydream.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class CompanyMember implements Serializable {

    private String cor_num;
    private String cor_name;
    private String password;
    private int watt;

    // 사업 객체(사업명,z 목표마일리지, 모금마일리지, 사진, 모금기간, 소개글) 리스트
    private ArrayList<Business> businessList;


    public CompanyMember() {

    }

    public CompanyMember(String cor_num, String cor_name, String password, ArrayList<Business> businessList, int watt) {
        this.cor_num = cor_num;
        this.cor_name = cor_name;
        this.password = password;
        this.watt=watt;
        if(businessList == null){
            this.businessList = new ArrayList<>();

        }
        else {
            this.businessList = businessList;
        }
    }


    public void addBusiness(Business newBusiness){
        businessList.add(newBusiness);
    }


    // get fucntion
    public String getCor_num() { return cor_num; }
    public String getCor_name() { return cor_name; }
    public String getPassword() { return password; }
    public int getWatt() { return  watt; }
    public ArrayList<Business> getBusinessList() {
        return businessList;
    }

    // set funcion
    public void setBusinessList(ArrayList<Business> businessList) { this.businessList = businessList;}
    public void setCor_num(String cor_num) {this.cor_num = cor_num; }
    public void setCor_name(String cor_name) { this.cor_name = cor_name; }
    public void setPassword(String password) { this.password = password;}
    public void setWatt(int watt){this.watt = watt;}


    public void sumWatt(int watt){
       this.watt+=watt;
    }

}
