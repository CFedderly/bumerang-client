package com.seng480b.bumerang.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.seng480b.bumerang.BuildConfig;
import com.seng480b.bumerang.models.Offer;
import com.seng480b.bumerang.models.Request;

import java.io.IOException;
import java.util.HashMap;

public class KarmaUtility {
    private static final String updateKarmaUrl = BuildConfig.SERVER_URL + "/profile/karma/edit/";
    private static final int lendMultiplier = 5;
    private static final int borrowMultiplier = 2;
    private static final int offenceMultiplier = 10;
    private static final int firstLoginKarma = 10;



    public int giveKarmaForFirstLogin(){
        String karmaUrl = updateKarmaUrl + UserDataCache.getCurrentUser().getUserId();
        UserDataCache.getCurrentUser().addKarma(firstLoginKarma);
        try {
            new UpdateKarmaTask().execute(
                    karmaUrl,
                    "add",
                    String.valueOf(firstLoginKarma)
                    ).get();
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: Error handle pls.
        }
        return firstLoginKarma;
    }

    /**
     * gives karma to the users involved in the offer
     * @param offer
     * @return
     */
    public int giveKarmaForRequest(Offer offer){
        Request.RequestType rType = offer.getRequest().getRequestType();
        if(rType== Request.RequestType.BORROW){
            addKarmaForLending(offer.getOfferProfile().getUserId());
            addKarmaForBorrowing(offer.getRequest().getUserId());
            return borrowMultiplier;

        }else if(rType== Request.RequestType.LEND){
            addKarmaForLending(offer.getRequest().getUserId());
            addKarmaForBorrowing(offer.getOfferProfile().getUserId());
            return lendMultiplier;

        }else{
            return 0;
        }
    }
    /**
     * adds karma to the user that is lending an item
     * @param id
     * @return
     */
    private void addKarmaForLending(int id){
        if(id==UserDataCache.getCurrentUser().getUserId()){
            UserDataCache.getCurrentUser().addKarma(lendMultiplier);
        }
        String karmaUrl = updateKarmaUrl + id;
        try {
            new UpdateKarmaTask().execute(
                    karmaUrl,
                    "add",
                    String.valueOf(lendMultiplier)
                    ).get();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR","Could not update user " + id + "'s Karma.");
            // TODO: Error handle pls.
        }

    }

    /**
     * adds karma to the user that is borrowing an item
     * @param id
     * @return
     */
    private void addKarmaForBorrowing(int id){
        if(id==UserDataCache.getCurrentUser().getUserId()){
            UserDataCache.getCurrentUser().addKarma(borrowMultiplier);
        }
        String karmaUrl = updateKarmaUrl + id;
        try {
            new UpdateKarmaTask().execute(
                    karmaUrl,
                    "add",
                    String.valueOf(borrowMultiplier)
                    ).get();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR","Could not update user " + id + "'s Karma.");
            // TODO: Error handle pls.
        }
    }

    /**
     * removes karma from the user that commits an offence
     * @param id
     * @return
     */
    public void removeKarmaForOffence(int id){
        if(id==UserDataCache.getCurrentUser().getUserId()){
            UserDataCache.getCurrentUser().removeKarma(offenceMultiplier);
        }
        String karmaUrl = updateKarmaUrl + id;
        try {
            new UpdateKarmaTask().execute(
                    karmaUrl,
                    "sub",
                    String.valueOf(offenceMultiplier)
                    ).get();
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: Error handle pls.
        }
    }

    private static class UpdateKarmaTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            try{
                HashMap karmaVal = new HashMap<>();
                karmaVal.put("method", params[1]);
                karmaVal.put("amount", Integer.valueOf(params[2]));
                result = ConnectivityUtility.makeHttpPostRequest(params[0], karmaVal);

            }catch(IOException e) {
                Log.e("ERROR", "Could'nt make request to the server.");
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
        }

    }
}
