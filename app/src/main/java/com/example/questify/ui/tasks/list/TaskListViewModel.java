package com.example.questify.ui.tasks.list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.questify.domain.model.Task;
import com.example.questify.domain.usecase.plans.filter.FilterTasksUseCase;
import com.example.questify.domain.usecase.plans.filter.TaskFilter;
import com.example.questify.domain.usecase.plans.sort.SortOrder;
import com.example.questify.domain.usecase.plans.sort.SortTasksUseCase;
import com.example.questify.domain.usecase.plans.sort.SortType;
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
    @Inject
    SortTasksUseCase sortTasksUseCase;
    @Inject
    FilterTasksUseCase filterTasksUseCase;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private TaskFilter currentFilter = null;
    private SortType currentSortType = SortType.DEADLINE;
    private SortOrder currentSortOrder = SortOrder.ASC;

    private final MutableLiveData<List<Task>> tasks = new MutableLiveData<>();
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


    public void loadTasks() {
        executor.execute(() -> {
            List<Task> list = getAllTasksUseCase.execute();
            tasks.postValue(currentFilter != null
                    ? filterTasksUseCase.execute(list, currentFilter)
                    : list);
        });
    }

    public void completeTask(Task task, boolean isDone) {
        executor.execute(() -> {
            if (isDone) {
                completeTaskUseCase.execute(task);
            }
            loadTasks();
        });
    }

    public void sort(SortType type) {
        executor.execute(() -> {
            List<Task> list = getAllTasksUseCase.execute();

            if (type == currentSortType) {
                currentSortOrder = (currentSortOrder == SortOrder.ASC)
                        ? SortOrder.DESC
                        : SortOrder.ASC;
            } else {
                currentSortType = type;
                currentSortOrder = SortOrder.ASC;
            }

            list = sortTasksUseCase.execute(list, currentSortType, currentSortOrder);
            tasks.postValue(list);
        });
    }

    // todo мб хранить сразу список чтобы делать частичные сортировки
    // todo сделать визуализацию по возрастанию или по убыванию
    public void applyFilter(TaskFilter filter) {
        if (filter.getPriority() == null && filter.getDifficulty() == null && filter.getDeadlineBefore() == null) {
            currentFilter = null;
        } else {
            currentFilter = filter;
        }
        loadTasks();
    }
}
