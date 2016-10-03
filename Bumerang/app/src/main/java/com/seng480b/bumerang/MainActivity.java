package com.seng480b.bumerang;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        AppEventsLogger.activateApp(this);
    }


    /* called when skip button is tapped */
    public void skipLoginScreen(View view) {
        Intent intent = new Intent(this, NavDrawer.class);
        startActivity(intent);
    }


}
