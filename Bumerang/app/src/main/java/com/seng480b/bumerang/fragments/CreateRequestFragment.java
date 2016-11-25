package com.seng480b.bumerang.fragments;


import android.app.Activity;
import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.seng480b.bumerang.BuildConfig;
import com.seng480b.bumerang.activities.HomeActivity;
import com.seng480b.bumerang.R;
import com.seng480b.bumerang.models.Request;
import com.seng480b.bumerang.utils.UserDataCache;
import com.seng480b.bumerang.utils.ConnectivityUtility;

import java.io.IOException;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.seng480b.bumerang.utils.Utility.*;
import static android.app.Activity.RESULT_OK;


public class CreateRequestFragment extends Fragment {

    FirebaseAnalytics mFireBaseAnalytics;
    private static final String REQUEST_URL = BuildConfig.SERVER_URL + "/request/";
    private static final int DEFAULT_MINUTES = 120;
    //that to :   private static final int defaultMinutes = 100;
    private static final int TITLE_FIELD = R.id.inputTitle;
    private static final int DESCRIPTION_FIELD = R.id.inputDescription;

    private int durationInMinutes = -1;
    private Calendar resultCalendar;

    private Request currRequest;
    private Request.RequestType requestType;
    private boolean distanceInMeters = false;
    private int distance;

    private TextView distanceText;
    private int multiplier = 1;

    private static final int PICK_IMAGE = 12345;
    private ImageView chosenImage;

    CheckBox postToFacebook;
    RadioButton borrow;

    View inflatedView;
    Button cancelButton;
    Button createButton;

    public static final int CREATE_REQUEST_FRAGMENT = 1;

    @Override
    // Fragment Cancel = new BrowseFragment();
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ((HomeActivity)getActivity()).setActionBarTitle("Create Request");

        inflatedView = inflater.inflate(R.layout.activity_create_request, container, false);

