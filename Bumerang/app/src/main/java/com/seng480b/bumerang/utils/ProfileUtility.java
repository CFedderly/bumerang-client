package com.seng480b.bumerang.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.seng480b.bumerang.BuildConfig;
import com.seng480b.bumerang.models.Profile;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;

/* This class contains methods for sending profile data to the database */
public class ProfileUtility {

    private static final String PROFILE_BY_USER_ID_URL = BuildConfig.SERVER_URL + "/profile/";
    private static final String PROFILE_BY_FACEBOOK_ID_URL = BuildConfig.SERVER_URL + "/profile/facebookid/";

    public static boolean isFirstLogin(com.facebook.Profile fbProfile) {
        long facebookId = Long.parseLong(fbProfile.getId());
        storeProfileFromFacebookId(facebookId);
        return !UserDataCache.hasProfile();
    }

    private static void storeProfileFromFacebookId(long facebookId) {
        String requestUrl = PROFILE_BY_FACEBOOK_ID_URL + String.valueOf(facebookId).trim();
        try {
            new LoadProfileTask().execute(requestUrl).get();
        } catch (Exception e) {
            // TODO: Error handle pls.
        }
    }

    public static void storeRecentUserFromUserId(int userID) {
        String requestUrl = PROFILE_BY_USER_ID_URL + String.valueOf(userID).trim();
        try {
            new LoadRecentUserTask().execute(requestUrl).get();
        } catch (Exception e) {
            // TODO: Error handle pls.
        }

    }

    /* Returns null if HTTP response was not OK, else returns JSON string for profile record */
    public static class LoadProfileTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            try {
                result = ConnectivityUtility.makeHttpGetRequest(params[0]);
                if (result != null) {
                    Profile profile = new Profile(result);
                    UserDataCache.setCurrentUser(profile);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("ERROR", "Unable to parse JSON object");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("ERROR", "Unable to retrieve profile record ");
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
        }
    }

    /* Returns null if HTTP response was not OK, else returns JSON string for profile record */
    private static class LoadRecentUserTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            try {
                String result = ConnectivityUtility.makeHttpGetRequest(params[0]);
                if (result != null) {
                    try {
                        UserDataCache.setRecentUser(new Profile(result));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("ERROR", "Unable to parse JSON object");
                    }
                }
                return result;
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("ERROR", "Unable to retrieve profile record ");
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
        }
    }



    public static class CreateProfileTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            try {
                result = ConnectivityUtility.makeHttpPostRequest(params[0],
                        UserDataCache.getCurrentUser().getJSONKeyValuePairs());
                Log.e("DEBUG", "" + result);
                if (result != null) {
                    JSONObject json = new JSONObject(result);
                    int id = json.getInt("id");
                    UserDataCache.updateUserId(id);
                }
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
        }

    }

    public static class EditProfileTask extends AsyncTask<String, Void, String>{
        protected String doInBackground(String... params) {
            String result = null;
            try {
                HashMap<String,String> json = UserDataCache.getCurrentUser()
                        .getJSONKeyValuePairs("description", "phoneNumber", "deviceId");
                result = ConnectivityUtility.makeHttpPostRequest(params[0], json);
                if (result != null) {
                    Log.d("DEBUG","Edited the current users profile using: " + json);
                }
            } catch (IOException e) {
                Log.e("ERROR", "Could not edit the current profile .");
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
        }
    }
}
