package com.example.questify;

import static com.example.questify.TaskTestFactory.daysFromNow;
import static com.example.questify.TaskTestFactory.task;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.example.questify.domain.model.Task;
import com.example.questify.domain.model.enums.Difficulty;
import com.example.questify.domain.model.enums.Priority;
import com.example.questify.domain.usecase.plans.tasks.filter.FilterTasksUseCase;
import com.example.questify.domain.usecase.plans.tasks.filter.TaskFilter;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FilterTasksUseCaseTest {

    private static final String PROJ_A = "proj-a";
    private static final String PROJ_B = "proj-b";

    private FilterTasksUseCase useCase;
    private List<Task> sample;

    @Before
    public void setUp() {
        useCase = new FilterTasksUseCase();
        long deadline = daysFromNow(1);
        sample = Arrays.asList(
                task("t1", PROJ_A, "High+Hard",   false, Priority.VERY_HIGH, Difficulty.VERY_DIFFICULT, deadline),
                task("t2", PROJ_A, "High+Easy",   false, Priority.VERY_HIGH, Difficulty.VERY_EASY,      deadline),
                task("t3", PROJ_B, "Low+Easy",    false, Priority.VERY_LOW,  Difficulty.VERY_EASY,      deadline),
                task("t4", PROJ_B, "Medium+Med",  false, Priority.MEDIUM,    Difficulty.MEDIUM,         deadline)
        );
    }

    @Test
    public void filter_byHighPriority_returnsOnlyHighPriority() {
        TaskFilter filter = new TaskFilter(Priority.VERY_HIGH, null, null, null, null, null);
        List<Task> result = useCase.execute(sample, filter);
        assertEquals(2, result.size());
        for (Task t : result) {
            assertEquals(Priority.VERY_HIGH, t.getPriority());
        }
    }

    @Test
    public void filter_byEasyDifficulty_returnsOnlyEasy() {
        TaskFilter filter = new TaskFilter(null, Difficulty.VERY_EASY, null, null, null, null);
        List<Task> result = useCase.execute(sample, filter);
        assertEquals(2, result.size());
        for (Task t : result) {
            assertEquals(Difficulty.VERY_EASY, t.getDifficulty());
        }
    }

    @Test
    public void filter_byProject_returnsOnlyMatching() {
        TaskFilter filter = new TaskFilter(null, null, null, null, null, PROJ_B);
        List<Task> result = useCase.execute(sample, filter);
        assertEquals(2, result.size());
        for (Task t : result) {
            assertEquals(PROJ_B, t.getProjectGlobalId());
        }
    }

    @Test
    public void filter_byPriorityAndDifficulty_returnsOnlyBoth() {
        TaskFilter filter = new TaskFilter(Priority.VERY_HIGH, Difficulty.VERY_DIFFICULT, null, null, null, null);
        List<Task> result = useCase.execute(sample, filter);
        assertEquals(1, result.size());
        assertEquals("t1", result.get(0).getGlobalId());
    }

    @Test
    public void filter_noMatch_returnsEmpty() {
        TaskFilter filter = new TaskFilter(Priority.VERY_HIGH, Difficulty.MEDIUM, null, null, null, null);
        List<Task> result = useCase.execute(sample, filter);
        assertTrue(result.isEmpty());
    }

    @Test
    public void filter_emptyInput_returnsEmpty() {
        TaskFilter filter = new TaskFilter(null, null, null, null, null, null);
        List<Task> result = useCase.execute(Collections.emptyList(), filter);
        assertTrue(result.isEmpty());
    }
}
