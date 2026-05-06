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
// todo
        Object[][] items = {
                {"black dress", 120, R.drawable.cat_in_black_dress},
                {"green suit", 30, R.drawable.cat_in_green_suit},
                {"sport suit", 40, R.drawable.cat_in_sport_suit},
                {"brown coat", 60, R.drawable.cat_with_brown_coat},
                {"corset_dress", 50, R.drawable.cat_with_corset_dress},
                {"denim overalls", 30, R.drawable.cat_with_denim_overalls},
                {"evening dress", 70, R.drawable.cat_with_evening_dress},
                {"jeans and t-shirt", 25, R.drawable.cat_with_jeans_and_tshirt},
                {"leather pants and blue sweater", 30, R.drawable.cat_with_leather_pants_blue_sweater},
                {"pants and shirt", 35, R.drawable.cat_with_pants_and_shirt},
                {"pink dress", 90, R.drawable.cat_with_pink_dress},
                {"red skirt and black top", 30, R.drawable.cat_with_red_skirt_black_top},
                {"shorts and tanktop", 45, R.drawable.cat_with_shorts_and_tanktop},
                {"white t-shirt and jeans", 40, R.drawable.cat_with_white_t_shirt_and_jeans}
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