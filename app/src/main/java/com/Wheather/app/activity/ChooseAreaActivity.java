package com.Wheather.app.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AndroidException;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.Wheather.app.R;
import com.Wheather.app.db.WeatherDB;
import com.Wheather.app.modle.City;
import com.Wheather.app.modle.County;
import com.Wheather.app.modle.Province;
import com.Wheather.app.util.HttpCallbackListener;
import com.Wheather.app.util.HttpUtil;
import com.Wheather.app.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/7.
 */

public class ChooseAreaActivity extends Activity {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;
    private TextView titleView;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private WeatherDB weatherDB;
    private List<String> dataList = new ArrayList<String>();

    /*
    * 省列表*/

    private List<Province> provinceList;

    private List<City> cityList;

    private List<County> countyList;

    /*
    * 选中的省份*/

    private Province selectedProvince;

    private City selectedCity;

    private int currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("city_selected",false)){
            Intent intent = new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        listView = (ListView) findViewById(R.id.list_view);
        titleView = (TextView)findViewById(R.id.title_text);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        weatherDB = WeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) {
                if (currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(index);
                    queryCities();

                }else if (currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(index);
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY){
                    String countyCode = countyList.get(index).getCountyCode();
                    Intent intent = new Intent(ChooseAreaActivity.this,WeatherActivity.class);
                    intent.putExtra("countyCode",countyCode);
                    startActivity(intent);
                    finish();
                }
            }
        });
        queryProvinces();
    }

    private void queryProvinces(){
        provinceList = weatherDB.loadProvince();
        if (provinceList.size() > 0){
            dataList.clear();
            for (Province province : provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleView.setText("中国");
            currentLevel = LEVEL_PROVINCE;

        } else {
            queryFromServer(null,"province");
        }

    }

    private void queryCities(){

        cityList = weatherDB.loadCities(selectedProvince.getId());
        if (cityList.size() > 0){
            dataList.clear();
            for (City city : cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleView.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;

        } else {
            queryFromServer(selectedProvince.getProvinceCode(),"city");
        }

    }

    private void queryCounties(){

        countyList = weatherDB.loadCounties(selectedCity.getId());
        if (countyList.size() > 0){
            dataList.clear();
            for (County county : countyList){
                dataList.add( county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleView.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        }else {
            queryFromServer(selectedCity.getCityCode(),"county");
        }

    }

    private void queryFromServer(final String code,final String type){

        String address;
        if (!TextUtils.isEmpty(code)){
            address = "Http://www.weather.com.cn/data/list3/city" + code + ".xml";
        } else {
            address = "Http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address,new HttpCallbackListener(){

            @Override
            public void onFinish(String response) {

                boolean result = false;
                if ("province".equals(type)){
                    result = Utility.handleProvincesResponse(weatherDB,response);
                } else if ("city".equals(type)){
                    result = Utility.handleCitiesResponse(weatherDB,response,selectedProvince.getId());
                } else if ("county".equals(type)){
                    Utility.handleCountiesResponse(weatherDB,response,selectedCity.getId());
                }
                if (result){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)){
                                queryProvinces();
                            } else  if ("city".equals(type)){
                                queryCities();
                            } else if ("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }

            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });


    }

    private void showProgressDialog(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("加载失败...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();

    }

    private void closeProgressDialog(){
        if (progressDialog !=null){
            progressDialog.dismiss();
        }

    }
    public void onBackPressed(){
        if (currentLevel == LEVEL_COUNTY){
            queryCities();
        } else if (currentLevel == LEVEL_CITY){
            queryProvinces();
        } else {
            finish();
        }
    }
}
