package com.example.questify.domain.usecase.user;

import com.example.questify.data.repository.PetClothingRefRepository;
import com.example.questify.data.repository.PetRepository;
import com.example.questify.data.repository.UserRepository;

import javax.inject.Inject;

public class DeleteProgressUseCase {

    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final PetClothingRefRepository petClothingRefRepository;

    @Inject
    public DeleteProgressUseCase(UserRepository userRepository,
                                 PetRepository petRepository,
                                 PetClothingRefRepository petClothingRefRepository) {
        this.userRepository = userRepository;
        this.petRepository = petRepository;
        this.petClothingRefRepository = petClothingRefRepository;
    }

    public void execute() {
        userRepository.resetProgress();
        petRepository.resetProgress();
        petClothingRefRepository.resetProgress();
    }
}