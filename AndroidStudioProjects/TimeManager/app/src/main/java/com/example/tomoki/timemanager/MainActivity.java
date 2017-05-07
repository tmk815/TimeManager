package com.example.tomoki.timemanager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener {

    private TextView dateText;
    private TextView startTime,endTime;
    private NumberPicker breaktime;
    private ListView timelistView;
    private int text;
    static SQLiteDatabase timedb;
    private DatabaseHelper databaseHelper;
    private Cursor cursor=null;
    private SimpleCursorAdapter adapter;
    private Date s_time_date,e_time_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dateText = (TextView) findViewById(R.id.dateText);
        startTime = (TextView) findViewById(R.id.startTimeText);
        endTime = (TextView) findViewById(R.id.endTimeText);
        breaktime = (NumberPicker) findViewById(R.id.breaktime);
        timelistView = (ListView) findViewById(R.id.list);
        breaktime.setMinValue(0);
        breaktime.setMaxValue(150);

        databaseHelper = new DatabaseHelper(getApplicationContext());
        timedb = databaseHelper.getWritableDatabase();
        cursor=timedb.query("timedb",null,null,null,null,null,null);
        cursor.moveToFirst();

        adapter = new SimpleCursorAdapter(this, R.layout.item, cursor, new String[]{
                "date", "starttime"}, new int[]{R.id.listdate, R.id.listtime}, 0);

        timelistView.setAdapter(adapter);
    }

    //DBへの書き込み(暫定)
    public void addData(View v){
        String s_time=dateText.getText().toString()+" "+startTime.getText().toString();
        String e_time=dateText.getText().toString()+" "+endTime.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            s_time_date = sdf.parse(s_time);
            e_time_date=sdf.parse(e_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long dateTimeTo = s_time_date.getTime();
        long dateTimeFrom = e_time_date.getTime();
        long result=(dateTimeFrom-dateTimeTo)/ (1000 * 60);
        Log.d("result",String.valueOf(result));
        timedb = databaseHelper.getWritableDatabase();
        timedb.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("date", dateText.getText().toString());
            values.put("starttime", startTime.getText().toString());
            values.put("endtime",endTime.getText().toString());
            values.put("breaktime",String.valueOf(breaktime.getValue()));
            values.put("result",result);
            timedb.insert("timedb", null, values);
            Log.d("MainActivity", "データを追加しました。");
            Toast.makeText(this,"データを追加しました",Toast.LENGTH_SHORT).show();
            timedb.setTransactionSuccessful();
        }catch (Exception e){
                Log.e("Database", e.getMessage());
        }finally {
            timedb.endTransaction();
            timedb.close();
            timedb = null;
        }
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
