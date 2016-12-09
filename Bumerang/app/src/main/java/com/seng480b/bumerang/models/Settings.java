package com.seng480b.bumerang.models;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;
import java.util.HashMap;

public class Settings implements Serializable {
    private boolean requestNotifications;
    private boolean offerNotifications;

    public Settings(){
        this.requestNotifications = false;
        this.offerNotifications = true;
    }

    public Settings(boolean requestNotifications, boolean offerNotifications){
        this.requestNotifications = requestNotifications;
        this.offerNotifications = offerNotifications;
    }

    public Settings(String JSONString) throws JSONException {
        JSONObject full = new JSONObject(JSONString);
        JSONObject obj = full.getJSONObject("settings");
        this.requestNotifications = obj.getBoolean("request_notification");
        this.offerNotifications = obj.getBoolean("offer_notification");
    }

    public void setOfferNotifications(boolean offerNotifications) {
        this.offerNotifications = offerNotifications;
    }

    public void setRequestNotifications(boolean requestNotifications) {
        this.requestNotifications = requestNotifications;
    }

    public boolean isRequestNotifications() {
        return requestNotifications;
    }

    public boolean isOfferNotifications() {
        return offerNotifications;
    }

    public HashMap<String, String> getJSONKeyValuePairs(){
        HashMap<String, String> keyValue = new HashMap<>();
        keyValue.put("request_notification", String.valueOf(requestNotifications));
        keyValue.put("offer_notification", String.valueOf(offerNotifications));
        return keyValue;
    }
}