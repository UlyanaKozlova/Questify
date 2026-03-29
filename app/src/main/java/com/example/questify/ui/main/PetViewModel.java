package com.example.questify.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.questify.domain.model.User;

import com.example.questify.domain.usecase.user.GetUserUseCase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class PetViewModel extends ViewModel {
    private final MutableLiveData<User> user = new MutableLiveData<>();
    ExecutorService executor = Executors.newSingleThreadExecutor();

    @Inject
    public PetViewModel(GetUserUseCase getUserUseCase) {
        executor.execute(() -> {
            User newUser = getUserUseCase.execute();
            user.postValue(newUser);
        });
    }

    public LiveData<User> getUser() {
        return user;
    }
}
