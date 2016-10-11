package com.seng480b.bumerang;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class CreateProfile extends AppCompatActivity {
	private FirebaseAnalytics mFirebaseAnalytics;
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
        startActivity(forward);
    }
}
