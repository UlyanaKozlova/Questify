package com.example.questify.domain.model.enums;

import android.content.Context;
import com.example.questify.R;

public enum TaskStatus {
    ALL(null, R.string.task_status_all),
    DONE(true, R.string.task_status_done),
    NOT_DONE(false, R.string.task_status_not_done);

    private final Boolean isDone;
    private final int stringResId;

    TaskStatus(Boolean isDone, int stringResId) {
        this.isDone = isDone;
        this.stringResId = stringResId;
    }

    public Boolean getIsDone() {
        return isDone;
    }

    public String getString(Context context) {
        return context.getString(stringResId);
    }

    public static TaskStatus fromString(String value, Context context) {
        for (TaskStatus status : TaskStatus.values()) {
            if (status.getString(context).equals(value)) {
                return status;
            }
        }
        return ALL;
    }
}