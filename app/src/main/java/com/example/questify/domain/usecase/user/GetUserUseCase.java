package com.example.questify.domain.usecase.user;

import androidx.lifecycle.LiveData;

import com.example.questify.data.repository.UserRepository;
import com.example.questify.domain.model.User;


import javax.inject.Inject;

public class GetUserUseCase {

    private final UserRepository userRepository;

    @Inject
    public GetUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User execute() {
        return userRepository.getUser();
    }

    public LiveData<User> executeLive() {
        return userRepository.getUserLive();
    }
}
