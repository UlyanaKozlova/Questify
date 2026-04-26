package com.example.questify.ui.projects.detail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.questify.domain.model.Task;
import com.example.questify.domain.usecase.plans.project.GetProjectStatisticsUseCase;
import com.example.questify.domain.usecase.plans.project.GetProjectTasksUseCase;
import com.example.questify.domain.usecase.plans.tasks.task.UpdateTaskUseCase;
import com.example.questify.sync.SyncManager;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ProjectDetailViewModel extends ViewModel {

    private final GetProjectTasksUseCase getProjectTasksUseCase;
    private final GetProjectStatisticsUseCase getProjectStatisticsUseCase;
    private final UpdateTaskUseCase updateTaskUseCase;
    private final SyncManager syncManager;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final MutableLiveData<List<Task>> tasks = new MutableLiveData<>();
    private final MutableLiveData<GetProjectStatisticsUseCase.ProjectStatistics> statistics = new MutableLiveData<>();

    @Inject
    public ProjectDetailViewModel(GetProjectTasksUseCase getProjectTasksUseCase,
                                  GetProjectStatisticsUseCase getProjectStatisticsUseCase,
                                  UpdateTaskUseCase updateTaskUseCase,
                                  SyncManager syncManager) {
        this.getProjectTasksUseCase = getProjectTasksUseCase;
        this.getProjectStatisticsUseCase = getProjectStatisticsUseCase;
        this.updateTaskUseCase = updateTaskUseCase;
        this.syncManager = syncManager;
    }

    public LiveData<List<Task>> getProjectTasks() {
        return tasks;
    }

    public LiveData<GetProjectStatisticsUseCase.ProjectStatistics> getProjectStatistics() {
        return statistics;
    }

    public void loadProjectData(String projectGlobalId) {
        executor.execute(() -> {
            List<Task> projectTasks = getProjectTasksUseCase.execute(projectGlobalId);
            tasks.postValue(projectTasks);

            GetProjectStatisticsUseCase.ProjectStatistics stats = getProjectStatisticsUseCase.execute(projectGlobalId);
            statistics.postValue(stats);
        });
    }

    public void updateTaskDone(Task task, boolean isDone) {
        executor.execute(() -> {
            updateTaskUseCase.updateDoneStatus(task, isDone);
            syncManager.scheduleSyncSoon();
            loadProjectData(task.getProjectGlobalId());
        });
    }
}