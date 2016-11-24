package com.seng480b.bumerang.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import com.seng480b.bumerang.R;

import java.util.Calendar;

import static com.seng480b.bumerang.utils.Utility.longToast;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private int dayOfMonth;
    private int month;
    private int year;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the current time as the default values for the picker --> chaaange dis plz
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        dayOfMonth = getArguments().getInt("dayOfMonth");
        month = getArguments().getInt("month");
        year = getArguments().getInt("year");

        if (dayOfMonth < 0) dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        if (month < 0) month = c.get(Calendar.MONTH);
        if (year < 0) year = c.get(Calendar.YEAR);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        Calendar desiredDate = Calendar.getInstance();
        desiredDate.set(Calendar.MINUTE,minute);
        desiredDate.set(Calendar.HOUR_OF_DAY,hourOfDay);
        desiredDate.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        desiredDate.set(Calendar.MONTH,month);
        desiredDate.set(Calendar.YEAR,year);

        String formattedTime = "hh:mm aaa";
        String expiryTime = (String)DateFormat.format(formattedTime,desiredDate.getTime());

        TextView tv1=(TextView) getActivity().findViewById(R.id.inputTime);
        tv1.setText(expiryTime);

        //set duration text
        int durationInMinutes = getDurationInMinutes(desiredDate);
        TextView durationText = (TextView) getActivity().findViewById(R.id.inputDuration);
        if (durationInMinutes > -1){
            durationText.setText(createHourMinuteMessage(durationInMinutes));
        } else {
            TextView expiryText = (TextView) getActivity().findViewById(R.id.labelExpiresIn);
            expiryText.setVisibility(View.GONE);
            longToast(getActivity(), R.string.going_back_in_time_toast);
            durationText.setText(getString(R.string.going_back_in_time_message));
        }

        //transfer the user's desired time (minutes,hours) back to Create Request
        Intent i = new Intent();
        i.putExtra("durationInMinutes",durationInMinutes);

        i.putExtra("minute",minute);
        i.putExtra("hour",hourOfDay);
        i.putExtra("dayOfMonth",dayOfMonth);
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