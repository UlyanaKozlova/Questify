package com.example.questify.ui.advice;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.questify.R;
import com.example.questify.data.repository.ClothingRepository;
import com.example.questify.data.repository.PetRepository;
import com.example.questify.domain.model.Clothing;
import com.example.questify.domain.model.Pet;
import com.example.questify.domain.usecase.statistics.ExportStatisticsToPngUseCase;
import com.example.questify.domain.usecase.statistics.GetAdviceUseCase;
import com.example.questify.domain.usecase.statistics.GetTasksAmountUseCase;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AdviceViewModel extends ViewModel {

    private final GetAdviceUseCase getAdviceUseCase;
    private final GetTasksAmountUseCase getTasksAmountUseCase;
    private final ExportStatisticsToPngUseCase exportStatisticsToPngUseCase;
    private final ClothingRepository clothingRepository;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final MutableLiveData<String> advice = new MutableLiveData<>();
    private final MutableLiveData<Boolean> adviceLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> exportResult = new MutableLiveData<>();
    private final MediatorLiveData<Integer> petImageRes = new MediatorLiveData<>();

    public LiveData<String> getAdvice() {
        return advice;
    }

    public LiveData<Boolean> getAdviceLoading() {
        return adviceLoading;
    }

    public LiveData<Integer> getExportResult() {
        return exportResult;
    }

    public LiveData<Integer> getPetImageRes() {
        return petImageRes;
    }

    @Inject
    public AdviceViewModel(GetAdviceUseCase getAdviceUseCase,
                           GetTasksAmountUseCase getTasksAmountUseCase,
                           ExportStatisticsToPngUseCase exportStatisticsToPngUseCase,
                           PetRepository petRepository,
                           ClothingRepository clothingRepository) {
        this.getAdviceUseCase = getAdviceUseCase;
        this.getTasksAmountUseCase = getTasksAmountUseCase;
        this.exportStatisticsToPngUseCase = exportStatisticsToPngUseCase;
        this.clothingRepository = clothingRepository;

        petImageRes.addSource(petRepository.getPetLive(), this::resolvePetImage);
        executor.execute(() -> resolvePetImageSync(petRepository.getPet()));
        loadAdvice();
    }

    private void resolvePetImageSync(Pet pet) {
        if (pet == null || pet.getCurrentClothingGlobalId() == null) {
            petImageRes.postValue(R.drawable.pet_default);
            return;
        }
        Clothing clothing = clothingRepository.getByGlobalId(pet.getCurrentClothingGlobalId());
        if (clothing != null && clothing.getImageResId() != 0) {
            petImageRes.postValue(clothing.getImageResId());
        } else {
            petImageRes.postValue(R.drawable.pet_default);
        }
    }

    private void resolvePetImage(Pet pet) {
        executor.execute(() -> resolvePetImageSync(pet));
    }

    private void loadAdvice() {
        executor.execute(() -> {
            adviceLoading.postValue(true);
            String text = getAdviceUseCase.execute(getTasksAmountUseCase.execute());
            advice.postValue(text);
            adviceLoading.postValue(false);
        });
    }

    public void exportAsPng(View cardView) {
        executor.execute(() -> {
            Bitmap bitmap = Bitmap.createBitmap(
                    cardView.getWidth(), cardView.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            cardView.draw(canvas);
            boolean saved = exportStatisticsToPngUseCase.execute(
                    bitmap, "questify_advice_" + System.currentTimeMillis());
            exportResult.postValue(saved ? R.string.stats_export_png_saved : R.string.stats_export_save_error);
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdownNow();
    }
}
