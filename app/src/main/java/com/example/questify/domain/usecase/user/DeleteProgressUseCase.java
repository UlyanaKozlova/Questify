package com.example.questify.domain.usecase.user;

import com.example.questify.data.repository.PetClothingRefRepository;
import com.example.questify.data.repository.PetRepository;
import com.example.questify.data.repository.ProjectRepository;
import com.example.questify.data.repository.SubtaskRepository;
import com.example.questify.data.repository.TaskRepository;
import com.example.questify.data.repository.UserRepository;

import javax.inject.Inject;

public class DeleteProgressUseCase {
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final PetRepository petRepository;
    private final SubtaskRepository subtaskRepository;
    private final ProjectRepository projectRepository;
    private final PetClothingRefRepository petClothingRefRepository;

    @Inject
    public DeleteProgressUseCase(TaskRepository taskRepository,
                                 UserRepository userRepository,
                                 PetRepository petRepository,
                                 SubtaskRepository subtaskRepository,
                                 ProjectRepository projectRepository,
                                 PetClothingRefRepository petClothingRefRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.petRepository = petRepository;
        this.subtaskRepository = subtaskRepository;
        this.projectRepository = projectRepository;
        this.petClothingRefRepository = petClothingRefRepository;
    }

    public void execute() {
        taskRepository.deleteAll();
        userRepository.deleteProgress();
        petRepository.deleteProgress();
        subtaskRepository.deleteAll();
        projectRepository.deleteAll();
        petClothingRefRepository.deleteAll();
    }
}
