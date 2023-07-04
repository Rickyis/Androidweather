package com.example.weatherapplication.db;

import org.litepal.crud.LitePalSupport;

public class Province extends LitePalSupport {

    private String provinceName;//省的名字
    private String  provinceCode;//省的代号

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String  getProvinceCode() { return provinceCode; }

    public void setProvinceCode(String  provinceCode) {
        this.provinceCode = provinceCode;
    }

}