package com.seng480b.bumerang;

import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;

import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.analytics.FirebaseAnalytics;

import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;

import java.util.Arrays;
import org.json.JSONObject;
import org.json.JSONException;




public class MainActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        //This should collect basic analytics as described here
        // https://codelabs.developers.google.com/codelabs/firebase-android/#11
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        //the FacebookSdk needs a 'second' to load the AccessToken
        //This should eventually be turned into an async task
        SystemClock.sleep(100);
        boolean loggedIn = AccessToken.getCurrentAccessToken()!=null;

        //If user is already logged in automatically goes to the 'Browse page"
        if  (loggedIn) {
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
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
        Intent intent = new Intent(this, Home.class );
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        // We will need to retrieve the profile values from facebook and
        // pass the values to EditText
        // String profileInfo = GET FACEBOOK DATA VALUES HERE
        // intent.putExtra(PROFILE_VALUES, profileInfo);

        //grabbing the profile information from facebook
        //grabFBinfo();

        startActivity(intent);
    }

    public void grabFBinfo(){
        Profile profile = Profile.getCurrentProfile();
        String  name=profile.getName();
        Log.d("user name ", name);

        String id =profile.getId();
        Log.d("user id ", id);

    }
    public void skipLoginScreen(View view) {
        //Intent intent = new Intent(this, CreateProfile.class);
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }

}
