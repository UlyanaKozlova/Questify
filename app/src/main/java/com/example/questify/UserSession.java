package com.example.questify;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserSession {

    private static final String PREF_NAME = "user_session";
    private static final String KEY_USER_GLOBAL_ID = "user_global_id";
    private static final String KEY_FIREBASE_USER_ID = "firebase_user_id";

    private String userGlobalId;
    private String firebaseUserId;

    private final SharedPreferences sharedPreferences;

    @Inject
    public UserSession(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        loadFromPreferences();
    }

    private void loadFromPreferences() {
        this.userGlobalId = sharedPreferences.getString(KEY_USER_GLOBAL_ID, null);
        this.firebaseUserId = sharedPreferences.getString(KEY_FIREBASE_USER_ID, null);
    }

    private void saveToPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (userGlobalId != null) {
            editor.putString(KEY_USER_GLOBAL_ID, userGlobalId);
        }
        if (firebaseUserId != null) {
            editor.putString(KEY_FIREBASE_USER_ID, firebaseUserId);
        }
        editor.apply();
    }

    public String getUserGlobalId() {
        if (userGlobalId == null) {
            userGlobalId = java.util.UUID.randomUUID().toString();
            saveToPreferences();
        }
        return userGlobalId;
    }

    public void setUserGlobalId(String userGlobalId) {
        this.userGlobalId = userGlobalId;
        saveToPreferences();
    }

    public void setFirebaseUserId(String firebaseUserId) {
        this.firebaseUserId = firebaseUserId;
        saveToPreferences();
    }
}