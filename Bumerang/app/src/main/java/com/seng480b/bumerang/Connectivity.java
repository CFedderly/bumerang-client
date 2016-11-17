package com.seng480b.bumerang;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

class Connectivity {

    private static final int Timeout = 15000;

    private enum HttpMethod {
        GET,
        POST
    }

    private static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager conMan = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return conMan.getActiveNetworkInfo();
    }

    @SuppressWarnings("unused")
    public static boolean checkNetworkAndShowGenericAlert(Context context) {
        return checkNetworkAndShowAlert(context, R.string.no_internet_connection_generic);
    }

    static boolean checkNetworkAndShowAlert(Context context, int alertTextResource) {
        boolean isConnected = checkNetworkConnection(context);
        if (!isConnected) {
            Toast.makeText(context, alertTextResource, Toast.LENGTH_LONG).show();
        }
        return isConnected;
    }

    static boolean checkNetworkConnection(Context context) {
        NetworkInfo netInfo = Connectivity.getNetworkInfo(context);
        if (netInfo != null) {
            Log.d("DEBUG", "Network connection: " + netInfo.isConnected());
            return netInfo.isConnected();
        } else {
            return false;
        }
    }

    static String makeHttpGetRequest(String requestUrl) throws IOException {
        return makeHttpRequest(requestUrl, HttpMethod.GET, null);
    }

    static String makeHttpPostRequest(String requestUrl, HashMap<String, String> params) throws IOException {
        return makeHttpRequest(requestUrl, HttpMethod.POST, params);
    }

    private static String makeHttpRequest(String requestUrl, HttpMethod method, HashMap<String, String> params) throws IOException {
        String result = "";
        try {
            URL url = new URL(requestUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(Timeout);
            connection.setConnectTimeout(Timeout);
            switch (method) {
                case GET:
                    result = httpGet(connection);
                    break;
                case POST:
                    result = httpPost(connection, params);
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    private static String httpGet(HttpURLConnection connection) throws IOException {
        String result = null;
        connection.connect();
        int status = connection.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            result = streamToString(connection.getInputStream());
        }
        Log.d("DEBUG", "Response from HTTP GET: " + result);
        Log.d("DEBUG", "Response code: " + status);
        return result;
    }

    private static String httpPost(HttpURLConnection connection, HashMap<String, String> params) throws IOException {
        String response;

        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        DataOutputStream os = new DataOutputStream(connection.getOutputStream());
        os.writeBytes(postParamsToJSONString(params));
        os.flush();
        os.close();

        int status = connection.getResponseCode();

        if (status == HttpURLConnection.HTTP_OK) {
            String line;
            response = "";
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = br.readLine()) != null) {
                response += line;
            }
            Log.d("DEBUG", "response = " + response);
        } else {
            Log.d("DEBUG", "status = " + status);
            throw new IOException();
        }
        Log.d("DEBUG", "status = " + status);
        return response;
    }

    private static String postParamsToJSONString(HashMap<String, String> params) {
        try {
            JSONObject json = new JSONObject();
            for (Map.Entry<String, String> pair : params.entrySet()) {
                json.put(pair.getKey(), pair.getValue());
            }
            return json.toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private static String streamToString(InputStream inStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
        StringBuilder sb = new StringBuilder();
        String inString;

        while((inString = br.readLine()) != null) {
            sb.append(inString);
        }
        br.close();
        return sb.toString();
    }
}
