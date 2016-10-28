package com.seng480b.bumerang;

import android.os.AsyncTask;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by SPAR_Robin on 2016-10-27.
 */

public class Logout extends AsyncTask{


    @Override
    protected Object doInBackground(Object[] params) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        if (AccessToken.getCurrentAccessToken() == null) {
            return null; // already logged out
        }
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                .Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                LoginManager.getInstance().logOut();
            }
        }).executeAsync();
        return null;
    }

    //@Override
    //protected void onPostExecute(Intent intent){
        //Sets the page back to the login page so when the user logs out they're forced to log back in
        //Intent intent = new Intent(this, MainActivity.class);
      //  startActivity(intent);
    //}



}
