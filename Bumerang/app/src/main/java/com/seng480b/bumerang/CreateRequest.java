package com.seng480b.bumerang;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class CreateRequest extends Fragment {
    private SeekBar distanceBar;
    private TextView distance;
    View inflatedView;
    Button cancelButton, createButton;
    @Override
    // Fragment Cancel = new Browse();
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.activity_create_request, container, false);

        // Setup for Seekbars
        distanceBar = (SeekBar) inflatedView.findViewById(R.id.barDistance);

        // Setup for editText associated with above SeekBars
        distance = (TextView) inflatedView.findViewById(R.id.labelDistanceNum);

        // Setup for the distance seek bar
        distanceBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                distance.setText(progress + " km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Setup for the Cancel button on screen
        cancelButton = (Button) inflatedView.findViewById(R.id.buttonCancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reload = new Intent(getActivity(), Home.class );
                startActivity(reload);
               /*
                Fragment back = new Browse();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.mainFrame,back);
                ft.commit(); */
            }
        });

        createButton = (Button) inflatedView.findViewById(R.id.buttonCreate);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Make the create button go to my requests page
            }
        });


        return inflatedView;
    }
}
