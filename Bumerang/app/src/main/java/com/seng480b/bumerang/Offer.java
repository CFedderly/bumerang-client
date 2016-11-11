package com.seng480b.bumerang;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

public class Offer {
    Profile offer_profile;
    Request offerReq;

    public Offer(Profile offer_profile,
                 Request request) {
        this.offer_profile = offer_profile;
        this.offerReq = request;
    }

    public Profile getOfferProfile() { return offer_profile; }
    public Request getRequestInfo(){
        return offerReq;
    }
}
