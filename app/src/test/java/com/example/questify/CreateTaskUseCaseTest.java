package com.example.questify;

import static com.example.questify.TaskTestFactory.daysFromNow;
import static com.example.questify.TaskTestFactory.task;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.questify.data.repository.ProjectRepository;
import com.example.questify.data.repository.TaskRepository;
import com.example.questify.domain.model.Project;
import com.example.questify.domain.model.Task;
import com.example.questify.domain.model.enums.Difficulty;
import com.example.questify.domain.model.enums.Priority;
import com.example.questify.domain.usecase.plans.tasks.reward.RewardEngine;
import com.example.questify.domain.usecase.plans.tasks.task.CreateTaskUseCase;
import com.example.questify.domain.usecase.plans.tasks.task.UpdateTaskUseCase;
import com.example.questify.util.exception.DomainValidationException;

import org.junit.Before;
import org.junit.Test;

public class CreateTaskUseCaseTest {

    private TaskRepository taskRepository;

    private CreateTaskUseCase createTaskUseCase;
    private UpdateTaskUseCase updateTaskUseCase;

    @Before
    public void setUp() {
        taskRepository = mock(TaskRepository.class);
        ProjectRepository projectRepository = mock(ProjectRepository.class);
        RewardEngine rewardEngine = mock(RewardEngine.class);

        Project project = new Project("p-1", "u-1", "Default", "#000", 0L);
        when(projectRepository.getByProjectName(anyString())).thenReturn(project);

        createTaskUseCase = new CreateTaskUseCase(taskRepository, projectRepository);
        updateTaskUseCase = new UpdateTaskUseCase(taskRepository, projectRepository, rewardEngine);
    }

    @Test
    public void create_duplicateName_throws() {
        Task existing = task("e-1", "p-1", "Same name", false,
                Priority.MEDIUM, Difficulty.MEDIUM, daysFromNow(1));
        when(taskRepository.getByTaskName("Same name")).thenReturn(existing);

        try {
            createTaskUseCase.execute("Same name", "desc", daysFromNow(1),
                    "Default", Difficulty.MEDIUM, Priority.MEDIUM);
            fail("expected DomainValidationException");
        } catch (DomainValidationException e) {
            assertEquals(R.string.error_task_name_exists, e.resId);
        }
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    public void create_uniqueName_succeeds() {
        when(taskRepository.getByTaskName("Brand new")).thenReturn(null);

        Task created = createTaskUseCase.execute("Brand new", "desc", daysFromNow(2),
                "Default", Difficulty.MEDIUM, Priority.MEDIUM);

        assertNotNull(created);
        assertEquals("Brand new", created.getTaskName());
        verify(taskRepository).save(created);
    }

    @Test
    public void update_keepsSameName_succeeds() {
        Task editing = task("t-1", "p-1", "My task", false,
                Priority.MEDIUM, Difficulty.MEDIUM, daysFromNow(1));
        when(taskRepository.getByTaskName("My task")).thenReturn(editing);

        updateTaskUseCase.execute(editing, "My task", "new desc", daysFromNow(3),
                "Default", Priority.HIGH, Difficulty.DIFFICULT, false);

        assertEquals("My task", editing.getTaskName());
        assertEquals(Priority.HIGH, editing.getPriority());
        assertEquals(Difficulty.DIFFICULT, editing.getDifficulty());
    }
}
