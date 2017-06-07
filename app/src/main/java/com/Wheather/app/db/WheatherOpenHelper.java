package com.Wheather.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2017/5/26.
 */

public class WheatherOpenHelper extends SQLiteOpenHelper {

    /*
    * Province表建表语句*/
    public static final String CREATE_PROVING = "create table Province("

            + "id integer primary key autoincrement,"
            + "province_name text,"
            + "province_code text)";

    /*
    * City表建表语句*/

    public  static  final String CREATE_CITY = "create table City("

            + "id integer primary key autoincrment,"
            + "city_name text,"
            + "city_code text,"
            + "province_id integer)";

    /*
    * County表建表语句*/

    public static final String CREATE_COUNTY = "create table County("

            +"id integer primary key autoincrement,"
            +"county_name text,"
            +"county_code text,"
            +"city_id integer)";

    public  WheatherOpenHelper(Context context, String name, CursorFactory factory,int version){
        super(context,name,factory,version);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CITY);
        db.execSQL(CREATE_PROVING);
        db.execSQL(CREATE_COUNTY);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
