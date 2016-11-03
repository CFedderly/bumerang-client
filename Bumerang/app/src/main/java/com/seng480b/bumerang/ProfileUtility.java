package com.seng480b.bumerang;

import com.facebook.*;

/* This class contains methods for sending profile data to the database */
class ProfileUtility {

    static boolean isFirstLogin(int userId) {
        return false;
    }

    static int getUserIdOfUser() {
        com.facebook.Profile profile = com.facebook.Profile.getCurrentProfile();
        int userId = 0;
        return userId;
    }

    static String getUserNameFromUserId(int userId) {
        return "Name name";
    }

}