        // set duration texts to current time (plus 2 hours)
        Calendar today = Calendar.getInstance();
        TextView dateText=(TextView) inflatedView.findViewById(R.id.inputDate);
        TextView timeText=(TextView) inflatedView.findViewById(R.id.inputTime);
        today.add(Calendar.HOUR_OF_DAY,2);
        //change formatting for date and time so they're readable
        String formattedTime = "hh:mm aaa";
        String expiryTime = (String) DateFormat.format(formattedTime,today.getTime());
        SimpleDateFormat monthFormat = new SimpleDateFormat("LLL", Locale.getDefault());
        String monthName = monthFormat.format(today.getTime());
        String expiryDate =  monthName + "." + today.get(Calendar.DAY_OF_MONTH);
        dateText.setText(expiryDate);
        timeText.setText(expiryTime);

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
                //checkedId is the radio button that was selected
                switch(checkedId){
                    case R.id.radio_borrow:
                        longToast(getActivity(), "Borrow!");
                        requestType = Request.RequestType.BORROW;
                        TextView postToFacebookMessage=(TextView) inflatedView.findViewById(R.id.labelPostToFacebookCheckbox);
                        postToFacebookMessage.setText(R.string.post_to_facebook_message_borrow);
                        break;
                    case R.id.radio_lend:
                        longToast(getActivity(), "Lend!");
                        requestType = Request.RequestType.LEND;
                        TextView postToFacebookMessageLend=(TextView) inflatedView.findViewById(R.id.labelPostToFacebookCheckbox);
                        postToFacebookMessageLend.setText(R.string.post_to_facebook_message_lend);
                        break;
                }
            }
        });

        //initialize Calendar to NOW
        resultCalendar = Calendar.getInstance();

        // Setup for the button to change time (ie the time your ad will exist for)
        Button buttonToSetTime = (Button) inflatedView.findViewById(R.id.buttonToSetTime);
        buttonToSetTime.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Bundle args = new Bundle();
               args.putInt("minute",resultCalendar.get(Calendar.MINUTE));
               args.putInt("hour",resultCalendar.get(Calendar.HOUR_OF_DAY));
               args.putInt("dayOfMonth",resultCalendar.get(Calendar.DAY_OF_MONTH));
               args.putInt("month",resultCalendar.get(Calendar.MONTH));
               args.putInt("year",resultCalendar.get(Calendar.YEAR));

               DialogFragment timePicker = new TimePickerFragment();

               timePicker.setArguments(args);

               //the link back from the timePickerFragment to access -hours- and -minutes-
               timePicker.setTargetFragment(CreateRequestFragment.this, CREATE_REQUEST_FRAGMENT);

               FragmentManager fm = getFragmentManager();
               timePicker.show(fm,"time picker");
           }
        });

        // Setup for the button to change date (ie the date your ad will exist for)
        Button buttonToSetDate = (Button) inflatedView.findViewById(R.id.buttonToSetDate);
        buttonToSetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putInt("minute",resultCalendar.get(Calendar.MINUTE));
                args.putInt("hour",resultCalendar.get(Calendar.HOUR_OF_DAY));
                args.putInt("dayOfMonth",resultCalendar.get(Calendar.DAY_OF_MONTH));
                args.putInt("month",resultCalendar.get(Calendar.MONTH));
                args.putInt("year",resultCalendar.get(Calendar.YEAR));

                DialogFragment datePicker = new DatePickerFragment();

                datePicker.setArguments(args);

                //the link back from the datePickerFragment to access day/month/year
                datePicker.setTargetFragment(CreateRequestFragment.this, CREATE_REQUEST_FRAGMENT);

                FragmentManager fm = getFragmentManager();
                datePicker.show(fm,"date picker");
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

        //getting distanceText here so we can change it whenever the spinner value is changed
        distanceText = (TextView) inflatedView.findViewById(R.id.labelDistanceNum);
        //Set so that NO selection within the spinner is selected before the user selects it
        distSpinner.setSelection(0,false);

        distSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    multiplier = 10;
                    distanceInMeters = true;
                    int distanceNum = Integer.parseInt(distanceText.getText().toString());
                    distanceText.setText(String.valueOf(distanceNum*multiplier));
                } else {
                    multiplier = 1;
                    distanceInMeters = false;
                    int distanceNum = Integer.parseInt(distanceText.getText().toString());
                    distanceText.setText(String.valueOf(distanceNum/10));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
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
                distanceText.setText(String.format(Locale.getDefault(),"%d", value));
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
                Fragment back = new BrowseFragment();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.mainFrame,back);
                ft.commit();
            }
        });

        // Setup for the Create button on screen
        createButton = (Button) inflatedView.findViewById(R.id.buttonCreate);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currRequest = createRequest();
                if (currRequest != null) {
                    if (ConnectivityUtility.checkNetworkAndShowAlert(getContext(), R.string.no_internet_connection_create_request)) {
                        new CreateRequestTask().execute();
                    }
                }
            }
        });

        //Setup for checkbox to enable posting to Facebook
        postToFacebook=(CheckBox)inflatedView.findViewById(R.id.checkbox_enablePostToFacebook);

        //Setup for radio group to see if borrow or lending to auto post to Facebook
        borrow = (RadioButton)inflatedView.findViewById(R.id.radio_borrow);


        //set up button for hiding and expanding advanced options
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

        //Set up button and gallery to pick an image
        chosenImage = (ImageView) inflatedView.findViewById(R.id.createRequest_Image);
        Button pickImageButton = (Button) inflatedView.findViewById(R.id.createRequest_buttonPickImage);
        pickImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        return inflatedView;
    }

    private Request createRequest() {
        EditText title = (EditText) inflatedView.findViewById(TITLE_FIELD);
        EditText description = (EditText) inflatedView.findViewById(DESCRIPTION_FIELD);

        // Check that all fields are filled, return null if not
        if (isEmpty(title)) {
            alertForEmptyFields();
            return null;
        }

        String titleStr = editTextToString(title);
        String descriptionStr;

        if (!isEmpty(description)) {
            descriptionStr = editTextToString(description);
        } else {
            descriptionStr = "";
        }

        if (durationInMinutes < 0) {
            durationInMinutes= DEFAULT_MINUTES;
        }

        if (UserDataCache.hasProfile()) {
            int userId = UserDataCache.getCurrentUser().getUserId();

            //This is where info on what users are entering is collected
            mFireBaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

            //Turn total minutes (totalTimeInMinutes) into hours and minutes
            int hoursInt = durationInMinutes/60;
            int minutesInt = durationInMinutes % 60;

            //One of these should be tracking something
            Bundle params = new Bundle();
            params.putString( FirebaseAnalytics.Param.CONTENT_TYPE, titleStr);
            params.putString(FirebaseAnalytics.Param.ITEM_ID, descriptionStr);
            mFireBaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, params);

            Bundle params2 = new Bundle();
            params2.putString("Title", titleStr);
            params2.putString("Description", descriptionStr);
            params2.putInt("Time", ((hoursInt*60)+minutesInt));
            mFireBaseAnalytics.logEvent("request", params2);

            mFireBaseAnalytics.setUserProperty("title", titleStr);
            mFireBaseAnalytics.setUserProperty("Timeset", String.valueOf((hoursInt*60)+minutesInt));
            mFireBaseAnalytics.setUserProperty("description", descriptionStr);

            if (postToFacebook.isChecked()) {
                postToFacebook(borrow.isChecked(),titleStr, descriptionStr, hoursInt, minutesInt);
            }

            return new Request(userId, titleStr, descriptionStr, hoursInt, minutesInt, distance, requestType);
        } else {
            alertForRequestNotCreated();
            return null;
        }
    }

    private void postToFacebook(boolean borrow,String titleStr, String descriptionStr, int hoursInt, int minutesInt) {
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        ShareDialog shareDialog = new ShareDialog(getActivity());

        String quote = "";

        if (borrow==true) {
            quote = "Hey everybody, I'm currently looking for " + titleStr + ".";
            if (descriptionStr.equals("")) {
                quote = quote + " I need it within " + String.valueOf(hoursInt + ((double) minutesInt / 60)) + " hours please!";
            } else {
                quote = quote + " I would describe it as " + descriptionStr + ". Contact me within " +
                        String.valueOf(hoursInt + ((double) minutesInt / 60)) + " hours please!";
            }
        }
        else{
            quote = "Hey everybody, I'm currently looking to lend " + titleStr + ".";
            if (descriptionStr.equals("")) {
                quote = quote + " You could use it for " + String.valueOf(hoursInt + ((double) minutesInt / 60)) + " hours!";
            } else {
                quote = quote + " I would describe it as " + descriptionStr + ". Contact me within " +
                        String.valueOf(hoursInt + ((double) minutesInt / 60)) + " hours please!";
            }
        }
        //This code was found here: https://developers.facebook.com/docs/sharing/android#share_dialog
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setQuote(quote)
                    .setContentUrl(Uri.parse("http://www.bumerangapp.com"))
                    .build();
            shareDialog.show(linkContent);

        }
    }


    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery,PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            Uri imageUri = data.getData();
            chosenImage.setImageURI(imageUri);
        }

        //grab data (total duration in minutes) from TimePickerFragment
        switch(requestCode){
            case CREATE_REQUEST_FRAGMENT:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();

                    resultCalendar.set(Calendar.MINUTE,bundle.getInt("minute"));
                    resultCalendar.set(Calendar.HOUR_OF_DAY,bundle.getInt("hour"));
                    resultCalendar.set(Calendar.DAY_OF_MONTH,bundle.getInt("dayOfMonth"));
                    resultCalendar.set(Calendar.MONTH,bundle.getInt("month"));
                    resultCalendar.set(Calendar.YEAR,bundle.getInt("year"));

                    durationInMinutes = bundle.getInt("durationInMinutes");
                    if (durationInMinutes<0){
                        durationInMinutes = DEFAULT_MINUTES;
                    }
                }
                break;
        }
    }

    private void alertForEmptyFields() {
        longToast(getActivity(), R.string.empty_request_field_message);
    }

    private void alertForRequestNotCreated() {
        longToast(getActivity(), R.string.unable_to_create_request);
    }

    private class CreateRequestTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                ConnectivityUtility.makeHttpPostRequest(REQUEST_URL, currRequest.getJSONKeyValuePairs());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("ERROR","Unable to create request.");
                cancel(true);
            }
            return null;
        }

        @Override
        protected void onCancelled(String result) {
            Fragment browse = new BrowseFragment();
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.mainFrame, browse);
            ft.commit();
            alertForRequestNotCreated();
        }

        @Override
        protected void onPostExecute(String result) {
            Fragment myRequests = new MyRequestsFragment();
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.mainFrame, myRequests);
            ft.addToBackStack("my_requests");
            ft.commit();
            longToast(getActivity(), R.string.created_request);
        }
    }
}