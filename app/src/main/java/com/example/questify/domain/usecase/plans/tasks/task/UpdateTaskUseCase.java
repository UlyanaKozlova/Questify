package com.example.questify.domain.usecase.plans.tasks.task;

import android.content.Context;

import com.example.questify.data.repository.ProjectRepository;
import com.example.questify.data.repository.TaskRepository;
import com.example.questify.domain.model.Project;
import com.example.questify.domain.model.Task;
import com.example.questify.domain.model.enums.Difficulty;
import com.example.questify.domain.model.enums.Priority;

import javax.inject.Inject;

public class UpdateTaskUseCase {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    @Inject
    public UpdateTaskUseCase(TaskRepository taskRepository,
                             ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    public void execute(Task taskToEdit,
                        String name,
                        String description,
                        long deadline,
                        String projectName,
                        Priority priority,
                        Difficulty difficulty,
                        boolean isDone,
                        Context context) {
        Project project = projectRepository.getByProjectName(projectName);
        if (project == null) {
            project = new Project(projectName, context);
            projectRepository.save(project);
        }
        taskToEdit.setTaskName(name, context);
        taskToEdit.setDescription(description);
        taskToEdit.setDeadline(deadline, context);
        taskToEdit.setProjectGlobalId(project.getGlobalId());
        taskToEdit.setPriority(priority);
        taskToEdit.setDifficulty(difficulty);
        taskToEdit.setDone(isDone);
        taskToEdit.setUpdatedAt(System.currentTimeMillis());
        taskRepository.update(taskToEdit);
    }

    public void updateDoneStatus(Task taskToEdit, boolean isDone) {
        taskToEdit.setDone(isDone);
        taskToEdit.setUpdatedAt(System.currentTimeMillis());
        taskRepository.update(taskToEdit);
    }
}