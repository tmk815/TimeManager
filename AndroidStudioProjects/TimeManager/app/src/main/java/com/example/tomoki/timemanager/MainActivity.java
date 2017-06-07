package com.example.tomoki.timemanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener, SimpleCursorAdapter.ViewBinder {

    private TextView dateText;
    private TextView startTime, endTime;
    private TextView total_result;
    private NumberPicker breaktime;
    private EditText place;
    private ListView timelistView;
    private int text, total;
    private SQLiteDatabase timedb;
    private DatabaseHelper databaseHelper;
    private Cursor cursor = null, year_cursor = null, month_cursor = null, listcursor = null;
    private SimpleCursorAdapter adapter;
    private Date s_time_date, e_time_date,over_s_time_date,over_e_time_date;
    private long result, overtimeresult=0;
    private ArrayAdapter<String> spinner_adapter_year, spinner_adapter_date;
    boolean dateflag = false, stimeflag = false, etimeflag = false;
    private String spinnerYearItem, spinnerMonthItem;
    private String closingDate;
    private String sotime="",eotime="", remarks="";

    private static final int DATE = 1;
    private static final int RESULT = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        closingDate = sharedPreferences.getString("closeday","31");
        Log.d("Preference",closingDate);

        dateText = (TextView) findViewById(R.id.dateText);
        startTime = (TextView) findViewById(R.id.startTimeText);
        endTime = (TextView) findViewById(R.id.endTimeText);
        breaktime = (NumberPicker) findViewById(R.id.breaktime);
        place = (EditText) findViewById(R.id.place);
        timelistView = (ListView) findViewById(R.id.list);
        total_result = (TextView) findViewById(R.id.total_result);
        breaktime.setMinValue(0);
        breaktime.setMaxValue(150);

        databaseHelper = new DatabaseHelper(getApplicationContext());
        timedb = databaseHelper.getWritableDatabase();
        cursor = timedb.query("timedb", null, null, null, null, null, "year DESC,month DESC,date DESC");
        cursor.moveToFirst();

        adapter = new SimpleCursorAdapter(this, R.layout.item, cursor, new String[]{
                "year", "result"}, new int[]{R.id.listdate, R.id.listtime}, 0);
        adapter.setViewBinder(this);
        timelistView.setAdapter(adapter);


        //Spinnerを設定
        spinner_adapter_year = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        spinner_adapter_year.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_adapter_date = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        spinner_adapter_date.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // アイテムを追加します
        year_cursor = timedb.query(true, "timedb", null, null, null, "year", null, "year DESC", null);
        while (year_cursor.moveToNext()) {
            String year = year_cursor.getString(1);
            spinner_adapter_year.add(year);
        }

        Spinner spinner_year = (Spinner) findViewById(R.id.year_spinner);
        final Spinner spinner_month = (Spinner) findViewById(R.id.month_spinner);
        // アダプターを設定します
        spinner_year.setAdapter(spinner_adapter_year);


        //年選択のSpinnerのタップ時処理
        spinner_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner spinner = (Spinner) parent;
                // 選択されたアイテムを取得します
                spinnerYearItem = (String) spinner.getSelectedItem();
                //Spinnerの設定
                // アイテムを追加します
                spinner_adapter_date.clear();
                timedb = databaseHelper.getWritableDatabase();
                month_cursor = timedb.query(true, "timedb", null, "year = " + spinnerYearItem, null, "month", null, "month DESC", null);
                while (month_cursor.moveToNext()) {
                    String month = month_cursor.getString(2);
                    spinner_adapter_date.add(month);
                }

                // アダプターを設定します
                spinner_month.setAdapter(spinner_adapter_date);
                timedb.close();
                timedb = null;
            }


            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


        //月選択のSpinnerのタップ時処理
        spinner_month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner monthspinner = (Spinner) parent;
                spinnerMonthItem = (String) monthspinner.getSelectedItem();

                timedb = databaseHelper.getWritableDatabase();
                listcursor = timedb.query(true, "timedb", null, "year = " + spinnerYearItem + " and month = '" + spinnerMonthItem + "'", null, null, null, "date DESC", null);
                adapter.changeCursor(listcursor);
                total = 0;
                listcursor.moveToFirst();
                for (int i = 0; i < listcursor.getCount(); i++) {
                    total = total + listcursor.getInt(7);
                    listcursor.moveToNext();
                }
                total_result.setText("総労働時間：" + total / 60 + "時間" + total % 60 + "分");
                timedb.close();
                timedb = null;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


        //ListViewのクリック時処理
        timelistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String currentId = listcursor.getString(listcursor.getColumnIndex("_id"));
                String currentName = listcursor.getString(listcursor.getColumnIndex("place"));
                //.makeText(getApplicationContext(), "id=" + currentId + ",place=" + currentName, Toast.LENGTH_SHORT).show();

                Dialog(currentId);
            }
        });

    }

    public void SpinnerRefresh(String yitem, String mitem) {
        timedb = databaseHelper.getWritableDatabase();
        spinner_adapter_year.clear();
        year_cursor = timedb.query(true, "timedb", null, null, null, "year", null, "year DESC", null);
        while (year_cursor.moveToNext()) {
            String year = year_cursor.getString(1);
            spinner_adapter_year.add(year);
        }
        timedb.close();
        timedb = null;
        Spinner spinner = (Spinner) findViewById(R.id.year_spinner);
        //Spinner mspinner=(Spinner)findViewById(R.id.month_spinner);
        // アダプターを設定します
        spinner.setAdapter(spinner_adapter_year);
        //setSelection(spinner,mspinner,yitem,mitem);
    }

    /*public void setSelection(Spinner yearSpinner,Spinner monthSpinner, String yearItem,String monthItem) {
        SpinnerAdapter yearAdapter = yearSpinner.getAdapter();
        SpinnerAdapter monthAdapter = monthSpinner.getAdapter();
        int yearindex = 0;
        int monthindex = 0;
        for (int i = 0; i < yearAdapter.getCount(); i++) {
            Log.d("うううううう"+yearAdapter.getItem(i).toString(),yearItem);
            if (yearAdapter.getItem(i).toString().equals(yearItem)) {
                yearindex = i;
                break;
            }
        }
        for (int j = 0; j < monthAdapter.getCount(); j++) {
            Log.d("えええええ"+monthAdapter.getItem(j).toString(),monthItem);
            if (monthAdapter.getItem(j).toString().equals(monthItem)) {
                monthindex = j;
                break;
            }
        }
        yearSpinner.setSelection(yearindex,false);
        monthSpinner.setSelection(monthindex,false);
    }*/

    //ダイアログの表示と処理
    public void Dialog(final String currentId) {
        CharSequence[] items = {"削除","編集", "閉じる"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(currentId + "番の操作");
        builder.setItems(items,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                SQLiteDatabase deletedb = databaseHelper.getWritableDatabase();
                                timedb = databaseHelper.getWritableDatabase();
                                deletedb.delete("timedb", "_id = " + currentId, null);
                                refresh_list();
                                //SpinnerRefresh();
                                break;
                            case 1:
                                //編集画面への移行処理
                                Intent intent = new Intent(getApplicationContext(),EditActivity.class);
                                intent.putExtra("id",currentId);
                                startActivity(intent);

                                break;
                            case 2:

                                break;
                        }
                    }
                }).show();
    }

    //ListViewの更新
    public void refresh_list() {
        listcursor = timedb.query("timedb", null, "year = " + spinnerYearItem + " and month = '" + spinnerMonthItem + "'", null, null, null, "year DESC,month DESC,date DESC");
        adapter.changeCursor(listcursor);
        total = 0;
        listcursor.moveToFirst();
        for (int i = 0; i < listcursor.getCount(); i++) {
            total = total + listcursor.getInt(7);
            listcursor.moveToNext();
        }
        total_result.setText("総労働時間：" + total / 60 + "時間" + total % 60 + "分");
        timedb.close();
        timedb = null;
    }

    //指定した書式でListViewにデータを挿入
    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
        switch (columnIndex) {
            case DATE:
                TextView date = (TextView) view;
                date.setText(cursor.getString(1) + "-" + cursor.getString(2) + "-" + cursor.getString(3));
                return true;
            case RESULT:
                TextView id = (TextView) view;
                int result = Integer.parseInt(cursor.getString(columnIndex));
                int time = result / 60;
                int minute = result % 60;
                //id.setText("勤務時間："+time+"時間"+minute+"分");
                id.setText(String.format("%d時間%02d分", time, minute));
                return true;
            default:
                break;
        }
        return false;
    }


    //DBへの書き込み
    public void addData(View v) {
        if (dateflag == true && stimeflag == true && etimeflag == true) {
            timedb = databaseHelper.getWritableDatabase();
            timedb.beginTransaction();
            try {
                //勤務時間の計算
                String s_time = startTime.getText().toString();
                Log.d("time", s_time);
                String e_time = endTime.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                s_time_date = sdf.parse(s_time);
                e_time_date = sdf.parse(e_time);

                long dateTimeTo = s_time_date.getTime();
                long dateTimeFrom = e_time_date.getTime();
                result = (dateTimeFrom - dateTimeTo) / (1000 * 60);
                if (result < 0) {
                    result += 24 * 60;
                }
                result = result - breaktime.getValue();
                Log.d("result", String.valueOf(result));

                if(!sotime.equals("")&&!eotime.equals("")) {
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
                }

                String[] year = dateText.getText().toString().split("-");
                ContentValues values = new ContentValues();
                values.put("year", year[0]);
                values.put("month", year[1]);
                values.put("date", year[2]);
                values.put("starttime", startTime.getText().toString());
                values.put("endtime", endTime.getText().toString());
                values.put("breaktime", String.valueOf(breaktime.getValue()));
                values.put("result", result);
                values.put("yearmonthdate", dateText.getText().toString());
                values.put("place", place.getText().toString());
                values.put("startovertime",sotime);
                values.put("endovertime",eotime);
                values.put("overresult",overtimeresult);
                values.put("remarks", remarks);
                timedb.insert("timedb", null, values);
                Log.d("MainActivity", "データを追加しました。");
                Toast.makeText(this, "データを追加しました", Toast.LENGTH_SHORT).show();
                timedb.setTransactionSuccessful();
                clearText();
            } catch (Exception e) {
                Log.e("Database", e.getMessage());
            } finally {
                timedb.endTransaction();
                refresh_list();
                SpinnerRefresh(spinnerYearItem, spinnerMonthItem);
            }
        } else {
            //エラーのダイアログを表示
            new AlertDialog.Builder(this)
                    .setTitle("エラー")
                    .setMessage("未選択の項目があります")
                    .setPositiveButton("OK", null)
                    .show();
        }
    }

    public void setOption(View v){
        Intent intent=new Intent(this,Advanced.class);
        // 遷移先から返却されてくる際の識別コード
        int requestCode = 1001;
        // 返却値を考慮したActivityの起動を行う
        intent.putExtra("sotime",sotime);
        intent.putExtra("eotime", eotime);
        intent.putExtra("remarks", remarks);
        startActivityForResult(intent, requestCode);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // startActivityForResult()の際に指定した識別コードとの比較
        if (requestCode == 1001) {

            // 返却結果ステータスとの比較
            if (resultCode == Activity.RESULT_OK) {

                // 返却されてきたintentから値を取り出す
                sotime = intent.getStringExtra("sotime");
                eotime = intent.getStringExtra("eotime");
                remarks = intent.getStringExtra("remarks");
                Log.d("sotime",sotime);
                Log.d("eotime", eotime);
                Log.d("remarks", remarks);
            }
        }
    }

    private void clearText() {
        dateText.setText("");
        startTime.setText("");
        endTime.setText("");
        place.setText("");
        sotime = "";
        eotime = "";
        remarks = "";
        breaktime.setValue(0);
        dateflag = false;
        stimeflag = false;
        etimeflag = false;
    }

    //取得した年月日をTextViewに表示
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        dateText.setText(String.format("%d-%02d-%02d", year, monthOfYear + 1, dayOfMonth));
        dateflag = true;
    }

    //DatePickerDialogの表示
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePick();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    //取得した時刻をTextViewに表示
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (text == 0) {
            startTime.setText(String.format("%02d:%02d", hourOfDay, minute));
            stimeflag = true;
        } else {
            endTime.setText(String.format("%02d:%02d", hourOfDay, minute));
            etimeflag = true;
        }
    }

    //開始時刻
    public void showStartTimePickerDialog(View v) {
        text = 0;
        DialogFragment newFragment = new TimePick();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    //終了時刻
    public void showEndTimePickerDialog(View v) {
        text = 1;
        DialogFragment newFragment = new TimePick();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.setting) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

}
