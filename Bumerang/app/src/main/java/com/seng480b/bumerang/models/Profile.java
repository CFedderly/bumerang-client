package com.seng480b.bumerang.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;

public class Profile implements Serializable {
    private int userId;
    private long facebookId;
    private String deviceId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String description;
    private String tags = "";
    private int karma;

    public Profile(int userId,
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

    public Profile(String JSONString) throws JSONException {
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

    public String getFirstName() { return this.firstName; }

    public String getLastName() { return this.lastName; }

    public String getPhoneNumber() { return this.phoneNumber; }

    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getTags() { return this.tags; }

    public long getFacebookId() { return this.facebookId; }

    public int getKarma() { return this.karma; }

    public int getUserId() { return this.userId; }

    public void setUserId(int id) { this.userId = id; }

    public String getDescription() { return this.description; }

    public void setDescription(String description) { this.description = description; }

    public String getDeviceId() { return this.deviceId; }

    public void setDeviceId(String deviceId ) { this.deviceId = deviceId; }

    public HashMap<String, String> getJSONKeyValuePairs() {
        HashMap<String, String> keyValue = new HashMap<>();
        keyValue.put("facebook_id", String.valueOf(facebookId));
        keyValue.put("device_id", deviceId);
        keyValue.put("phone_number", phoneNumber);
        keyValue.put("first_name", firstName);
        keyValue.put("last_name", lastName);
        keyValue.put("description", description);
        return keyValue;
    }

    /* Can specify the data members to be put into json if not all are required */
    public HashMap<String,String> getJSONKeyValuePairs(String... fields) {
        HashMap<String,String> keyValue = new HashMap<>();
        for (String field : fields) {
            switch(field) {
                case "facebook_id":
                    keyValue.put("facebook_id", String.valueOf(facebookId));
                    break;
                case "deviceId":
                    keyValue.put("device_id", deviceId);
                    break;
                case "phoneNumber":
                    keyValue.put("phone_number", phoneNumber);
                    break;
                case "firstName":
                    keyValue.put("first_name", firstName);
                    break;
                case "lastName":
                    keyValue.put("last_name", lastName);
                    break;
                case "description":
                    keyValue.put("description", description);
                    break;
                default:
                    Log.e("ERROR", "Invalid field provided: " + field);
                    return null;
            }
        }
        return keyValue;
    }
}
