package com.example.questify.domain.usecase;

import com.example.questify.data.repository.ClothingRepository;
import com.example.questify.data.repository.UserRepository;
import com.example.questify.domain.model.Clothing;
import com.example.questify.domain.model.User;

public class BuyClothingUseCase {
    private final ClothingRepository clothingRepository;
    private final UserRepository userRepository;

    public BuyClothingUseCase(ClothingRepository clothingRepository, UserRepository userRepository) {
        this.clothingRepository = clothingRepository;
        this.userRepository = userRepository;
    }

    public boolean execute(User user, Clothing clothing) {
        if (user.getCoins() < clothing.getPrice()) {
            return false;
        }
        user.setCoins(user.getCoins() - clothing.getPrice());
        userRepository.update(user);
        clothingRepository.update(clothing);

        return true;
    }
}
