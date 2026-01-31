package com.example.questify.domain.usecase.plans.tasks.task;


import androidx.lifecycle.LiveData;

import com.example.questify.data.repository.TaskRepository;
import com.example.questify.domain.model.Task;

import java.util.List;

import javax.inject.Inject;

public class GetAllTasksUseCase {
    private final TaskRepository taskRepository;

    @Inject
    public GetAllTasksUseCase(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> execute() {
        return taskRepository.getAll();
    }
    public LiveData<List<Task>> executeLive() {
        return taskRepository.getAllLive();
    } // todo не круто
}