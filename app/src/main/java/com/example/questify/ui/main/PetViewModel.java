package com.example.questify.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.questify.R;
import com.example.questify.domain.model.Clothing;
import com.example.questify.domain.model.User;
import com.example.questify.domain.usecase.game.pet.GetCurrentClothingUseCase;
import com.example.questify.domain.usecase.user.GetUserUseCase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class PetViewModel extends ViewModel {

    private final LiveData<User> user;
    private final MutableLiveData<Integer> currentPetImageRes = new MutableLiveData<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Inject
    public PetViewModel(GetUserUseCase getUserUseCase,
                        GetCurrentClothingUseCase getCurrentClothingUseCase) {
        this.user = getUserUseCase.executeLive();
        executor.execute(() -> {
            Clothing clothing = getCurrentClothingUseCase.execute();
            if (clothing != null && clothing.getImageResId() != 0) {
                currentPetImageRes.postValue(clothing.getImageResId());
            } else {
                currentPetImageRes.postValue(R.drawable.pet_default);
            }
        });
    }

    public LiveData<User> getUser() {
        return user;
    }

    public LiveData<Long> getCoinsToNextLevel() {
        return Transformations.map(user, u -> {
            if (u == null) {
                return 0L;
            }
            long nextLevelCost = (long) (u.getLevel() + 1) * 35;
            return Math.max(0L, nextLevelCost - u.getEarnedCoins());
        });
    }

    public LiveData<Integer> getCurrentPetImageRes() {
        return currentPetImageRes;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdownNow();
    }
}
