package com.example.questify.domain.usecase;

import com.example.questify.data.repository.PetRepository;
import com.example.questify.domain.model.Pet;

public class ChangePetClothingUseCase {

    private final PetRepository petRepository;

    public ChangePetClothingUseCase(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public void execute(Pet pet, String newClothingGlobalId) {
        pet.setCurrentClothingGlobalId(newClothingGlobalId);
        petRepository.update(pet);
    }
}
