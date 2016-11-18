package com.seng480b.bumerang.services;


import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.seng480b.bumerang.BuildConfig;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "FBInstanceID";
    private static final String Url = BuildConfig.SERVER_URL;
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // TODO: Implement this method to send any registration to your app's servers.
        //sendRegistrationToServer(refreshedToken);

    }

    public void sendRegistrationToServer(String refereshedToken) {
        // Retrieve Facebook ID of current logged in person.
        com.facebook.Profile profile = com.facebook.Profile.getCurrentProfile();
        String user_id = profile.getId();
        if (user_id != null) {
            // TODO: send the device ID to the server paired with the facebook id.
        }

    }

}
