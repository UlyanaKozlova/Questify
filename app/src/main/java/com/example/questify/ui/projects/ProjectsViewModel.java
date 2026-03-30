package com.example.questify.ui.projects;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.questify.data.repository.ProjectRepository;
import com.example.questify.domain.model.Project;
import com.example.questify.domain.usecase.plans.project.CreateProjectUseCase;
import com.example.questify.domain.usecase.plans.project.DeleteProjectUseCase;
import com.example.questify.domain.usecase.plans.project.DeleteProjectWithTasksUseCase;
import com.example.questify.domain.usecase.plans.project.GetAllProjectsUseCase;
import com.example.questify.domain.usecase.plans.project.GetProjectStatisticsUseCase;
import com.example.questify.domain.usecase.plans.project.UpdateProjectUseCase;

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
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final MutableLiveData<Map<String, GetProjectStatisticsUseCase.ProjectStatistics>> projectStatsMap = new MutableLiveData<>();
    private final Map<String, GetProjectStatisticsUseCase.ProjectStatistics> statsCache = new HashMap<>();

    @Inject
    public ProjectsViewModel(GetAllProjectsUseCase getAllProjectsUseCase,
                             CreateProjectUseCase createProjectUseCase,
                             UpdateProjectUseCase updateProjectUseCase,
                             DeleteProjectWithTasksUseCase deleteProjectWithTasksUseCase,
                             GetProjectStatisticsUseCase getProjectStatisticsUseCase,
                             ProjectRepository projectRepository) {
        this.getAllProjectsUseCase = getAllProjectsUseCase;
        this.createProjectUseCase = createProjectUseCase;
        this.updateProjectUseCase = updateProjectUseCase;
        this.deleteProjectWithTasksUseCase = deleteProjectWithTasksUseCase;
        this.getProjectStatisticsUseCase = getProjectStatisticsUseCase;

        executor.execute(projectRepository::ensureDefaultProjectExists);
    }

    public LiveData<List<Project>> getAllProjects() {
        return getAllProjectsUseCase.executeLive();
    }

    public LiveData<Map<String, GetProjectStatisticsUseCase.ProjectStatistics>> getProjectStatisticsMap() {
        return projectStatsMap;
    }
    public void loadProjectStatistics(String projectGlobalId) {
        executor.execute(() -> {
            GetProjectStatisticsUseCase.ProjectStatistics stats =
                    getProjectStatisticsUseCase.execute(projectGlobalId);
            statsCache.put(projectGlobalId, stats);
            projectStatsMap.postValue(new HashMap<>(statsCache));
        });
    }
    public void createProject(String name, String color, OnProjectCreatedListener listener) {
        executor.execute(() -> {
            try {
                Project newProject = new Project(name, color);
                boolean created = createProjectUseCase.execute(newProject);
                if (listener != null) {
                    listener.onResult(created);
                }
            } catch (Exception e) {
                if (listener != null) {
                    listener.onError(e.getMessage());
                }
            }
        });
    }

    public void updateProject(Project project, String newName, String newColor, OnProjectUpdatedListener listener) {
        executor.execute(() -> {
            try {
                if (ProjectRepository.DEFAULT_PROJECT_NAME.equals(project.getProjectName())
                        && !newName.equals(project.getProjectName())) {
                    if (listener != null) {
                        listener.onError("Нельзя изменить название стандартного проекта");
                    }
                    return;
                }
                project.setProjectName(newName);
                project.setColor(newColor);
                updateProjectUseCase.execute(project);
                if (listener != null) {
                    listener.onSuccess();
                }
            } catch (Exception e) {
                if (listener != null) {
                    listener.onError(e.getMessage());
                }
            }
        });
    }
    public void deleteProjectWithTasks(Project project, OnProjectDeletedListener listener) {
        if (ProjectRepository.DEFAULT_PROJECT_NAME.equals(project.getProjectName())) {
            if (listener != null) {
                listener.onError("Нельзя удалить стандартный проект");
            }
            return;
        }

        executor.execute(() -> {
            try {
                deleteProjectWithTasksUseCase.execute(project);
                if (listener != null) {
                    listener.onSuccess();
                }
            } catch (Exception e) {
                if (listener != null) {
                    listener.onError(e.getMessage());
                }
            }
        });
    }

    public boolean isDefaultProject(Project project) {
        return ProjectRepository.DEFAULT_PROJECT_NAME.equals(project.getProjectName());
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