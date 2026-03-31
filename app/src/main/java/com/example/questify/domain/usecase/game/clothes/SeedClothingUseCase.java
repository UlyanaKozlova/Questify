package com.example.questify.domain.usecase.game.clothes;


import com.example.questify.R;
import com.example.questify.data.repository.ClothingRepository;
import com.example.questify.domain.model.Clothing;

import javax.inject.Inject;

public class SeedClothingUseCase {

    private final ClothingRepository clothingRepository;
    private final static int CLOTHING_COUNT = 4;

    @Inject
    public SeedClothingUseCase(ClothingRepository clothingRepository) {
        this.clothingRepository = clothingRepository;
    }

    public void execute() {
        int existingCount = clothingRepository.getAll().size();
        if (existingCount >= CLOTHING_COUNT) {
            return;
        }

        Object[][] items = {
                {"Шляпа", 50, R.drawable.pet_hat},
                {"Шарф", 30, R.drawable.pet_scarf},
                {"Очки", 40, R.drawable.pet_glasses}
        };

        for (Object[] item : items) {
            String name = (String) item[0];
            int price = (int) item[1];
            int imageResId = (int) item[2];
            boolean exists = clothingRepository.getAll().stream()
                    .anyMatch(c -> c.getName().equals(name));

            if (!exists) {
                Clothing clothing = new Clothing(name, price, imageResId);
                clothingRepository.save(clothing);
            }
        }
    }
}