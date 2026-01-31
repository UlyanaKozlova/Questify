package com.example.questify.ui.tasks.create;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.questify.domain.model.Difficulty;
import com.example.questify.domain.model.Priority;
import com.example.questify.domain.model.Project;
import com.example.questify.domain.usecase.plans.project.CreateProjectUseCase;
import com.example.questify.domain.usecase.plans.project.GetProjectsUseCase;
import com.example.questify.domain.usecase.plans.tasks.task.CreateTaskUseCase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class TaskCreateViewModel extends ViewModel {
    private final CreateTaskUseCase createTaskUseCase;
    private final GetProjectsUseCase getProjectsUseCase;
    private final CreateProjectUseCase createProjectUseCase;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final MutableLiveData<List<Project>> projects = new MutableLiveData<>();

    @Inject
    public TaskCreateViewModel(CreateTaskUseCase createTaskUseCase,
                               GetProjectsUseCase getProjectsUseCase,
                               CreateProjectUseCase createProjectUseCase) {
        this.createTaskUseCase = createTaskUseCase;
        this.getProjectsUseCase = getProjectsUseCase;
        this.createProjectUseCase = createProjectUseCase;

        loadProjects();
    }

    public void loadProjects() {
        executor.execute(() -> {
            List<Project> list = getProjectsUseCase.execute();
            projects.postValue(list);
        });
    }

    public void saveTask(String taskName,
                         String description,
                         Long deadline,
                         String projectGlobalId,
                         Difficulty difficulty,
                         Priority priority) {
        executor.execute(() -> createTaskUseCase
                .execute(taskName,
                        description,
                        deadline,
                        projectGlobalId,
                        difficulty,
                        priority));
    }
}
