package com.example.questify.ui.tasks.create;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;

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
        if (deadline == null) {
            error.setValue("Выберите дату дедлайна");
            return;
        }
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
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Validation error: " + e.getMessage());
                error.postValue(e.getMessage());
            } catch (Exception e) {
                Log.e(TAG, "Unexpected error", e);
                error.postValue("Ошибка при сохранении задачи: " + e.getMessage());
            }
        });
    }
}