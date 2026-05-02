package com.example.questify.ui.tasks.edit;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.questify.util.exception.DomainValidationException;
import com.example.questify.domain.model.Subtask;
import com.example.questify.domain.model.enums.Difficulty;
import com.example.questify.domain.model.enums.Priority;
import com.example.questify.domain.model.Project;
import com.example.questify.domain.model.Task;
import com.example.questify.domain.usecase.plans.project.GetAllProjectsUseCase;
import com.example.questify.domain.usecase.plans.subtask.CompleteSubtaskUseCase;
import com.example.questify.domain.usecase.plans.subtask.CreateSubtaskUseCase;
import com.example.questify.domain.usecase.plans.subtask.DeleteSubtaskUseCase;
import com.example.questify.domain.usecase.plans.subtask.GetSubtasksForTaskUseCase;
import com.example.questify.domain.usecase.plans.subtask.UpdateSubtaskUseCase;
import com.example.questify.domain.usecase.plans.tasks.task.DeleteTaskUseCase;
import com.example.questify.domain.usecase.plans.tasks.task.GetTaskUseCase;
import com.example.questify.domain.usecase.plans.tasks.task.UpdateTaskUseCase;
import com.example.questify.sync.SyncManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;

@HiltViewModel
public class TaskEditViewModel extends ViewModel {

    private final GetTaskUseCase getTaskUseCase;
    private final UpdateTaskUseCase updateTaskUseCase;
    private final DeleteTaskUseCase deleteTaskUseCase;
    private final GetSubtasksForTaskUseCase getSubtasksForTaskUseCase;
    private final CreateSubtaskUseCase createSubtaskUseCase;
    private final CompleteSubtaskUseCase completeSubtaskUseCase;
    private final UpdateSubtaskUseCase updateSubtaskUseCase;
    private final DeleteSubtaskUseCase deleteSubtaskUseCase;
    private final SyncManager syncManager;
    private final Context context;

    private final MutableLiveData<Task> task = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<List<Subtask>> subtasks = new MutableLiveData<>(new ArrayList<>());

    private String currentTaskGlobalId;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    public final LiveData<List<Project>> projects;

    public LiveData<Task> getTask() {
        return task;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<List<Subtask>> getSubtasks() {
        return subtasks;
    }

    @Inject
    public TaskEditViewModel(@ApplicationContext Context context,
                             GetTaskUseCase getTaskUseCase,
                             UpdateTaskUseCase updateTaskUseCase,
                             DeleteTaskUseCase deleteTaskUseCase,
                             GetAllProjectsUseCase getAllProjectsUseCase,
                             GetSubtasksForTaskUseCase getSubtasksForTaskUseCase,
                             CreateSubtaskUseCase createSubtaskUseCase,
                             CompleteSubtaskUseCase completeSubtaskUseCase,
                             UpdateSubtaskUseCase updateSubtaskUseCase,
                             DeleteSubtaskUseCase deleteSubtaskUseCase,
                             SyncManager syncManager) {
        this.context = context;
        this.getTaskUseCase = getTaskUseCase;
        this.updateTaskUseCase = updateTaskUseCase;
        this.deleteTaskUseCase = deleteTaskUseCase;
        this.getSubtasksForTaskUseCase = getSubtasksForTaskUseCase;
        this.createSubtaskUseCase = createSubtaskUseCase;
        this.completeSubtaskUseCase = completeSubtaskUseCase;
        this.updateSubtaskUseCase = updateSubtaskUseCase;
        this.deleteSubtaskUseCase = deleteSubtaskUseCase;
        this.syncManager = syncManager;
        this.projects = getAllProjectsUseCase.executeLive();
    }

    public void loadTask(String globalId) {
        currentTaskGlobalId = globalId;
        executor.execute(() -> {
            Task taskToLoad = getTaskUseCase.execute(globalId);
            task.postValue(taskToLoad);
            List<Subtask> list = getSubtasksForTaskUseCase.execute(globalId);
            subtasks.postValue(list);
        });
    }

    public void saveTask(String name,
                         String description,
                         long deadline,
                         String projectName,
                         Priority priority,
                         Difficulty difficulty,
                         boolean isDone) {
        Task taskToEdit = task.getValue();
        if (taskToEdit == null) return;

        executor.execute(() -> {
            try {
                updateTaskUseCase.execute(
                        taskToEdit,
                        name,
                        description,
                        deadline,
                        projectName,
                        priority,
                        difficulty,
                        isDone
                );
                syncManager.scheduleSyncSoon();
            } catch (DomainValidationException e) {
                error.postValue(context.getString(e.resId));
            }
        });
    }

    public void deleteTask() {
        Task currentTask = task.getValue();
        if (currentTask == null) return;
        executor.execute(() -> {
            deleteTaskUseCase.execute(currentTask);
            syncManager.scheduleSyncSoon();
        });
    }

    public void addSubtask(String name) {
        if (currentTaskGlobalId == null || name == null || name.trim().isEmpty()) {
            return;
        }
        executor.execute(() -> {
            createSubtaskUseCase.execute(currentTaskGlobalId, name.trim());
            refreshSubtasks();
            syncManager.scheduleSyncSoon();
        });
    }

    public void toggleSubtask(Subtask subtask, boolean isDone) {
        executor.execute(() -> {
            completeSubtaskUseCase.execute(subtask, isDone);
            refreshSubtasks();
            syncManager.scheduleSyncSoon();
        });
    }

    public void updateSubtask(Subtask subtask, String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            return;
        }
        executor.execute(() -> {
            updateSubtaskUseCase.execute(subtask, newName.trim());
            refreshSubtasks();
            syncManager.scheduleSyncSoon();
        });
    }

    public void deleteSubtask(Subtask subtask) {
        executor.execute(() -> {
            deleteSubtaskUseCase.execute(subtask);
            refreshSubtasks();
            syncManager.scheduleSyncSoon();
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdownNow();
    }

    private void refreshSubtasks() {
        if (currentTaskGlobalId == null) {
            return;
        }
        List<Subtask> list = getSubtasksForTaskUseCase.execute(currentTaskGlobalId);
        subtasks.postValue(list);
    }
}
