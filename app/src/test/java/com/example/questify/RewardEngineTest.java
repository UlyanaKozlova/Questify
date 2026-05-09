package com.example.questify;

import static com.example.questify.TaskTestFactory.daysFromNow;
import static com.example.questify.TaskTestFactory.task;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.questify.data.repository.SubtaskRepository;
import com.example.questify.data.repository.TaskRepository;
import com.example.questify.data.repository.UserRepository;
import com.example.questify.domain.model.Subtask;
import com.example.questify.domain.model.Task;
import com.example.questify.domain.model.User;
import com.example.questify.domain.model.enums.Difficulty;
import com.example.questify.domain.model.enums.Priority;
import com.example.questify.domain.usecase.plans.tasks.reward.RewardEngine;

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;


public class RewardEngineTest {

    private static final int COINS_PER_LEVEL = 35;

    private RewardEngine rewardEngine;

    private User user;

    @Before
    public void setUp() {
        SubtaskRepository subtaskRepository = mock(SubtaskRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        TaskRepository taskRepository = mock(TaskRepository.class);

        user = new User("u-1", "name", "", 0, 0L, 0L, 0L);
        when(userRepository.getUser()).thenReturn(user);
        when(subtaskRepository.getSubtasksForTask(anyString()))
                .thenReturn(Collections.<Subtask>emptyList());

        rewardEngine = new RewardEngine(subtaskRepository, userRepository, taskRepository);
    }


    @Test
    public void completeEnoughTasks_levelsUp() {
        Task t1 = task("t1", "p", "T1", false, Priority.VERY_HIGH, Difficulty.VERY_DIFFICULT, daysFromNow(1));
        t1.setDone(true);
        rewardEngine.applyAfterChange(t1);

        Task t2 = task("t2", "p", "T2", false, Priority.VERY_HIGH, Difficulty.VERY_DIFFICULT, daysFromNow(1));
        t2.setDone(true);
        rewardEngine.applyAfterChange(t2);

        assertEquals(40L, user.getEarnedCoins());
        assertEquals(1, user.getLevel());
    }


    @Test
    public void harderTaskContributesMoreThanEasy() {
        Task hard = task("h", "p", "Hard", false, Priority.VERY_HIGH, Difficulty.VERY_DIFFICULT, daysFromNow(1));
        hard.setDone(true);
        rewardEngine.applyAfterChange(hard);
        long afterHard = user.getEarnedCoins();

        Task easy = task("e", "p", "Easy", false, Priority.VERY_LOW, Difficulty.VERY_EASY, daysFromNow(1));
        easy.setDone(true);
        rewardEngine.applyAfterChange(easy);
        long afterEasy = user.getEarnedCoins();

        long easyContribution = afterEasy - afterHard;

        assertEquals(20L, afterHard);
        assertEquals(4L, easyContribution);
        assertTrue(afterHard > easyContribution);
    }

    @Test
    public void unmarkingDone_subtractsCoins() {
        Task t = task("t", "p", "T", false, Priority.VERY_HIGH, Difficulty.VERY_DIFFICULT, daysFromNow(1));
        t.setDone(true);
        rewardEngine.applyAfterChange(t);
        assertEquals(20L, user.getEarnedCoins());

        t.setDone(false);
        rewardEngine.applyAfterChange(t);
        assertEquals(0L, user.getEarnedCoins());
        assertEquals(0, user.getLevel());
    }


    @Test
    public void level_isFloorOfEarnedCoinsDividedByThreshold() {
        Task t = task("t", "p", "T", false, Priority.VERY_HIGH, Difficulty.VERY_DIFFICULT, daysFromNow(1));
        t.setDone(true);

        rewardEngine.applyAfterChange(t);
        assertEquals(0, user.getLevel());

        user.setEarnedCoins(70L);
        user.setLevel((int) (user.getEarnedCoins() / COINS_PER_LEVEL));
        assertEquals(2, user.getLevel());
    }
}
