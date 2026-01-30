package com.example.questify.ui;

import androidx.lifecycle.ViewModel;

import com.example.questify.domain.usecase.user.InitUserUseCase;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AppInitViewModel extends ViewModel {

    @Inject
    public AppInitViewModel(InitUserUseCase initUserUseCase) {
        initUserUseCase.execute();
    }
}
