package com.seng480b.bumerang;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private int minutes;
    private int hours;
    private int dayOfMonth;
    private int month;
    private int year;

    private Calendar setDuration;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);

        minutes = getArguments().getInt("minutes");
        hours = getArguments().getInt("hours");
        dayOfMonth = getArguments().getInt("dayOfMonth");
        month = getArguments().getInt("month");
        year = getArguments().getInt("year");

        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        //.....
        //setDuration.set()

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        TextView tv1=(TextView) getActivity().findViewById(R.id.inputDate);
        tv1.setText(view.getDayOfMonth() + ", " + view.getMonth() + "..." + hours + ":" + minutes);

        int durationInMinutes = 111;

        Intent i = new Intent();
        i.putExtra("durationInMinutes",durationInMinutes);
        i.putExtra("calendar",setDuration);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
    }
}