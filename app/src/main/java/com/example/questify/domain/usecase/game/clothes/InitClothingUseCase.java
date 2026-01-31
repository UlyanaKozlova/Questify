package com.example.questify.domain.usecase.game.clothes;

import com.example.questify.data.repository.ClothingRepository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

public class InitClothingUseCase {
    private final ClothingRepository clothingRepository;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Inject
    public InitClothingUseCase(ClothingRepository clothingRepository) {
        this.clothingRepository = clothingRepository;
    }

    public void execute() {
        executorService.execute(clothingRepository::ensureLocalClothingExists);
    }
}
