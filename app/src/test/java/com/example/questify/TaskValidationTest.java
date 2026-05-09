package com.example.questify;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.example.questify.domain.model.Task;
import com.example.questify.domain.model.enums.Difficulty;
import com.example.questify.domain.model.enums.Priority;
import com.example.questify.util.exception.DomainValidationException;

import org.junit.Test;

import java.util.Calendar;

public class TaskValidationTest {

    private static final String PROJECT_ID = "project-1";

    private static long futureDeadline() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 1);
        return c.getTimeInMillis();
    }

    private static long startOfTodayMillis() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    private static long yesterdayMillis() {
        return startOfTodayMillis() - 24L * 60 * 60 * 1000;
    }

    private static long daysFromNowMillis(int days) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, days);
        return c.getTimeInMillis();
    }

    private static String repeat(char ch, int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) sb.append(ch);
        return sb.toString();
    }

    private static Task buildWithName(String name) {
        return new Task(PROJECT_ID, name, "desc", Priority.MEDIUM, Difficulty.MEDIUM, futureDeadline());
    }

    private static Task buildWithDeadline(long deadline) {
        return new Task(PROJECT_ID, "Valid name", "desc", Priority.MEDIUM, Difficulty.MEDIUM, deadline);
    }

    @Test
    public void name_empty_throws() {
        try {
            buildWithName("");
            fail("expected DomainValidationException");
        } catch (DomainValidationException e) {
            assertEquals(R.string.error_task_name_short, e.resId);
        }
    }

    @Test
    public void name_oneChar_throws() {
        try {
            buildWithName("a");
            fail("expected DomainValidationException");
        } catch (DomainValidationException e) {
            assertEquals(R.string.error_task_name_short, e.resId);
        }
    }

    @Test
    public void name_twoChars_throws() {
        try {
            buildWithName("ab");
            fail("expected DomainValidationException");
        } catch (DomainValidationException e) {
            assertEquals(R.string.error_task_name_short, e.resId);
        }
    }

    @Test
    public void name_threeChars_passes() {
        Task t = buildWithName("abc");
        assertEquals("abc", t.getTaskName());
    }

    @Test
    public void name_fiftyChars_passes() {
        String name = repeat('x', 50);
        Task t = buildWithName(name);
        assertEquals(name, t.getTaskName());
    }

    @Test
    public void deadline_yesterday_throws() {
        try {
            buildWithDeadline(yesterdayMillis());
            fail("expected DomainValidationException");
        } catch (DomainValidationException e) {
            assertEquals(R.string.error_task_deadline_past, e.resId);
        }
    }

    @Test
    public void deadline_today_passes() {
        Task t = buildWithDeadline(startOfTodayMillis());
        assertEquals(startOfTodayMillis(), t.getDeadline());
    }

    @Test
    public void deadline_tomorrow_passes() {
        long deadline = daysFromNowMillis(1);
        Task t = buildWithDeadline(deadline);
        assertEquals(deadline, t.getDeadline());
    }

    @Test
    public void deadline_inThirtyDays_passes() {
        long deadline = daysFromNowMillis(30);
        Task t = buildWithDeadline(deadline);
        assertEquals(deadline, t.getDeadline());
    }
}
