package com.example.questify.domain.usecase.user;

import com.example.questify.data.repository.UserRepository;

import javax.inject.Inject;

public class InitUserUseCase {

    private final UserRepository repository;

    @Inject
    public InitUserUseCase(UserRepository repository) {
        this.repository = repository;
    }

    public void execute() {
        repository.ensureLocalUserExists();
    }
}
