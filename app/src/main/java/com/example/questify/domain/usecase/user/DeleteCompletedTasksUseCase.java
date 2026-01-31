package com.example.questify.domain.usecase.user;

import com.example.questify.domain.model.Task;
import com.example.questify.domain.usecase.plans.tasks.task.DeleteTaskUseCase;
import com.example.questify.domain.usecase.plans.tasks.task.GetAllTasksUseCase;

import java.util.List;

import javax.inject.Inject;

public class DeleteCompletedTasksUseCase {
    private final GetAllTasksUseCase getAllTasksUseCase;
    private final DeleteTaskUseCase deleteTaskUseCase;

    @Inject
    public DeleteCompletedTasksUseCase(GetAllTasksUseCase getAllTasksUseCase,
                                       DeleteTaskUseCase deleteTaskUseCase) {
        this.getAllTasksUseCase = getAllTasksUseCase;
        this.deleteTaskUseCase = deleteTaskUseCase;
    }

    public void execute() {
        List<Task> tasks = getAllTasksUseCase.execute();
        for (Task task : tasks) {
            if (task.isDone()) {
                deleteTaskUseCase.execute(task);
            }
        }
    }
}
