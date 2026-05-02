package com.example.questify.ui.advice;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.questify.R;
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

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final MutableLiveData<String> advice = new MutableLiveData<>();
    private final MutableLiveData<Boolean> adviceLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> exportResult = new MutableLiveData<>();

    public LiveData<String> getAdvice() {
        return advice;
    }

    public LiveData<Boolean> getAdviceLoading() {
        return adviceLoading;
    }

    public LiveData<Integer> getExportResult() {
        return exportResult;
    }

    @Inject
    public AdviceViewModel(GetAdviceUseCase getAdviceUseCase,
                           GetTasksAmountUseCase getTasksAmountUseCase,
                           ExportStatisticsToPngUseCase exportStatisticsToPngUseCase) {
        this.getAdviceUseCase = getAdviceUseCase;
        this.getTasksAmountUseCase = getTasksAmountUseCase;
        this.exportStatisticsToPngUseCase = exportStatisticsToPngUseCase;
        loadAdvice();
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
