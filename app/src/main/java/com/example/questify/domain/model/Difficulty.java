package com.example.questify.domain.model;

public enum Difficulty {
    VERY_DIFFICULT("very-difficult", 5),
    DIFFICULT("difficult", 4),
    MEDIUM("medium", 3),
    EASY("easy", 2),
    VERY_EASY("very-easy", 1);

    private final String difficulty;
    private final int weight;

    Difficulty(String difficulty, int weight) {
        this.difficulty = difficulty;
        this.weight = weight;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public int getWeight() {
        return weight;
    }
}
