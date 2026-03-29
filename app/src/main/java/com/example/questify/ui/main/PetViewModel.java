package com.example.questify.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.questify.domain.model.User;
import com.example.questify.domain.usecase.user.GetUserUseCase;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class PetViewModel extends ViewModel {

    private final LiveData<User> user;

    @Inject
    public PetViewModel(GetUserUseCase getUserUseCase) {
        this.user = getUserUseCase.executeLive();
    }

    public LiveData<User> getUser() {
        return user;
    }
}