package com.example.questify.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.questify.R;
import com.example.questify.data.repository.ClothingRepository;
import com.example.questify.data.repository.PetRepository;
import com.example.questify.domain.model.Clothing;
import com.example.questify.domain.model.Pet;
import com.example.questify.domain.model.User;
import com.example.questify.domain.usecase.user.GetUserUseCase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class PetViewModel extends ViewModel {

    private static final int COINS_PER_LEVEL = 35;

    private final ClothingRepository clothingRepository;
    private final LiveData<User> user;
    private final MediatorLiveData<Integer> currentPetImageRes = new MediatorLiveData<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Inject
    public PetViewModel(GetUserUseCase getUserUseCase,
                        PetRepository petRepository,
                        ClothingRepository clothingRepository) {
        this.clothingRepository = clothingRepository;
        this.user = getUserUseCase.executeLive();

        currentPetImageRes.addSource(petRepository.getPetLive(), this::resolvePetImage);
    }

    private void resolvePetImage(Pet pet) {
        executor.execute(() -> {
            if (pet == null || pet.getCurrentClothingGlobalId() == null) {
                currentPetImageRes.postValue(R.drawable.pet_default);
                return;
            }
            Clothing clothing = clothingRepository.getByGlobalId(pet.getCurrentClothingGlobalId());
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
            long nextLevelCost = (long) (u.getLevel() + 1) * COINS_PER_LEVEL;
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
