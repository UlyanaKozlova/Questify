package com.example.questify.domain.usecase.plans.tasks.filter;

import com.example.questify.domain.model.Task;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class FilterTasksUseCase {
    @Inject
    public FilterTasksUseCase() {
    }

    public List<Task> execute(List<Task> tasks, TaskFilter filter) {
        return tasks.stream()
                .filter(task ->
                        filter.getPriority() == null || task.getPriority() == filter.getPriority())
                .filter(task ->
                        filter.getDifficulty() == null || task.getDifficulty() == filter.getDifficulty())
                .filter(task ->
                        (filter.getStartDate() == null || task.getDeadline() >= filter.getStartDate()) &&
                                (filter.getEndDate() == null || task.getDeadline() <= filter.getEndDate()))
                .filter(task ->
                        filter.getIsDone() == null || task.isDone() == filter.getIsDone())
                .filter(task -> filter.getProjectGlobalId() == null ||
                        task.getProjectGlobalId().equals(filter.getProjectGlobalId()))
                .collect(Collectors.toList());
    }
}
