package com.seng480b.bumerang;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.facebook.login.widget.ProfilePictureView;

import com.google.firebase.iid.FirebaseInstanceId;

public class EditProfileFragment extends Fragment {
    private static final String profileUrl = BuildConfig.SERVER_URL + "/profile/";
    private static final String editProfileUrl = BuildConfig.SERVER_URL + "/profile/edit/";

    private static final int firstNameField = R.id.editProfile_InputFirstName;
    private static final int lastNameField = R.id.editProfile_InputLastName;
    private static final int phoneNumberField = R.id.editProfile_InputPhoneNumber;
    private static final int descriptionField = R.id.editProfile_InputBio;
    private static final int tagsField = R.id.editProfile_InputTags;

    private View inflatedView;
    private com.facebook.Profile FBProfile = com.facebook.Profile.getCurrentProfile();

    //both the create and cancel buttons redirect to the profile page
    private Fragment back = new ProfilePage();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        ((Home)getActivity()).setActionBarTitle("Edit Profile");


        /* make the tabs invisible */
        ViewPager mViewPager = (ViewPager) getActivity().findViewById(R.id.container);
        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.tabs);
        mViewPager.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);

        // Check if the user already has a profile
        if (UserDataCache.hasProfile()) {
            String profileUrlWithId = profileUrl + UserDataCache.getCurrentUser().getUserId();
            if (Connectivity.checkNetworkConnection(getActivity().getApplicationContext())) {
                // if user has profile, populate profile fields
                new ProfileUtility.LoadProfileTask().execute(profileUrlWithId);
                populateFields();
            }
        } else {
            populateFieldsFromFacebook();
        }

        // Setup for the Cancel button on screen
        Button cancelButton = (Button) inflatedView.findViewById(R.id.editProfile_ButtonCancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment();

            }
        });

        Button createButton = (Button) inflatedView.findViewById(R.id.editProfile_ButtonSave);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            EditText description = (EditText) inflatedView.findViewById(descriptionField);
            EditText phoneNumber = (EditText) inflatedView.findViewById(phoneNumberField);

            // Check that all required fields are filled
            if (isEmpty(phoneNumber)) {
                Toast.makeText(getActivity(), R.string.empty_request_field_message, Toast.LENGTH_LONG).show();
            } else {
                if (!UserDataCache.hasProfile()) {

                    // create temporary profile from fields
                    Profile tempProfile = new Profile(
                            0, Long.parseLong(FBProfile.getId()),
                            FirebaseInstanceId.getInstance().getToken(),
                            FBProfile.getFirstName(),
                            FBProfile.getLastName(),
                            phoneNumber.getText().toString().trim(),
                            description.getText().toString().trim(), 0);
                    // Set temporary profile as current profile in cache
                    UserDataCache.setCurrentUser(tempProfile);
                    // If we are connected to the network, send profile object to server
                    if (Connectivity.checkNetworkConnection(getActivity().getApplicationContext())) {
                        try {
                            new ProfileUtility.CreateProfileTask().execute(profileUrl).get();
                        } catch (Exception e) {
                            // TODO: this is a hacky solution, will need real error handling
                        }
                        changeFragment();
                    }

                } else {
                    Profile currProfile = UserDataCache.getCurrentUser();
                    //create a temp profile with the new inputted data from the editText fields
                    Profile tempProfile = new Profile(
                            currProfile.getUserId(),
                            currProfile.getFacebookId(),
                            FirebaseInstanceId.getInstance().getToken(),
                            currProfile.getFirstName(),
                            currProfile.getLastName(),
                            phoneNumber.getText().toString().trim(),
                            description.getText().toString().trim(),
                            currProfile.getKarma());
                    // cache the temp profile as the current profile
                    UserDataCache.setCurrentUser(tempProfile);
                    // update the database with the new current profile data
                    if(Connectivity.checkNetworkConnection(getActivity().getApplicationContext())) {
                        String result = null;
                        try {
                            result = new ProfileUtility.EditProfileTask().execute(editProfileUrl).get();
                        } catch (Exception e) {
                            // TODO proper error checking
                        }
                        if (result != null) {
                            Toast.makeText(getActivity(), "You profile information has been updated.", Toast.LENGTH_LONG).show();
                            Log.d("DEBUG", "The Profile was edited too " + result);
                            changeFragment();
                        } else {
                            UserDataCache.setCurrentUser(currProfile);
                            Toast.makeText(getActivity(), "Oops, something went wrong. Please try again.", Toast.LENGTH_LONG).show();
                            Log.d("DEBUG", "Oops, something went wrong. Please try again.");
                        }
                    }
                }
            }
            }
        });
        return inflatedView;
    }
    private void populateFieldsFromFacebook() {
        //grab the profile picture form FB
        ProfilePictureView profilePicture = (ProfilePictureView) inflatedView.findViewById(R.id.editProfile_ProfilePicture);
        profilePicture.setProfileId(FBProfile.getId());
        ((TextView) inflatedView.findViewById(firstNameField)).setText(FBProfile.getFirstName());
        ((TextView) inflatedView.findViewById(lastNameField)).setText(FBProfile.getLastName());
    }

    private void populateFields() {

        Profile currProfile = UserDataCache.getCurrentUser();

        ((ProfilePictureView) inflatedView.findViewById(R.id.editProfile_ProfilePicture)).setProfileId(String.valueOf(currProfile.getFacebookId()));
        ((TextView) inflatedView.findViewById(firstNameField)).setText(currProfile.getFirstName());
        ((TextView) inflatedView.findViewById(lastNameField)).setText(currProfile.getLastName());
        ((EditText) inflatedView.findViewById(descriptionField)).setText(currProfile.getDescription());
        ((EditText) inflatedView.findViewById(phoneNumberField)).setText(currProfile.getPhoneNumber());
        ((EditText) inflatedView.findViewById(tagsField)).setText(currProfile.getTags());

    }


    private static boolean isEmpty(EditText eText) {
        return eText.getText().toString().trim().length() == 0;
    }

    private void changeFragment(){
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFrame,back);
        ft.commit();
    }
}
