package com.seng480b.bumerang;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

class Request {

    enum RequestType {
        BORROW,
        LEND
    }

    private String user;
    private String title;
    private String description;
    private Calendar expiryTime;
    private int distance;
    private RequestType requestType;

    Request(String title,
                   String description,
                   int durationHours,
                   int durationMinutes,
                   int distanceMeters,
                   RequestType requestType) {
        // TODO: Associate a request with a particular user
        // TODO: store the type of request (borrow, lend)
        this.user = "";
        this.title = title;
        this.description = description;
        this.distance = distanceMeters;
        this.requestType = requestType;

        // add hours and minutes to current time
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, durationMinutes);
        cal.add(Calendar.HOUR, durationHours);
        this.expiryTime = cal;
    }

    public Request(String JSONString) {
        try {
            JSONObject obj = new JSONObject(JSONString);
            this.user = "";
            this.title = obj.getString("title");
            this.description = obj.getString("description");
            this.distance = obj.getInt("distance");

            int minutesToExpiry = obj.getInt("duration");
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, minutesToExpiry);
            this.expiryTime = cal;
        } catch (JSONException e) {
            Log.e("ERROR", "Unable to create profile object from JSON string");
            e.printStackTrace();
        }
    }

    public int getMinutesUntilExpiry() {
        Calendar now = Calendar.getInstance();
        long millisUntilExpiry = expiryTime.getTimeInMillis() - now.getTimeInMillis();
        return (int) TimeUnit.MINUTES.convert(millisUntilExpiry, TimeUnit.MILLISECONDS);
    }

    public String getUser(){
        return this.user;
    }

    public String getTitle() { return this.title; }

    public String getDescription() { return this.description; }


    HashMap<String, String> getJSONKeyValuePairs() {
        HashMap<String, String> keyValue = new HashMap<>();
        keyValue.put("title", title);
        keyValue.put("description", description);
        keyValue.put("distance", String.valueOf(distance));
        keyValue.put("duration", String.valueOf(getMinutesUntilExpiry()));
        return keyValue;
    }
}