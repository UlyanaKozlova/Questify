package com.example.questify.domain.usecase.user;

import com.example.questify.data.repository.UserRepository;
import com.example.questify.domain.model.User;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

public class UpdateUserUseCase {
    private final UserRepository userRepository;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Inject
    public UpdateUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void execute(User user) {
        executor.execute(() -> userRepository.update(user));
    }
}
