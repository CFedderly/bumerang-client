package com.seng480b.bumerang;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

class Profile {
    private int userId;
    private long facebookId;
    private String deviceId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String description;
    private String tags = "";
    private int karma;

    Profile(int userId,
                   long facebookId,
                   String deviceId,
                   String firstName,
                   String lastName,
                   String phoneNumber,
                   String description,
                   int karma) {
        this.userId = userId;
        this.facebookId = facebookId;
        this.deviceId = deviceId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.description = description;
        this.karma = karma;
    }

    Profile(String JSONString) throws JSONException {
        JSONObject full = new JSONObject(JSONString);
        JSONObject obj = full.getJSONObject("profile");
        this.userId = obj.getInt("id");
        this.facebookId = obj.getLong("facebook_id");
        this.firstName = obj.getString("first_name");
        this.lastName = obj.getString("last_name");
        this.description = obj.getString("description");
        this.deviceId = obj.getString("device_id");
        this.phoneNumber = obj.getString("phone_number");
        this.karma = obj.getInt("karma");
    }

    String getFirstName() { return this.firstName; }

    String getLastName() { return this.lastName; }

    String getPhoneNumber() { return this.phoneNumber; }

    String getTags() { return this.tags; }

    long getFacebookId() { return this.facebookId; }

    int getKarma() { return this.karma; }

    int getUserId() { return this.userId; }

    void setUserId(int id) { this.userId = id; }

    public String getDescription() { return this.description; }

    HashMap<String, String> getJSONKeyValuePairs() {
        HashMap<String, String> keyValue = new HashMap<>();
        keyValue.put("facebook_id", String.valueOf(facebookId));
        keyValue.put("device_id", deviceId);
        keyValue.put("phone_number", phoneNumber);
        keyValue.put("first_name", firstName);
        keyValue.put("last_name", lastName);
        keyValue.put("description", description);
        return keyValue;
    }

    static ArrayList<Profile> getListOfProfilesFromJSON(String json) {
        ArrayList<Profile> profiles = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(json);
            JSONArray results = obj.getJSONArray("results");
            for (int i = 0; i < results.length() ; i++) {
                JSONObject result = results.getJSONObject(i).getJSONObject("profile");
                profiles.add(new Profile(result.toString()));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return profiles;
    }
}
