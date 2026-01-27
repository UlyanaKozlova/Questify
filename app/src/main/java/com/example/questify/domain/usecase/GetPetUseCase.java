package com.example.questify.domain.usecase;

import androidx.lifecycle.LiveData;

import com.example.questify.data.repository.PetRepository;
import com.example.questify.domain.model.Pet;

public class GetPetUseCase {

    private final PetRepository petRepository;

    public GetPetUseCase(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public LiveData<Pet> execute(String userGlobalId) {
        return petRepository.getPetForUser(userGlobalId);
    }
}
