package com.seng480b.bumerang;

/**
 * Static class to store user information for the current session
 */

final class UserDataCache {
    static private Profile currentUser = null;
    static private Profile recentUser = null;

    private UserDataCache() {}

    static boolean hasProfile() {
        return currentUser != null;
    }

    static Profile getCurrentUser() {
        return currentUser;
    }

    static Profile getRecentUser() {
        return recentUser;
    }

    static void setCurrentUser(Profile currentUser) {
        UserDataCache.currentUser = currentUser;
    }

    static void setRecentUser(Profile recentUser) {
        UserDataCache.recentUser = recentUser;
    }

    static void updateUserId(int id) {
        UserDataCache.currentUser.setUserId(id);
    }

}
