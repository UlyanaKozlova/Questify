package com.example.questify.ui.tasks.list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.questify.domain.model.Task;
import com.example.questify.domain.usecase.plans.task.CompleteTaskUseCase;
import com.example.questify.domain.usecase.plans.task.GetAllTasksUseCase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class TaskListViewModel extends ViewModel {

    private final GetAllTasksUseCase getAllTasksUseCase;
    private final CompleteTaskUseCase completeTaskUseCase;

    private final MutableLiveData<List<Task>> tasks = new MutableLiveData<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public LiveData<List<Task>> getTasks() {
        return tasks;
    }

    @Inject
    public TaskListViewModel(GetAllTasksUseCase getAllTasksUseCase,
                             CompleteTaskUseCase completeTaskUseCase) {
        this.getAllTasksUseCase = getAllTasksUseCase;
        this.completeTaskUseCase = completeTaskUseCase;
        loadTasks();
    }

    public void completeTask(Task task, boolean isDone) {
        executor.execute(() -> {
            if (isDone) {
                completeTaskUseCase.execute(task);
            }
            loadTasks();
        });
    }

    public void loadTasks() {
        executor.execute(() -> tasks.postValue(getAllTasksUseCase.execute()));
    }
}
