package com.example.questify.domain.model.enums;

import android.content.Context;
import com.example.questify.R;

public enum Priority {
    VERY_HIGH(R.string.priority_very_high, 5),
    HIGH(R.string.priority_high, 4),
    MEDIUM(R.string.priority_medium, 3),
    LOW(R.string.priority_low, 2),
    VERY_LOW(R.string.priority_very_low, 1);

    private final int stringResId;
    private final int weight;

    Priority(int stringResId, int weight) {
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