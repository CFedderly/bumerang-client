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

    static boolean isFirstLogin(com.facebook.Profile fbProfile) {
        long facebookId = Long.parseLong(fbProfile.getId());
        storeProfileFromFacebookId(facebookId);
        return UserDataCache.hasProfile();
    }

    static int getUserIdOfUser() {
        com.facebook.Profile profile = com.facebook.Profile.getCurrentProfile();
        int userId = 0;
        return userId;
    }

    static void storeProfileFromFacebookId(long facebookId) {
        String requestUrl = profileByFacebookIdUrl + String.valueOf(facebookId).trim();
        new LoadProfileTask().execute(requestUrl);
    }

    static void storeProfileFromUserId(int userID) {
        String requestUrl = profileByUserIdUrl + String.valueOf(userID).trim();
        new LoadProfileTask().execute(requestUrl);
    }


    /* Returns null if HTTP response was not OK, else returns JSON string for profile record */
    public static class LoadProfileTask extends AsyncTask<String, Void, String>{

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
            if (result != null) {
                try {
                    UserDataCache.setCurrentUser(new Profile(result));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("ERROR", "Unable to parse JSON object");
                }
            }
        }
    }

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
            Log.d("DEBUG", result);
            // Update currentProfile with new userId
            UserDataCache.updateUserId(id);
        }

    }
}

