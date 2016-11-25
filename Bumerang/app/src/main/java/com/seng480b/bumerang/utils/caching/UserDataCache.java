package com.seng480b.bumerang.utils.caching;

import com.seng480b.bumerang.models.Offer;
import com.seng480b.bumerang.models.Profile;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Static class to store user information for the current session
 */

public final class UserDataCache {
    static private Profile currentUser = null;
    static private Profile recentUser = null;
    static private ArrayList<Offer> currentUserOffers = null;

    private UserDataCache() {}

    public static boolean hasProfile() {
        return currentUser != null;
    }

    public static Profile getCurrentUser() {
        return currentUser;
    }

    public static Profile getRecentUser() {
        return recentUser;
    }

    public static void setCurrentUser(Profile currentUser) {
        UserDataCache.currentUser = currentUser;
    }

    @SuppressWarnings("unused")
    public static void setRecentUser(Profile recentUser) {
        UserDataCache.recentUser = recentUser;
    }

    @SuppressWarnings("unused")
    public static void updateUserId(int id) {
        UserDataCache.currentUser.setUserId(id);
    }

    public static ArrayList<Offer> getOffers() { return currentUserOffers; }

    public static void setOffers(ArrayList<Offer> offers) {
        UserDataCache.currentUserOffers = offers;
    }

    public static void addOffer(Offer offer) {
        if (offer == null) {
            return;
        }
        if (UserDataCache.currentUserOffers != null) {
            UserDataCache.currentUserOffers.add(offer);
        } else {
            ArrayList<Offer> offers = new ArrayList<>();
            offers.add(offer);
            UserDataCache.setOffers(offers);
        }
    }
}
