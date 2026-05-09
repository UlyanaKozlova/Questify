package com.example.questify;

import static com.example.questify.TaskTestFactory.daysFromNow;
import static com.example.questify.TaskTestFactory.endOfToday;
import static com.example.questify.TaskTestFactory.task;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.questify.data.repository.TaskRepository;
import com.example.questify.domain.model.Task;
import com.example.questify.domain.model.enums.Difficulty;
import com.example.questify.domain.model.enums.Priority;
import com.example.questify.domain.model.helpers.TaskStatistics;
import com.example.questify.domain.usecase.statistics.GetTasksAmountUseCase;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

public class GetTasksAmountUseCaseTest {

    private TaskRepository taskRepository;
    private GetTasksAmountUseCase useCase;

    @Before
    public void setUp() {
        taskRepository = mock(TaskRepository.class);
        useCase = new GetTasksAmountUseCase(taskRepository);
    }

    private static Task overdueOpen() {
        return task("o", "p", "Overdue", false, Priority.MEDIUM, Difficulty.MEDIUM, daysFromNow(-1));
    }

    private static Task completedYesterday() {
        return task("c-y", "p", "Done yest.", true, Priority.MEDIUM, Difficulty.MEDIUM, daysFromNow(-1));
    }

    private static Task openTomorrow() {
        return task("o-t", "p", "Open tom.", false, Priority.MEDIUM, Difficulty.MEDIUM, daysFromNow(1));
    }

    private static Task openToday() {
        // Конец дня — заведомо больше now → не просрочена.
        return task("o-d", "p", "Open today", false, Priority.MEDIUM, Difficulty.MEDIUM, endOfToday());
    }

    @Test
    public void total_countsAllTasks() {
        when(taskRepository.getAll()).thenReturn(Arrays.asList(
                overdueOpen(), completedYesterday(), openTomorrow(), openToday()
        ));
        TaskStatistics s = useCase.execute();
        assertEquals(4, s.total);
    }

    @Test
    public void completed_countsOnlyDone() {
        when(taskRepository.getAll()).thenReturn(Arrays.asList(
                overdueOpen(), completedYesterday(), openTomorrow()
        ));
        TaskStatistics s = useCase.execute();
        assertEquals(1, s.completed);
    }

    @Test
    public void overdue_countsOnlyOpenPastDeadline() {
        when(taskRepository.getAll()).thenReturn(Arrays.asList(
                overdueOpen(),
                completedYesterday(),
                openTomorrow(),
                openToday()
        ));
        TaskStatistics s = useCase.execute();
        assertEquals(1, s.overdue);
    }

    @Test
    public void completionPercent_isCorrect() {
        when(taskRepository.getAll()).thenReturn(Arrays.asList(
                completedYesterday(), completedYesterday(), openTomorrow(), openTomorrow()
        ));
        TaskStatistics s = useCase.execute();
        assertEquals(50, s.getCompletionPercent());
    }

    @Test
    public void emptyList_allZeros() {
        when(taskRepository.getAll()).thenReturn(Collections.<Task>emptyList());
        TaskStatistics s = useCase.execute();
        assertEquals(0, s.total);
        assertEquals(0, s.completed);
        assertEquals(0, s.overdue);
        assertEquals(0, s.getCompletionPercent());
    }

    @Test
    public void overdue_yesterdayOpen_isOverdue() {
        when(taskRepository.getAll()).thenReturn(Collections.singletonList(overdueOpen()));
        assertEquals(1, useCase.execute().overdue);
    }

    @Test
    public void overdue_yesterdayDone_notOverdue() {
        when(taskRepository.getAll()).thenReturn(Collections.singletonList(completedYesterday()));
        assertEquals(0, useCase.execute().overdue);
    }

    @Test
    public void overdue_todayEndOfDay_notOverdue() {
        when(taskRepository.getAll()).thenReturn(Collections.singletonList(openToday()));
        assertEquals(0, useCase.execute().overdue);
    }

    @Test
    public void overdue_tomorrowOpen_notOverdue() {
        when(taskRepository.getAll()).thenReturn(Collections.singletonList(openTomorrow()));
        assertEquals(0, useCase.execute().overdue);
    }

    @Test
    public void total_handlesMultipleTasks() {
        when(taskRepository.getAll()).thenReturn(Arrays.asList(
                overdueOpen(), openTomorrow(), openTomorrow(), openTomorrow(), openTomorrow()
        ));
        TaskStatistics s = useCase.execute();
        assertEquals(5, s.total);
    }
}
