package com.seng480b.bumerang.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.seng480b.bumerang.BuildConfig;
import com.seng480b.bumerang.models.Offer;

import java.io.IOException;
import java.util.HashMap;


public class OfferUtility {
    private static final String GET_OFFER_URL = BuildConfig.SERVER_URL + "/offer/ids/";
    private static final String CREATE_OFFER_URL = BuildConfig.SERVER_URL + "/offer/";

    public static String getOffers(Context context, int requestId) {
        String getOfferUrlWithId = GET_OFFER_URL + requestId;
        String result = null;
        if (ConnectivityUtility.checkNetworkConnection(context)) {
            try {
                result = new GetOfferTask().execute(getOfferUrlWithId.trim()).get();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return result;
    }

    public static boolean createOffer(Context context, String profileId, String requestId) {
        if (ConnectivityUtility.checkNetworkConnection(context)) {
            try {
                return new CreateOfferTask().execute(CREATE_OFFER_URL, profileId, requestId).get();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    private static class CreateOfferTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String result;
            try {
                HashMap<String, String> json = Offer.getJSONForOffer(params[1], params[2]);
                result = ConnectivityUtility.makeHttpPostRequest(params[0], json);
                Log.d("DEBUG", "Created Offer: " + result);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("ERROR", "something went wrong with the post request");
                return false;
            }
            return result != null;
        }

        @Override
        protected void onPostExecute(Boolean result) {
        }

    }

    private static class GetOfferTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            if (UserDataCache.hasProfile()) {
                try {
                    return ConnectivityUtility.makeHttpGetRequest(params[0]);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("ERROR", "Unable to retrieve requests");
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }

}
