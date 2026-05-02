package com.example.questify.domain.model;

import com.example.questify.R;
import com.example.questify.domain.model.enums.Difficulty;
import com.example.questify.domain.model.enums.Priority;
import com.example.questify.util.exception.DomainValidationException;

import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;

public class Task {
    private long localId;

    private String globalId;

    private String projectGlobalId;
    private String userGlobalId;

    private boolean isDone;
    private String taskName;
    private String description;

    private Priority priority;
    private Difficulty difficulty;

    private long deadline;

    private long updatedAt;

    public Task(long localId,
                String globalId,
                String projectGlobalId,
                String userGlobalId,
                boolean isDone,
                String taskName,
                String description,
                Priority priority,
                Difficulty difficulty,
                long deadline,
                long updatedAt) {
        this.localId = localId;
        this.globalId = globalId;
        this.projectGlobalId = projectGlobalId;
        this.userGlobalId = userGlobalId;
        this.isDone = isDone;
        this.taskName = taskName;
        this.description = description;
        this.priority = priority;
        this.difficulty = difficulty;
        this.deadline = deadline;
        this.updatedAt = updatedAt;
    }

    public Task(String projectGlobalId,
                String taskName,
                String description,
                Priority priority,
                Difficulty difficulty,
                long deadline) {
        checkTaskName(taskName);
        checkDeadline(deadline);
        this.globalId = UUID.randomUUID().toString();
        this.projectGlobalId = projectGlobalId;
        this.isDone = false;
        this.taskName = taskName;
        this.description = description;
        this.priority = priority;
        this.difficulty = difficulty;
        this.deadline = deadline;
        this.updatedAt = System.currentTimeMillis();
    }

    public Task() {
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Task task = (Task) o;
        return Objects.equals(globalId, task.globalId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(globalId);
    }

    private static void checkTaskName(String taskName) {
        if (taskName == null || taskName.length() < 3) {
            throw new DomainValidationException(R.string.error_task_name_short);
        }
    }

    private static void checkDeadline(long deadline) {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        if (deadline < todayStart.getTimeInMillis()) {
            throw new DomainValidationException(R.string.error_task_deadline_past);
        }
    }

    public String getGlobalId() {
        return globalId;
    }

    public void setGlobalId(String globalId) {
        this.globalId = globalId;
    }

    public String getProjectGlobalId() {
        return projectGlobalId;
    }

    public void setProjectGlobalId(String projectGlobalId) {
        this.projectGlobalId = projectGlobalId;
    }

    public void setTaskName(String taskName) {
        checkTaskName(taskName);
        this.taskName = taskName;
    }

    public void setDeadline(long deadline) {
        checkDeadline(deadline);
        this.deadline = deadline;
    }

    public String getUserGlobalId() {
        return userGlobalId;
    }

    public void setUserGlobalId(String userGlobalId) {
        this.userGlobalId = userGlobalId;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public long getDeadline() {
        return deadline;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public long getLocalId() {
        return localId;
    }

    public void setLocalId(long localId) {
        this.localId = localId;
    }

    public void setTaskNameWithoutValidation(String taskName) {
        this.taskName = taskName;
    }

    public void setDeadlineWithoutValidation(long deadline) {
        this.deadline = deadline;
    }
}
