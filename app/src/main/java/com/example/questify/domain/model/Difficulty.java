package com.example.questify.domain.model;

public enum Difficulty {
    VERY_DIFFICULT("very-difficult"),
    DIFFICULT("difficult"),
    MEDIUM("medium"),
    EASY("easy"),
    VERY_EASY("very-easy");

    private final String difficulty;

    Difficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getValue() {
        return difficulty;
    }
}
