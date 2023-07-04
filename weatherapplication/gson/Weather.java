package com.example.weatherapplication.gson;

import com.google.gson.annotations.SerializedName;

public class Weather {

    @SerializedName("province")//省
    public String provinceName;

    @SerializedName("city")//城市
    public String cityName;

    @SerializedName("adcode")//ID
    public String adcodeName;

    @SerializedName("weather")//天气
    public String weatherName;

    @SerializedName("temperature")//温度
    public String temperatureName;

    @SerializedName("winddirection")//风向
    public String windDirection;

    @SerializedName("windpower")//风力
    public String windPower;

    @SerializedName("humidity")//湿度
    public String humidityName;

    @SerializedName("reporttime")//报告时间
    public String reportTimeName;

}