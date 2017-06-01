package com.example.tomoki.timemanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper{

    static final String DB_NAME="timemanager.db";
    static final int DB_VERSION=1;
    /*                                                      0                                       1                   2                   3                          4                       5                      6                     7                      8                            9               10                  11              12                  13*/
    static final String CREATE_TABLE="create table timedb ( _id integer primary key autoincrement, year text not null, month text not null,date text not null , starttime text not null, endtime text not null, breaktime text not null, result integer not null, yearmonthdate text not null, place text, startovertime text, endovertime text, overresult integer, remarks text );";
    static final String DROP_TABLE="drop table timedb;";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        Log.d("DBHelper","DBを作成しました。");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }
}
