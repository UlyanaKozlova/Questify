package com.example.questify.domain.usecase.user;

import com.example.questify.data.repository.UserRepository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

public class InitUserUseCase {

    private final UserRepository repository;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Inject
    public InitUserUseCase(UserRepository repository) {
        this.repository = repository;
    }

    public void execute() {
        executor.execute(repository::ensureLocalUserExists);
    }
}
