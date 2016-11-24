package com.seng480b.bumerang.activities;

import android.content.Intent;
import android.net.http.HttpResponseCache;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

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
import com.seng480b.bumerang.utils.ConnectivityUtility;
import com.seng480b.bumerang.utils.Utility;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final long CACHE_SIZE = 10 * 1024 * 1024;

    CallbackManager callbackManager;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeCaching();

        //Makes screen fullscreen
        getSupportActionBar().hide();
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
                        Utility.longToast(getApplicationContext(), getString(R.string.error_message));
                        logoutFromFacebook();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Utility.longToast(getApplicationContext(), getString(R.string.error_message));
                        logoutFromFacebook();
                    }
                });
        AppEventsLogger.activateApp(this);
        //This should collect basic analytics as described here
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
            // Ensure that the user can connect to the internet
            if (ConnectivityUtility.checkNetworkConnection(this)) {
                Intent browse = new Intent(this, HomeActivity.class);
                startActivity(browse);
            } else {
                logoutFromFacebook();
                Utility.longToast(this, getString(R.string.no_internet_connection_generic));
            }
        }
    }

    public void logoutFromFacebook(){
        FacebookSdk.sdkInitialize(getApplicationContext());
        if (AccessToken.getCurrentAccessToken()==null){
            return; //already logged out
        }
        LoginManager.getInstance().logOut();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {
        super.onStop();
        HttpResponseCache cache = HttpResponseCache.getInstalled();
        if (cache != null) {
            cache.flush();
        }
    }

    public void loginSuccess() {
        Intent intent = new Intent(this, HomeActivity.class );
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));

        startActivity(intent);
    }

    private void initializeCaching() {
        try {
            File httpCacheDir = new File(getCacheDir(), "http");
            HttpResponseCache.install(httpCacheDir, CACHE_SIZE);
        } catch (IOException e) {
            Log.e("ERROR", "HTTP response cache installation failed:" + e);
        }
    }

}
