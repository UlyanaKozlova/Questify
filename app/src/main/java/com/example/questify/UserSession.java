package com.example.questify;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserSession {

    private String userGlobalId = "1";
    @Inject
    public UserSession() { }
    public String getUserGlobalId() {
        return userGlobalId;
    }

    public void setUserGlobalId(String userGlobalId) {
        this.userGlobalId = userGlobalId;
    }
}
