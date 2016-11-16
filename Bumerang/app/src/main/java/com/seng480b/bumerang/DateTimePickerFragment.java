package com.seng480b.bumerang;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class DateTimePickerFragment extends DialogFragment {

    private View rootView;

    public DateTimePickerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_date_time_picker, container, false);
        getDialog().setTitle("-SET DURATION- DIALOG");

        Calendar today = Calendar.getInstance();

        int day = today.get(Calendar.DAY_OF_MONTH);
        //maxDate is a week from today
        Calendar maxDate = Calendar.getInstance();
        maxDate.set(Calendar.DAY_OF_MONTH, day + 7);

        DatePicker datePicker = (DatePicker) rootView.findViewById(R.id.date_picker);
        datePicker.setMaxDate(maxDate.getTimeInMillis());
        //minDate should be the current date (note: in order to work, it is current date minus 1 second)
        datePicker.setMinDate(today.getTimeInMillis()-1000);

        //when you tap the 'set' button
        rootView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int durationInMinutes = findDuration();
                int hours = durationInMinutes/60;
                int minutes = durationInMinutes % 60;
                String display_text = createHourMinuteMessage(hours,minutes);

                TextView time_text = (TextView) getActivity().findViewById(R.id.inputTime);
                time_text.setText(display_text);

                Intent i = new Intent();
                i.putExtra("durationInMinutes",durationInMinutes);
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);

                getDialog().dismiss();
            }
        });

        return rootView;
    }

    /**
     *
     * @return int[] returns array of 2 ints: 1rst element is hour, 2nd is minute
     */
    public int findDuration(){
        DatePicker datePicker = (DatePicker) rootView.findViewById(R.id.date_picker);
        TimePicker timePicker = (TimePicker) rootView.findViewById(R.id.time_picker);

        //set up a Calendar for the current time
        Calendar current = Calendar.getInstance();

        //set up a Calendar the time the user inputs
        Calendar changedTime = Calendar.getInstance();

        int hour;
        int minute;
        if (Build.VERSION.SDK_INT >= 23 ) {
            hour = timePicker.getHour();
            minute = timePicker.getMinute();
        } else {
            hour = timePicker.getCurrentHour();
            minute = timePicker.getCurrentMinute();
        }

        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();

        changedTime.set(Calendar.HOUR_OF_DAY,hour);
        changedTime.set(Calendar.MINUTE,minute);
        changedTime.set(Calendar.DAY_OF_MONTH,day);
        changedTime.set(Calendar.MONTH,month);
        changedTime.set(Calendar.YEAR,year);

        return getDiffInMinutes(changedTime,current);

    }

    public int getDiffInMinutes(Calendar calChanged, Calendar calCurrent){
        long diff = calChanged.getTimeInMillis() - calCurrent.getTimeInMillis();
        return (int)diff/(60*1000);
    }

    public String createHourMinuteMessage(int hour_diff, int minute_diff){
        String hour_text = " hours, ";
        String minute_text = " minutes";
        if (hour_diff == 1) {
            hour_text = " hour, ";
        }
        if (minute_diff == 1) {
            minute_text = " minute";
        }
        return (hour_diff + hour_text + minute_diff + minute_text);

    }

}
