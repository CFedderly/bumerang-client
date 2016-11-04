package com.seng480b.bumerang;

import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.IOException;

public class CreateRequest extends Fragment {

    FirebaseAnalytics mFireBaseAnalytics;
    private static final String requestUrl = BuildConfig.SERVER_URL + "/request/";
    private static final int titleField = R.id.inputTitle;
    private static final int descriptionField = R.id.inputDescription;
    private static final int hoursField = R.id.inputHours;
    private static final int minutesField = R.id.inputMinutes;

    private Request currRequest;
    private Request.RequestType requestType;
    private boolean distanceInMeters = false;
    private int distance;

    private TextView distanceText;
    private int multiplier;

    View inflatedView;
    Button cancelButton;
    Button createButton;

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
        RadioGroup radioLendBorrow = (RadioGroup) inflatedView.findViewById(R.id.radio_borrow_lend);
        radioLendBorrow.check(R.id.radio_borrow);
        requestType = Request.RequestType.BORROW;
        radioLendBorrow.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //checkedId is the radiobutton that was selected
                switch(checkedId){
                    case R.id.radio_borrow:
                        Toast.makeText(getActivity(),"Borrow!",Toast.LENGTH_LONG).show();
                        requestType = Request.RequestType.BORROW;
                        break;
                    case R.id.radio_lend:
                        Toast.makeText(getActivity(),"Lend!",Toast.LENGTH_LONG).show();
                        requestType = Request.RequestType.LEND;
                        break;
                }
            }
        });

        // Setup for Seekbars
        SeekBar distanceBar = (SeekBar) inflatedView.findViewById(R.id.barDistance);
        distanceBar.setProgress(0);

        // Setup for editText associated with above SeekBars
        distanceText = (TextView) inflatedView.findViewById(R.id.labelDistanceNum);

        // Setup for the distance seek bar
        distanceBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = multiplier * progress;
                 if (distanceInMeters) {
                     distance = value;
                 } else {
                     distance = value*1000;
                 }
                distanceText.setText(Integer.toString(value));
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
                Fragment back = new Browse();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.mainFrame,back);
                ft.commit();
            }
        });

        createButton = (Button) inflatedView.findViewById(R.id.buttonCreate);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currRequest = createRequest();
                if (currRequest != null) {
                    if (Connectivity.checkNetworkAndShowAlert(getContext(), R.string.no_internet_connection_create_request)) {
                        new CreateRequestTask().execute();
                    }
                }
            }
        });

        final Button adv_options_button = (Button)inflatedView.findViewById(R.id.buttonAdvancedOptions);
        adv_options_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RelativeLayout advanced_options = (RelativeLayout)inflatedView.findViewById(R.id.layout_advanced_options);
                if (advanced_options.getVisibility() == View.GONE){
                    advanced_options.setVisibility(View.VISIBLE);
                    adv_options_button.setText(R.string.hide_advanced_options);
                } else {
                    advanced_options.setVisibility(View.GONE);
                    adv_options_button.setText(R.string.expand_advanced_options);
                }

            }
        });


       Spinner distSpinner = (Spinner) inflatedView.findViewById(R.id.spinnerDistance);

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
                    multiplier = 10;
                    distanceInMeters = true;
                } else {
                    multiplier = 1;
                    distanceInMeters = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return inflatedView;
    }



    private Request createRequest() {

        EditText title = (EditText) inflatedView.findViewById(titleField);
        EditText description = (EditText) inflatedView.findViewById(descriptionField);
        EditText hours = (EditText) inflatedView.findViewById(hoursField);
        EditText minutes = (EditText) inflatedView.findViewById(minutesField);

        // Check that all fields are filled, return null if not
        if (isEmpty(title) || isEmpty(description) || isEmpty(hours) || isEmpty(minutes)) {
            alertForEmptyFields();
            return null;
        }

        String titleStr = title.getText().toString().trim();
        String descriptionStr = description.getText().toString().trim();
        int hoursInt = Integer.parseInt(hours.getText().toString().trim());
        int minutesInt = Integer.parseInt(minutes.getText().toString().trim());
        if (UserDataCache.hasProfile()) {
            int userId = UserDataCache.getCurrentUser().getUserId();

            //This is where info on what users are entering is collected
            mFireBaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

            //Just turn hours into minutes
            int totalTime = (hoursInt*60)+minutesInt;

            Bundle params = new Bundle();
            params.putString( FirebaseAnalytics.Param.ITEM_NAME, titleStr);
            params.putString("Description", descriptionStr);
            params.putLong(FirebaseAnalytics.Param.VALUE, totalTime);
            mFireBaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, params);

            return new Request(userId, titleStr, descriptionStr, hoursInt, minutesInt, distance, requestType);
        } else {
            alertForRequestNotCreated();
            return null;
        }
    }

    private void alertForEmptyFields() {
        Toast.makeText(getActivity(), R.string.empty_request_field_message, Toast.LENGTH_LONG).show();
    }

    private void alertForRequestNotCreated() {
        Toast.makeText(inflatedView.getContext(), R.string.unable_to_create_request, Toast.LENGTH_LONG).show();
    }

    private static boolean isEmpty(EditText eText) {
        return eText.getText().toString().trim().length() == 0;
    }

    private class CreateRequestTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                Connectivity.makeHttpPostRequest(requestUrl, currRequest.getJSONKeyValuePairs());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("ERROR","Unable to create request.");
                cancel(true);
            }
            return null;
        }

        @Override
        protected void onCancelled(String result) {
            Fragment browse = new Browse();
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.mainFrame, browse);
            ft.commit();
            alertForRequestNotCreated();
        }

        @Override
        protected void onPostExecute(String result) {
            Fragment myRequests = new MyRequests();
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.mainFrame, myRequests);
            ft.commit();
            Toast.makeText(getActivity(), R.string.created_request, Toast.LENGTH_LONG).show();
        }
    }

}
