package com.Wheather.app.util;

import android.text.TextUtils;

import com.Wheather.app.modle.Province;

/**
 * Created by Administrator on 2017/5/31.
 */

public class Utility {

    public synchronized static boolean handleProvincesResponse(WeatherDB weatherDB, String response) {

        if (!TextUtils.isEmpty(response)){
            String[] allProvince = response.split(",");
            if (allProvince != null &&allProvince.length > 0){
            for (String p :allProvince){
                String[] array = p.split("\\|");
                Province province = new Province();
                province.setProvinceCode(array[0]);
                province.setProvinceName(array[1]);
                weatherDB.save
            }
            }
        }
        return true;
    }
}
