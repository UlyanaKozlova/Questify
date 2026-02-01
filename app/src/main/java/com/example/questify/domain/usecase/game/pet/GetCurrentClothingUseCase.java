package com.example.questify.domain.usecase.game.pet;

import com.example.questify.data.repository.ClothingRepository;
import com.example.questify.data.repository.PetRepository;
import com.example.questify.domain.model.Clothing;


import javax.inject.Inject;

public class GetCurrentClothingUseCase {
    private final PetRepository petRepository;
    private final ClothingRepository clothingRepository;

    @Inject
    public GetCurrentClothingUseCase(PetRepository petRepository,
                                     ClothingRepository clothingRepository) {
        this.petRepository = petRepository;
        this.clothingRepository = clothingRepository;
    }

    public Clothing execute() {
        return clothingRepository.getByGlobalId(petRepository.getCurrentClothingGlobalId());
    }
}

