package com.example.questify.ui.tasks.list;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.questify.domain.model.Task;
import com.example.questify.domain.usecase.plans.tasks.imp.ImportTasksUseCaseFactory;
import com.example.questify.domain.usecase.plans.tasks.filter.FilterTasksUseCase;
import com.example.questify.domain.usecase.plans.tasks.filter.TaskFilter;
import com.example.questify.domain.usecase.plans.tasks.sort.SortOrder;
import com.example.questify.domain.usecase.plans.tasks.sort.SortTasksUseCase;
import com.example.questify.domain.usecase.plans.tasks.sort.SortType;
import com.example.questify.domain.usecase.plans.tasks.task.CompleteTaskUseCase;
import com.example.questify.domain.usecase.plans.tasks.task.CreateTaskUseCase;
import com.example.questify.domain.usecase.plans.tasks.task.GetAllTasksUseCase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class TaskListViewModel extends ViewModel {
    private final GetAllTasksUseCase getAllTasksUseCase;
    private final CompleteTaskUseCase completeTaskUseCase;
    private final ImportTasksUseCaseFactory importFactory;
    private final CreateTaskUseCase createTaskUseCase;
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
                             CompleteTaskUseCase completeTaskUseCase,
                             ImportTasksUseCaseFactory importFactory,
                             CreateTaskUseCase createTaskUseCase) {
        this.getAllTasksUseCase = getAllTasksUseCase;
        this.completeTaskUseCase = completeTaskUseCase;
        this.importFactory = importFactory;
        this.createTaskUseCase = createTaskUseCase;

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
            completeTaskUseCase.execute(task, isDone);
            loadTasks(); // todo сортировка после отметки сбрасывается
        });
    }


    public void sort(SortType type) {
        executor.execute(() -> {
            List<Task> list = getAllTasksUseCase.execute();

            if (type == currentSortType) {
                currentSortOrder = (currentSortOrder == SortOrder.ASC)
                        ? SortOrder.DESC
                        : SortOrder.ASC;
                // todo сортировка не сочетается с фильтрацией???
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

    public void importFromFile(Context context, Uri uri, String fileName) {
        importFactory.get(fileName, createTaskUseCase).execute(context, uri);
    }
}
