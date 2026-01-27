package com.example.questify.domain.model;

public enum Priority {
    VERY_HIGH("very-high"),
    HIGH("high"),
    MEDIUM("medium"),
    LOW("low"),
    VERY_LOW("very-low");

    private final String priority;

    Priority(String priority) {
        this.priority = priority;
    }

    public String getValue() {
        return priority;
    }
}
