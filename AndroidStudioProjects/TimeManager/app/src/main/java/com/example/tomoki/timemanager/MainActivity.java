package com.example.tomoki.timemanager;

import android.app.DatePickerDialog;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,DatePickerDialog.OnDateSetListener {

    private TextView dateText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dateText=(TextView)findViewById(R.id.dateText);
        Button dateButton=(Button)findViewById(R.id.date);
        dateButton.setOnClickListener(this);
    }

    @Override
    public void onDateSet(DatePicker view,int year,int monthOfYear,int dayOfMonth){
        dateText.setText(String.valueOf(year)+"/"+ String.valueOf(monthOfYear));
    }

    public void showDatePiclerDialog(View v){
        DialogFragment newFlagment=new DatePick();
        newFlagment.show(getSupportFragmentManager(),"datePicker");
    }

    @Override
    public void onClick(View v) {
        if(R.id.date==v.getId()){

        }
    }
}
