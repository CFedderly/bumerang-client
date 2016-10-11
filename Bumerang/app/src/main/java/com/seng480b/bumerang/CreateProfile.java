package com.seng480b.bumerang;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.analytics.FirebaseAnalytics.Event;
import com.google.firebase.analytics.FirebaseAnalytics.Param;

public class CreateProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
    }

    public void cancelButton(View view){
        Intent back = new Intent(this, MainActivity.class);
        startActivity(back);
    }

    public void goButton(View view) {
        Intent forward = new Intent(this, NavDrawer.class); // NavDrawer is the "home"
        // Get input from all the text fields here.
        // This would be collecting data on what people are willing to lend out
        Bundle itemsUserIsWillingToLendOut = new Bundle();
        itemsUserIsWillingToLendOut.putString( Param.ITEM_ID, "USERS_ID" );
        itemsUserIsWillingToLendOut.putString( Param.ITEM_CATEGORY, "OUT" );
        //This would be the array of values which the user enters they are willing to lend out
        String[] stringList = new String[10];
        itemsUserIsWillingToLendOut.putStringArray( Param.VALUE,stringList );
        FirebaseAnalytics itemsUserIsWillingToLendOutAnalytics = FirebaseAnalytics.getInstance( CreateProfile.this );
        itemsUserIsWillingToLendOutAnalytics.logEvent( Event.SELECT_CONTENT, itemsUserIsWillingToLendOut );

        startActivity(forward);
    }
}
