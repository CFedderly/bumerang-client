package com.seng480b.bumerang.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.seng480b.bumerang.BuildConfig;
import com.seng480b.bumerang.R;
import com.seng480b.bumerang.models.Profile;
import com.seng480b.bumerang.utils.ConnectivityUtility;
import com.seng480b.bumerang.utils.ProfileUtility;
import com.seng480b.bumerang.utils.caching.UserDataCache;

import static com.seng480b.bumerang.utils.Utility.editTextToString;
import static com.seng480b.bumerang.utils.Utility.isEmpty;
import static com.seng480b.bumerang.utils.Utility.longToast;

public class WelcomeWalkthroughActivity extends AppCompatActivity {

    private static final String PROFILE_URL = BuildConfig.SERVER_URL + "/profile/";

    private com.facebook.Profile FBProfile = com.facebook.Profile.getCurrentProfile();

    private boolean last = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_walkthrough);

        final Button button = (Button) findViewById(R.id.welcomeWalkthrough_buttonContinue);
        button.setOnClickListener(new View.OnClickListener() {
            TextView stepCounter, title, moreInfo, bioLabel, tagsLabel;
            EditText phoneNumber, bio, tags;
            ImageView imageFirst, imageSecond;
            @Override
            public void onClick(View v) {
                if (!last){
                    //grab all the items in the current view
                    stepCounter = (TextView) findViewById(R.id.walkthrough_stepCounter);
                    title = (TextView) findViewById(R.id.walkthrough_title);
                    moreInfo = (TextView) findViewById(R.id.walkthrough_moreInfo);

                    phoneNumber = (EditText) findViewById(R.id.walkthrough_InputPhoneNumber);
                    imageFirst = (ImageView) findViewById((R.id.walkthrough_picture));

                    bioLabel = (TextView) findViewById(R.id.walkthrough_LabelBio);
                    bio = (EditText) findViewById(R.id.walkthrough_InputBio);
                    tagsLabel = (TextView) findViewById(R.id.walkthrough_LabelTags);
                    tags = (EditText) findViewById(R.id.walkthrough_InputTags);
                    imageSecond = (ImageView) findViewById(R.id.walkthrough_second_pic);

                    //make sure user input a valid phone number
                    if (isEmpty(phoneNumber)) {
                        longToast(WelcomeWalkthroughActivity.this, R.string.empty_phone_number_message);
                    } else if (!isValidPhoneNumber(phoneNumber)) {
                        longToast(WelcomeWalkthroughActivity.this, R.string.invalid_phone_number_message);
                    } else {
                        if (!UserDataCache.hasProfile()) {
                            //they have no profile (good!); prepare next screen
                            last = true;
                            stepCounter.setText("2/2");
                            title.setText(R.string.walkthrough_extra_info_title);
                            moreInfo.setText(R.string.extra_info_explanation);

                            phoneNumber.setVisibility(View.GONE);
                            imageFirst.setVisibility(View.GONE);

                            bioLabel.setVisibility(View.VISIBLE);
                            bio.setVisibility(View.VISIBLE);
                            tagsLabel.setVisibility(View.VISIBLE);
                            tags.setVisibility(View.VISIBLE);
                            imageSecond.setVisibility(View.VISIBLE);

                        } else {
                            //they already have an account somehow, so redirect to browse page
                            finish();
                        }
                    }
                } else {
                    //this is the last welcome screen
                    createNewProfile(phoneNumber, bio);
                    Intent browse = new Intent(WelcomeWalkthroughActivity.this, HomeActivity.class);
                    startActivity(browse);
                    finish();
                }
            }
        });
    }

    private static String stripPhoneNumber(EditText phoneNumber) {
        return PhoneNumberUtils.stripSeparators(editTextToString(phoneNumber));
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

    //note: transferred this over from edit profile fragment
    private void createNewProfile(EditText phoneNumber, EditText description) {

        // create temporary profile from fields
        Profile tempProfile = new Profile(
                0, Long.parseLong(FBProfile.getId()),
                FirebaseInstanceId.getInstance().getToken(),
                FBProfile.getFirstName(),
                FBProfile.getLastName(),
                stripPhoneNumber(phoneNumber),
                editTextToString(description), 10);
        // Set temporary profile as current profile in cache
        UserDataCache.setCurrentUser(tempProfile);
        // If we are connected to the network, send profile object to server
        if (ConnectivityUtility.checkNetworkConnection(WelcomeWalkthroughActivity.this.getApplicationContext())) {
            try {
                new ProfileUtility.CreateProfileTask().execute(PROFILE_URL).get();
            } catch (Exception e) {
                e.printStackTrace();
                // TODO: this is a hacky solution, will need real error handling
            }
        }
    }
}
