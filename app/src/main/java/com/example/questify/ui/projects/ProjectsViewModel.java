package com.example.questify.ui.projects;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.questify.R;
import com.example.questify.data.repository.ProjectRepository;
import com.example.questify.util.exception.DomainValidationException;
import com.example.questify.domain.model.Project;
import com.example.questify.domain.model.Task;
import com.example.questify.domain.usecase.plans.project.CreateProjectUseCase;
import com.example.questify.domain.usecase.plans.project.DeleteProjectWithTasksUseCase;
import com.example.questify.domain.usecase.plans.project.GetAllProjectsUseCase;
import com.example.questify.domain.usecase.plans.project.GetProjectStatisticsUseCase;
import com.example.questify.domain.usecase.plans.project.UpdateProjectUseCase;
import com.example.questify.domain.usecase.plans.tasks.task.GetAllTasksUseCase;
import com.example.questify.sync.SyncManager;

import androidx.lifecycle.Observer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;

@HiltViewModel
public class ProjectsViewModel extends ViewModel {
    private final GetAllProjectsUseCase getAllProjectsUseCase;
    private final CreateProjectUseCase createProjectUseCase;
    private final UpdateProjectUseCase updateProjectUseCase;
    private final DeleteProjectWithTasksUseCase deleteProjectWithTasksUseCase;
    private final GetProjectStatisticsUseCase getProjectStatisticsUseCase;
    private final ProjectRepository projectRepository;
    private final SyncManager syncManager;
    private final Context context;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final MutableLiveData<Map<String, GetProjectStatisticsUseCase.ProjectStatistics>> projectStatsMap = new MutableLiveData<>();
    private final Map<String, GetProjectStatisticsUseCase.ProjectStatistics> statsCache = new ConcurrentHashMap<>();

    private final LiveData<List<Task>> allTasksLive;
    private final Observer<List<Task>> taskChangeObserver;
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> operationSuccess = new MutableLiveData<>();

    public LiveData<List<Project>> getAllProjects() {
        return getAllProjectsUseCase.executeLive();
    }

    public LiveData<Map<String, GetProjectStatisticsUseCase.ProjectStatistics>> getProjectStatisticsMap() {
        return projectStatsMap;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getOperationSuccess() {
        return operationSuccess;
    }

    @Inject
    public ProjectsViewModel(@ApplicationContext Context context,
                             GetAllProjectsUseCase getAllProjectsUseCase,
                             GetAllTasksUseCase getAllTasksUseCase,
                             CreateProjectUseCase createProjectUseCase,
                             UpdateProjectUseCase updateProjectUseCase,
                             DeleteProjectWithTasksUseCase deleteProjectWithTasksUseCase,
                             GetProjectStatisticsUseCase getProjectStatisticsUseCase,
                             ProjectRepository projectRepository,
                             SyncManager syncManager) {
        this.context = context;
        this.getAllProjectsUseCase = getAllProjectsUseCase;
        this.createProjectUseCase = createProjectUseCase;
        this.updateProjectUseCase = updateProjectUseCase;
        this.deleteProjectWithTasksUseCase = deleteProjectWithTasksUseCase;
        this.getProjectStatisticsUseCase = getProjectStatisticsUseCase;
        this.projectRepository = projectRepository;
        this.syncManager = syncManager;

        executor.execute(projectRepository::ensureDefaultProjectExists);

        taskChangeObserver = tasks -> {
            for (String projectId : new HashSet<>(statsCache.keySet())) {
                loadProjectStatistics(projectId);
            }
        };
        allTasksLive = getAllTasksUseCase.executeLive();
        allTasksLive.observeForever(taskChangeObserver);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        allTasksLive.removeObserver(taskChangeObserver);
        executor.shutdownNow();
    }

    public void loadProjectStatistics(String projectGlobalId) {
        executor.execute(() -> {
            try {
                GetProjectStatisticsUseCase.ProjectStatistics stats =
                        getProjectStatisticsUseCase.execute(projectGlobalId);
                statsCache.put(projectGlobalId, stats);
                projectStatsMap.postValue(new HashMap<>(statsCache));
            } catch (Exception e) {
                errorMessage.postValue(e.getMessage());
            }
        });
    }

    public void createProject(String name, String color, OnProjectCreatedListener listener) {
        executor.execute(() -> {
            try {
                boolean created = createProjectUseCase.execute(name, color);
                operationSuccess.postValue(created);
                if (created) {
                    errorMessage.postValue(null);
                    syncManager.scheduleSyncSoon();
                }
                if (listener != null) {
                    listener.onResult(created);
                }
            } catch (DomainValidationException e) {
                String msg = context.getString(e.resId);
                errorMessage.postValue(msg);
                operationSuccess.postValue(false);
                if (listener != null) {
                    listener.onError(msg);
                }
            } catch (Exception e) {
                errorMessage.postValue(e.getMessage());
                operationSuccess.postValue(false);
                if (listener != null) {
                    listener.onError(e.getMessage());
                }
            }
        });
    }

    public void updateProject(Project project,
                              String newName,
                              String newColor,
                              OnProjectUpdatedListener listener) {
        executor.execute(() -> {
            try {
                if (projectRepository.isDefaultProject(project) && !newName.equals(project.getProjectName())) {
                    String msg = context.getString(R.string.error_cannot_rename_default);
                    errorMessage.postValue(msg);
                    operationSuccess.postValue(false);
                    if (listener != null) {
                        listener.onError(msg);
                    }
                    return;
                }
                project.setProjectName(newName);
                project.setColor(newColor);
                updateProjectUseCase.execute(project);
                errorMessage.postValue(null);
                operationSuccess.postValue(true);
                syncManager.scheduleSyncSoon();
                if (listener != null) {
                    listener.onSuccess();
                }
            } catch (DomainValidationException e) {
                String msg = context.getString(e.resId);
                errorMessage.postValue(msg);
                operationSuccess.postValue(false);
                if (listener != null) {
                    listener.onError(msg);
                }
            } catch (Exception e) {
                errorMessage.postValue(e.getMessage());
                operationSuccess.postValue(false);
                if (listener != null) {
                    listener.onError(e.getMessage());
                }
            }
        });
    }

    public void deleteProjectWithTasks(Project project, OnProjectDeletedListener listener) {
        if (projectRepository.isDefaultProject(project)) {
            String msg = context.getString(R.string.error_cannot_delete_default);
            errorMessage.postValue(msg);
            operationSuccess.postValue(false);
            if (listener != null) {
                listener.onError(msg);
            }
            return;
        }

        executor.execute(() -> {
            try {
                deleteProjectWithTasksUseCase.execute(project);
                errorMessage.postValue(null);
                operationSuccess.postValue(true);
                syncManager.scheduleSyncSoon();
                if (listener != null) {
                    listener.onSuccess();
                }
            } catch (Exception e) {
                errorMessage.postValue(e.getMessage());
                operationSuccess.postValue(false);
                if (listener != null) {
                    listener.onError(e.getMessage());
                }
            }
        });
    }

    public boolean isDefaultProject(Project project) {
        return projectRepository.isDefaultProject(project);
    }

    public interface OnProjectCreatedListener {
        void onResult(boolean success);
        void onError(String error);
    }

    public interface OnProjectUpdatedListener {
        void onSuccess();
        void onError(String error);
    }

    public interface OnProjectDeletedListener {
        void onSuccess();
        void onError(String error);
    }
}
