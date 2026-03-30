package com.example.questify.ui.tasks.create;


import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.questify.domain.model.enums.Difficulty;
import com.example.questify.domain.model.enums.Priority;
import com.example.questify.domain.model.Project;
import com.example.questify.domain.usecase.plans.project.GetAllProjectsUseCase;
import com.example.questify.domain.usecase.plans.tasks.task.CreateTaskUseCase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class TaskCreateViewModel extends ViewModel {
    private final CreateTaskUseCase createTaskUseCase;
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
                               GetAllProjectsUseCase getAllProjectsUseCase) {
        this.createTaskUseCase = createTaskUseCase;
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
                createTaskUseCase.execute(
                        taskName,
                        description,
                        deadline,
                        projectName,
                        difficulty,
                        priority,
                        context
                );
                success.postValue(true);
            } catch (Exception e) {
                error.postValue(e.getMessage());
            }
        });
    }
}