package com.seng480b.bumerang.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.seng480b.bumerang.R;
import com.seng480b.bumerang.utils.ConnectivityUtility;
import com.seng480b.bumerang.utils.Utility;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    private LoginButton loginButton;
    private CallbackManager callbackManager;


    private FirebaseAnalytics mFirebaseAnalytics;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Makes screen fullscreen
        if (getSupportActionBar() != null){ getSupportActionBar().hide(); }

        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        callbackManager = CallbackManager.Factory.create();

        setupLoginButton();
        setupAnalytics();
        checkIfLoggedIn();

    }

    public void logoutFromFacebook(boolean manual){
        FacebookSdk.sdkInitialize(getApplicationContext());
        if (AccessToken.getCurrentAccessToken()==null){
            return; //already logged out
        }
        if(manual) {
            com.facebook.Profile p = com.facebook.Profile.getCurrentProfile();
            Utility.longToast(getApplicationContext(), getString(R.string.goodbye) +" "+ p.getFirstName()+".");
        }
        LoginManager.getInstance().logOut();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(callbackManager.onActivityResult(requestCode, resultCode, data)){
            return;
        }
    }

    public void loginSuccess() {
        Intent intent = new Intent(this, HomeActivity.class );
        startActivity(intent);
    }

    private void setupAnalytics(){
        //analytics
        AppEventsLogger.activateApp(this);
        //This should collect basic analytics as described here
        // https://codelabs.developers.google.com/codelabs/firebase-android/#11
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        // Check for Google play services API is available and updated.
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        // Check if up to date, if not display
        if (status != ConnectionResult.SUCCESS) {
            if(googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(this, status, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
        }
    }
    private void checkIfLoggedIn(){
        //check if already logged in
        boolean loggedIn = AccessToken.getCurrentAccessToken()!=null;

        //If user is already logged in automatically goes to the 'BrowseFragment page"
        if  (loggedIn) {
            // Ensure that the user can connect to the internet
            if (ConnectivityUtility.checkNetworkConnection(this) == true) {
                com.facebook.Profile p = com.facebook.Profile.getCurrentProfile();
                Utility.longToast(getApplicationContext(), getString(R.string.welcome_back) +" "+ p.getFirstName()+"!");
                Intent browse = new Intent(this, HomeActivity.class);
                startActivity(browse);
            } else {
                logoutFromFacebook(false); //true = if manually logged out
                Utility.longToast(this, getString(R.string.no_internet_connection_generic));
            }
        }
    }

    private void setupLoginButton(){
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile"));

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            private ProfileTracker mProfileTracker;

            @Override
            public void onSuccess(LoginResult loginResult) {
                if(com.facebook.Profile.getCurrentProfile() == null) {
                    mProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(com.facebook.Profile oldProfile, com.facebook.Profile newProfile) {
                            // profile2 is the new profile
                            Log.v("facebook - profile", newProfile.getFirstName());
                            mProfileTracker.stopTracking();
                            Utility.longToast(getApplicationContext(), getString(R.string.welcome) +" "+ newProfile.getFirstName()+"!");
                            loginSuccess();
                        }
                    };
                }
                else {
                    com.facebook.Profile profile = com.facebook.Profile.getCurrentProfile();
                    Log.v("facebook - profile", profile.getFirstName());
                    Utility.longToast(getApplicationContext(), getString(R.string.welcome) +" "+ profile.getFirstName()+"!");
                    loginSuccess();
                }
            }

            @Override
            public void onCancel() {
                Utility.longToast(getApplicationContext(), getString(R.string.login_cancelled));
                logoutFromFacebook(false);// true = if manually logged out
            }

            @Override
            public void onError(FacebookException error) {
                Utility.longToast(getApplicationContext(), getString(R.string.error_message));
                logoutFromFacebook(false);// true = if manually logged out
            }
        });

    }


}
