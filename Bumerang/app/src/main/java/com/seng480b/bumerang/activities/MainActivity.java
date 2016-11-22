package com.seng480b.bumerang.activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.seng480b.bumerang.R;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    private FirebaseAnalytics mFirebaseAnalytics;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());


        setContentView(R.layout.activity_main);
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,

                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        loginSuccess();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
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

        boolean loggedIn = AccessToken.getCurrentAccessToken()!=null;

        //If user is already logged in automatically goes to the 'BrowseFragment page"
        if  (loggedIn) {
            Intent browse = new Intent(this, HomeActivity.class);
            startActivity(browse);
        //If user is not logged in they are taken to the Login page.
        } else{
            setContentView(R.layout.activity_main);
            callbackManager = CallbackManager.Factory.create();
            LoginManager.getInstance().registerCallback(callbackManager,

                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            loginSuccess();
                        }

                        @Override
                        public void onCancel() {
                            // App code
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            // App code
                        }
                    });
            AppEventsLogger.activateApp(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void loginSuccess() {
        Intent intent = new Intent(this, HomeActivity.class );
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));

        startActivity(intent);
    }

}
