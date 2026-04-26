package com.example.questify.ui.tasks.list;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.questify.domain.model.Project;
import com.example.questify.domain.model.Task;
import com.example.questify.domain.usecase.plans.project.GetAllProjectsUseCase;
import com.example.questify.domain.usecase.plans.tasks.imp.ImportTasksUseCaseFactory;
import com.example.questify.domain.usecase.plans.tasks.filter.FilterTasksUseCase;
import com.example.questify.domain.usecase.plans.tasks.filter.TaskFilter;
import com.example.questify.domain.usecase.plans.tasks.sort.SortOrder;
import com.example.questify.domain.usecase.plans.tasks.sort.SortTasksUseCase;
import com.example.questify.domain.usecase.plans.tasks.sort.SortType;
import com.example.questify.domain.usecase.plans.tasks.task.CompleteTaskUseCase;
import com.example.questify.domain.usecase.plans.tasks.task.CreateTaskUseCase;
import com.example.questify.domain.usecase.plans.tasks.task.GetAllTasksUseCase;
import com.example.questify.sync.SyncManager;

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
    private final SyncManager syncManager;

    @Inject
    SortTasksUseCase sortTasksUseCase;
    @Inject
    FilterTasksUseCase filterTasksUseCase;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final LiveData<List<Project>> projects;
    private TaskFilter currentFilter = new TaskFilter(
            null,
            null,
            null,
            null,
            null,
            null);
    private SortType currentSortType = SortType.DEADLINE;
    private SortOrder currentSortOrder = SortOrder.ASC;

    private final MediatorLiveData<List<Task>> tasks = new MediatorLiveData<>();
    private final LiveData<List<Task>> source;

    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<List<Task>> getTasks() {
        return tasks;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    public TaskFilter getCurrentFilter() {
        return currentFilter;
    }
    public LiveData<List<Project>> getProjects() {
        return projects;
    }
    @Inject
    public TaskListViewModel(GetAllTasksUseCase getAllTasksUseCase,
                             CompleteTaskUseCase completeTaskUseCase,
                             ImportTasksUseCaseFactory importFactory,
                             CreateTaskUseCase createTaskUseCase,
                             GetAllProjectsUseCase getAllProjectsUseCase,
                             SyncManager syncManager) {

        this.completeTaskUseCase = completeTaskUseCase;
        this.importFactory = importFactory;
        this.createTaskUseCase = createTaskUseCase;
        this.syncManager = syncManager;

        this.source = getAllTasksUseCase.executeLive();
        this.projects = getAllProjectsUseCase.executeLive();

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
        executor.execute(() -> {
            try {
                completeTaskUseCase.execute(task, isDone);
                syncManager.scheduleSyncSoon();
            } catch (Exception e) {
                errorMessage.postValue(e.getMessage());
            }
        });
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
        currentFilter = filter;
        List<Task> original = source.getValue();
        if (original != null) {
            recalc(original);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdownNow();
    }

    public void importFromFile(Context context, Uri uri, String fileName) {
        executor.execute(() -> {
            try {
                importFactory.get(fileName, createTaskUseCase, context).execute(context, uri);
            } catch (Exception e) {
                errorMessage.postValue(e.getMessage());
            }
        });
    }
}