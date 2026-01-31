package com.example.questify.domain.usecase.game.pet;

import com.example.questify.data.repository.PetClothingRefRepository;
import com.example.questify.data.repository.PetRepository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

public class InitPetUseCase {
    private final PetRepository petRepository;
    private final PetClothingRefRepository petClothingRefRepository;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Inject
    public InitPetUseCase(PetRepository petRepository,
                          PetClothingRefRepository petClothingRefRepository) {
        this.petRepository = petRepository;
        this.petClothingRefRepository = petClothingRefRepository;
    }

    public void execute() {
        executorService.execute(() -> {
            petRepository.ensureLocalPetExists();
            petClothingRefRepository.ensureLocalClothingExists();
        });
    }
}
