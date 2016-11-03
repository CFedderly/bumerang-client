package com.seng480b.bumerang;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;

/* This class contains methods for sending profile data to the database */
class ProfileUtility {

    private static final String profileByUserIdUrl = BuildConfig.SERVER_URL + "/profile/";
    private static final String profileByFacebookIdUrl = BuildConfig.SERVER_URL
            + "/profile/facebookid/";
    private static Profile currProfile;

    static boolean isFirstLogin(com.facebook.Profile fbProfile) {
        long facebookId = Long.parseLong(fbProfile.getId());
        Profile existingProfile = getProfileFromFacebookId(facebookId);
        if (existingProfile != null) {
            UserDataCache.setCurrentUser(existingProfile);
            return false;
        } else {
            return true;
        }
    }

    static int getUserIdOfUser() {
        com.facebook.Profile profile = com.facebook.Profile.getCurrentProfile();
        int userId = 0;
        return userId;
    }

    static Profile getProfileFromFacebookId(long facebookId) {
        String requestUrl = profileByFacebookIdUrl + String.valueOf(facebookId).trim();
        new LoadProfileTask(new AsyncResponse() {
            @Override
            public Profile processFinish(String output) {
                currProfile = null;
                if (output != null) {
                    try {
                        currProfile = new Profile(output);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("ERROR", "Unable to parse JSON object");
                    }
                }
                return currProfile;
            }
        }).execute(requestUrl);
        return null;
    }

    public interface AsyncResponse {
        Profile processFinish(String output);
    }

    /* Returns null if HTTP response was not OK, else returns JSON string for profile record */
    public static class LoadProfileTask extends AsyncTask<String, Void, String>{

        public AsyncResponse delegate = null;

        public LoadProfileTask(AsyncResponse delegate) {
            this.delegate = delegate;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                return Connectivity.makeHttpGetRequest(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("ERROR", "Unable to retrieve profile record ");
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            delegate.processFinish(result);
        }
    }

    /* */
    public static class CreateProfileTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                return Connectivity.makeHttpPostRequest(params[0],
                        UserDataCache.getCurrentUser().getJSONKeyValuePairs());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // Parse result for user id
            int id = 0;
            // Update currentProfile with new userId
            UserDataCache.updateUserId(id);
        }

    }
}

