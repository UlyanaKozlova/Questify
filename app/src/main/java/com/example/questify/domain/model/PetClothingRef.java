package com.example.questify.domain.model;


import java.util.Objects;

public class PetClothingRef {

    private String petGlobalId;
    private String clothingGlobalId;

    public PetClothingRef(String petGlobalId, String clothingGlobalId) {
        this.petGlobalId = petGlobalId;
        this.clothingGlobalId = clothingGlobalId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PetClothingRef that = (PetClothingRef) o;
        return Objects.equals(petGlobalId, that.petGlobalId)
                && Objects.equals(clothingGlobalId, that.clothingGlobalId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(petGlobalId, clothingGlobalId);
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

