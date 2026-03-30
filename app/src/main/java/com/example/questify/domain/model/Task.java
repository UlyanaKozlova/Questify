package com.example.questify.domain.model;

import android.content.Context;

import com.example.questify.R;
import com.example.questify.domain.model.enums.Difficulty;
import com.example.questify.domain.model.enums.Priority;

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
                long updatedAt,
                Context context) {
        checkTaskName(taskName, context);
        checkDeadline(deadline, context);
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
                long deadline,
                Context context) {
        checkTaskName(taskName, context);
        checkDeadline(deadline, context);
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
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return isDone == task.isDone
                && deadline == task.deadline
                && Objects.equals(projectGlobalId, task.projectGlobalId)
                && Objects.equals(userGlobalId, task.userGlobalId)
                && Objects.equals(taskName, task.taskName)
                && Objects.equals(description, task.description)
                && priority == task.priority
                && difficulty == task.difficulty;
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectGlobalId, userGlobalId, isDone, taskName, description, priority, difficulty, deadline);
    }

    private void checkTaskName(String taskName,
                               Context context) {
        if (taskName.length() < 3) {
            throw new IllegalArgumentException(context.getString(R.string.error_task_name_short));
        }
    }

    private void checkDeadline(Long deadline,
                               Context context)
{
    if (deadline == null){
        throw new IllegalArgumentException(context.getString(R.string.error_task_deadline_empty));

    }
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        long todayStartMillis = todayStart.getTimeInMillis();
        if (deadline < todayStartMillis) {
            throw new IllegalArgumentException(context.getString(R.string.error_task_deadline_past));
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

    public void setTaskName(String taskName,
                            Context context) {
        checkTaskName(taskName, context);
        this.taskName = taskName;
    }

    public void setDeadline(long deadline,
                            Context context) {
        checkDeadline(deadline, context);
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
}
