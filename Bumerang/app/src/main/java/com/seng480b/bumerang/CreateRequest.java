package com.seng480b.bumerang;

import android.content.Intent;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CreateRequest extends Fragment {
    private SeekBar distanceBar;
    private TextView distance;
    private int multipler;
    private Spinner distSpinner;

    private RadioGroup radioLendBorrow;

    View inflatedView;
    Button cancelButton, createButton;

    @Override
    // Fragment Cancel = new Browse();
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ((Home)getActivity()).setActionBarTitle("Create Request");

        inflatedView = inflater.inflate(R.layout.activity_create_request, container, false);

        /** make the tabs invisible **/
        ViewPager mViewPager = (ViewPager) getActivity().findViewById(R.id.container);
        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.tabs);
        mViewPager.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);

        // Setup radiogroup (choose lend or borrow)
        radioLendBorrow = (RadioGroup) inflatedView.findViewById(R.id.radio_borrow_lend);
        radioLendBorrow.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //checkedId is the radiobutton that was selected
                switch(checkedId){
                    case R.id.radio_borrow:
                        Toast.makeText(getActivity(),"Borrow!",Toast.LENGTH_LONG).show();
                        break;
                    case R.id.radio_lend:
                        Toast.makeText(getActivity(),"Lend!",Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });

        // Setup for Seekbars
        distanceBar = (SeekBar) inflatedView.findViewById(R.id.barDistance);

        // Setup for editText associated with above SeekBars
        distance = (TextView) inflatedView.findViewById(R.id.labelDistanceNum);

        // Setup for the distance seek bar
        distanceBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = multipler * progress;
                distance.setText(Integer.toString(value));
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

       distSpinner = (Spinner) inflatedView.findViewById(R.id.spinnerDistance);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.distance_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        distSpinner.setAdapter(adapter);

        distSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    multipler = 10;
                } else {
                    multipler = 1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return inflatedView;
    }
}
