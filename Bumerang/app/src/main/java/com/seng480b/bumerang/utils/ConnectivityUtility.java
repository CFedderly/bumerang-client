package com.seng480b.bumerang.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.HttpResponseCache;
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
import java.net.CacheResponse;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
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
        return makeHttpRequest(requestUrl, HttpMethod.GET, null, false);
    }

    static String makeHttpGetRequestForceRefresh(String requestUrl) throws IOException {
        return makeHttpRequest(requestUrl, HttpMethod.GET, null, true);
    }

    public static String makeHttpPostRequest(String requestUrl, HashMap<String, String> params) throws IOException {
        return makeHttpRequest(requestUrl, HttpMethod.POST, params, false);
    }

    static String makeHttpDeleteRequest(String requestUrl) throws IOException {
        return makeHttpRequest(requestUrl, HttpMethod.DELETE, null, false);
    }

    private static String makeHttpRequest(String requestUrl, HttpMethod method,
                                          HashMap<String, String> params, boolean requireRefresh) throws IOException {
        String result = null;
        URI uri;
        URL url = new URL(requestUrl);
        try {
            uri = url.toURI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.e("ERROR", "Unable to parse URL to URI.");
            return null;
        }
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            connection.setReadTimeout(TIMEOUT);
            connection.setConnectTimeout(TIMEOUT);
            connection.setUseCaches(true);
            switch (method) {
                case GET:
                    if (!requireRefresh) {
                        result = getCacheData(connection, uri, method);
                    }
                    // If a cache isn't found, or we are requiring a refresh, do get request
                    if (result == null) {
                        result = httpGet(connection);
                    }
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
            Log.e("ERROR", "Invalid HTTP method");
        } finally {
            connection.disconnect();
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

        connection.setDoInput(true);
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

        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        DataOutputStream os = new DataOutputStream(connection.getOutputStream());
        os.writeBytes(postParamsToJSONString(params));
        os.flush();
        os.close();
        Log.d("DEBUG", "Post body " + postParamsToJSONString(params));
        int status = connection.getResponseCode();
        Log.d("DEBUG", "status = " + status);
        if (status == HttpURLConnection.HTTP_OK) {
            response = streamToString(connection.getInputStream());
            Log.d("DEBUG", "response = " + response);
        } else {
            String error = streamToString(connection.getErrorStream());
            Log.e("DEBUG", "response = " + error);
            throw new IOException();
        }
        Log.d("DEBUG", "status = " + status);
        return response;
    }

    private static String getCacheData(HttpURLConnection connection, URI uri,
                                       HttpMethod method) throws IOException {
        HttpResponseCache cache = HttpResponseCache.getInstalled();
        String requestMethod = method.name();
        CacheResponse cacheResponse = cache.get(uri, requestMethod,
                connection.getHeaderFields());
        if (cacheResponse != null) {
            Log.d("DEBUG", "Cache found");
            return streamToString(cacheResponse.getBody());
        }
        Log.d("DEBUG", "Cache not found");
        return null;
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
        Log.d("DEBUG", "Response: " + sb.toString());
        return sb.toString();
    }
}
