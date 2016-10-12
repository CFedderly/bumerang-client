package com.seng480b.bumerang;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Profile {
    private String firstName;
    private String lastName;
    private String description;
    private String tags;

    public Profile(String firstName,
                   String lastName,
                   String description,
                   String tags) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.description = description;
        this.tags = tags;
    }

    public Profile(String JSONString) {
        try {
            JSONObject obj = new JSONObject(JSONString);
            this.firstName = obj.getString("firstname");
            this.lastName = obj.getString("lastname");
            this.description = obj.getString("description");
            this.tags = obj.getString("tags");
        } catch (JSONException e) {
            Log.e("ERROR", "Unable to create profile object from JSON string");
            e.printStackTrace();
        }
    }

    public String getFirstName() { return this.firstName; }

    public String getLastName() { return this.lastName; }

    public String getDescription() { return this.description; }

    public String getTags() { return this.tags; }

    public String toJSONString() {
        try {
            JSONObject json = new JSONObject();
            json.put("firstname", firstName);
            json.put("lastname", lastName);
            json.put("description", description);
            json.put("tags", tags);
            return json.toString();
        } catch (JSONException e) {
            Log.e("ERROR", "Unable to create JSON string from profile object");
            e.printStackTrace();
            return null;
        }
    }
}
