package com.seng480b.bumerang.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.seng480b.bumerang.R;

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

public class ConnectivityUtility {

    private static final int TIMEOUT = 15000;

    private enum HttpMethod {
        GET,
        POST,
        DELETE
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

    public static boolean checkNetworkAndShowAlert(Context context, int alertTextResource) {
        boolean isConnected = checkNetworkConnection(context);
        if (!isConnected) {
            Toast.makeText(context, alertTextResource, Toast.LENGTH_LONG).show();
        }
        return isConnected;
    }

    public static boolean checkNetworkConnection(Context context) {
        NetworkInfo netInfo = ConnectivityUtility.getNetworkInfo(context);
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

    public static String makeHttpPostRequest(String requestUrl, HashMap<String, String> params) throws IOException {
        return makeHttpRequest(requestUrl, HttpMethod.POST, params);
    }

    static String makeHttpDeleteRequest(String requestUrl) throws IOException {
        return makeHttpRequest(requestUrl, HttpMethod.DELETE, null);
    }

    private static String makeHttpRequest(String requestUrl, HttpMethod method, HashMap<String, String> params) throws IOException {
        String result;
        try {
            URL url = new URL(requestUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(TIMEOUT);
            connection.setConnectTimeout(TIMEOUT);
            switch (method) {
                case GET:
                    result = httpGet(connection);
                    break;
                case POST:
                    result = httpPost(connection, params);
                    break;
                case DELETE:
                    result = httpDelete(connection);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid HTTP method: " + method);
            }
        } catch (IllegalArgumentException e) {
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
        } else if (status == HttpURLConnection.HTTP_NOT_FOUND) {
            result = "";
        }
        Log.d("DEBUG", "Response from HTTP GET: " + result);
        Log.d("DEBUG", "Response code: " + status);
        return result;
    }

    private static String httpDelete(HttpURLConnection connection) throws IOException {
        String result = null;

        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json" );
        connection.setRequestMethod("DELETE");
        connection.connect();
        int status = connection.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            result = streamToString(connection.getInputStream());
        }
        Log.d("DEBUG", "Response from HTTP DELETE: " + result);
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
