package com.seng480b.bumerang;

/**
 * Static class to store user information for the current session
 */

public final class UserDataCache {
    static private int userId = 1;
    static private com.facebook.Profile userFacebookId;

    private UserDataCache() {}

    static public int getUserId() {
        return userId;
    }

    static public void setUserId(int id) {
        userId = id;
    }

    static public com.facebook.Profile getUserFacebookId() {
        return userFacebookId;
    }

    static public void setUserFacebookId(com.facebook.Profile id) {
        userFacebookId = id;
    }
}
