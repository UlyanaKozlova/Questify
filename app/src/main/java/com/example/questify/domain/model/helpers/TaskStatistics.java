package com.example.questify.domain.model.helpers;

public class TaskStatistics {
    public final int total;
    public final int completed;
    public final int overdue;

    public TaskStatistics(int total, int completed, int overdue) {
        this.total = total;
        this.completed = completed;
        this.overdue = overdue;
    }

    public int getCompletionPercent() {
        return total == 0 ? 0 : (completed * 100 / total);
    }

    public int getOverduePercent() {
        return total == 0 ? 0 : (overdue * 100 / total);
    }
}
