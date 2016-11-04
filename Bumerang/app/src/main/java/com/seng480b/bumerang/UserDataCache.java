package com.seng480b.bumerang;

/**
 * Static class to store user information for the current session
 */

public final class UserDataCache {
    static private Profile currentUser = null;

    private UserDataCache() {}

    public static boolean hasProfile() {
        return currentUser != null;
    }

    public static Profile getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(Profile currentUser) {
        UserDataCache.currentUser = currentUser;
    }

    public static void updateUserId(int id) {
        UserDataCache.currentUser.setUserId(id);
    }
}
