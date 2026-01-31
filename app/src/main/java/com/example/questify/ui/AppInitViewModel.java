package com.example.questify.ui;

import androidx.lifecycle.ViewModel;

import com.example.questify.domain.usecase.game.clothes.InitClothingUseCase;
import com.example.questify.domain.usecase.game.pet.InitPetUseCase;
import com.example.questify.domain.usecase.user.InitUserUseCase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AppInitViewModel extends ViewModel {

    @Inject
    public AppInitViewModel(InitUserUseCase initUserUseCase,
                            InitPetUseCase initPetUseCase,
                            InitClothingUseCase initClothingUseCase) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            initClothingUseCase.execute();
            initUserUseCase.execute();
            initPetUseCase.execute();
        });
        executor.close();
    }
}
