package com.example.tomoki.timemanager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
    private TextView startTime,endTime;
    private NumberPicker breaktime;
    private EditText place;
    private ListView timelistView;
    private int text;
    private SQLiteDatabase timedb;
    private DatabaseHelper databaseHelper;
    private Cursor cursor=null,year_cursor=null,month_cursor=null;
    private SimpleCursorAdapter adapter;
    private Date s_time_date,e_time_date;
    private long result;
    private ArrayAdapter<String> spinner_adapter_year,spinner_adapter_date;
    boolean dateflag=false,stimeflag=false,etimeflag=false;

    private static final int ID = 0;
    private static final int DATE = 1;
    private static final int RESULT = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dateText = (TextView) findViewById(R.id.dateText);
        startTime = (TextView) findViewById(R.id.startTimeText);
        endTime = (TextView) findViewById(R.id.endTimeText);
        breaktime = (NumberPicker) findViewById(R.id.breaktime);
        place=(EditText)findViewById(R.id.place);
        timelistView = (ListView) findViewById(R.id.list);
        breaktime.setMinValue(0);
        breaktime.setMaxValue(150);

        databaseHelper = new DatabaseHelper(getApplicationContext());
        timedb = databaseHelper.getWritableDatabase();
        cursor=timedb.query("timedb",null,null,null,null,null,"yearmonthdate");
        cursor.moveToFirst();

        adapter = new SimpleCursorAdapter(this, R.layout.item, cursor, new String[]{
                "year", "result"}, new int[]{R.id.listdate, R.id.listtime}, 0);
        adapter.setViewBinder(this);
        timelistView.setAdapter(adapter);


        //Spinnerの設定
        spinner_adapter_year = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        spinner_adapter_year.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_adapter_date = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        spinner_adapter_date.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // アイテムを追加します
        year_cursor=timedb.query(true,"timedb",null,null,null,"year",null,"year",null);
        while (year_cursor.moveToNext()) {
            String year = year_cursor.getString(1);
            spinner_adapter_year.add(year);
        }

        Spinner spinner_year = (Spinner) findViewById(R.id.year_spinner);
        // アダプターを設定します
        spinner_year.setAdapter(spinner_adapter_year);
        //year_cursor=null;

        spinner_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
                Spinner spinner = (Spinner) parent;
                // 選択されたアイテムを取得します
                String item = (String) spinner.getSelectedItem();
                //Spinnerの設定
                // アイテムを追加します
                spinner_adapter_date.clear();
                timedb = databaseHelper.getWritableDatabase();
                month_cursor = timedb.query(true, "timedb", null, "year = " + item, null, "month", null, "month",null);
                while (month_cursor.moveToNext()) {
                    String month = month_cursor.getString(2);
                    spinner_adapter_date.add(month);
                }

                Spinner spinner_month = (Spinner) findViewById(R.id.month_spinner);
                // アダプターを設定します
                spinner_month.setAdapter(spinner_adapter_date);
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
                String currentId = cursor.getString(cursor.getColumnIndex("_id"));
                String currentName = cursor.getString(cursor.getColumnIndex("place"));
                Toast.makeText(getApplicationContext(), "id="+currentId+",place="+currentName, Toast.LENGTH_SHORT).show();

                Dialog(currentId);
                /*Bundle bundle = new Bundle();
                bundle.putString("id", currentId);
                Log.d(currentId, cursor.getString(cursor.getColumnIndex("_id")));
                DialogFragment newFragment = new ContactUsDialogFragment();
                newFragment.setArguments(bundle);
                newFragment.show(getSupportFragmentManager(), "Dialog");*/
            }
        });

    }

    public void SpinnerRefresh(){
        timedb = databaseHelper.getWritableDatabase();
        spinner_adapter_year.clear();
        year_cursor=timedb.query(true,"timedb",null,null,null,"year",null,"year",null);
        while (year_cursor.moveToNext()) {
            String year = year_cursor.getString(1);
            spinner_adapter_year.add(year);
        }
        timedb.close();
        timedb=null;
        Spinner spinner = (Spinner) findViewById(R.id.year_spinner);
        // アダプターを設定します
        spinner.setAdapter(spinner_adapter_year);
    }

    //ダイアログの表示と処理
    public void Dialog(final String currentId){
        CharSequence[] items={"削除","編集","閉じる"};
        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle(currentId+"番の操作");
        builder.setItems(items,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                SQLiteDatabase deletedb=databaseHelper.getWritableDatabase();
                                timedb = databaseHelper.getWritableDatabase();
                                deletedb.delete("timedb","_id = "+currentId,null);
                                refresh_list();
                                SpinnerRefresh();
                                break;
                            case 2:

                                break;
                        }
                    }
                }).show();
    }

    //ListViewの更新
    public void refresh_list(){
        cursor=timedb.query("timedb",null,null,null,null,null,"date");
        adapter.changeCursor(cursor);
        timedb.close();
        timedb=null;
    }

    //指定した書式でListViewにデータを挿入
    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
        switch (columnIndex) {
            case DATE:
                TextView date = (TextView) view;
                date.setText(cursor.getString(1)+"-"+cursor.getString(2)+"-"+cursor.getString(3));
                return true;
            case RESULT:
                TextView id = (TextView) view;
                int result=Integer.parseInt(cursor.getString(columnIndex));
                int time=result/60;
                int minute=result%60;
                //id.setText("勤務時間："+time+"時間"+minute+"分");
                id.setText(String.format("勤務時間：%02d時間%02d分",time,minute));
                return true;
            default:
                break;
        }
        return false;
    }



    //DBへの書き込み
    public void addData(View v){
        if(dateflag==true&&stimeflag==true&&etimeflag==true) {
            timedb = databaseHelper.getWritableDatabase();
            timedb.beginTransaction();
            try {
                String s_time = startTime.getText().toString();
                Log.d("time", s_time);
                String e_time = endTime.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                s_time_date = sdf.parse(s_time);
                e_time_date = sdf.parse(e_time);

                long dateTimeTo = s_time_date.getTime();
                long dateTimeFrom = e_time_date.getTime();
                result = (dateTimeFrom - dateTimeTo) / (1000 * 60) - breaktime.getValue();
                if (result < 0) {
                    result += 24 * 60;
                }
                Log.d("result", String.valueOf(result));
                String[] year=dateText.getText().toString().split("-");
                ContentValues values = new ContentValues();
                values.put("year",year[0]);
                values.put("month",year[1]);
                values.put("date",year[2]);
                //values.put("date", dateText.getText().toString());
                values.put("starttime", startTime.getText().toString());
                values.put("endtime", endTime.getText().toString());
                values.put("breaktime", String.valueOf(breaktime.getValue()));
                values.put("result", result);
                values.put("yearmonthdate",dateText.getText().toString());
                values.put("place",place.getText().toString());
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
                SpinnerRefresh();
            }
        }else{
            //エラーのダイアログを表示
            new AlertDialog.Builder(this)
                    .setTitle("エラー")
                    .setMessage("未選択の項目があります")
                    .setPositiveButton("OK", null)
                    .show();
        }
    }

    private void clearText(){
        dateText.setText("");
        startTime.setText("");
        endTime.setText("");
        place.setText("");
        breaktime.setValue(0);
        dateflag=false;
        stimeflag=false;
        etimeflag=false;
    }

    //取得した年月日をTextViewに表示
    @Override
    public void onDateSet(DatePicker view,int year,int monthOfYear,int dayOfMonth){
        //dateText.setText(String.valueOf(year)+"/"+ String.valueOf(monthOfYear+1)+"/"+String.valueOf(dayOfMonth));
        dateText.setText(String.format("%d-%02d-%02d",year,monthOfYear+1,dayOfMonth));
        dateflag=true;

    }

    //DatePickerDialogの表示
    public void showDatePickerDialog(View v){
        DialogFragment newFragment=new DatePick();
        newFragment.show(getSupportFragmentManager(),"datePicker");
    }

    //取得した時刻をTextViewに表示
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if(text==0) {
            startTime.setText(String.format("%02d:%02d",hourOfDay,minute));
            stimeflag=true;
        }else {
            endTime.setText(String.format("%02d:%02d",hourOfDay,minute));
            etimeflag=true;
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

}
