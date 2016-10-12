package com.seng480b.bumerang;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Connectivity {

    public static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager conMan = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return conMan.getActiveNetworkInfo();
    }

    public static boolean checkNetworkConnection(Context context) {
        NetworkInfo netInfo = Connectivity.getNetworkInfo(context);
        if (netInfo != null) {
            return netInfo.isConnected();
        } else {
            return false;
        }
    }
}
