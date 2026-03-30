package com.example.questify.domain.usecase.plans.project;

import com.example.questify.data.repository.TaskRepository;
import com.example.questify.domain.model.Task;

import java.util.List;

import javax.inject.Inject;

public class GetProjectStatisticsUseCase {
    private final TaskRepository taskRepository;

    @Inject
    public GetProjectStatisticsUseCase(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public ProjectStatistics execute(String projectGlobalId) {
        List<Task> tasks = taskRepository.getTasksByProject(projectGlobalId);
        int total = tasks.size();
        int completed = (int) tasks.stream().filter(Task::isDone).count();
        int overdue = (int) tasks.stream()
                .filter(t -> !t.isDone() && t.getDeadline() < System.currentTimeMillis())
                .count();

        return new ProjectStatistics(total, completed, overdue);
    }

    public static class ProjectStatistics {
        public final int total;
        public final int completed;
        public final int overdue;

        public ProjectStatistics(int total, int completed, int overdue) {
            this.total = total;
            this.completed = completed;
            this.overdue = overdue;
        }

        public int getProgressPercent() {
            return total == 0 ? 0 : (completed * 100 / total);
        }
    }
}