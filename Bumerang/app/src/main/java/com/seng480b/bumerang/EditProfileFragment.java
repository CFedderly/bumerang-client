package com.seng480b.bumerang;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import java.io.IOException;

import static com.facebook.FacebookSdk.getApplicationContext;

public class EditProfileFragment extends Fragment {
    private static final String profileUrl = BuildConfig.SERVER_URL + "/profile/";
    private static final int firstNameField = R.id.editProfile_InputFirstName;
    private static final int lastNameField = R.id.editProfile_InputLastName;
    private static final int descriptionField = R.id.editProfile_InputBio;
    private static final int tagsField = R.id.editProfile_InputTags;

    private Intent forward;
    private Profile currProfile;
    private View inflatedView;

    @Override
    // Fragment Cancel = new Browse();
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        ((Home)getActivity()).setActionBarTitle("Edit Profile");



        /* make the tabs invisible */
        //crashes the app as well - unfortunately for now the tabs are over top of the edit profile page
        ViewPager mViewPager = (ViewPager) getActivity().findViewById(R.id.container);
        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.tabs);
        mViewPager.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);


        // Check if the user already has a profile
        if (Connectivity.checkNetworkConnection(getApplicationContext())) {
            new LoadProfileTask().execute(profileUrl);
        }
        // Setup for the Cancel button on screen
        Button cancelButton = (Button) inflatedView.findViewById(R.id.buttonCancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // forward = new Intent(getActivity(), Home.class );
                // startActivity(forward);
                changeFragmentToBrowse();

            }
        });

        Button createButton = (Button) inflatedView.findViewById(R.id.button5);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //forward = new Intent(getActivity(), Home.class );

                // get the text from the fields
                currProfile = new Profile(
                        ((EditText) inflatedView.findViewById(firstNameField)).getText().toString().trim(),
                        ((EditText) inflatedView.findViewById(lastNameField)).getText().toString().trim(),
                        ((EditText) inflatedView.findViewById(descriptionField)).getText().toString().trim(),
                        ((EditText) inflatedView.findViewById(tagsField)).getText().toString().trim());

                // If we are connected to the network, send profile object to server
                if (Connectivity.checkNetworkConnection(getApplicationContext())) {
                    Log.d("DEBUG", "Profile JSON to send: " + currProfile.toJSONString());
                    new CreateProfileTask().execute(profileUrl, currProfile.toJSONString());
                    //startActivity(forward);
                    changeFragmentToBrowse();
                }
                // TODO: error handling if not connected to internet
                //startActivity(forward);
                changeFragmentToBrowse();

            }
        });


        return inflatedView;
    }


    private void populateFields() {

        ((EditText) inflatedView.findViewById(firstNameField)).setText(currProfile.getFirstName());
        ((EditText) inflatedView.findViewById(lastNameField)).setText(currProfile.getLastName());
        ((EditText) inflatedView.findViewById(descriptionField)).setText(currProfile.getDescription());
        ((EditText) inflatedView.findViewById(tagsField)).setText(currProfile.getTags());

    }

    private class CreateProfileTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                Connectivity.httpPost(params[0], params[1]);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("ERROR","Unable to create profile. URL may be invalid.");
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
        }
    }

    public class LoadProfileTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                String result = Connectivity.httpGet(params[0]);
                // if response isn't empty attempt to fill the profile fields
                if (!result.equals("")) {
                    currProfile = new Profile(result);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("ERROR", "Unable to get profile.");
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            populateFields();
        }
    }

    public void changeFragmentToBrowse(){
        Fragment back = new Browse();
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFrame,back);
        ft.commit();
    }

}
