package com.example.questify.domain.usecase.user;

import com.example.questify.UserSession;
import com.example.questify.data.repository.UserRepository;
import com.example.questify.domain.model.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

public class GetUserUseCase {

    private final UserRepository userRepository;
    private final UserSession userSession;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Inject
    public GetUserUseCase(UserRepository userRepository, UserSession userSession) {
        this.userRepository = userRepository;
        this.userSession = userSession;
    }

    public User execute() {
        try {
            return executor.submit(() ->
                    userRepository.getUserSync(userSession.getUserGlobalId())
            ).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
            // todo
        }
    }
}
