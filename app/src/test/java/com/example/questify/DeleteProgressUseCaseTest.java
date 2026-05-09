package com.example.questify;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.questify.data.repository.PetClothingRefRepository;
import com.example.questify.data.repository.PetRepository;
import com.example.questify.data.repository.UserRepository;
import com.example.questify.domain.usecase.user.DeleteProgressUseCase;

import org.junit.Before;
import org.junit.Test;


public class DeleteProgressUseCaseTest {

    private UserRepository userRepository;
    private PetRepository petRepository;
    private PetClothingRefRepository petClothingRefRepository;
    private DeleteProgressUseCase useCase;

    @Before
    public void setUp() {
        userRepository = mock(UserRepository.class);
        petRepository = mock(PetRepository.class);
        petClothingRefRepository = mock(PetClothingRefRepository.class);
        useCase = new DeleteProgressUseCase(userRepository, petRepository, petClothingRefRepository);
    }

    @Test
    public void execute_resetsUserProgress() {
        useCase.execute();
        verify(userRepository, times(1)).resetProgress();
    }

    @Test
    public void execute_resetsPet() {
        useCase.execute();
        verify(petRepository, times(1)).resetProgress();
    }

    @Test
    public void execute_resetsPetClothingRefs() {
        useCase.execute();
        verify(petClothingRefRepository, times(1)).resetProgress();
    }
}
