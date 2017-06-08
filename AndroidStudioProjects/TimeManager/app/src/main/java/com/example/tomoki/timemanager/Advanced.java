package com.example.tomoki.timemanager;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

/**
 * Created by tomoki on 2017/05/25.
 */

public class Advanced extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener{
    private int text;
    private TextView startOverTime, endOverTime;
    private EditText remarks;
    private String sotime="",eotime="",remark="";
    private Button submit;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.advanced);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setTitle("オプション");
        Intent intent = getIntent();
        sotime = intent.getStringExtra("sotime");
        eotime = intent.getStringExtra("eotime");
        remark = intent.getStringExtra("remarks");

        startOverTime=(TextView)findViewById(R.id.startOvertimeText);
        endOverTime = (TextView) findViewById(R.id.endOvertimeText);
        remarks = (EditText) findViewById(R.id.remarks);
        submit = (Button) findViewById(R.id.submit);
        if (!sotime.equals("")){
            startOverTime.setText(sotime);
        }
        if (!eotime.equals("")){
            endOverTime.setText(eotime);
        }
        if (!remark.equals("")){
            remarks.setText(remark);
        }

    }

    //残業開始時刻
    public void showStartOvertimePickerDialog(View v) {
        text = 0;
        DialogFragment newFragment = new AdvancedTimePick();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    //残業終了時刻
    public void showEndOvertimePickerDialog(View v) {
        text = 1;
        DialogFragment newFragment = new AdvancedTimePick();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    //取得した時刻をTextViewに表示
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (text == 0) {
            startOverTime.setText(String.format("%02d:%02d", hourOfDay, minute));
        } else {
            endOverTime.setText(String.format("%02d:%02d", hourOfDay, minute));
        }
    }

    public void submit(View v){
        Intent intent = new Intent();
        intent.putExtra("sotime", startOverTime.getText().toString());
        intent.putExtra("eotime", endOverTime.getText().toString());
        intent.putExtra("remarks", remarks.getText().toString());
        setResult(Activity.RESULT_OK, intent);

        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
