package com.example.questify.domain.usecase.statistics;

import com.example.questify.data.repository.TaskRepository;
import com.example.questify.domain.model.Task;
import com.example.questify.domain.model.helpers.TaskStatistics;

import java.util.List;

import javax.inject.Inject;

public class GetTasksAmountUseCase {

    private final TaskRepository taskRepository;

    @Inject
    public GetTasksAmountUseCase(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public TaskStatistics execute() {
        List<Task> tasks = taskRepository.getAll();
        long now = System.currentTimeMillis();
        int total = tasks.size();
        int completed = (int) tasks.stream().filter(Task::isDone).count();
        int overdue = (int) tasks.stream()
                .filter(task
                        -> !task.isDone() && task.getDeadline() > 0 && task.getDeadline() < now)
                .count();
        return new TaskStatistics(total, completed, overdue);
    }
}
