package com.example.tomoki.timemanager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener {

    private TextView dateText;
    private TextView startTime,endTime;
    private int text;
    static SQLiteDatabase timedb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dateText=(TextView)findViewById(R.id.dateText);
        startTime=(TextView)findViewById(R.id.startTimeText);
        endTime=(TextView)findViewById(R.id.endTimeText);

        DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
        timedb = databaseHelper.getWritableDatabase();
    }

    //DBへの書き込み(暫定)
    public void addData(View v){
        timedb.beginTransaction();
        ContentValues values = new ContentValues();
        values.put("time", "data1");
        timedb.insert("timedb", null, values);
        Log.d("MainActivity","データを追加しました。");
        timedb.endTransaction();
    }

    //取得した年月日をTextViewに表示
    @Override
    public void onDateSet(DatePicker view,int year,int monthOfYear,int dayOfMonth){
        //dateText.setText(String.valueOf(year)+"/"+ String.valueOf(monthOfYear+1)+"/"+String.valueOf(dayOfMonth));
        dateText.setText(String.format("%d-%02d-%02d",year,monthOfYear+1,dayOfMonth));
    }

    public void showDatePickerDialog(View v){
        DialogFragment newFragment=new DatePick();
        newFragment.show(getSupportFragmentManager(),"datePicker");
    }

    //取得した時刻をTextViewに表示
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if(text==0) {
            //startTime.setText(String.valueOf(hourOfDay) + ":" + String.valueOf(minute));
            startTime.setText(String.format("%02d:%02d",hourOfDay,minute));
        }else {
            //endTime.setText(String.valueOf(hourOfDay) + ":" + String.valueOf(minute));
            endTime.setText(String.format("%02d:%02d",hourOfDay,minute));
        }
    }

    //開始時刻
    public void showStartTimePickerDialog(View v) {
        text=0;
        DialogFragment newFragment = new TimePick();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    //終了時刻
    public void showEndTimePickerDialog(View v) {
        text=1;
        DialogFragment newFragment = new TimePick();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    //文字列の分離
    public String[] cutString(String text){
        String[] cutText=text.split(":");
        return cutText;
    }

}
