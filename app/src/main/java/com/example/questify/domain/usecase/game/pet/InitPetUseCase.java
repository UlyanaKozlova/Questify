package com.example.questify.domain.usecase.game.pet;

import com.example.questify.data.repository.PetClothingRefRepository;
import com.example.questify.data.repository.PetRepository;
import com.example.questify.domain.usecase.game.clothes.InitClothingUseCase;

import javax.inject.Inject;

public class InitPetUseCase {

    private final PetRepository petRepository;
    private final PetClothingRefRepository petClothingRefRepository;
    private final InitClothingUseCase initClothingUseCase;

    @Inject
    public InitPetUseCase(PetRepository petRepository,
                          PetClothingRefRepository petClothingRefRepository,
                          InitClothingUseCase initClothingUseCase) {
        this.petRepository = petRepository;
        this.petClothingRefRepository = petClothingRefRepository;
        this.initClothingUseCase = initClothingUseCase;
    }

    public void execute() {
        initClothingUseCase.execute();
        petRepository.ensureLocalPetExists();
        petClothingRefRepository.ensureLocalClothingExists();
    }
}