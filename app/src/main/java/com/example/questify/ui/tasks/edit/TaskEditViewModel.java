package com.example.questify.ui.tasks.edit;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.questify.domain.model.Difficulty;
import com.example.questify.domain.model.Priority;
import com.example.questify.domain.model.Project;
import com.example.questify.domain.model.Task;
import com.example.questify.domain.usecase.plans.project.GetAllProjectsUseCase;
import com.example.questify.domain.usecase.plans.tasks.task.DeleteTaskUseCase;
import com.example.questify.domain.usecase.plans.tasks.task.GetTaskUseCase;
import com.example.questify.domain.usecase.plans.tasks.task.UpdateTaskUseCase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;


@HiltViewModel
public class TaskEditViewModel extends ViewModel {

    private final GetTaskUseCase getTaskUseCase;
    private final UpdateTaskUseCase updateTaskUseCase;
    private final DeleteTaskUseCase deleteTaskUseCase;

    private final MutableLiveData<Task> task = new MutableLiveData<>();

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    public final LiveData<List<Project>> projects;


    public LiveData<Task> getTask() {
        return task;
    }

    @Inject
    public TaskEditViewModel(GetTaskUseCase getTaskUseCase,
                             UpdateTaskUseCase updateTaskUseCase,
                             DeleteTaskUseCase deleteTaskUseCase,
                             GetAllProjectsUseCase getAllProjectsUseCase) {
        this.getTaskUseCase = getTaskUseCase;
        this.updateTaskUseCase = updateTaskUseCase;
        this.deleteTaskUseCase = deleteTaskUseCase;
        this.projects = getAllProjectsUseCase.executeLive();
    }

    public void loadTask(String globalId) {
        executor.execute(() -> {
            Task taskToLoad = getTaskUseCase.execute(globalId);
            task.postValue(taskToLoad);
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
        if (taskToEdit == null) {
            return;
        }
        executor.execute(() -> updateTaskUseCase.execute(
                taskToEdit,
                name,
                description,
                deadline,
                projectName,
                priority,
                difficulty,
                isDone));
    }


    public void deleteTask() {
        Task task = this.task.getValue();
        if (task != null) {
            executor.execute(() ->
                    deleteTaskUseCase.execute(task));
        }
    }
}

