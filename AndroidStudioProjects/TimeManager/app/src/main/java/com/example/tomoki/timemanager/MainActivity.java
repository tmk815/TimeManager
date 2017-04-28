package com.example.tomoki.timemanager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener {

    private TextView dateText;
    private TextView startTime,endTime;
    private int text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dateText=(TextView)findViewById(R.id.dateText);
        startTime=(TextView)findViewById(R.id.startTimeText);
        endTime=(TextView)findViewById(R.id.endTimeText);
    }

    //取得した年月日をTextViewに表示
    @Override
    public void onDateSet(DatePicker view,int year,int monthOfYear,int dayOfMonth){
        dateText.setText(String.valueOf(year)+"/"+ String.valueOf(monthOfYear+1)+"/"+String.valueOf(dayOfMonth));
    }

    public void showDatePickerDialog(View v){
        DialogFragment newFragment=new DatePick();
        newFragment.show(getSupportFragmentManager(),"datePicker");
    }

    //取得した時刻をTextViewに表示
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if(text==0) {
            startTime.setText(String.valueOf(hourOfDay) + ":" + String.valueOf(minute));
        }else {
            endTime.setText(String.valueOf(hourOfDay) + ":" + String.valueOf(minute));
        }
    }

    public void showStartTimePickerDialog(View v) {
        text=0;
        DialogFragment newFragment = new TimePick();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void showEndTimePickerDialog(View v) {
        text=1;
        DialogFragment newFragment = new TimePick();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }
}
