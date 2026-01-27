package com.example.questify.domain.usecase;

import com.example.questify.data.repository.UserRepository;
import com.example.questify.domain.model.User;

public class UpdateUserStateOnTaskCompleteUseCase {

    private final UserRepository userRepository;

    public UpdateUserStateOnTaskCompleteUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void execute(User user, int rewardCoins, int rewardExp) {
        user.setCoins(user.getCoins() + rewardCoins);
        user.setLevel(user.getLevel() + rewardExp);

        userRepository.update(user);
    }
}
