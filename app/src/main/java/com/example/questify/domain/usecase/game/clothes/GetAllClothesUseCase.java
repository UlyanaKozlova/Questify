package com.example.questify.domain.usecase.game.clothes;

import com.example.questify.data.repository.ClothingRepository;
import com.example.questify.domain.model.Clothing;

import java.util.List;

import javax.inject.Inject;

public class GetAllClothesUseCase {
    private final ClothingRepository clothingRepository;

    @Inject
    public GetAllClothesUseCase(ClothingRepository clothingRepository) {
        this.clothingRepository = clothingRepository;
    }

    public List<Clothing> execute() {
        return clothingRepository.getAll();
    }
}
