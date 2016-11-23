package com.seng480b.bumerang.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Offer {
    private int offerId;
    private Profile offerProfile;
    private Request offerReq;

    public Offer(Profile offerProfile, Request request, int id) {
        this.offerProfile = offerProfile;
        this.offerReq = request;
        this.offerId = id;
    }

    public Offer(String JSONString) {
        try {
            JSONObject obj = new JSONObject(JSONString);
            this.offerId = obj.getInt("id");
            this.offerProfile = new Profile(obj.getString("profile"));

            JSONObject request = obj.getJSONObject("request");
            this.offerReq = new Request(request.getString("request"));
        } catch (JSONException e) {
            Log.e("ERROR", "Unable to create offer object from JSON string");
            e.printStackTrace();
        }
    }

    public Profile getOfferProfile() { return offerProfile; }

    public Request getRequestInfo(){
        return offerReq;
    }

    @SuppressWarnings("unused")
    public int getOfferId() { return offerId; }

    public static ArrayList<Offer> getListOfOffersFromJSON(String json) {
        ArrayList<Offer> offers = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(json);
            JSONArray results = obj.getJSONArray("results");
            for (int i = 0; i < results.length() ; i++) {
                JSONObject result = results.getJSONObject(i).getJSONObject("offer");
                offers.add(new Offer(result.toString()));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return offers;
    }

    public static HashMap<String, String> getJSONForOffer(String profileId, String requestId) {
        HashMap<String, String> keyValue = new HashMap<>();
        keyValue.put("profile_id", profileId);
        keyValue.put("borrow_id", requestId);
        return keyValue;
    }
}
