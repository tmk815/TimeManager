package com.example.tomoki.timemanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper{

    static final String DB_NAME="timemanager.db";
    static final int DB_VERSION=1;
    static final String CREATE_TABLE="create table timedb ( _id integer primary key autoincrement, date text not null, starttime text not null, endtime text not null, breaktime text not null );";
    static final String DROP_TABLE="drop table timedb;";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        Log.d("hogehoge","DBを作成しました。");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }
}
