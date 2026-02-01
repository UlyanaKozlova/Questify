package com.example.questify.ui.tasks.list;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
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

    private final MediatorLiveData<List<Task>> tasks = new MediatorLiveData<>();
    private final LiveData<List<Task>> source;

    public LiveData<List<Task>> getTasks() {
        return tasks;
    }

    @Inject
    public TaskListViewModel(GetAllTasksUseCase getAllTasksUseCase,
                             CompleteTaskUseCase completeTaskUseCase,
                             ImportTasksUseCaseFactory importFactory,
                             CreateTaskUseCase createTaskUseCase) {

        this.completeTaskUseCase = completeTaskUseCase;
        this.importFactory = importFactory;
        this.createTaskUseCase = createTaskUseCase;

        this.source = getAllTasksUseCase.executeLive();

        tasks.addSource(source, this::recalc);
    }

    private void recalc(List<Task> original) {
        if (original == null) {
            return;
        }
        List<Task> sorted = sortTasksUseCase.execute(original, currentSortType, currentSortOrder);

        if (currentFilter != null) {
            sorted = filterTasksUseCase.execute(sorted, currentFilter);
        }
        tasks.setValue(sorted);
    }

    public void completeTask(Task task, boolean isDone) {
        executor.execute(() -> completeTaskUseCase.execute(task, isDone));
    }

    public void sort(SortType type) {
        if (type == currentSortType) {
            currentSortOrder = (currentSortOrder == SortOrder.ASC)
                    ? SortOrder.DESC
                    : SortOrder.ASC;
        } else {
            currentSortType = type;
            currentSortOrder = SortOrder.ASC;
        }

        List<Task> original = source.getValue();
        if (original != null) {
            recalc(original);
        }
    }

    public void applyFilter(TaskFilter filter) {
        currentFilter = filter.isEmpty() ? null : filter;
        List<Task> original = source.getValue();
        if (original != null) {
            recalc(original);
        }
    }

    public void importFromFile(Context context, Uri uri, String fileName) {
        importFactory.get(fileName, createTaskUseCase).execute(context, uri);
    }
}
