package com.example.tomoki.timemanager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * Created by tomoki on 2017/05/24.
 */

public class EditActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{
    private Cursor cursor=null;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase timedb;
    private String id;
    private int text=0;
    private EditText editPlace, editOption;
    private TextView editDateText,editsTime,editeTime;
    private NumberPicker editBreakTime;

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
        editOption = (EditText) findViewById(R.id.editPlace);
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
        editOption.setText(cursor.getString(cursor.getColumnIndex("remarks")));
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
}
