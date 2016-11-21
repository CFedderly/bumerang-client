package com.seng480b.bumerang.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;

import com.seng480b.bumerang.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private int minutes;
    private int hours;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the current date as the default date in the picker
        final Calendar today = Calendar.getInstance();
        int year = today.get(Calendar.YEAR);
        int month = today.get(Calendar.MONTH);
        int day = today.get(Calendar.DAY_OF_MONTH);

        //grab minutes and hours from CreateRequest
        minutes = getArguments().getInt("minute");
        hours = getArguments().getInt("hour");
        if (minutes < 0) minutes = today.get(Calendar.MINUTE);
        if (hours < 0) hours = today.get(Calendar.HOUR_OF_DAY);

        //maxDate is a week from today
        Calendar maxDate = Calendar.getInstance();
        maxDate.set(Calendar.DAY_OF_MONTH, day + 7);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog dateDialog = new DatePickerDialog(getActivity(), this, year, month, day);
        dateDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
        //minDate should be the current date (note: in order to work, it is current date minus 1 second)
        dateDialog.getDatePicker().setMinDate(today.getTimeInMillis()-1000);

        return dateDialog;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do things with the date chosen by the user:
        Calendar desiredDate = Calendar.getInstance();
        desiredDate.set(Calendar.MINUTE,minutes);
        desiredDate.set(Calendar.HOUR_OF_DAY,hours);
        desiredDate.set(Calendar.DAY_OF_MONTH,day);
        desiredDate.set(Calendar.MONTH,month);
        desiredDate.set(Calendar.YEAR,year);

        SimpleDateFormat monthFormat = new SimpleDateFormat("LLL", Locale.getDefault());
        String monthName = monthFormat.format(desiredDate.getTime());
        String expiryDate =  monthName + "." + day;

        TextView tv1=(TextView) getActivity().findViewById(R.id.inputDate);
        tv1.setText(expiryDate);

        //set duration text
        int durationInMinutes = getDurationInMinutes(desiredDate);
        TextView durationText = (TextView) getActivity().findViewById(R.id.inputDuration);
        if (durationInMinutes > -1){
            durationText.setText(createHourMinuteMessage(durationInMinutes));
        } else {
            durationText.setText(getString(R.string.going_back_in_time_message));
        }

        //transfer the user's desired time back to Create Request
        Intent i = new Intent();
        i.putExtra("durationInMinutes",durationInMinutes);

        i.putExtra("minute",minutes);
        i.putExtra("hour",hours);
        i.putExtra("dayOfMonth",day);
        i.putExtra("month",month);
        i.putExtra("year",year);

        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
    }


    public int getDurationInMinutes(Calendar desiredDate){
        //set up a Calendar for the current time
        Calendar current = Calendar.getInstance();
        long diff = desiredDate.getTimeInMillis() - current.getTimeInMillis();
        //convert milliseconds to minutes
        return (int)diff/(60*1000);
    }

    public String createHourMinuteMessage(int minuteDiff){
        int hour = minuteDiff/60;
        int minute = minuteDiff%60;

        String hourText = " hours, ";
        String minuteText = " minutes";
        if (hour == 1) {
            hourText = " hour, ";
        }
        if (minute == 1) {
            minuteText = " minute";
        }
        return (hour + hourText + minute + minuteText);
    }
}