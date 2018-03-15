package com.Wheather.app.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.renderscript.Sampler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Wheather.app.R;
import com.Wheather.app.modle.City;
import com.Wheather.app.util.HttpCallbackListener;
import com.Wheather.app.util.HttpUtil;
import com.Wheather.app.util.Utility;

/**
 * Created by Administrator on 2017/6/9.
 */

public class WeatherActivity extends Activity {

    private LinearLayout weatherInfoLayout;
    private TextView cityNameText;
    private TextView publishText;
    private TextView weatherDespText;
    private TextView temp1Text;
    private TextView temp2Text;
    private TextView currentDateText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);
        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        weatherDespText = (TextView) findViewById(R.id.weather_desp);
        publishText = (TextView) findViewById(R.id.publish_text);
        cityNameText = (TextView) findViewById(R.id.city_name);
        temp1Text = (TextView) findViewById(R.id.temp1);
        temp2Text = (TextView) findViewById(R.id.temp2);
        currentDateText = (TextView) findViewById(R.id.current_data);
        String countyCode = getIntent().getStringExtra("county_Code");
        if (!TextUtils.isEmpty(countyCode)){
            publishText.setText("同步中...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);

        }else {
            showWeather();
            Toast.makeText(this, "today", Toast.LENGTH_SHORT).show();

        }
    }
    /*
    * 查询天气代号所对应的天气代号*/
    private void queryWeatherCode(String countyCode){
        String address = "Http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
        queryFromServer(address,"countyCode");
    }

    /*
    * 查询天气代号的天气*/

    private void queryWeatherInfo(String weatherCode){
        String address = "Http://weather.com.cn/data/list3/city" + weatherCode + ".html";
        queryFromServer(address,"weatherCode");
    }

    /*
    * 根据传入的地址和类型区服务器查询天气代号或者天气信息*/

    private void queryFromServer(final String address,final String type){
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                if ("countyCode".equals(type)){
                    if (!TextUtils.isEmpty(response)){
                        String[] array = response.split("\\|");
                        if (array != null && array.length == 2){
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                } else if ("weatherCode".equals(type)){
                    Utility.handleWeatherResponse(WeatherActivity.this,response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }

            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败");
                    }
                });

            }
        });
    }


    /*
    * 从sharePreferences文件中读取存储天气信息，并显示到界面上*/

    private void showWeather(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(prefs.getString("city_name",""));
        temp1Text.setText(prefs.getString("temp1",""));
        temp2Text.setText(prefs.getString("temp2",""));
        weatherDespText.setText(prefs.getString("weather_desp",""));
        publishText.setText("今天" + prefs.getString("publish_time", "") + "发布");
        currentDateText.setText(prefs.getString("current_data",""));
        weatherInfoLayout.setVisibility(View.INVISIBLE);
        cityNameText.setText(View.VISIBLE);

    }
}
