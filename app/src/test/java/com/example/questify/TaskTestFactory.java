package com.example.questify;

import com.example.questify.domain.model.Task;
import com.example.questify.domain.model.enums.Difficulty;
import com.example.questify.domain.model.enums.Priority;

import java.util.Calendar;

final class TaskTestFactory {

    private TaskTestFactory() {
    }

    static Task task(String globalId,
                     String projectGlobalId,
                     String name,
                     boolean done,
                     Priority priority,
                     Difficulty difficulty,
                     long deadline) {
        Task t = new Task();
        t.setGlobalId(globalId);
        t.setProjectGlobalId(projectGlobalId);
        t.setTaskNameWithoutValidation(name);
        t.setDone(done);
        t.setPriority(priority);
        t.setDifficulty(difficulty);
        t.setDeadlineWithoutValidation(deadline);
        return t;
    }

    static long startOfToday() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    static long endOfToday() {
        return startOfToday() + 24L * 60 * 60 * 1000 - 1;
    }

    static long daysFromNow(int days) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, days);
        return c.getTimeInMillis();
    }
}
