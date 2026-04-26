package com.example.questify.ui.settings;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.questify.domain.usecase.plans.tasks.exp.ExportStatisticsToJsonUseCase;
import com.example.questify.domain.usecase.plans.tasks.exp.ExportStatisticsToPngUseCase;
import com.example.questify.domain.usecase.plans.tasks.exp.ExportToIcsUseCase;
import com.example.questify.domain.usecase.plans.tasks.exp.ExportToJsonUseCase;
import com.example.questify.domain.usecase.user.DeleteCompletedTasksUseCase;
import com.example.questify.domain.usecase.user.DeleteProgressUseCase;
import com.example.questify.sync.SyncManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SettingsViewModel extends ViewModel {

    private final DeleteProgressUseCase deleteProgressUseCase;
    private final DeleteCompletedTasksUseCase deleteCompletedTasksUseCase;
    private final SyncManager syncManager;
    private final ExportToJsonUseCase exportToJsonUseCase;
    private final ExportToIcsUseCase exportToIcsUseCase;
    private final ExportStatisticsToPngUseCase exportStatisticsToPngUseCase;
    private final ExportStatisticsToJsonUseCase exportStatisticsToJsonUseCase;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    @Inject
    public SettingsViewModel(DeleteProgressUseCase deleteProgressUseCase,
                             DeleteCompletedTasksUseCase deleteCompletedTasksUseCase,
                             ExportToJsonUseCase exportToJsonUseCase,
                             ExportToIcsUseCase exportToIcsUseCase,
                             ExportStatisticsToPngUseCase exportStatisticsToPngUseCase,
                             ExportStatisticsToJsonUseCase exportStatisticsToJsonUseCase,
                             SyncManager syncManager) {
        this.deleteProgressUseCase = deleteProgressUseCase;
        this.deleteCompletedTasksUseCase = deleteCompletedTasksUseCase;
        this.exportToJsonUseCase = exportToJsonUseCase;
        this.exportToIcsUseCase = exportToIcsUseCase;
        this.exportStatisticsToPngUseCase = exportStatisticsToPngUseCase;
        this.exportStatisticsToJsonUseCase = exportStatisticsToJsonUseCase;
        this.syncManager = syncManager;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdownNow();
    }

    public void resetProgress() {
        executor.execute(() -> {
            try {
                deleteProgressUseCase.execute();
                syncManager.scheduleSyncSoon();
            } catch (Exception e) {
                errorMessage.postValue(e.getMessage());
            }
        });
    }

    public void deleteCompletedTasks() {
        executor.execute(() -> {
            try {
                deleteCompletedTasksUseCase.execute();
                syncManager.scheduleSyncSoon();
            } catch (Exception e) {
                errorMessage.postValue(e.getMessage());
            }
        });
    }

    public void exportToJson(Context context, Uri uri) {
        executor.execute(() -> {
            try {
                exportToJsonUseCase.execute(context, uri);
            } catch (Exception e) {
                errorMessage.postValue(e.getMessage());
            }
        });
    }

    public void exportToIcs(Context context, Uri uri) {
        executor.execute(() -> {
            try {
                exportToIcsUseCase.execute(context, uri);
            } catch (Exception e) {
                errorMessage.postValue(e.getMessage());
            }
        });
    }

    public void exportStatisticsToPng(Context context, Uri uri) {
        executor.execute(() -> {
            try {
                exportStatisticsToPngUseCase.execute(context, uri);
            } catch (Exception e) {
                errorMessage.postValue(e.getMessage());
            }
        });
    }

    public void exportStatisticsToJson(Context context, Uri uri) {
        executor.execute(() -> {
            try {
                exportStatisticsToJsonUseCase.execute(context, uri);
            } catch (Exception e) {
                errorMessage.postValue(e.getMessage());
            }
        });
    }
}