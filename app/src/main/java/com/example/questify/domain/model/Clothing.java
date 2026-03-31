package com.example.questify.domain.model;

import java.util.Objects;
import java.util.UUID;

public class Clothing {

    private String globalId;
    private String name;
    private int price;
    private int imageResId;
    private long updatedAt;

    public Clothing(String globalId, String name, int price, int imageResId, long updatedAt) {
        this.globalId = globalId;
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
        this.updatedAt = updatedAt;
    }

    public Clothing(String name, int price, int imageResId) {
        this.globalId = UUID.randomUUID().toString();
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
    }

    public Clothing() {
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Clothing clothing = (Clothing) o;
        return Objects.equals(globalId, clothing.globalId);
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }
    public int getImageResId() {
        return imageResId;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}