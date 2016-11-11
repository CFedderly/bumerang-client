package com.seng480b.bumerang;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Offer {
    int offerID;
    int profile_ID;
    int request_ID;
    Request offerReq;

    public Offer(int offerID,
                 int profile_ID,
                 int request_ID,
                 Request request) {
        this.offerID = offerID;
        this.profile_ID = profile_ID;
        this.request_ID = request_ID;
        this.offerReq = request;
    }

    public Offer(String JSONString, Request request) {
        try {
            JSONObject obj = new JSONObject(JSONString);
            this.offerID = obj.getInt("id");
            this.profile_ID = obj.getInt("profile_id");
            this.request_ID = obj.getInt("request_id");
            this.offerReq = request;
        } catch (JSONException e) {
            Log.e("ERROR", "Unable to create profile object from JSON string");
            e.printStackTrace();
        }
    }

    public int getOfferID() {
        return offerID;
    }

    public int getProfile_ID() {
        return profile_ID;
    }

    public int getRequest_ID() {
        return request_ID;
    }

    public Request getRequestInfo(){
        return offerReq;
    }
}
