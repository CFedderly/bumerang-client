package com.seng480b.bumerang;

import android.util.Log;

/**
 * Static class to store user information for the current session
 */

public final class UserDataCache {
    static private Profile currentUser = null;
    static private Profile recentUser = null;

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

    public static void setRecentUser(Profile recentUser) {
        UserDataCache.recentUser = recentUser;
    }

    public static void updateUserId(int id) {
        UserDataCache.currentUser.setUserId(id);
    }

}
