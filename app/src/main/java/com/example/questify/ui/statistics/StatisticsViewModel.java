package com.example.questify.ui.statistics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.view.View;

import androidx.core.content.FileProvider;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.questify.R;
import com.example.questify.domain.model.Project;
import com.example.questify.domain.model.Task;
import com.example.questify.domain.model.User;
import com.example.questify.domain.model.helpers.ProjectTaskCount;
import com.example.questify.domain.model.helpers.TaskStatistics;
import com.example.questify.domain.usecase.plans.project.GetAllProjectsUseCase;
import com.example.questify.domain.usecase.plans.tasks.task.GetAllTasksUseCase;
import com.example.questify.domain.usecase.statistics.ExportStatisticsToJsonUseCase;
import com.example.questify.domain.usecase.statistics.ExportStatisticsToPngUseCase;
import com.example.questify.domain.usecase.statistics.GetGraphicsUseCase;
import com.example.questify.domain.usecase.statistics.GetTasksAmountUseCase;
import com.example.questify.domain.usecase.user.GetUserUseCase;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;

@HiltViewModel
public class StatisticsViewModel extends ViewModel {

    private final Context appContext;
    private final GetTasksAmountUseCase getTasksAmountUseCase;
    private final GetGraphicsUseCase getGraphicsUseCase;
    private final ExportStatisticsToPngUseCase exportStatisticsToPngUseCase;
    private final ExportStatisticsToJsonUseCase exportStatisticsToJsonUseCase;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final MediatorLiveData<TaskStatistics> taskStatistics = new MediatorLiveData<>();
    private final MediatorLiveData<List<ProjectTaskCount>> projectChartData = new MediatorLiveData<>();
    private final MutableLiveData<Integer> exportResult = new MutableLiveData<>();
    private final MutableLiveData<Uri> shareUri = new MutableLiveData<>();
    private final LiveData<User> user;

    public LiveData<TaskStatistics> getTaskStatistics() {
        return taskStatistics;
    }

    public LiveData<List<ProjectTaskCount>> getProjectChartData() {
        return projectChartData;
    }

    public LiveData<Integer> getExportResult() {
        return exportResult;
    }

    public LiveData<Uri> getShareUri() {
        return shareUri;
    }

    public LiveData<User> getUser() {
        return user;
    }

    @Inject
    public StatisticsViewModel(@ApplicationContext Context appContext,
                               GetTasksAmountUseCase getTasksAmountUseCase,
                               GetGraphicsUseCase getGraphicsUseCase,
                               ExportStatisticsToPngUseCase exportStatisticsToPngUseCase,
                               ExportStatisticsToJsonUseCase exportStatisticsToJsonUseCase,
                               GetUserUseCase getUserUseCase,
                               GetAllTasksUseCase getAllTasksUseCase,
                               GetAllProjectsUseCase getAllProjectsUseCase) {
        this.appContext = appContext;
        this.getTasksAmountUseCase = getTasksAmountUseCase;
        this.getGraphicsUseCase = getGraphicsUseCase;
        this.exportStatisticsToPngUseCase = exportStatisticsToPngUseCase;
        this.exportStatisticsToJsonUseCase = exportStatisticsToJsonUseCase;
        this.user = getUserUseCase.executeLive();

        LiveData<List<Task>> tasksLive = getAllTasksUseCase.executeLive();
        LiveData<List<Project>> projectsLive = getAllProjectsUseCase.executeLive();

        taskStatistics.addSource(tasksLive, tasks -> reloadStatistics());
        projectChartData.addSource(tasksLive, tasks -> reloadChart());
        projectChartData.addSource(projectsLive, projects -> reloadChart());
    }

    private void reloadStatistics() {
        executor.execute(() -> taskStatistics.postValue(getTasksAmountUseCase.execute()));
    }

    private void reloadChart() {
        executor.execute(() -> projectChartData.postValue(getGraphicsUseCase.execute()));
    }

    public void exportStatsAsPng(View cardView) {
        Bitmap bitmap = captureBitmap(cardView);
        executor.execute(() -> {
            boolean saved = exportStatisticsToPngUseCase.execute(
                    bitmap,
                    "questify_statics_" + System.currentTimeMillis()
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

    public void shareStatsAsPng(View cardView) {
        Bitmap bitmap = captureBitmap(cardView);
        executor.execute(() -> {
            File file = exportStatisticsToPngUseCase.executeToCacheFile(
                    bitmap,
                    "questify_statics_share_" + System.currentTimeMillis()
            );
            if (file == null) {
                exportResult.postValue(R.string.stats_export_save_error);
                return;
            }
            try {
                Uri uri = FileProvider.getUriForFile(
                        appContext,
                        appContext.getPackageName() + ".fileprovider",
                        file
                );
                shareUri.postValue(uri);
            } catch (IllegalArgumentException e) {
                exportResult.postValue(R.string.stats_export_save_error);
            }
        });
    }

    public void clearShareUri() {
        shareUri.setValue(null);
    }

    public void clearExportResult() {
        exportResult.setValue(null);
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
