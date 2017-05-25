package com.example.tomoki.timemanager;


import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.TimePicker;

import java.util.Calendar;

public class AdvancedTimePick extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), (Advanced)getActivity(), hour, minute, true);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

    }
}
