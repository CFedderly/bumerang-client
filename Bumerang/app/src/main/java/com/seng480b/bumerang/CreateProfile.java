package com.seng480b.bumerang;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

public class CreateProfile extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, new EditProfileFragment()).commit();
    }
}
