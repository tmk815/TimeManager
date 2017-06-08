package com.example.tomoki.timemanager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by tomoki on 2017/05/24.
 */

public class EditActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{
    private Cursor cursor=null;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase timedb;
    private String id;
    private int text=0;
    private EditText editPlace,editRemarks;
    private TextView editDateText,editsTime,editeTime;
    private NumberPicker editBreakTime;
    private Date s_time_date, e_time_date;
    private long result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setTitle("編集");
        editDateText = (TextView) findViewById(R.id.editDateText);
        editsTime = (TextView) findViewById(R.id.editStartTimeText);
        editeTime = (TextView) findViewById(R.id.editEndTimeText);
        editPlace = (EditText) findViewById(R.id.editPlace);
        editRemarks = (EditText) findViewById(R.id.editRemarks);
        editBreakTime = (NumberPicker) findViewById(R.id.editBreakTime);
        editBreakTime.setMinValue(0);
        editBreakTime.setMaxValue(150);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        databaseHelper = new DatabaseHelper(getApplicationContext());
        timedb = databaseHelper.getWritableDatabase();
        cursor = timedb.query("timedb", null, "_id = "+id, null, null, null, null);
        cursor.moveToFirst();
        editDateText.setText(cursor.getString(cursor.getColumnIndex("yearmonthdate")));
        editsTime.setText(cursor.getString(cursor.getColumnIndex("starttime")));
        editeTime.setText(cursor.getString(cursor.getColumnIndex("endtime")));
        editPlace.setText(cursor.getString(cursor.getColumnIndex("place")));
        editBreakTime.setValue(Integer.parseInt(cursor.getString(cursor.getColumnIndex("breaktime"))));
        editRemarks.setText(cursor.getString(cursor.getColumnIndex("remarks")));
    }

    //日付修正
    public void editDatePickerDialog(View v) {
        DialogFragment newFragment = new EditDatePick();
        newFragment.show(getSupportFragmentManager(), "editDatePicker");
    }
    //開始時刻修正
    public void editStartTimePickerDialog(View v) {
        text = 0;
        DialogFragment newFragment = new EditTimePick();
        newFragment.show(getSupportFragmentManager(), "editTimePicker");
    }

    //終了時刻修正
    public void editEndTimePickerDialog(View v) {
        text = 1;
        DialogFragment newFragment = new EditTimePick();
        newFragment.show(getSupportFragmentManager(), "editTimePicker");
    }

    //修正後の時刻をTextViewに表示
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (text == 0) {
            editsTime.setText(String.format("%02d:%02d", hourOfDay, minute));
        } else {
            editeTime.setText(String.format("%02d:%02d", hourOfDay, minute));
        }
    }

    //取得した年月日をTextViewに表示
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        editDateText.setText(String.format("%d-%02d-%02d", year, monthOfYear + 1, dayOfMonth));
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

    //DBへの書き込み
    public void updateData(View v) {
        timedb = databaseHelper.getWritableDatabase();
        timedb.beginTransaction();
        try {
            //勤務時間の計算
            String s_time = editsTime.getText().toString();
            Log.d("time", s_time);
            String e_time = editeTime.getText().toString();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            s_time_date = sdf.parse(s_time);
            e_time_date = sdf.parse(e_time);

            long dateTimeTo = s_time_date.getTime();
            long dateTimeFrom = e_time_date.getTime();
            result = (dateTimeFrom - dateTimeTo) / (1000 * 60);
            if (result < 0) {
                result += 24 * 60;
            }
            result = result - editBreakTime.getValue();
            Log.d("result", String.valueOf(result));

                /*if (!sotime.equals("") && !eotime.equals("")) {
                    //残業時間の計算
                    over_s_time_date = sdf.parse(sotime);
                    over_e_time_date = sdf.parse(eotime);
                    long overDateTimeTo = over_s_time_date.getTime();
                    long overDateTimeFrom = over_e_time_date.getTime();
                    overtimeresult = (overDateTimeFrom - overDateTimeTo) / (1000 * 60);
                    if (overtimeresult < 0) {
                        overtimeresult += 24 * 60;
                    }
                    Log.d("overtimeresult", String.valueOf(overtimeresult));
                }*/

            String[] year = editDateText.getText().toString().split("-");
            ContentValues values = new ContentValues();
            values.put("year", year[0]);
            values.put("month", year[1]);
            values.put("date", year[2]);
            values.put("starttime", editsTime.getText().toString());
            values.put("endtime", editeTime.getText().toString());
            values.put("breaktime", String.valueOf(editBreakTime.getValue()));
            values.put("result", result);
            values.put("yearmonthdate", editDateText.getText().toString());
            values.put("place", editPlace.getText().toString());
                /*values.put("startovertime", sotime);
                values.put("endovertime", eotime);
                values.put("overresult", overtimeresult);*/
            values.put("remarks", editRemarks.getText().toString());
            timedb.update("timedb",values,"_id = "+id,null);
            Log.d("MainActivity", "データを更新しました。");
            Toast.makeText(this, "データを更新しました", Toast.LENGTH_SHORT).show();
            timedb.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Database", e.getMessage());
        } finally {
            timedb.endTransaction();
            finish();
        }
    }
}
