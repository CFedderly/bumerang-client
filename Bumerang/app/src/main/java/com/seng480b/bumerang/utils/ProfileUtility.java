package com.seng480b.bumerang.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.seng480b.bumerang.BuildConfig;
import com.seng480b.bumerang.exceptions.LoginException;
import com.seng480b.bumerang.models.Profile;
import com.seng480b.bumerang.models.Settings;
import com.seng480b.bumerang.utils.caching.UserDataCache;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;

/* This class contains methods for sending profile data to the database */
public class ProfileUtility {

    private static final String PROFILE_BY_USER_ID_URL = BuildConfig.SERVER_URL + "/profile/";
    private static final String PROFILE_BY_FACEBOOK_ID_URL = BuildConfig.SERVER_URL + "/profile/facebookid/";
    private static final String EDIT_PROFILE_URL = BuildConfig.SERVER_URL + "/profile/edit/";
    private static final String EDIT_PROFILE_SETTINGS_URL = BuildConfig.SERVER_URL + "/profile/settings/";

    public static boolean isFirstLogin (com.facebook.Profile fbProfile) throws Exception {
        long facebookId = Long.parseLong(fbProfile.getId());
        storeProfileFromFacebookId(facebookId);
        return !UserDataCache.hasProfile();
    }

    private static void storeProfileFromFacebookId (long facebookId) throws Exception {
        String requestUrl = PROFILE_BY_FACEBOOK_ID_URL + String.valueOf(facebookId).trim();
        String result = new LoadProfileTask().execute(requestUrl).get();
        if (result == null) {
            throw new LoginException("ERROR LOADING PROFILE");
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

    public static void updateDeviceId(){
        // Check if the current device_id is out of date with server one.
        if(!UserDataCache.getCurrentUser().getDeviceId().equals(FirebaseInstanceId.getInstance().getToken())) {
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            String result = null;
            Profile profile = UserDataCache.getCurrentUser();
            String profileEditUrlWithId = EDIT_PROFILE_URL + profile.getUserId();
            String oldDeviceId = profile.getDeviceId();
            try {
                profile.setDeviceId(refreshedToken);
                result = ProfileUtility.editDeviceId(profileEditUrlWithId);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ERROR", "Unable to update device id");
            }
            if (result != null) {
                Log.e("DEBUG", "Successfully updated device id to: " + refreshedToken);
            } else {
                profile.setDeviceId(oldDeviceId);
            }
        }
    }

    public static String loadNotificationSettings() throws Exception{
        String url = EDIT_PROFILE_SETTINGS_URL+UserDataCache.getCurrentUser().getUserId();
        return new LoadProfileSettingsTask().execute(url).get();
    }

    public static class LoadProfileSettingsTask extends  AsyncTask<String, Void, String> {
        protected String doInBackground(String... params) {
            String result = null;
            try {
                result = ConnectivityUtility.makeHttpGetRequest(params[0]);
                if (result != null) {
                    Settings setting = new Settings(result);
                    UserDataCache.getCurrentUser().setSettings(setting);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("ERROR", "Unable to parse JSON object in settings");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("ERROR", "Unable to retrieve profile record for the settings");
            }
            return result;
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

    public static String editProfile(String url) throws Exception {
        return new EditProfileTask().execute(url, "description", "phoneNumber").get();
    }

    public static String editDeviceId(String url) throws Exception {
        return new EditProfileTask().execute(url, "deviceId").get();
    }

    public static String editNotificationSettings() throws Exception{
        String url = EDIT_PROFILE_SETTINGS_URL+UserDataCache.getCurrentUser().getUserId();
        return new EditProfileSettingsTask().execute(url).get();
    }

    private static class EditProfileSettingsTask extends AsyncTask<String, Void, String>{
        protected  String doInBackground(String ... params){
            HashMap<String,String> json = UserDataCache.getCurrentUser().getSettings().getJSONKeyValuePairs();
            String result= null;
            try{
                result = ConnectivityUtility.makeHttpPostRequest(params[0], json);
                if (result != null) {
                    Log.d("DEBUG","Edited the current users profile setting using: " + json);
                }
                else{
                    Log.e("ERROR", "Unable edit the users notification settings");
                }
            }
            catch (IOException e){
                Log.e("ERROR", "Could not edit the current profile settings.");
            }
            return result;
        }
    }

    private static class EditProfileTask extends AsyncTask<String, Void, String>{
        protected String doInBackground(String... params) {
            String result = null;
            String[] keys = new String[params.length-1];
            System.arraycopy(params, 1, keys, 0, params.length-1);
            HashMap<String,String> json = UserDataCache.getCurrentUser().getJSONKeyValuePairs(keys);
            try {
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

