package com.seng480b.bumerang;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

public class getOfferTask {
    public static class offerTask extends AsyncTask<String, Void, String> {
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
