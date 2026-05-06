package com.example.questify.domain.usecase.user;

import com.example.questify.data.repository.UserRepository;
import com.example.questify.domain.model.User;

import javax.inject.Inject;

public class UpdateUserUseCase {
    private final UserRepository userRepository;

    @Inject
    public UpdateUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void execute(User user) {
        userRepository.update(user);
    }
}
