package com.example.questify.domain.usecase;

import androidx.lifecycle.LiveData;

import com.example.questify.data.repository.UserRepository;
import com.example.questify.domain.model.User;

public class GetUserStateUseCase {

    private final UserRepository userRepository;

    public GetUserStateUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<User> execute(String userGlobalId) {
        return userRepository.getUser(userGlobalId);
    }
}
