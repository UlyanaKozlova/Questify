package com.example.questify.domain.usecase.plans.tasks.sort;

import com.example.questify.domain.model.Task;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class SortTasksUseCase {

    private final Map<SortType, Comparator<Task>> comparators = new HashMap<>();

    @Inject
    public SortTasksUseCase() {
        comparators.put(SortType.DEADLINE, Comparator.comparingLong(Task::getDeadline));
        comparators.put(SortType.PRIORITY, Comparator.comparing(Task::getPriority));
        comparators.put(SortType.DIFFICULTY, Comparator.comparing(Task::getDifficulty));
        comparators.put(SortType.UPDATED_AT, Comparator.comparingLong(Task::getUpdatedAt));
    }

    public List<Task> execute(List<Task> tasks, SortType type, SortOrder order) {
        Comparator<Task> comparator = comparators.get(type);
        if (order == SortOrder.DESC) {
            comparator = comparator == null ? comparator: comparator.reversed();
        }
        tasks.sort(comparator);
        return tasks;
    }
}
