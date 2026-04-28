package com.example.questify.ui.statistics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.questify.R;
import com.example.questify.domain.model.helpers.ProjectTaskCount;
import com.example.questify.domain.model.helpers.TaskStatistics;
import com.example.questify.domain.usecase.statistics.ExportStatisticsToJsonUseCase;
import com.example.questify.domain.usecase.statistics.ExportStatisticsToPngUseCase;
import com.example.questify.domain.usecase.statistics.GetAdviceUseCase;
import com.example.questify.domain.usecase.statistics.GetGraphicsUseCase;
import com.example.questify.domain.usecase.statistics.GetTasksAmountUseCase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class StatisticsViewModel extends ViewModel {

    private final GetTasksAmountUseCase getTasksAmountUseCase;
    private final GetGraphicsUseCase getGraphicsUseCase;
    private final GetAdviceUseCase getAdviceUseCase;
    private final ExportStatisticsToPngUseCase exportStatisticsToPngUseCase;
    private final ExportStatisticsToJsonUseCase exportStatisticsToJsonUseCase;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final MutableLiveData<TaskStatistics> taskStatistics = new MutableLiveData<>();
    private final MutableLiveData<List<ProjectTaskCount>> projectChartData = new MutableLiveData<>();
    private final MutableLiveData<String> advice = new MutableLiveData<>();
    private final MutableLiveData<Boolean> adviceLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> exportResult = new MutableLiveData<>();

    public LiveData<TaskStatistics> getTaskStatistics() {
        return taskStatistics;
    }

    public LiveData<List<ProjectTaskCount>> getProjectChartData() {
        return projectChartData;
    }

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
    public StatisticsViewModel(GetTasksAmountUseCase getTasksAmountUseCase,
                               GetGraphicsUseCase getGraphicsUseCase,
                               GetAdviceUseCase getAdviceUseCase,
                               ExportStatisticsToPngUseCase exportStatisticsToPngUseCase,
                               ExportStatisticsToJsonUseCase exportStatisticsToJsonUseCase) {
        this.getTasksAmountUseCase = getTasksAmountUseCase;
        this.getGraphicsUseCase = getGraphicsUseCase;
        this.getAdviceUseCase = getAdviceUseCase;
        this.exportStatisticsToPngUseCase = exportStatisticsToPngUseCase;
        this.exportStatisticsToJsonUseCase = exportStatisticsToJsonUseCase;
        loadStatistics();
    }

    private void loadStatistics() {
        executor.execute(() -> {
            TaskStatistics stats = getTasksAmountUseCase.execute();
            taskStatistics.postValue(stats);
            projectChartData.postValue(getGraphicsUseCase.execute());

            adviceLoading.postValue(true);
            advice.postValue(getAdviceUseCase.execute(stats));
            adviceLoading.postValue(false);
        });
    }

    public void exportStatsAsPng(View cardView) {
        executor.execute(() -> {
            boolean saved = exportStatisticsToPngUseCase.execute(
                    captureBitmap(cardView),
                    "questify_statics_" + System.currentTimeMillis()
            );
            exportResult.postValue(saved ? R.string.stats_export_png_saved : R.string.stats_export_save_error);
        });
    }

    public void exportAdviceAsPng(View cardView) {
        executor.execute(() -> {
            boolean saved = exportStatisticsToPngUseCase.execute(
                    captureBitmap(cardView),
                    "questify_advice_" + System.currentTimeMillis()
            );
            exportResult.postValue(saved ? R.string.stats_export_png_saved : R.string.stats_export_save_error);
        });
    }

    public void exportStatsAsJson() {
        executor.execute(() -> {
            TaskStatistics stats = taskStatistics.getValue();
            if (stats == null) {
                exportResult.postValue(R.string.stats_export_data_not_loaded);
                return;
            }
            boolean saved = exportStatisticsToJsonUseCase.execute(
                    stats,
                    projectChartData.getValue(),
                    "questify_statics_" + System.currentTimeMillis()
            );
            exportResult.postValue(saved ? R.string.stats_export_json_saved : R.string.stats_export_save_error);
        });
    }

    private Bitmap captureBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdownNow();
    }
}
