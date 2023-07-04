package com.example.weatherapplication.db;

import org.litepal.crud.LitePalSupport;

public class County extends LitePalSupport {

    private String countyName;//县的名字
    private String  countyCode;//县的代号
    private String  cityCode;//市的代号


    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String  getCityCode(){
        return cityCode;
    }

    public void setCityCode(String  cityCode){
        this.cityCode = cityCode;
    }

    public void setCountyCode(String  countyCode){
        this.countyCode = countyCode;
    }

    public String   getCountyCode(){
        return countyCode;
    }


}