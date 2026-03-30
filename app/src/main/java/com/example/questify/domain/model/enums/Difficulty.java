package com.example.questify.domain.model.enums;

import android.content.Context;
import com.example.questify.R;

public enum Difficulty {
    VERY_DIFFICULT(R.string.difficulty_very_difficult, 5),
    DIFFICULT(R.string.difficulty_difficult, 4),
    MEDIUM(R.string.difficulty_medium, 3),
    EASY(R.string.difficulty_easy, 2),
    VERY_EASY(R.string.difficulty_very_easy, 1);

    private final int stringResId;
    private final int weight;

    Difficulty(int stringResId, int weight) {
        this.stringResId = stringResId;
        this.weight = weight;
    }

    public String getString(Context context) {
        return context.getString(stringResId);
    }

    public int getWeight() {
        return weight;
    }
}