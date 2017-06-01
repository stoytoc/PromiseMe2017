package com.example.hj.testproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static java.sql.DriverManager.println;

/**
 * Created by gudwl on 2017-05-11.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String TAG = "MainActuvuty";
    private static String DATABASE_NAME ="habit";
    private static String TABLE_NAME ="item";
    private static int DATABASE_VERSION =10;
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {//DB가 처음 만들어질때 호출 되고 테이블을 생성하고 초기 레코드를 삽입한다.
        println("creating table [" + TABLE_NAME + "].");
        try {
            String DROP_SQL = "drop table if exists " + TABLE_NAME;
            db.execSQL(DROP_SQL);
        } catch (Exception ex) {
            Log.e(TAG, "Exception in DROP_SQL", ex);
        }
        String CREATE_SQL = "create table " + TABLE_NAME + "("
                + " _id integer PRIMARY KEY autoincrement, "
                + " name text null, "
                + " period text null, "
                + " count text null, "
                + " alarm text null, "
                + " hour  Integer null, "
                + " minute Integer null)";
        try {
            db.execSQL(CREATE_SQL);
            System.out.println("Table is successfully opened!");
        } catch (Exception ex) {
            Log.e(TAG, "Exception in CREATE_SQL", ex);
        }
    }
    public void onOpen(SQLiteDatabase db) {
        println("opened database [" + DATABASE_NAME + "].");
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {//DB 업그레이드 시 호출된다. 기존 테이블 삭제 및 생성하거나 Alter table로 스키마 수정
        db.execSQL("DROP TABLE IF EXISTS habit");
        onCreate(db);
    }
}