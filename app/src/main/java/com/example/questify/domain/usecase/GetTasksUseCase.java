package com.example.questify.domain.usecase;

import androidx.lifecycle.LiveData;

import com.example.questify.data.repository.TaskRepository;
import com.example.questify.domain.model.Task;

import java.util.List;

public class GetTasksUseCase {

    private final TaskRepository taskRepository;

    public GetTasksUseCase(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public LiveData<List<Task>> execute(String userGlobalId) {
        return taskRepository.getTasksForUser(userGlobalId);
    }
}
