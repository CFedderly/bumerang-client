package com.seng480b.bumerang;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;


class OfferUtility {


    static class CreateOfferTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            try {
                HashMap<String, String> keyValue = new HashMap<>();
                keyValue.put("profile_id", params[1]);
                keyValue.put("borrow_id", params[2]);
                result = Connectivity.makeHttpPostRequest(params[0], keyValue);
                Log.d("DEBUG", "Created Offer: " + result);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("ERROR", "something went wrong with the post request");
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
        }

    }

    static class GetOfferTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            // Holds the result of the server query
            if (UserDataCache.hasProfile()) {
                try {
                    return Connectivity.makeHttpGetRequest(params[0]);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("ERROR", "Unable to retrieve requests");
                    cancel(true);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }

}
