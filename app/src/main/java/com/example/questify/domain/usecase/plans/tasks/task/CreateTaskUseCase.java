package com.example.questify.domain.usecase.plans.tasks.task;

import com.example.questify.UserSession;
import com.example.questify.data.repository.TaskRepository;
import com.example.questify.domain.model.Difficulty;
import com.example.questify.domain.model.Priority;
import com.example.questify.domain.model.Task;

import javax.inject.Inject;

public class CreateTaskUseCase {
    private final TaskRepository taskRepository;
    private final UserSession session;

    @Inject
    public CreateTaskUseCase(TaskRepository taskRepository, UserSession session) {
        this.taskRepository = taskRepository;
        this.session = session;
    }

    public void execute(String taskName,
                        String description,
                        Long deadline,
                        String projectGlobalId,
                        Difficulty difficulty,
                        Priority priority) {
        taskRepository.save(new Task(projectGlobalId,
                session.getUserGlobalId(),
                taskName,
                description,
                priority,
                difficulty,
                deadline));
        // todo если проекта не существует то создать
        // todo сгенерировать подзадачи
        // todo проверка на существование такой же задачи
    }
}