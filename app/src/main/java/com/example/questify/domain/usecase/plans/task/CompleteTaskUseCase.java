package com.example.questify.domain.usecase.plans.task;

import com.example.questify.UserSession;
import com.example.questify.data.repository.TaskRepository;
import com.example.questify.domain.model.Task;

import javax.inject.Inject;

public class CompleteTaskUseCase {

    private final TaskRepository taskRepository;
    private final UserSession userSession;

    @Inject
    public CompleteTaskUseCase(TaskRepository taskRepository,
                               UserSession userSession) {
        this.taskRepository = taskRepository;
        this.userSession = userSession;
    }

    public void execute(Task task, boolean isDone) {
        task.setDone(isDone);
        taskRepository.update(task);
        // todo обновлять монеты, уровень
    }
}
