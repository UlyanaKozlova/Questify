package com.example.questify.domain.usecase.game.pet;


import com.example.questify.data.repository.PetRepository;
import com.example.questify.domain.model.Pet;

public class GetPetUseCase {

    private final PetRepository petRepository;

    public GetPetUseCase(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public Pet execute() {
        return petRepository.getPet();
    }
}
