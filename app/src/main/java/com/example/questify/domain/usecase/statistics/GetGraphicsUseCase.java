package com.example.questify.domain.usecase.statistics;

import com.example.questify.data.repository.ProjectRepository;
import com.example.questify.data.repository.TaskRepository;
import com.example.questify.domain.model.Project;
import com.example.questify.domain.model.helpers.ProjectTaskCount;
import com.example.questify.domain.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class GetGraphicsUseCase {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    @Inject
    public GetGraphicsUseCase(TaskRepository taskRepository, ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    public List<ProjectTaskCount> execute() {
        List<Task> tasks = taskRepository.getAll();
        List<Project> projects = projectRepository.getAll();

        Map<String, Project> projectMap = new HashMap<>();
        for (Project project : projects) {
            projectMap.put(project.getGlobalId(), project);
        }

        Map<String, Integer> countMap = new HashMap<>();
        for (Task task : tasks) {
            String projectId = task.getProjectGlobalId();
            countMap.merge(projectId, 1, Integer::sum);
        }
        List<ProjectTaskCount> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
            Project project = projectMap.get(entry.getKey());
            if (project != null && entry.getValue() > 0) {
                result.add(new ProjectTaskCount(
                        project.getProjectName(),
                        project.getColor(),
                        entry.getValue()
                ));
            }
        }
        return result;
    }
}
