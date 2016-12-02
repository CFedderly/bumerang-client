package com.seng480b.bumerang.fragments;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.facebook.login.widget.ProfilePictureView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.seng480b.bumerang.BuildConfig;
import com.seng480b.bumerang.activities.HomeActivity;
import com.seng480b.bumerang.models.Profile;
import com.seng480b.bumerang.utils.KarmaUtility;
import com.seng480b.bumerang.utils.ProfileUtility;
import com.seng480b.bumerang.R;
import com.seng480b.bumerang.utils.Utility;
import com.seng480b.bumerang.utils.caching.UserDataCache;
import com.seng480b.bumerang.utils.ConnectivityUtility;

import static com.seng480b.bumerang.utils.Utility.*;

public class EditProfileFragment extends Fragment {
    private static final String PROFILE_URL = BuildConfig.SERVER_URL + "/profile/";
    private static final String EDIT_PROFILE_URL = BuildConfig.SERVER_URL + "/profile/edit/";

    private static final int FIRST_NAME_FIELD = R.id.editProfile_InputFirstName;
    private static final int LAST_NAME_FIELD = R.id.editProfile_InputLastName;
    private static final int PHONE_NUMBER_FIELD = R.id.editProfile_InputPhoneNumber;
    private static final int DESCRIPTION_FIELD = R.id.editProfile_InputBio;
    private static final int TAGS_FIELD = R.id.editProfile_InputTags;

    private View inflatedView;
    private com.facebook.Profile FBProfile = com.facebook.Profile.getCurrentProfile();

    private ToggleButton notificationToggle;

    //both the create and cancel buttons redirect to the profile page
    private Fragment back = new ProfilePageFragment();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        ((HomeActivity)getActivity()).setActionBarTitle("Edit Profile");


