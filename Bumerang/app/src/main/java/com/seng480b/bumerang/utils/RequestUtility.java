package com.seng480b.bumerang.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.seng480b.bumerang.BuildConfig;
import com.seng480b.bumerang.interfaces.AsyncTaskHandler;

import java.io.IOException;

public class RequestUtility<T extends AsyncTaskHandler> {
    private static final String DELETE_REQUEST_URL = BuildConfig.SERVER_URL + "/request/";
    private static final String REQUEST_URL_BY_USER = BuildConfig.SERVER_URL + "/requests/user/";
    private static final String REQUEST_URL_BY_RECENT = BuildConfig.SERVER_URL + "/requests/recent/";

    private T container;

    public RequestUtility(T container) {
        this.container = container;
    }

    public DeleteRequestTask deleteRequest(Context context, int requestId) {
        String deleteRequestUrlWithId = DELETE_REQUEST_URL + requestId;
        if (ConnectivityUtility.checkNetworkConnection(context)) {
            DeleteRequestTask deleteRequestTask = new DeleteRequestTask(container);
            deleteRequestTask.execute(deleteRequestUrlWithId.trim());
            return deleteRequestTask;
        }
        return null;
    }

    public GetRequestsTask getRequestsByRecent(Context context) {
       return getRequestsByRecent(context, 100);
    }

    private GetRequestsTask getRequestsByRecent(Context context, int numRequests) {
        String getRequestUrlRecent = REQUEST_URL_BY_RECENT + numRequests;
        return callGetRequest(context, getRequestUrlRecent);
    }

    public GetRequestsTask getRequestsFromUser(Context context, int userId) {
        String getRequestUrlWithId = REQUEST_URL_BY_USER + userId;
        return callGetRequest(context, getRequestUrlWithId);
    }

    private GetRequestsTask callGetRequest(Context context, String url) {
        if (ConnectivityUtility.checkNetworkConnection(context)) {
            GetRequestsTask requestsTask = new GetRequestsTask(container);
            requestsTask.execute(url.trim());
            return requestsTask;
        }
        return null;
    }

    public class DeleteRequestTask extends AsyncTask<String, Void, String> {
        private T container;

        DeleteRequestTask(T container) {
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
                result = ConnectivityUtility.makeHttpDeleteRequest(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("ERROR", "Unable to delete request");
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

    public class GetRequestsTask extends AsyncTask<String, Void, String> {
        private T container;

        GetRequestsTask(T container) {
            this.container = container;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            container.beforeAsyncTask();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                return ConnectivityUtility.makeHttpGetRequest(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("ERROR", "Unable to retrieve requests");
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
