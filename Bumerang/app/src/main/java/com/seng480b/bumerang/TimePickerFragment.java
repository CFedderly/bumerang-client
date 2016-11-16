package com.seng480b.bumerang;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;



public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    int minute;
    int hour;
    int dayOfMonth;
    int month;
    int year;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        minute = getArguments().getInt("minute");
        hour = getArguments().getInt("hour");
        dayOfMonth = getArguments().getInt("dayOfMonth");
        month = getArguments().getInt("month");
        year = getArguments().getInt("year");

        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        TextView tv1=(TextView) getActivity().findViewById(R.id.inputTime);
        tv1.setText(hourOfDay+":"+minute+"....." + month + ", " + year);

        int durationInMinutes = 222;

        Intent i = new Intent();
        i.putExtra("durationInMinutes",durationInMinutes);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);

    }
}
