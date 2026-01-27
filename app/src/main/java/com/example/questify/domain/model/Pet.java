package com.example.questify.domain.model;

public class Pet {

    private String globalId;
    private String userGlobalId;
    private String currentClothingGlobalId;

    private long updatedAt;

    public String getCurrentClothingGlobalId() {
        return currentClothingGlobalId;
    }

    public void setCurrentClothingGlobalId(String currentClothingGlobalId) {
        this.currentClothingGlobalId = currentClothingGlobalId;
    }

    public String getGlobalId() {
        return globalId;
    }

    public void setGlobalId(String globalId) {
        this.globalId = globalId;
    }

    public String getUserGlobalId() {
        return userGlobalId;
    }

    public void setUserGlobalId(String userGlobalId) {
        this.userGlobalId = userGlobalId;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
