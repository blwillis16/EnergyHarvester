package com.bobbylwillis.energyharvester;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Bobby on 1/18/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper{
    public static final String databaseName = "Harvester.db";
    public static final String tableName1  =  "total_table";
    public static final String tableName2  =  "piezo_table";
    public static final String tableName4  =  "thermal_table";
    public static final String tableName3  =  "solar_table";

    public static final String column1 = "ID";
    public static final String column2  = "created at";
    public static final String column3 = "POWER";
    public static final String column4 = "VOLTAGE";
    public static final String column5 = "CURRENT";

    public DatabaseHelper(Context context) {
        super(context, databaseName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + tableName1 + " (column1 INTEGER PRIMARY KEY AUTOINCREMENT, column2 DATETIME DEFAULT CURRENT_DATE, column3 INTEGER, column4 INTEGER,column5 INTEGER )");
        db.execSQL("CREATE TABLE " + tableName2 + " (column1 INTEGER PRIMARY KEY AUTOINCREMENT, column2 DATETIME DEFAULT CURRENT_DATE, column3 INTEGER, column4 INTEGER,column5 INTEGER )");
        db.execSQL("CREATE TABLE " + tableName3 + " (column1 INTEGER PRIMARY KEY AUTOINCREMENT, column2 DATETIME DEFAULT CURRENT_DATE, column3 INTEGER, column4 INTEGER,column5 INTEGER )");
        db.execSQL("CREATE TABLE " + tableName4 + " (column1 INTEGER PRIMARY KEY AUTOINCREMENT, column2 DATETIME DEFAULT CURRENT_DATE, column3 INTEGER, column4 INTEGER,column5 INTEGER )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + tableName1);
        db.execSQL("DROP TABLE IF EXISTS" + tableName2);
        db.execSQL("DROP TABLE IF EXISTS" + tableName3);
        db.execSQL("DROP TABLE IF EXISTS" + tableName4);
        onCreate(db);
    }

    public boolean insertData(String power, String voltage, String current){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(column3, power);
        contentValues.put(column4, voltage);
        contentValues.put(column5, current);

        long isInserted = db.insert(tableName4, null, contentValues);
        if(isInserted == -1){
            return false;
        }
        else
            return true;
    }
    //set up data
    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor results = db.rawQuery("select * from "+ tableName4, null);
        return results;
    }
}
