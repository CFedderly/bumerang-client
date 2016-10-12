package com.seng480b.bumerang;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import org.json.JSONObject;

import java.io.IOException;

public class CreateProfile extends AppCompatActivity {
    private static final String profileUrl = BuildConfig.SERVER_URL + "/profile/";
    private Intent forward;
    private Profile currProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        currProfile = new Profile(
                (EditText) findViewById(R.id.inputFirstName),
                (EditText) findViewById(R.id.inputLastName),
                (EditText) findViewById(R.id.inputAbout),
                (EditText) findViewById(R.id.inputTags));
    }

    public void cancelButton(View view){
        Intent back = new Intent(this, MainActivity.class);
        startActivity(back);
    }

    public void goButton(View view) {
        forward = new Intent(this, Home.class); // NavDrawer is the "home"
        if (Connectivity.checkNetworkConnection(getApplicationContext())) {
            new CreateProfileTask().execute(profileUrl, currProfile.toJsonString());
        }
    }

    private class CreateProfileTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                return Connectivity.httpPut(params[0], params[1]);
            } catch (IOException e) {
                e.printStackTrace();
                return "Unable to create profile. URL may be invalid.";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            startActivity(forward);
        }
    }

    public class Profile {
        private EditText firstName;
        private EditText lastName;
        private EditText description;
        private EditText tags;

        public Profile(EditText firstName,
                       EditText lastName,
                       EditText description,
                       EditText tags) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.description = description;
            this.tags = tags;
        }

        public String toJsonString() {
            JSONObject json = new JSONObject();
            return "hello";
        }
    }
}
