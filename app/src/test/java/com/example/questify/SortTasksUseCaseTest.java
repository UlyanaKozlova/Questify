package com.example.questify;

import static com.example.questify.TaskTestFactory.daysFromNow;
import static com.example.questify.TaskTestFactory.task;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.example.questify.domain.model.Task;
import com.example.questify.domain.model.enums.Difficulty;
import com.example.questify.domain.model.enums.Priority;
import com.example.questify.domain.usecase.plans.tasks.sort.SortOrder;
import com.example.questify.domain.usecase.plans.tasks.sort.SortTasksUseCase;
import com.example.questify.domain.usecase.plans.tasks.sort.SortType;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SortTasksUseCaseTest {

    private SortTasksUseCase useCase;

    @Before
    public void setUp() {
        useCase = new SortTasksUseCase();
    }

    private static List<Task> mutable(Task... tasks) {
        return new ArrayList<>(Arrays.asList(tasks));
    }

    @Test
    public void sort_deadlineAsc_nearestFirst() {
        Task far = task("t-far", "p", "Far", false, Priority.MEDIUM, Difficulty.MEDIUM, daysFromNow(10));
        Task near = task("t-near", "p", "Near", false, Priority.MEDIUM, Difficulty.MEDIUM, daysFromNow(1));
        Task mid = task("t-mid", "p", "Mid", false, Priority.MEDIUM, Difficulty.MEDIUM, daysFromNow(5));

        List<Task> result = useCase.execute(mutable(far, near, mid), SortType.DEADLINE, SortOrder.ASC);

        assertEquals("t-near", result.get(0).getGlobalId());
        assertEquals("t-far", result.get(2).getGlobalId());
    }

    @Test
    public void sort_deadlineDesc_farthestFirst() {
        Task far = task("t-far", "p", "Far", false, Priority.MEDIUM, Difficulty.MEDIUM, daysFromNow(10));
        Task near = task("t-near", "p", "Near", false, Priority.MEDIUM, Difficulty.MEDIUM, daysFromNow(1));

        List<Task> result = useCase.execute(mutable(near, far), SortType.DEADLINE, SortOrder.DESC);

        assertEquals("t-far", result.get(0).getGlobalId());
        assertEquals("t-near", result.get(1).getGlobalId());
    }

    @Test
    public void sort_priorityAsc_highPriorityFirst() {
        long d = daysFromNow(1);
        Task low = task("low", "p", "Low", false, Priority.VERY_LOW, Difficulty.MEDIUM, d);
        Task high = task("high", "p", "High", false, Priority.VERY_HIGH, Difficulty.MEDIUM, d);
        Task med = task("med", "p", "Med", false, Priority.MEDIUM, Difficulty.MEDIUM, d);

        List<Task> result = useCase.execute(mutable(low, high, med), SortType.PRIORITY, SortOrder.ASC);

        assertEquals(Priority.VERY_HIGH, result.get(0).getPriority());
        assertEquals(Priority.VERY_LOW, result.get(2).getPriority());
    }

    @Test
    public void sort_difficultyAsc_hardestFirst() {
        long d = daysFromNow(1);
        Task easy = task("easy", "p", "E", false, Priority.MEDIUM, Difficulty.VERY_EASY, d);
        Task hard = task("hard", "p", "H", false, Priority.MEDIUM, Difficulty.VERY_DIFFICULT, d);
        Task mid = task("mid", "p", "M", false, Priority.MEDIUM, Difficulty.MEDIUM, d);

        List<Task> result = useCase.execute(mutable(easy, hard, mid), SortType.DIFFICULTY, SortOrder.ASC);

        assertEquals(Difficulty.VERY_DIFFICULT, result.get(0).getDifficulty());
        assertEquals(Difficulty.VERY_EASY, result.get(2).getDifficulty());
    }

    @Test
    public void sort_emptyList_returnsEmpty() {
        List<Task> result = useCase.execute(new ArrayList<>(Collections.emptyList()),
                SortType.DEADLINE, SortOrder.ASC);
        assertTrue(result.isEmpty());
    }
}
