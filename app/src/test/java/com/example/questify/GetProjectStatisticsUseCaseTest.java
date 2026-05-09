package com.example.questify;

import static com.example.questify.TaskTestFactory.daysFromNow;
import static com.example.questify.TaskTestFactory.task;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.questify.data.repository.TaskRepository;
import com.example.questify.domain.model.Task;
import com.example.questify.domain.model.enums.Difficulty;
import com.example.questify.domain.model.enums.Priority;
import com.example.questify.domain.usecase.plans.project.GetProjectStatisticsUseCase;
import com.example.questify.domain.usecase.plans.project.GetProjectStatisticsUseCase.ProjectStatistics;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GetProjectStatisticsUseCaseTest {

    private static final String PROJECT_ID = "proj-1";

    private TaskRepository taskRepository;
    private GetProjectStatisticsUseCase useCase;

    @Before
    public void setUp() {
        taskRepository = mock(TaskRepository.class);
        useCase = new GetProjectStatisticsUseCase(taskRepository);
    }

    private static Task done() {
        return task("d-" + System.nanoTime(), PROJECT_ID, "Done",
                true, Priority.MEDIUM, Difficulty.MEDIUM, daysFromNow(1));
    }

    private static Task open() {
        return task("o-" + System.nanoTime(), PROJECT_ID, "Open",
                false, Priority.MEDIUM, Difficulty.MEDIUM, daysFromNow(1));
    }

    @Test
    public void progress_emptyProject_isZero() {
        when(taskRepository.getTasksByProject(PROJECT_ID)).thenReturn(Collections.<Task>emptyList());
        ProjectStatistics s = useCase.execute(PROJECT_ID);
        assertEquals(0, s.total);
        assertEquals(0, s.getProgressPercent());
    }

    @Test
    public void progress_allDone_isHundred() {
        List<Task> tasks = Arrays.asList(done(), done(), done());
        when(taskRepository.getTasksByProject(PROJECT_ID)).thenReturn(tasks);
        ProjectStatistics s = useCase.execute(PROJECT_ID);
        assertEquals(100, s.getProgressPercent());
    }

    @Test
    public void progress_halfDone_isFifty() {
        List<Task> tasks = Arrays.asList(done(), done(), open(), open());
        when(taskRepository.getTasksByProject(PROJECT_ID)).thenReturn(tasks);
        ProjectStatistics s = useCase.execute(PROJECT_ID);
        assertEquals(4, s.total);
        assertEquals(2, s.completed);
        assertEquals(50, s.getProgressPercent());
    }

    @Test
    public void progress_noneDone_isZero() {
        List<Task> tasks = Arrays.asList(open(), open(), open());
        when(taskRepository.getTasksByProject(PROJECT_ID)).thenReturn(tasks);
        ProjectStatistics s = useCase.execute(PROJECT_ID);
        assertEquals(3, s.total);
        assertEquals(0, s.completed);
        assertEquals(0, s.getProgressPercent());
    }
}
