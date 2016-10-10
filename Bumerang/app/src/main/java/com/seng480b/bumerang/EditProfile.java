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

public class EditProfile extends Fragment {
    private View inflatedView;
    private Button cancelButton, createButton;
    @Override
    // Fragment Cancel = new Browse();
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.fragment_edit_profile, container, false);

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

        createButton = (Button) inflatedView.findViewById(R.id.button5);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Get all the values from the editTexts and update values
                Intent reload = new Intent(getActivity(), Home.class );
                startActivity(reload);
            }
        });


        return inflatedView;
    }
}
