package com.seng480b.bumerang.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.seng480b.bumerang.R;

public class WelcomeActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        final Button button = (Button) findViewById(R.id.welcome_buttonContinue);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                //build intent for 'adding phone number screen, etc' (the rest of the welcoming screens)
                Intent welcomeScreens = new Intent(getApplicationContext(),WelcomeWalkthroughActivity.class);
                startActivity(welcomeScreens);
            }

        });

    }

}

