package com.seng480b.bumerang.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.seng480b.bumerang.BuildConfig;
import com.seng480b.bumerang.interfaces.AsyncTaskHandler;
import com.seng480b.bumerang.models.Offer;
import com.seng480b.bumerang.models.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class OfferUtility<T extends AsyncTaskHandler> {
    private static final String GET_OFFER_URL = BuildConfig.SERVER_URL + "/offer/ids/";
    private static final String CREATE_OFFER_URL = BuildConfig.SERVER_URL + "/offer/";

    private T container;

    public OfferUtility(T container) {
        this.container = container;
    }

    public GetOfferTask getOffers(Context context, ArrayList<Request> requests) {
        String getOfferUrlWithId = GET_OFFER_URL + getRequestIdString(requests);
        if (ConnectivityUtility.checkNetworkConnection(context)) {
            GetOfferTask getOfferTask = new GetOfferTask(container);
            getOfferTask.execute(getOfferUrlWithId.trim());
            return getOfferTask;
        }
        return null;
    }

    public CreateOfferTask createOffer(Context context, String profileId, String requestId) {
        if (ConnectivityUtility.checkNetworkConnection(context)) {
            CreateOfferTask createOfferTask = new CreateOfferTask(container);
            createOfferTask.execute(CREATE_OFFER_URL, profileId, requestId);
            return createOfferTask;
        }
        return null;
    }

    private static String getRequestIdString(ArrayList<Request> requests) {
        StringBuilder idList = new StringBuilder();
        boolean first = true;
        for (Request req : requests) {
            if (!first) {
                idList.append(",");
            } else {
                first = false;
            }
            idList.append(req.getRequestId());
        }
        return idList.toString();
    }

    public class CreateOfferTask extends AsyncTask<String, Void, String> {
        private T container;

        CreateOfferTask(T container) {
            this.container = container;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            container.beforeAsyncTask();
        }

        @Override
        protected String doInBackground(String... params) {
            String result;
            try {
                HashMap<String, String> json = Offer.getJSONForOffer(params[1], params[2]);
                result = ConnectivityUtility.makeHttpPostRequest(params[0], json);
                Log.d("DEBUG", "Created Offer: " + result);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("ERROR", "something went wrong with the post request");
                return null;
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (container!=null) {
                container.afterAsyncTask(result);
                this.container = null;
            }
        }

    }

    public class GetOfferTask extends AsyncTask<String, Void, String> {
        private T container;

        GetOfferTask(T container) {
            this.container = container;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            container.beforeAsyncTask();
        }

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
            super.onPostExecute(result);
            if (container!=null) {
                container.afterAsyncTask(result);
                this.container = null;
            }
        }
    }

}
