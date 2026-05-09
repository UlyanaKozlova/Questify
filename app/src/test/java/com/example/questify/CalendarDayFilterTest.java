package com.example.questify;

import static com.example.questify.TaskTestFactory.daysFromNow;
import static com.example.questify.TaskTestFactory.task;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.example.questify.domain.model.Task;
import com.example.questify.domain.model.enums.Difficulty;
import com.example.questify.domain.model.enums.Priority;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class CalendarDayFilterTest {

    private static List<Task> tasksForDay(List<Task> source, Date day) {
        Calendar start = Calendar.getInstance();
        start.setTime(day);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);
        long startTime = start.getTimeInMillis();
        start.add(Calendar.DAY_OF_MONTH, 1);
        long endTime = start.getTimeInMillis();

        List<Task> result = new ArrayList<>();
        for (Task t : source) {
            if (t.getDeadline() >= startTime && t.getDeadline() < endTime) {
                result.add(t);
            }
        }
        result.sort(Comparator.comparingLong(Task::getDeadline));
        return result;
    }

    private static Date dateOf(long millis) {
        return new Date(millis);
    }

    @Test
    public void selectedDay_multipleTasks_returnsAllSorted() {
        long today = daysFromNow(0);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(today);
        c.set(Calendar.HOUR_OF_DAY, 9);
        long morning = c.getTimeInMillis();
        c.set(Calendar.HOUR_OF_DAY, 18);
        long evening = c.getTimeInMillis();

        Task tEvening = task("e", "p", "Evening", false, Priority.MEDIUM, Difficulty.MEDIUM, evening);
        Task tMorning = task("m", "p", "Morning", false, Priority.MEDIUM, Difficulty.MEDIUM, morning);
        Task tTomorrow = task("t", "p", "Tomorrow", false, Priority.MEDIUM, Difficulty.MEDIUM, daysFromNow(1));

        List<Task> result = tasksForDay(java.util.Arrays.asList(tEvening, tMorning, tTomorrow), dateOf(today));

        assertEquals(2, result.size());
        assertEquals("m", result.get(0).getGlobalId());
        assertEquals("e", result.get(1).getGlobalId());
    }

    @Test
    public void selectedDay_noMatchingTasks_returnsEmpty() {
        Task other = task("x", "p", "Other", false, Priority.MEDIUM, Difficulty.MEDIUM, daysFromNow(5));
        List<Task> result = tasksForDay(Collections.singletonList(other), dateOf(daysFromNow(0)));
        assertTrue(result.isEmpty());
    }

    @Test
    public void selectedDay_tasksFromOtherDays_excluded() {
        Task today = task("today", "p", "T", false, Priority.MEDIUM, Difficulty.MEDIUM, daysFromNow(0));
        Task tomorrow = task("tom", "p", "T+1", false, Priority.MEDIUM, Difficulty.MEDIUM, daysFromNow(1));
        Task yesterday = task("yest", "p", "T-1", false, Priority.MEDIUM, Difficulty.MEDIUM, daysFromNow(-1));

        List<Task> result = tasksForDay(
                java.util.Arrays.asList(today, tomorrow, yesterday),
                dateOf(daysFromNow(0))
        );

        assertEquals(1, result.size());
        assertEquals("today", result.get(0).getGlobalId());
    }
}
