package com.example.questify.domain.model;

import java.util.Objects;

public class User {
    private String globalId;
    private String username;
    private String passwordHash;

    private int level;
    private long coins;

    private long updatedAt;

    public User(String globalId,
                String username,
                String passwordHash,
                int level,
                long coins,
                long updatedAt) {
        this.globalId = globalId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.level = level;
        this.coins = coins;
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(globalId, user.globalId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(globalId);
    }

    public String getGlobalId() {
        return globalId;
    }

    public void setGlobalId(String globalId) {
        this.globalId = globalId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getCoins() {
        return coins;
    }

    public void setCoins(long coins) {
        this.coins = coins;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
