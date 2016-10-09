package com.seng480b.bumerang;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

public class CreateRequest extends Fragment {
    private SeekBar distanceBar;
    private TextView time, distance;

    @Override
    // Fragment Cancel = new Browse();
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.activity_create_request, container, false);

        // Setup for Seekbars
        distanceBar = (SeekBar) inflatedView.findViewById(R.id.barDistance);

        // Setup for editText associated with above SeekBars
        distance = (TextView) inflatedView.findViewById(R.id.labelDistanceNum);

        // Setup for the distance seek bar
        distanceBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                distance.setText(progress + " kms");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return inflatedView;
    }

    public void cancelClicked(View view) {
        // go back to the home page fragment
    }

    public void createClicked(View view) {
        //FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        //ft.replace(R.id.mainFrame,);
        //ft.commit();Intent create = new Intent(super.getActivity(), MyRequests.class );
        // Pull the information from the various fields of the form
        // push the collected information to the next activity
    }
}
