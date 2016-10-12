package com.seng480b.bumerang;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.HttpURLConnection;

public class Connectivity {

    public static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager conMan = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return conMan.getActiveNetworkInfo();
    }

    public static boolean checkNetworkConnection(Context context) {
        NetworkInfo netInfo = Connectivity.getNetworkInfo(context);
        if (netInfo != null) {
            System.out.println(netInfo.isConnected());
            return netInfo.isConnected();
        } else {
            return false;
        }
    }

    public static String httpGet(String myUrl) throws IOException {
        String result = "";
        URL url = new URL(myUrl);
        HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
        urlCon.connect();
        result = streamToString(urlCon.getInputStream());
        return result;
    }

    public static String httpPut(String myUrl, String myJSON) throws IOException {
        String result = "";
        URL url = new URL(myUrl);
        HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
        urlCon.setRequestMethod("PUT");
        OutputStreamWriter out = new OutputStreamWriter(urlCon.getOutputStream());
        out.write(myJSON);
        out.close();
        result = streamToString(urlCon.getInputStream());
        System.out.println(result);
        return result;
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
