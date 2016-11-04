package com.seng480b.bumerang;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringReader;

/* This class contains methods for sending profile data to the database */
class ProfileUtility {

    private static final String profileByUserIdUrl = BuildConfig.SERVER_URL + "/profile/";
    private static final String profileByFacebookIdUrl = BuildConfig.SERVER_URL
            + "/profile/facebookid/";
    private static final String profileByOfferReqId = BuildConfig.SERVER_URL + "/offer/";

    static boolean isFirstLogin(com.facebook.Profile fbProfile) {
        long facebookId = Long.parseLong(fbProfile.getId());
        storeProfileFromFacebookId(facebookId);
        return !UserDataCache.hasProfile();
    }

    static void storeProfileFromUserId(int userID) {
        String requestUrl = profileByUserIdUrl + String.valueOf(userID).trim();
        try {
            new LoadProfileTask().execute(requestUrl).get();
        } catch (Exception e) {
            // TODO: Error handle pls.
        }
    }

    private static void storeProfileFromFacebookId(long facebookId) {
        String requestUrl = profileByFacebookIdUrl + String.valueOf(facebookId).trim();
        try {
            new LoadProfileTask().execute(requestUrl).get();
        } catch (Exception e) {
            // TODO: Error handle pls.
        }
    }

    static void storeRecentUserFromUserId(int userID) {
        String requestUrl = profileByUserIdUrl + String.valueOf(userID).trim();
        try {
            new LoadRecentUserTask().execute(requestUrl).get();
        } catch (Exception e) {
            // TODO: Error handle pls.
        }

    }

    /* Returns null if HTTP response was not OK, else returns JSON string for profile record */
    static class LoadProfileTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            try {
                result = Connectivity.makeHttpGetRequest(params[0]);
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
    public static class LoadRecentUserTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            try {
                String result = Connectivity.makeHttpGetRequest(params[0]);
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
                result = Connectivity.makeHttpPostRequest(params[0],
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
}

