package com.example.questify.domain.usecase.game.pet;

import com.example.questify.data.repository.PetClothingRefRepository;
import com.example.questify.data.repository.PetRepository;

import javax.inject.Inject;

public class InitPetUseCase {
    private final PetRepository petRepository;
    private final PetClothingRefRepository petClothingRefRepository;

    @Inject
    public InitPetUseCase(PetRepository petRepository,
                          PetClothingRefRepository petClothingRefRepository) {
        this.petRepository = petRepository;
        this.petClothingRefRepository = petClothingRefRepository;
    }

    public void execute() {
        petRepository.ensureLocalPetExists();
        petClothingRefRepository.ensureLocalClothingExists();
    }
}
