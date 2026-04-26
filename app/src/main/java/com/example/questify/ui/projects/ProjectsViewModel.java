package com.example.questify.ui.projects;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.questify.R;
import com.example.questify.data.repository.ProjectRepository;
import com.example.questify.domain.model.Project;
import com.example.questify.domain.usecase.plans.project.CreateProjectUseCase;
import com.example.questify.domain.usecase.plans.project.DeleteProjectWithTasksUseCase;
import com.example.questify.domain.usecase.plans.project.GetAllProjectsUseCase;
import com.example.questify.domain.usecase.plans.project.GetProjectStatisticsUseCase;
import com.example.questify.domain.usecase.plans.project.UpdateProjectUseCase;
import com.example.questify.sync.SyncManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ProjectsViewModel extends ViewModel {
    private final GetAllProjectsUseCase getAllProjectsUseCase;
    private final CreateProjectUseCase createProjectUseCase;
    private final UpdateProjectUseCase updateProjectUseCase;
    private final DeleteProjectWithTasksUseCase deleteProjectWithTasksUseCase;
    private final GetProjectStatisticsUseCase getProjectStatisticsUseCase;
    private final ProjectRepository projectRepository;
    private final SyncManager syncManager;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final MutableLiveData<Map<String, GetProjectStatisticsUseCase.ProjectStatistics>> projectStatsMap = new MutableLiveData<>();
    private final Map<String, GetProjectStatisticsUseCase.ProjectStatistics> statsCache = new HashMap<>();
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
    public ProjectsViewModel(GetAllProjectsUseCase getAllProjectsUseCase,
                             CreateProjectUseCase createProjectUseCase,
                             UpdateProjectUseCase updateProjectUseCase,
                             DeleteProjectWithTasksUseCase deleteProjectWithTasksUseCase,
                             GetProjectStatisticsUseCase getProjectStatisticsUseCase,
                             ProjectRepository projectRepository,
                             SyncManager syncManager) {
        this.getAllProjectsUseCase = getAllProjectsUseCase;
        this.createProjectUseCase = createProjectUseCase;
        this.updateProjectUseCase = updateProjectUseCase;
        this.deleteProjectWithTasksUseCase = deleteProjectWithTasksUseCase;
        this.getProjectStatisticsUseCase = getProjectStatisticsUseCase;
        this.projectRepository = projectRepository;
        this.syncManager = syncManager;

        executor.execute(projectRepository::ensureDefaultProjectExists);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
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

    public void createProject(String name, String color, OnProjectCreatedListener listener, Context context) {
        executor.execute(() -> {
            try {
                Project newProject = new Project(name, color, context);
                boolean created = createProjectUseCase.execute(newProject);
                operationSuccess.postValue(created);
                if (created) {
                    errorMessage.postValue(null);
                    syncManager.scheduleSyncSoon();
                }
                if (listener != null) {
                    listener.onResult(created);
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
                              OnProjectUpdatedListener listener,
                              Context context) {
        executor.execute(() -> {
            try {
                if (projectRepository.isDefaultProject(project) && !newName.equals(project.getProjectName())) {
                    String error = context.getString(R.string.error_cannot_rename_default);
                    errorMessage.postValue(error);
                    operationSuccess.postValue(false);
                    if (listener != null) {
                        listener.onError(error);
                    }
                    return;
                }
                project.setProjectName(newName, context);
                project.setColor(newColor);
                updateProjectUseCase.execute(project);
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

    public void deleteProjectWithTasks(Project project, OnProjectDeletedListener listener, Context context) {
        if (projectRepository.isDefaultProject(project)) {
            String error = context.getString(R.string.error_cannot_delete_default);
            errorMessage.postValue(error);
            operationSuccess.postValue(false);
            if (listener != null) {
                listener.onError(error);
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