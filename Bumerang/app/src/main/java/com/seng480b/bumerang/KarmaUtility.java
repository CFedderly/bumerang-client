package com.seng480b.bumerang;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

public class KarmaUtility {
    private static final String updateKarmaUrl = BuildConfig.SERVER_URL + "/karma/";
    int lendMultiplier = 5;
    int borrowMultiplier = 2;
    int offenceMultiplier = 10;


    /**
     * gives karma to the users involved in the completion of a request
     * @param borrowerId
     * @param lenderId
     */
    public boolean distributeKarmaForRequest(int borrowerId, int lenderId){
        String a = addKarmaForLending(lenderId);
        String b = addKarmaForBorrowing(borrowerId);
        if(a == null | b == null){
            return false;
        }else{
            return true;
        }
    }
    /**
     * adds karma to the user that is lending an item
     * @param id
     * @return
     */
    public String addKarmaForLending(int id){
        String karmaUrl = updateKarmaUrl + id;
        String result = null;
        try {
            result = new UpdateKarmaTask().execute(karmaUrl).get();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR","Could not update user " + id + "'s Karma.");
            // TODO: Error handle pls.
        }
        return result;
    }

    /**
     * adds karma to the user that is borrowing an item
     * @param id
     * @return
     */
    public String addKarmaForBorrowing(int id){
        String karmaUrl = updateKarmaUrl + id;
        UserDataCache.getCurrentUser().addKarma(borrowMultiplier);
        String result = null;
        try {
            result = new UpdateKarmaTask().execute(karmaUrl).get();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR", "Could not update Current user's Karma.");
            // TODO: Error handle pls.
        }
        return result;
    }

    /**
     * removes karma from the user that commits an offence
     * @param id
     * @return
     */
    public String removeKarmaForOffence(int id){
        String karmaUrl = updateKarmaUrl + id;
        UserDataCache.getCurrentUser().removeKarma(offenceMultiplier);
        String result = null;
        try {
            result = new UpdateKarmaTask().execute(karmaUrl).get();
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: Error handle pls.
        }
        return result;
    }



    public static class UpdateKarmaTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            try{
                result = Connectivity.makeHttpPostRequest(
                        params[0],
                        UserDataCache.getCurrentUser().getJSONKeyValuePairs()
                        );
            }catch(IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
        }

    }
}
