package com.example.questify.domain.usecase.game.clothes;

import com.example.questify.data.repository.PetClothingRefRepository;
import com.example.questify.data.repository.PetRepository;
import com.example.questify.data.repository.UserRepository;
import com.example.questify.domain.model.Clothing;
import com.example.questify.domain.model.Pet;
import com.example.questify.domain.model.PetClothingRef;
import com.example.questify.domain.model.User;

import javax.inject.Inject;

public class BuyClothingUseCase {

    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final PetClothingRefRepository petClothingRefRepository;

    @Inject
    public BuyClothingUseCase(UserRepository userRepository,
                              PetRepository petRepository,
                              PetClothingRefRepository petClothingRefRepository) {
        this.userRepository = userRepository;
        this.petRepository = petRepository;
        this.petClothingRefRepository = petClothingRefRepository;
    }

    public boolean execute(Clothing clothing) {
        User user = userRepository.getUser();
        Pet pet = petRepository.getPet();

        if (user.getCoins() < clothing.getPrice()) {
            return false;
        }

        user.setCoins(user.getCoins() - clothing.getPrice());
        userRepository.update(user);

        petClothingRefRepository.save(new PetClothingRef(pet.getGlobalId(), clothing.getGlobalId()));
        return true;
    }
}