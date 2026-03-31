package com.example.questify.ui.shop;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.questify.R;
import com.example.questify.domain.model.Clothing;
import com.example.questify.domain.model.User;
import com.example.questify.domain.usecase.game.clothes.BuyClothingUseCase;
import com.example.questify.domain.usecase.game.clothes.GetAllClothesUseCase;
import com.example.questify.domain.usecase.game.pet.ChangePetClothingUseCase;
import com.example.questify.domain.usecase.game.pet.GetAllBoughtClothesUseCase;
import com.example.questify.domain.usecase.game.pet.GetCurrentClothingUseCase;
import com.example.questify.domain.usecase.user.GetUserUseCase;

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
    private final GetUserUseCase getUserUseCase;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final MutableLiveData<List<Clothing>> allClothes = new MutableLiveData<>();
    private final MutableLiveData<List<Clothing>> boughtClothes = new MutableLiveData<>();
    private final MutableLiveData<Clothing> currentClothing = new MutableLiveData<>();
    private final MutableLiveData<Integer> currentIndex = new MutableLiveData<>();
    private final MutableLiveData<User> user = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public LiveData<List<Clothing>> getAllClothes() {
        return allClothes;
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
                         GetUserUseCase getUserUseCase) {
        this.getAllClothesUseCase = getAllClothesUseCase;
        this.getAllBoughtClothesUseCase = getAllBoughtClothesUseCase;
        this.getCurrentClothingUseCase = getCurrentClothingUseCase;
        this.buyClothingUseCase = buyClothingUseCase;
        this.changePetClothingUseCase = changePetClothingUseCase;
        this.getUserUseCase = getUserUseCase;

        loadData();
    }

    private void loadData() {
        executor.execute(() -> {
            List<Clothing> clothes = getAllClothesUseCase.execute();
            allClothes.postValue(clothes);

            List<Clothing> bought = getAllBoughtClothesUseCase.execute();
            boughtClothes.postValue(bought);

            Clothing current = getCurrentClothingUseCase.execute();
            currentClothing.postValue(current);

            User currentUser = getUserUseCase.execute();
            user.postValue(currentUser);
        });
    }

    public Clothing getCurrentDisplayClothing() {
        List<Clothing> clothes = allClothes.getValue();
        Integer index = currentIndex.getValue();
        if (clothes == null || clothes.isEmpty() || index == null) {
            return null;
        }
        return clothes.get(index);
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
        return bought.stream().anyMatch(c -> c.getGlobalId().equals(clothing.getGlobalId()));
    }

    public boolean isCurrentClothing(Clothing clothing) {
        Clothing current = currentClothing.getValue();
        if (current == null || clothing == null) {
            return false;
        }
        return current.getGlobalId().equals(clothing.getGlobalId());
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
                List<Clothing> clothes = getAllClothesUseCase.execute();
                allClothes.postValue(clothes);

                List<Clothing> bought = getAllBoughtClothesUseCase.execute();
                boughtClothes.postValue(bought);

                Clothing current = getCurrentClothingUseCase.execute();
                currentClothing.postValue(current);

                User currentUser = getUserUseCase.execute();
                user.postValue(currentUser);

                error.postValue(null);
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

            List<Clothing> clothes = getAllClothesUseCase.execute();
            allClothes.postValue(clothes);

            List<Clothing> bought = getAllBoughtClothesUseCase.execute();
            boughtClothes.postValue(bought);

            Clothing newCurrent = getCurrentClothingUseCase.execute();
            currentClothing.postValue(newCurrent);

            User currentUser = getUserUseCase.execute();
            user.postValue(currentUser);

            error.postValue(null);

            Integer index = currentIndex.getValue();
            if (index != null) {
                currentIndex.postValue(index);
            }
        });
    }
}