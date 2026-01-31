package com.example.questify.ui.settings;

import androidx.lifecycle.ViewModel;


import com.example.questify.domain.usecase.plans.tasks.exp.ExportTasksUseCase;
import com.example.questify.domain.usecase.user.DeleteCompletedTasksUseCase;
import com.example.questify.domain.usecase.user.DeleteProgressUseCase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SettingsViewModel extends ViewModel {

    private final DeleteProgressUseCase deleteProgressUseCase;
    private final DeleteCompletedTasksUseCase deleteCompletedTasksUseCase;
    private final ExportTasksUseCase exportTasksUseCase;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Inject
    public SettingsViewModel(DeleteProgressUseCase deleteProgressUseCase,
                             DeleteCompletedTasksUseCase deleteCompletedTasksUseCase,
                             ExportTasksUseCase exportTasksUseCase) {
        this.deleteProgressUseCase = deleteProgressUseCase;
        this.deleteCompletedTasksUseCase = deleteCompletedTasksUseCase;
        this.exportTasksUseCase = exportTasksUseCase;
    }

    public void resetProgress() {
        executor.execute(deleteProgressUseCase::execute);
    }

    public void deleteCompletedTasks() {
        executor.execute(deleteCompletedTasksUseCase::execute);
    }

    public void exportTasks() {
        executor.execute(exportTasksUseCase::execute);
    }
}
