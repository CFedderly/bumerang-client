package com.seng480b.bumerang;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/** SPLASH SCREEN **/

public class SplashLoaderActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, MainActivity.class); //forwards you to the mainactivity
        startActivity(intent);
        finish();
    }
}
