package com.example.questify.ui.tasks.create;


import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.questify.domain.model.Task;
import com.example.questify.domain.model.enums.Difficulty;
import com.example.questify.domain.model.enums.Priority;
import com.example.questify.domain.model.Project;
import com.example.questify.domain.usecase.plans.project.GetAllProjectsUseCase;
import com.example.questify.domain.usecase.plans.subtask.GenerateSubtasksUseCase;
import com.example.questify.domain.usecase.plans.tasks.task.CreateTaskUseCase;
import com.example.questify.sync.SyncManager;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class TaskCreateViewModel extends ViewModel {

    private static final String TAG = "GeminiSubtasks";

    private final CreateTaskUseCase createTaskUseCase;
    private final GenerateSubtasksUseCase generateSubtasksUseCase;
    private final SyncManager syncManager;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    public final LiveData<List<Project>> projects;
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> success = new MutableLiveData<>();

    public LiveData<Boolean> getSuccess() {
        return success;
    }

    public LiveData<String> getError() {
        return error;
    }

    @Inject
    public TaskCreateViewModel(CreateTaskUseCase createTaskUseCase,
                               GetAllProjectsUseCase getAllProjectsUseCase,
                               GenerateSubtasksUseCase generateSubtasksUseCase,
                               SyncManager syncManager) {
        this.createTaskUseCase = createTaskUseCase;
        this.generateSubtasksUseCase = generateSubtasksUseCase;
        this.syncManager = syncManager;
        this.projects = getAllProjectsUseCase.executeLive();
    }

    public void saveTask(String taskName,
                         String description,
                         Long deadline,
                         String projectName,
                         Difficulty difficulty,
                         Priority priority,
                         Context context) {
        executor.execute(() -> {
            try {
                Task task = createTaskUseCase.execute(
                        taskName,
                        description,
                        deadline,
                        projectName,
                        difficulty,
                        priority,
                        context
                );
                success.postValue(true);
                syncManager.scheduleSyncSoon();
                generateSubtasksUseCase.execute(task.getGlobalId(), task.getTaskName(), task.getDescription());
                syncManager.scheduleSyncSoon();
            } catch (Exception e) {
                Log.e(TAG, "Ошибка при создании задачи или генерации подзадач", e);
                error.postValue(e.getMessage());
            }
        });
    }
}
