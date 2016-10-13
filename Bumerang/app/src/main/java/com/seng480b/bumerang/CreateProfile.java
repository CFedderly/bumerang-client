package com.seng480b.bumerang;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.analytics.FirebaseAnalytics.Event;
import com.google.firebase.analytics.FirebaseAnalytics.Param;

public class CreateProfile extends FragmentActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This would be collecting data on what people are willing to lend out
        //This will need to be moved to an Activity that actually has the data
        Bundle itemsUserIsWillingToLendOut = new Bundle();
        itemsUserIsWillingToLendOut.putString(Param.ITEM_ID, "USERS_ID");
        itemsUserIsWillingToLendOut.putString(Param.ITEM_CATEGORY, "OUT");
        //This would be the array of values which the user enters they are willing to lend out
        String[] stringList = new String[10];
        itemsUserIsWillingToLendOut.putStringArray(Param.VALUE, stringList);
        FirebaseAnalytics itemsUserIsWillingToLendOutAnalytics = FirebaseAnalytics.getInstance(CreateProfile.this);
        itemsUserIsWillingToLendOutAnalytics.logEvent(Event.SELECT_CONTENT, itemsUserIsWillingToLendOut);

        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, new EditProfileFragment()).commit();
    }

}