        /* make the tabs invisible */
        ViewPager mViewPager = (ViewPager) getActivity().findViewById(R.id.container);
        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.tabs);
        mViewPager.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);

        // Check if the user has a profile
        if (UserDataCache.hasProfile()) {
            String profileUrlWithId = PROFILE_URL + UserDataCache.getCurrentUser().getUserId();
            if (ConnectivityUtility.checkNetworkConnection(getActivity().getApplicationContext())) {
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
            EditText description = (EditText) inflatedView.findViewById(DESCRIPTION_FIELD);
            EditText phoneNumber = (EditText) inflatedView.findViewById(PHONE_NUMBER_FIELD);

            if (isEmpty(phoneNumber)) {
                longToast(getActivity(), R.string.empty_phone_number_message);
            } else if (!isValidPhoneNumber(phoneNumber)) {
                longToast(getActivity(), R.string.invalid_phone_number_message);
            } else {
                if (!UserDataCache.hasProfile()) {
                    longToast(getActivity(),R.string.error_in_finding_profile);
                } else {
                    editProfile(phoneNumber, description);
                }
            }
            }
        });

        ImageButton aboutPhoneButton = (ImageButton) inflatedView.findViewById(R.id.editProfile_phoneInfoButton);
        aboutPhoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                TextView moreInfo = (TextView) inflatedView.findViewById(R.id.editProfile_phoneInfoText);
                if (moreInfo.getVisibility()==View.GONE) {
                    moreInfo.setVisibility(View.VISIBLE);
                    moreInfo.animate()
                            .alpha(1.0f)
                            .setDuration(200);
                } else {
                    moreInfo.setAlpha(0.0f);
                    moreInfo.setVisibility(View.GONE);
                }
            }
        });

        checkToggle(true);
        return inflatedView;
    }
    private void populateFieldsFromFacebook() {
        //grab the profile picture form FB
        ProfilePictureView profilePicture = (ProfilePictureView) inflatedView.findViewById(R.id.editProfile_ProfilePicture);
        profilePicture.setProfileId(FBProfile.getId());
        ((TextView) inflatedView.findViewById(FIRST_NAME_FIELD)).setText(FBProfile.getFirstName());
        ((TextView) inflatedView.findViewById(LAST_NAME_FIELD)).setText(FBProfile.getLastName());
    }

    private void populateFields() {

        Profile currProfile = UserDataCache.getCurrentUser();

        ((ProfilePictureView) inflatedView.findViewById(R.id.editProfile_ProfilePicture)).setProfileId(String.valueOf(currProfile.getFacebookId()));
        ((TextView) inflatedView.findViewById(FIRST_NAME_FIELD)).setText(currProfile.getFirstName());
        ((TextView) inflatedView.findViewById(LAST_NAME_FIELD)).setText(currProfile.getLastName());
        ((EditText) inflatedView.findViewById(DESCRIPTION_FIELD)).setText(currProfile.getDescription());
        ((EditText) inflatedView.findViewById(PHONE_NUMBER_FIELD)).setText(currProfile.getPhoneNumber());
        ((EditText) inflatedView.findViewById(TAGS_FIELD)).setText(currProfile.getTags());
    }

    private static boolean isValidPhoneNumber(EditText phoneNumber) {
        String number = editTextToString(phoneNumber);
        if (TextUtils.isEmpty(number)) {
            return false;
        }
        number = PhoneNumberUtils.stripSeparators(number);
        if (!number.equals(PhoneNumberUtils.convertKeypadLettersToDigits(number))) {
            return false;
        }
        number = PhoneNumberUtils.extractNetworkPortion(number);
        return PhoneNumberUtils.isGlobalPhoneNumber(number) && number.length() >= 10;
    }

    private static String stripPhoneNumber(EditText phoneNumber) {
        return PhoneNumberUtils.stripSeparators(editTextToString(phoneNumber));
    }

    private void changeFragment(){
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFrame,back);
        ft.commit();
    }

    
    private void createNewProfile(EditText phoneNumber, EditText description) {

        // create temporary profile from fields
        Profile tempProfile = new Profile(
                0, Long.parseLong(FBProfile.getId()),
                FirebaseInstanceId.getInstance().getToken(),
                FBProfile.getFirstName(),
                FBProfile.getLastName(),
                stripPhoneNumber(phoneNumber),
                editTextToString(description), 0);
        // Set temporary profile as current profile in cache
        UserDataCache.setCurrentUser(tempProfile);
        giveKarma();
        // If we are connected to the network, send profile object to server
        if (ConnectivityUtility.checkNetworkConnection(getActivity().getApplicationContext())) {
            try {
                new ProfileUtility.CreateProfileTask().execute(PROFILE_URL).get();
            } catch (Exception e) {
                e.printStackTrace();
                // TODO: this is a hacky solution, will need real error handling
            }
            changeFragment();
        }
    }


    private void editProfile(EditText phoneNumber, EditText description) {
        Profile currProfile = UserDataCache.getCurrentUser();

        Profile tempProfile = (Profile) deepClone(currProfile);
        tempProfile.setDescription(editTextToString(description));
        tempProfile.setPhoneNumber(stripPhoneNumber(phoneNumber));
        UserDataCache.setCurrentUser(tempProfile);

        String editProfileUrlWithId = EDIT_PROFILE_URL + UserDataCache.getCurrentUser().getUserId();
        if(ConnectivityUtility.checkNetworkConnection(getActivity().getApplicationContext())) {
            String result = null;
            try {
                result = ProfileUtility.editProfile(editProfileUrlWithId.trim());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (result != null) {
                longToast(getActivity(), R.string.updated_profile);
                Log.d("DEBUG", "Profile edited successfully");
                changeFragment();
            } else {
                // Revert to before profile was changed
                UserDataCache.setCurrentUser(currProfile);
                longToast(getActivity(), R.string.unable_to_update_profile);
                Log.d("DEBUG", "Could not edit profile.");
            }
        }
    }

    public void giveKarma(){
        int karma = new KarmaUtility().giveKarmaForFirstLogin();
        if (karma>0){
            Utility.karmaToast(getActivity(), karma);
        }
    }


    public void checkToggle(boolean check){
        notificationToggle = (ToggleButton) inflatedView.findViewById(R.id.notificationToggle);
        notificationToggle.setChecked(check);

        //attach a listener to check for changes in state
        notificationToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                //check the current state before we display the screen
                if(isChecked){
                    Toast.makeText(getActivity(), R.string.notifications_on,
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(), R.string.notifications_off,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    

}
