package com.example.questify.domain.model;

public enum Priority {
    VERY_HIGH("very-high", 5),
    HIGH("high", 4),
    MEDIUM("medium", 3),
    LOW("low", 2),
    VERY_LOW("very-low", 1);

    private final String priority;
    private final int weight;

    Priority(String priority, int weight) {
        this.priority = priority;
        this.weight = weight;
    }

    public String getPriority() {
        return priority;
    }

    public int getWeight() {
        return weight;
    }
}
