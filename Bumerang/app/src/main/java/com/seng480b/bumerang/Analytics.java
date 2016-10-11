package com.seng480b.bumerang;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.analytics.FirebaseAnalytics.Event;
import com.google.firebase.analytics.FirebaseAnalytics.Param;

public class Analytics extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //I wasn't sure where to set this so I just set it to the splash screen
        setContentView(R.layout.activity_splash_screen);

//The dream would be to eventually put the bundles where
// the data is pushed to from 'CreateRequest'.
// So eventually this file would actually be deleted and the
// contents spread out to where they would collect data

        // This would be collecting data on how long people set the timer for
        Bundle lengthOfRequest = new Bundle();
        lengthOfRequest.putString( Param.ITEM_ID, "time" );
        lengthOfRequest.putString( Param.ITEM_CATEGORY, "Timer" );
        //This is commented out as would need an actual value for timer.length
        //lengthOfRequest.putLong( Param.VALUE, timer.length );
        FirebaseAnalytics lengthOfRequestAnalytics = FirebaseAnalytics.getInstance( Analytics.this );
        lengthOfRequestAnalytics.logEvent( Event.SELECT_CONTENT, lengthOfRequest );

        // This would be collecting data on how how far people are
        // willing to travel to get the stuff they need
        Bundle distanceWillingToTravel = new Bundle();
        distanceWillingToTravel.putString( Param.ITEM_ID, "Distance" );
        distanceWillingToTravel.putString( Param.ITEM_CATEGORY, "Distance" );
        //This is commented out as would need an actual value for distance.length
        //distanceWillingToTravel.putLong( Param.VALUE, distance.length );
        FirebaseAnalytics distanceWillingToTravelAnalytics = FirebaseAnalytics.getInstance( Analytics.this );
        distanceWillingToTravelAnalytics.logEvent( Event.SELECT_CONTENT, distanceWillingToTravel );

        // This would be collecting data on what people are requesting
        Bundle itemBeingRequested = new Bundle();
        itemBeingRequested.putString( Param.ITEM_ID, "Request" );
        itemBeingRequested.putString( Param.ITEM_CATEGORY, "Request" );
        //This is just an example, it would have to actually take in the tag they made
        itemBeingRequested.putString( Param.VALUE, "Mac Charger" );
        FirebaseAnalytics itemBeingRequestedAnalytics = FirebaseAnalytics.getInstance( Analytics.this );
        itemBeingRequestedAnalytics.logEvent( Event.SELECT_CONTENT, itemBeingRequested);

    }


}



