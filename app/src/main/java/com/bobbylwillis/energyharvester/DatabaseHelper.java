package com.bobbylwillis.energyharvester;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Bobby on 1/18/2016.
 * Activity designed to build database for energy harvester app
 */
public class DatabaseHelper extends SQLiteOpenHelper{
    public static final String databaseName = "Harvester.db";
    public static final String tableName1  =  "total_table";
    public static final String tableName2  =  "piezo_table";
    public static final String tableName3  =  "solar_table";
    public static final String tableName4  =  "thermal_table";

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
        db.execSQL("CREATE TABLE " + tableName1 + " (column1 INTEGER PRIMARY KEY AUTOINCREMENT, column2 DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME')), POWER INTEGER, VOLTAGE INTEGER, CURRENT INTEGER) ");
        db.execSQL("CREATE TABLE " + tableName2 + " (column1 INTEGER PRIMARY KEY AUTOINCREMENT, column2 DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME')), POWER INTEGER, VOLTAGE INTEGER, CURRENT INTEGER) ");
        db.execSQL("CREATE TABLE " + tableName3 + " (column1 INTEGER PRIMARY KEY AUTOINCREMENT, column2 DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME')), POWER INTEGER, VOLTAGE INTEGER, CURRENT INTEGER) ");
        db.execSQL("CREATE TABLE " + tableName4 + " (column1 INTEGER PRIMARY KEY AUTOINCREMENT, column2 DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME')), POWER INTEGER, VOLTAGE INTEGER, CURRENT INTEGER) ");

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

        db.execSQL("DROP TABLE IF EXISTS " + tableName1);
        db.execSQL("DROP TABLE IF EXISTS " + tableName2);
        db.execSQL("DROP TABLE IF EXISTS " + tableName3);
        db.execSQL("DROP TABLE IF EXISTS " + tableName4);
        onCreate(db);
    }

    public boolean insertData(String tableType,String power, String voltage, String current){
        long isInserted;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(column3, power);
        contentValues.put(column4, voltage);
        contentValues.put(column5, current);

        boolean dataResult = false;
      if(tableType.equals("total")) {
          isInserted = db.insert(tableName1, null, contentValues);
          if (isInserted == -1) {
              dataResult = false;
          } else {
              dataResult = true;
          }
      }
        if(tableType.equals("piezo")) {
            isInserted = db.insert(tableName2, null, contentValues);
            if (isInserted == -1) {
                dataResult = false;
            } else {
                dataResult =true;
            }
        }
        if(tableType.equals("solar")) {
            isInserted = db.insert(tableName3, null, contentValues);
            if (isInserted == -1) {
                dataResult = false;
            } else {
                dataResult =true;
            }
        }
        if(tableType.equals("thermal")) {
            isInserted = db.insert(tableName4, null, contentValues);
            if (isInserted == -1) {
                dataResult =false;
            } else {
                dataResult =true;
            }
        }


        return dataResult;
    }
    public void clearTable(String Table)   {
        SQLiteDatabase db = this.getWritableDatabase();
        String whichTable ="";
        ContentValues contentValues = new ContentValues();
        contentValues.put(column3, 0);
        contentValues.put(column4, 0);
        contentValues.put(column5, 0);

        switch (Table){
            case "total": whichTable =tableName1; db.insert(tableName1, null, contentValues);break;
            case "solar":  whichTable =tableName2;db.insert(tableName2, null, contentValues); break;
            case "piezo":  whichTable =tableName3; db.insert(tableName3, null, contentValues);break;
            case "thermal":whichTable =tableName4; db.insert(tableName4, null, contentValues);break;
            default:break;
        }
        db.execSQL("delete from "+ whichTable);

    }
    //set up data
    public Cursor getTotalData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor results1 = db.rawQuery("select * from " + tableName1, null);
        return results1;
    }
    public Cursor getPiezoData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor results2 = db.rawQuery("select * from " + tableName2, null);
        return results2;
    }
    public Cursor getSolarData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor results3 = db.rawQuery("select * from " + tableName3, null);
        return results3;
    }
    public Cursor getThermalData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor results4 = db.rawQuery("select * from " + tableName4, null);
        return results4;

    }
}
