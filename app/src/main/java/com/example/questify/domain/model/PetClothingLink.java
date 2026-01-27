package com.example.questify.domain.model;


public class PetClothingLink {

    private String petGlobalId;
    private String clothingGlobalId;

    public PetClothingLink(String petGlobalId, String clothingGlobalId) {
        this.petGlobalId = petGlobalId;
        this.clothingGlobalId = clothingGlobalId;
    }

    public String getPetGlobalId() {
        return petGlobalId;
    }

    public void setPetGlobalId(String petGlobalId) {
        this.petGlobalId = petGlobalId;
    }

    public String getClothingGlobalId() {
        return clothingGlobalId;
    }

    public void setClothingGlobalId(String clothingGlobalId) {
        this.clothingGlobalId = clothingGlobalId;
    }
}

