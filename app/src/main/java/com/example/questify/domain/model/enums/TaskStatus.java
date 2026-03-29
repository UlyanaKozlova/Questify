package com.example.questify.domain.model.enums;

public enum TaskStatus {
    ALL(null, "all"),
    DONE(true, "done"),
    NOT_DONE(false, "not-done");

    private final Boolean isDone;
    private final String value;

    TaskStatus(Boolean isDone, String value) {
        this.isDone = isDone;
        this.value = value;
    }

    public Boolean getIsDone() {
        return isDone;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static TaskStatus fromString(String value) {
        for (TaskStatus status : TaskStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + value);
    }
}