package com.example.questify.domain.usecase.game.clothes;

import com.example.questify.data.repository.ClothingRepository;


import javax.inject.Inject;

public class InitClothingUseCase {
    private final ClothingRepository clothingRepository;

    @Inject
    public InitClothingUseCase(ClothingRepository clothingRepository) {
        this.clothingRepository = clothingRepository;
    }

    public void execute() {
        clothingRepository.ensureLocalClothingExists();
    }
}