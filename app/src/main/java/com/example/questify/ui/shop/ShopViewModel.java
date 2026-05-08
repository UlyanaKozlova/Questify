package com.example.questify.ui.shop;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.questify.R;
import com.example.questify.data.repository.PetRepository;
import com.example.questify.domain.model.Clothing;
import com.example.questify.domain.model.User;
import com.example.questify.domain.usecase.game.clothes.BuyClothingUseCase;
import com.example.questify.domain.usecase.game.clothes.GetAllClothesUseCase;
import com.example.questify.domain.usecase.game.pet.ChangePetClothingUseCase;
import com.example.questify.domain.usecase.game.pet.GetAllBoughtClothesUseCase;
import com.example.questify.domain.usecase.game.pet.GetCurrentClothingUseCase;
import com.example.questify.domain.usecase.user.GetUserUseCase;
import com.example.questify.sync.SyncManager;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ShopViewModel extends ViewModel {

    private final GetAllClothesUseCase getAllClothesUseCase;
    private final GetAllBoughtClothesUseCase getAllBoughtClothesUseCase;
    private final GetCurrentClothingUseCase getCurrentClothingUseCase;
    private final BuyClothingUseCase buyClothingUseCase;
    private final ChangePetClothingUseCase changePetClothingUseCase;
    private final SyncManager syncManager;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final MediatorLiveData<List<Clothing>> allClothes = new MediatorLiveData<>();
    private final MutableLiveData<List<Clothing>> boughtClothes = new MutableLiveData<>();
    private final MediatorLiveData<Clothing> currentClothing = new MediatorLiveData<>();
    private final MutableLiveData<Integer> currentIndex = new MutableLiveData<>();
    private final LiveData<User> user;
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public LiveData<List<Clothing>> getAllClothes() {
        return allClothes;
    }

    public LiveData<List<Clothing>> getBoughtClothes() {
        return boughtClothes;
    }

    public LiveData<Clothing> getCurrentClothing() {
        return currentClothing;
    }

    public LiveData<Integer> getCurrentIndex() {
        return currentIndex;
    }

    public LiveData<User> getUser() {
        return user;
    }

    public LiveData<String> getError() {
        return error;
    }

    @Inject
    public ShopViewModel(GetAllClothesUseCase getAllClothesUseCase,
                         GetAllBoughtClothesUseCase getAllBoughtClothesUseCase,
                         GetCurrentClothingUseCase getCurrentClothingUseCase,
                         BuyClothingUseCase buyClothingUseCase,
                         ChangePetClothingUseCase changePetClothingUseCase,
                         GetUserUseCase getUserUseCase,
                         PetRepository petRepository,
                         SyncManager syncManager) {
        this.getAllClothesUseCase = getAllClothesUseCase;
        this.getAllBoughtClothesUseCase = getAllBoughtClothesUseCase;
        this.getCurrentClothingUseCase = getCurrentClothingUseCase;
        this.buyClothingUseCase = buyClothingUseCase;
        this.changePetClothingUseCase = changePetClothingUseCase;
        this.syncManager = syncManager;
        this.user = getUserUseCase.executeLive();

        allClothes.addSource(petRepository.getPetLive(), pet -> reloadClothes());
        currentClothing.addSource(petRepository.getPetLive(), pet -> reloadCurrentClothing());

        reloadClothes();
        reloadCurrentClothing();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdownNow();
    }

    private void reloadClothes() {
        executor.execute(() -> {
            List<Clothing> clothes = getAllClothesUseCase.execute();
            allClothes.postValue(clothes);

            List<Clothing> bought = getAllBoughtClothesUseCase.execute();
            boughtClothes.postValue(bought);

            if (clothes != null && !clothes.isEmpty() && currentIndex.getValue() == null) {
                currentIndex.postValue(0);
            }
        });
    }

    private void reloadCurrentClothing() {
        executor.execute(() -> {
            Clothing current = getCurrentClothingUseCase.execute();
            currentClothing.postValue(current);
        });
    }

    public Clothing getCurrentDisplayClothing() {
        List<Clothing> clothes = allClothes.getValue();
        Integer index = currentIndex.getValue();
        if (clothes == null || clothes.isEmpty() || index == null) {
            return null;
        }
        return clothes.get(Math.min(index, clothes.size() - 1));
    }

    public void next() {
        List<Clothing> clothes = allClothes.getValue();
        if (clothes == null || clothes.isEmpty()) {
            return;
        }
        Integer index = currentIndex.getValue();
        if (index == null) {
            index = 0;
        }
        int newIndex = (index + 1) % clothes.size();
        currentIndex.setValue(newIndex);
    }

    public void previous() {
        List<Clothing> clothes = allClothes.getValue();
        if (clothes == null || clothes.isEmpty()) {
            return;
        }
        Integer index = currentIndex.getValue();
        if (index == null) {
            index = 0;
        }
        int newIndex = (index - 1 + clothes.size()) % clothes.size();
        currentIndex.setValue(newIndex);
    }

    public void setCurrentIndex(int index) {
        List<Clothing> clothes = allClothes.getValue();
        if (clothes != null && index >= 0 && index < clothes.size()) {
            currentIndex.setValue(index);
        }
    }

    public boolean isBought(Clothing clothing) {
        List<Clothing> bought = boughtClothes.getValue();
        if (bought == null || clothing == null) {
            return false;
        }
        return bought.stream().anyMatch(c -> c != null && c.getGlobalId() != null && c.getGlobalId().equals(clothing.getGlobalId()));
    }

    public boolean isCurrentClothing(Clothing clothing) {
        Clothing current = currentClothing.getValue();
        if (current == null || clothing == null) {
            return false;
        }
        return current.getGlobalId() != null && current.getGlobalId().equals(clothing.getGlobalId());
    }

    public void buyCurrentClothing(Context context) {
        Clothing clothing = getCurrentDisplayClothing();
        if (clothing == null) {
            return;
        }

        if (isBought(clothing)) {
            error.postValue(context.getString(R.string.shop_already_bought_error));
            return;
        }

        executor.execute(() -> {
            boolean success = buyClothingUseCase.execute(clothing);
            if (success) {
                changePetClothingUseCase.execute(clothing);
                List<Clothing> bought = getAllBoughtClothesUseCase.execute();
                boughtClothes.postValue(bought);
                error.postValue(null);
                syncManager.scheduleSyncSoon();
            } else {
                error.postValue(context.getString(R.string.shop_insufficient_coins));
            }
        });
    }

    public void wearCurrentClothing(Context context) {
        Clothing clothing = getCurrentDisplayClothing();
        if (clothing == null) {
            return;
        }

        if (!isBought(clothing)) {
            error.postValue(context.getString(R.string.shop_buy_first_error));
            return;
        }

        executor.execute(() -> {
            changePetClothingUseCase.execute(clothing);
            error.postValue(null);
            syncManager.scheduleSyncSoon();
        });
    }
}
