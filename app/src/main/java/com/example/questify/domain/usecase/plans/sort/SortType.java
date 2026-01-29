package com.example.questify.domain.usecase.plans.sort;

import java.util.Arrays;

public enum SortType {
    NONE(0),
    DEADLINE(1),
    PRIORITY(2),
    DIFFICULTY(3),
    UPDATED_AT(4);

    private final int position;

    SortType(int position) {
        this.position = position;
    }

    public static SortType getType(int position) {
        return Arrays
                .stream(values())
                .filter(type -> type.position == position)
                .findFirst()
                .orElse(NONE);
    }
}
