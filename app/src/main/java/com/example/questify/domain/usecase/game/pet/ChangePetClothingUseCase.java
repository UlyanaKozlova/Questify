package com.example.questify.domain.usecase.game.pet;

import com.example.questify.data.repository.PetRepository;
import com.example.questify.domain.model.Clothing;
import com.example.questify.domain.model.Pet;

import javax.inject.Inject;

public class ChangePetClothingUseCase {

    private final PetRepository petRepository;

    @Inject
    public ChangePetClothingUseCase(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public void execute(Clothing clothing) {
        Pet pet = petRepository.getPet();
        if (pet != null) {
            pet.setCurrentClothingGlobalId(clothing.getGlobalId());
            petRepository.update(pet);
        }
    }
}