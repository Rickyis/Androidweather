package com.example.weatherapplication.util;

import android.text.TextUtils;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.weatherapplication.db.City;
import com.example.weatherapplication.db.County;
import com.example.weatherapplication.db.Province;
import com.example.weatherapplication.gson.Weather;

public class Utility {
    //解析和处理服务器返回的省级数据
    public static boolean handleProvinceResponse(String response){
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray countryAll = jsonObject.getJSONArray("districts");
                for (int i = 0; i < countryAll.length(); i++) {
                    JSONObject countryLeve0 = countryAll.getJSONObject(i);
                    //插入省
                    JSONArray provinceAll = countryLeve0.getJSONArray("districts");
                    for (int j = 0; j < provinceAll.length(); j++) {
                        JSONObject province1 = provinceAll.getJSONObject(j);
                        String adcode1 = province1.getString("adcode");
                        String name1 = province1.getString("name");
                        Province provinceN = new Province();
                        provinceN.setProvinceCode(adcode1);
                        provinceN.setProvinceName(name1);
                        //存储到数据库中
                        provinceN.save();
                    }
                    return true;
                }
            }
            catch(JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }
    //解析和处理服务器返回的市级数据
    public static boolean handleCityResponse(String response, String provinceCode){
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray provinceAll = jsonObject.getJSONArray("districts");
                for (int i = 0; i < provinceAll.length(); i++) {
                    JSONObject province1 = provinceAll.getJSONObject(i);
                    //插入市
                    JSONArray cityAll = province1.getJSONArray("districts");
                    for (int j = 0; j < cityAll.length(); j++) {
                        JSONObject city2 = cityAll.getJSONObject(j);
                        String adcode2 = city2.getString("adcode");
                        String name2 = city2.getString("name");
                        City cityN = new City();
                        cityN.setCityCode(adcode2);
                        cityN.setCityName(name2);
                        cityN.setProvinceCode(provinceCode);
                        cityN.save();
                    }
                    return true;
                }
            }
            catch(JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }
    //解析和处理服务器返回的县级数据
    public static boolean handleCountyResponse(String response, String cityCode){
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray cityAll = jsonObject.getJSONArray("districts");
                for (int i = 0; i < cityAll.length(); i++) {
                    JSONObject city2 = cityAll.getJSONObject(i);
                    //插入市
                    JSONArray countyAll = city2.getJSONArray("districts");
                    for (int j = 0; j < countyAll.length(); j++) {
                        JSONObject county3 = countyAll.getJSONObject(j);
                        String adcode3 = county3.getString("adcode");
                        String name3 = county3.getString("name");
                        County countyN = new County();
                        countyN.setCountyCode(adcode3);
                        countyN.setCountyName(name3);
                        countyN.setCityCode(cityCode);
                        countyN.save();
                    }
                    return true;
                }
            }
            catch(JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }
    //将返回的JSON数据解析成weather实体类
    public static Weather handleWeatherResponse(String response) {
        try {
            //将天气数据中的主体内容解析出来
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("lives");
            for(int i=0; i<jsonArray.length(); i++){
                JSONObject x = jsonArray.getJSONObject(i);
                String weatherContent = x.toString();
                //将JSON数据转换成weather对象
                return new Gson().fromJson(weatherContent, Weather.class);
            } }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}