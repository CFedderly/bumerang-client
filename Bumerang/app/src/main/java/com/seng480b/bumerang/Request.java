package com.seng480b.bumerang;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

class Request {

    enum RequestType {
        BORROW(0), LEND(1);

        private int num;

        private static Map<Integer, RequestType> map = new HashMap<>();

        static {
            for (RequestType req : RequestType.values()) {
                map.put(req.num, req);
            }
        }

        RequestType(final int number) { num = number; }

        public int getValue() {
            return num;
        }

        public static RequestType valueOf(int number) {
            return map.get(number);
        }
    }

    private int userId;
    private String title;
    private String description;
    private Calendar expiryTime;
    private int distance;
    private RequestType requestType;

    Request(int userId,
            String title,
            String description,
            int durationHours,
            int durationMinutes,
            int distanceMeters,
            RequestType requestType) {
        this.userId = userId;
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
            this.userId = obj.getInt("user_id");
            this.title = obj.getString("title");
            this.description = obj.getString("description");
            this.distance = obj.getInt("distance");

            int minutesToExpiry = obj.getInt("duration");
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, minutesToExpiry);
            this.expiryTime = cal;

            this.requestType = RequestType.valueOf(obj.getInt("request_type"));
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

    int getUserId(){
        return this.userId;
    }

    public String getTitle() { return this.title; }

    public String getDescription() { return this.description; }

    HashMap<String, String> getJSONKeyValuePairs() {
        HashMap<String, String> keyValue = new HashMap<>();
        keyValue.put("title", title);
        keyValue.put("user_id", String.valueOf(userId));
        keyValue.put("description", description);
        keyValue.put("distance", String.valueOf(distance));
        keyValue.put("duration", String.valueOf(getMinutesUntilExpiry()));
        keyValue.put("request_type", String.valueOf(requestType.getValue()));
        return keyValue;
    }

    static ArrayList<Request> filterRequestsByType(ArrayList<Request> original, RequestType type) {
        ArrayList<Request> newList = new ArrayList<>();
        for (Request request : original) {
            if (request.requestType == type) {
                newList.add(request);
            }
        }
        return newList;
    }

    static ArrayList<Request> getListOfRequestsFromJSON(String json) {
        ArrayList<Request> requests = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(json);
            JSONArray results = obj.getJSONArray("results");
            for (int i = 0; i < results.length() ; i++) {
                JSONObject result = results.getJSONObject(i).getJSONObject("request");
                requests.add(new Request(result.toString()));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return requests;
    }
}