package com.example.weatherapplication.Service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;

import com.example.weatherapplication.gson.Weather;
import com.example.weatherapplication.util.HttpUtil;
import com.example.weatherapplication.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();//更新天气信息
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 6 * 60 * 60 * 1000;//6小时的毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 更新天气信息
     */
    private void updateWeather(){
        SharedPreferences prefs = getSharedPreferences(String.valueOf(this),MODE_PRIVATE);
        String weatherString = prefs.getString("weather", null);
        //有缓存直接解析天气数据
        if (weatherString != null){
            Weather weather = Utility.handleWeatherResponse(weatherString);
            String weatherId = weather.adcodeName;
            String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=b38826493b8a477eb8c1334f30de6ae2";
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    Weather weather = Utility.handleWeatherResponse(responseText);
                    if (weather != null /*&& "1".equals(weather.status) && "1".equals(weather.count) && "Ok".equals(weather.info) && "10000".equals(weather.infocode)*/){
                        SharedPreferences.Editor editor = getSharedPreferences(String.valueOf(AutoUpdateService.this),MODE_PRIVATE).edit();
                        editor.putString("weather", responseText);
                        editor.apply();
                    }
                }
            });
        }
    }

    /**
     *更新背景图片
     */


}
