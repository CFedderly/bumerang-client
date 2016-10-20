package com.seng480b.bumerang;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.analytics.FirebaseAnalytics;

import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Arrays;
import org.json.JSONObject;
import org.json.JSONException;




public class MainActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    private FirebaseAnalytics mFirebaseAnalytics;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        Dialog errorDialog = null;


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

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    String name;

    public void loginSuccess() {
        Intent createProfile = new Intent(this, CreateProfile.class );
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        // We will need to retrieve the profile values from facebook and
        // pass the values to EditText
        // String profileInfo = GET FACEBOOK DATA VALUES HERE
        // intent.putExtra(PROFILE_VALUES, profileInfo);

        //grabbing the profile information from facebook
        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        // Application code

                        //testing getting the information form facebook
                        // just logs the name and id
                        try {

                            //send the json file to the database
                            //we might want to just have this in the editProfile page

                            String  name=object.getString("name");
                            Log.d("user name ", name);

                            String id = object.getString("id");
                            Log.d("user id ",id);

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name");
        request.setParameters(parameters);
        request.executeAsync();


        startActivity(createProfile);
    }

    /* called when skip button is tapped */
    public void skipLoginScreen(View view) {
        Intent intent = new Intent(this, CreateProfile.class);
        startActivity(intent);
    }
}
