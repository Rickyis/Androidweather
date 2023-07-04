package com.example.weatherapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import com.bumptech.glide.Glide;
import com.example.weatherapplication.gson.Weather;
import com.example.weatherapplication.Service.AutoUpdateService;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import com.example.weatherapplication.util.HttpUtil;
import com.example.weatherapplication.util.Utility;

import static com.example.weatherapplication.MyDBhelper.DB_NAME;
import static com.example.weatherapplication.MyDBhelper.TABLE_NAME;

public class WeatherActivity extends AppCompatActivity {

    public DrawerLayout drawerLayout;
    private Button navButton;
    private Button concern;
    private Button concealConcern;
    private Button goBack;
    private Button refresh;
    public SwipeRefreshLayout swipeRefresh;//下拉刷新控件
    private ScrollView weatherLayout;
    private ImageView bingPicImg;
    private TextView provinceText;//省区
    private TextView cityText;//市区
    private TextView weatherText;//天气
    private TextView temperatureText;//温度
    private TextView humidityText;//湿度
    private TextView reportTimeText;//时间
    String countyCode;
    String countyName;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //5.0以上的系统
        if (Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();//获取DecorView实例控件
            //活动布局显示在状态栏上
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);//状态栏透明
        }
        setContentView(R.layout.activity_weather);
        //控件初始化
        weatherLayout = findViewById(R.id.weather_layout);
        bingPicImg = findViewById(R.id.bing_pic_img);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        provinceText = findViewById(R.id.province_text);
        cityText = findViewById(R.id.city_text);
        weatherText = findViewById(R.id.weather_text);
        temperatureText = findViewById(R.id.temperature_text);
        humidityText = findViewById(R.id.humidity_text);
        reportTimeText = findViewById(R.id.reporttime_text);
        drawerLayout = findViewById(R.id.drawer_layout);
        navButton = findViewById(R.id.nav_button);
        concern = findViewById(R.id.concern);
        concealConcern = findViewById(R.id.concealConcern);
        goBack = findViewById(R.id.goBack);
        refresh = findViewById(R.id.refresh);

        //先从SharedPreferences文件中读取天气数据
        SharedPreferences prefs = getSharedPreferences(String.valueOf(this),MODE_PRIVATE);
        String adcodeString = prefs.getString("weather",null);

        if (adcodeString != null) {
            //有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(adcodeString);
            countyCode = weather.adcodeName;
            countyName = weather.cityName;
            showWeatherInfo(weather);
        } else {
            //无缓存时去服务器查找天气
            countyCode = getIntent().getStringExtra("adcode");
            //countyName = getIntent().getStringExtra("city");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(countyCode);
        }
        //下拉刷新的监听器
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){//下拉进度条监听器
            @Override
            public void onRefresh() {
                requestWeather(countyCode);//回调方法，请求天气信息
            }
        });
        //左上角菜单按钮的事件监听
        navButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //打开滑动菜单
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        //关注按钮的事件监听器
        concern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDBhelper dbHelper = new MyDBhelper(WeatherActivity.this, DB_NAME, null, 1);
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put("city_code", countyCode);
                values.put("city_name", countyName);
                db.insert(TABLE_NAME, null, values);
                Toast.makeText(WeatherActivity.this, "关注成功！", Toast.LENGTH_LONG).show();
            }
        });
        //取消关注按钮的事件监听器
        concealConcern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDBhelper dbHelper = new MyDBhelper(WeatherActivity.this, DB_NAME, null, 1);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.delete(TABLE_NAME,"city_code=?",new String[]{String.valueOf(countyCode)});
                Toast.makeText(WeatherActivity.this, "取消关注成功！", Toast.LENGTH_LONG).show();
            }
        });
        goBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestWeather(countyCode);
            }
        });
        //先从SharedPreferences中读取缓存的背景图片
        String bingPic = prefs.getString("bing_pic",null);
        //有缓存，使用Glide加载图片
        if (bingPic != null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }else {
        }

    }
    //根据城市ID请求城市天气信息
    public void requestWeather(final String adCode) {
        //接口地址
        String weatherUrl = "https://restapi.amap.com/v3/weather/weatherInfo?city=" + adCode + "&key=d33b39e0bbac1d10f57bcdddd44e4e2b";
        //向该地址发出请求，服务器会将相应城市的天气信息以JSON格式返回
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //返回结果处理
                final String responseText = response.body().string();
                //将返回的JSON数据转换成weather对象
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //如果返回不为空，将返回的数据缓存到SharedPreferences中
                        if (weather != null ) {
                            countyCode = adCode;
                            countyName=weather.cityName;
                            SharedPreferences.Editor editor = getSharedPreferences(String.valueOf(this),MODE_PRIVATE).edit();
                            editor.putString("weather", responseText);//放入数据
                            editor.apply();
                            //内容显示
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败,城市ID不存在，请重新输入！", Toast.LENGTH_SHORT).show();
                        }
                        //刷新事件结束，并隐藏刷新进度条
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
//        loadBingPic();
    }

    //将天气信息显示到相应控件上
    private void showWeatherInfo(Weather weather) {
        String provinceName = weather.provinceName;
        String cityName = weather.cityName;
        String weatherName = weather.weatherName;
        String temperatureName = weather.temperatureName;
        String humidityName = weather.humidityName;
        String reportTime = weather.reportTimeName;
        provinceText.setText(provinceName);
        cityText.setText(cityName);
        weatherText.setText("天气:" + weatherName);
        temperatureText.setText("温度:" + temperatureName + "℃");
        humidityText.setText("湿度:" + humidityName + "%");
        reportTimeText.setText(reportTime);
        weatherLayout.setVisibility(View.VISIBLE);
        //后台8小时更新天气
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }
}