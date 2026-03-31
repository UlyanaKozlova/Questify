package com.example.questify.ui;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.questify.domain.usecase.game.pet.InitPetUseCase;
import com.example.questify.domain.usecase.plans.project.InitProjectUseCase;
import com.example.questify.domain.usecase.user.InitUserUseCase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AppInitViewModel extends ViewModel {

    private final MutableLiveData<Boolean> isInitialized = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    @Inject
    public AppInitViewModel(InitUserUseCase initUserUseCase,
                            InitProjectUseCase initProjectUseCase,
                            InitPetUseCase initPetUseCase) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                initUserUseCase.execute();
                initProjectUseCase.execute();
                initPetUseCase.execute();
                isInitialized.postValue(true);
            } catch (Exception e) {
                error.postValue(e.getMessage());
            }
        });
    }
}