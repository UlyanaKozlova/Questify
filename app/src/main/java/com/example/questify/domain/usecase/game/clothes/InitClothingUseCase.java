package com.example.questify.domain.usecase.game.clothes;

import com.example.questify.data.repository.ClothingRepository;

import javax.inject.Inject;

public class InitClothingUseCase {

    private final ClothingRepository clothingRepository;
    private final SeedClothingUseCase seedClothingUseCase;

    @Inject
    public InitClothingUseCase(ClothingRepository clothingRepository,
                               SeedClothingUseCase seedClothingUseCase) {
        this.clothingRepository = clothingRepository;
        this.seedClothingUseCase = seedClothingUseCase;
    }

    public void execute() {
        clothingRepository.ensureLocalClothingExists();
        seedClothingUseCase.execute();
    }
}