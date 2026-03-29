package com.example.questify.ui.tasks.create;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.questify.R;
import com.example.questify.domain.model.enums.Difficulty;
import com.example.questify.domain.model.enums.Priority;
import com.example.questify.domain.model.Project;

import dagger.hilt.android.AndroidEntryPoint;

import android.widget.Spinner;
import android.widget.ArrayAdapter;

import java.text.SimpleDateFormat;
import java.util.Locale;

@AndroidEntryPoint
public class TaskCreateFragment extends Fragment {
    private TaskCreateViewModel taskCreateViewModel;
    private EditText inputTitle;
    private EditText inputDescription;
    private EditText inputDeadline;
    private Spinner spinnerProjects;
    private Spinner spinnerDifficulty;
    private Spinner spinnerPriority;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_create, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        taskCreateViewModel = new ViewModelProvider(this).get(TaskCreateViewModel.class);

        inputTitle = view.findViewById(R.id.taskName);

        inputDescription = view.findViewById(R.id.inputDescription);

        inputDeadline = view.findViewById(R.id.inputDeadline);


        spinnerDifficulty = view.findViewById(R.id.spinnerDifficulty);
        ArrayAdapter<Difficulty> difficultyAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                Difficulty.values());
        difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDifficulty.setAdapter(difficultyAdapter);
        spinnerDifficulty.setSelection(Difficulty.MEDIUM.ordinal());


        spinnerPriority = view.findViewById(R.id.spinnerPriority);
        ArrayAdapter<Priority> priorityAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                Priority.values());
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(priorityAdapter);
        spinnerPriority.setSelection(Priority.MEDIUM.ordinal());

        spinnerProjects = view.findViewById(R.id.spinnerProjects);
        taskCreateViewModel.projects.observe(getViewLifecycleOwner(), list -> {
            ArrayAdapter<Project> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    list);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerProjects.setAdapter(adapter);
            spinnerProjects.setSelection(0);
        });
        Button buttonCancel = view.findViewById(R.id.buttonCancel);
        Button buttonSave = view.findViewById(R.id.buttonSave);


        buttonCancel.setOnClickListener(v -> requireActivity().onBackPressed());
        buttonSave.setOnClickListener(v -> saveTask());
    }


    private void saveTask() {
        taskCreateViewModel.saveTask(
                inputTitle.getText().toString(),
                inputDescription.getText().toString(),
                parseDate(inputDeadline.getText().toString()),
                ((Project) spinnerProjects.getSelectedItem()).getProjectName(),
                (Difficulty) spinnerDifficulty.getSelectedItem(),
                (Priority) spinnerPriority.getSelectedItem()
        );

        requireActivity().onBackPressed();
    }

    private long parseDate(String text) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy",
                    Locale.getDefault());
            return sdf.parse(text).getTime();
        } catch (Exception e) {
            return 0;
        }
    }
}
