package com.example.questify;

import static com.example.questify.TaskTestFactory.daysFromNow;
import static com.example.questify.TaskTestFactory.task;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.questify.data.repository.TaskRepository;
import com.example.questify.domain.model.Task;
import com.example.questify.domain.model.enums.Difficulty;
import com.example.questify.domain.model.enums.Priority;
import com.example.questify.domain.usecase.plans.tasks.task.DeleteTaskUseCase;
import com.example.questify.domain.usecase.plans.tasks.task.GetAllTasksUseCase;
import com.example.questify.domain.usecase.user.DeleteCompletedTasksUseCase;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

public class DeleteCompletedTasksUseCaseTest {

    private TaskRepository taskRepository;
    private DeleteCompletedTasksUseCase useCase;

    @Before
    public void setUp() {
        taskRepository = mock(TaskRepository.class);
        GetAllTasksUseCase getAll = new GetAllTasksUseCase(taskRepository);
        DeleteTaskUseCase delete = new DeleteTaskUseCase(taskRepository);
        useCase = new DeleteCompletedTasksUseCase(getAll, delete);
    }

    @Test
    public void execute_deletesOnlyDoneTasks() {
        Task done1 = task("d1", "p", "Done 1",  true,  Priority.MEDIUM, Difficulty.MEDIUM, daysFromNow(1));
        Task done2 = task("d2", "p", "Done 2",  true,  Priority.MEDIUM, Difficulty.MEDIUM, daysFromNow(1));
        Task open1 = task("o1", "p", "Open 1",  false, Priority.MEDIUM, Difficulty.MEDIUM, daysFromNow(1));

        when(taskRepository.getAll()).thenReturn(Arrays.asList(done1, done2, open1));

        useCase.execute();

        verify(taskRepository).delete(done1);
        verify(taskRepository).delete(done2);
        verify(taskRepository, never()).delete(open1);
    }

    @Test
    public void execute_doesNotTouchOpenTasks() {
        Task open1 = task("o1", "p", "Open 1", false, Priority.MEDIUM, Difficulty.MEDIUM, daysFromNow(1));
        Task open2 = task("o2", "p", "Open 2", false, Priority.MEDIUM, Difficulty.MEDIUM, daysFromNow(2));
        Task done1 = task("d1", "p", "Done 1", true,  Priority.MEDIUM, Difficulty.MEDIUM, daysFromNow(1));

        when(taskRepository.getAll()).thenReturn(Arrays.asList(open1, open2, done1));

        useCase.execute();

        verify(taskRepository, times(1)).delete(any(Task.class));
        verify(taskRepository).delete(done1);
    }

    @Test
    public void execute_noDoneTasks_doesNothing() {
        Task open1 = task("o1", "p", "Open 1", false, Priority.MEDIUM, Difficulty.MEDIUM, daysFromNow(1));
        when(taskRepository.getAll()).thenReturn(Collections.singletonList(open1));

        useCase.execute();

        verify(taskRepository, never()).delete(any(Task.class));
    }
}
