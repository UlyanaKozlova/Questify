package com.example.questify.domain.usecase.game.pet;

import com.example.questify.data.repository.ClothingRepository;
import com.example.questify.data.repository.PetClothingRefRepository;
import com.example.questify.domain.model.Clothing;
import com.example.questify.domain.model.PetClothingRef;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class GetAllBoughtClothesUseCase {
    private final PetClothingRefRepository petClothingRefRepository;
    private final ClothingRepository clothingRepository;

    @Inject
    public GetAllBoughtClothesUseCase(PetClothingRefRepository petClothingRefRepository,
                                      ClothingRepository clothingRepository) {
        this.petClothingRefRepository = petClothingRefRepository;
        this.clothingRepository = clothingRepository;
    }

    public List<Clothing> execute() {
        List<PetClothingRef> petClothingRefs = petClothingRefRepository.getAll();
        List<Clothing> clothes = new ArrayList<>();
        for (PetClothingRef petClothingRef : petClothingRefs) {
            clothes.add(clothingRepository.getByGlobalId(petClothingRef.getClothingGlobalId()));
        }
        return clothes;
    }
}
