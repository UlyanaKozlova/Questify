package com.example.questify.domain.usecase.user;

import com.example.questify.data.repository.UserRepository;

import javax.inject.Inject;

public class InitUserUseCase {

    private final UserRepository userRepository;

    @Inject
    public InitUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void execute() {
        userRepository.ensureLocalUserExists();
    }
}
