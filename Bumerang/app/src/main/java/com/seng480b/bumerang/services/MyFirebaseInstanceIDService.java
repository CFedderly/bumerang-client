package com.seng480b.bumerang.services;

import android.util.Log;

import com.facebook.AccessToken;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.seng480b.bumerang.BuildConfig;
import com.seng480b.bumerang.models.Profile;
import com.seng480b.bumerang.utils.ProfileUtility;
import com.seng480b.bumerang.utils.UserDataCache;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String PROFILE_EDIT_URL = BuildConfig.SERVER_URL + "/profile/edit/";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String refreshedToken) {
        boolean loggedIn = AccessToken.getCurrentAccessToken()!=null;
        if (loggedIn) {
            try {
                ProfileUtility.isFirstLogin(com.facebook.Profile.getCurrentProfile());
            } catch (Exception e) {
                // this is handled below by checking for null
            }
        }

        if (UserDataCache.getCurrentUser() != null) {
            String result = null;
            Profile profile = UserDataCache.getCurrentUser();
            String profileEditUrlWithId = PROFILE_EDIT_URL + profile.getUserId();
            String oldDeviceId = profile.getDeviceId();
            try {
                profile.setDeviceId(refreshedToken);
                result = ProfileUtility.editDeviceId(profileEditUrlWithId);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ERROR", "(Service) Unable to update device id");
            }
            if (result != null) {
                Log.e("DEBUG", " (Service) Successfully updated device id to: " + refreshedToken);
            } else {
                profile.setDeviceId(oldDeviceId);
            }
        }
    }

}
