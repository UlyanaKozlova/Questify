package com.example.questify.domain.usecase.game.pet;

import com.example.questify.data.repository.PetRepository;
import com.example.questify.domain.model.Clothing;

import javax.inject.Inject;

public class ChangePetClothingUseCase {

    private final PetRepository petRepository;

    @Inject
    public ChangePetClothingUseCase(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public void execute(Clothing clothing) {
        petRepository.getPet().setCurrentClothingGlobalId(clothing.getGlobalId());
        petRepository.update(petRepository.getPet());
    }
}