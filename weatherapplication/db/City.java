package com.example.weatherapplication.db;

import org.litepal.crud.LitePalSupport;

public class City extends LitePalSupport{

    private String cityName;//市的名字
    private String cityCode;//市的代号
    private String provinceCode;//省的代号

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getProvinceCode(){
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }
}