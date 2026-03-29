package com.example.questify.ui.tasks.edit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.questify.R;
import com.example.questify.domain.model.enums.Difficulty;
import com.example.questify.domain.model.enums.Priority;
import com.example.questify.domain.model.Project;

import java.text.SimpleDateFormat;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TaskEditFragment extends Fragment {

    private TaskEditViewModel taskEditViewModel;

    private EditText inputName;
    private EditText inputDescription;
    private EditText inputDeadline;
    private Spinner spinnerPriority;
    private Spinner spinnerDifficulty;
    private Spinner spinnerProjects;
    private CheckBox checkboxDone;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        inputName = view.findViewById(R.id.inputName);

        inputDescription = view.findViewById(R.id.inputDescription);

        inputDeadline = view.findViewById(R.id.inputDeadline);

        checkboxDone = view.findViewById(R.id.checkboxDone);


        spinnerPriority = view.findViewById(R.id.spinnerPriority);
        ArrayAdapter<Priority> priorityAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                Priority.values()
        );
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(priorityAdapter);


        spinnerDifficulty = view.findViewById(R.id.spinnerDifficulty);
        ArrayAdapter<Difficulty> difficultyAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                Difficulty.values()
        );
        difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDifficulty.setAdapter(difficultyAdapter);

        taskEditViewModel = new ViewModelProvider(this).get(TaskEditViewModel.class);
        taskEditViewModel.loadTask(requireArguments().getString("taskGlobalId"));


        spinnerProjects = view.findViewById(R.id.spinnerProjects);
        taskEditViewModel.projects.observe(getViewLifecycleOwner(), list -> {
            ArrayAdapter<Project> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    list);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerProjects.setAdapter(adapter);
            taskEditViewModel.getTask().observe(getViewLifecycleOwner(), task -> {
                if (task == null) return;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getGlobalId().equals(task.getProjectGlobalId())) {
                        spinnerProjects.setSelection(i);
                        break;
                    }
                }
            });
        });

        Button buttonCancel = view.findViewById(R.id.buttonCancel);
        Button buttonSave = view.findViewById(R.id.buttonSave);
        Button buttonDelete = view.findViewById(R.id.buttonDelete);


        taskEditViewModel.getTask().observe(getViewLifecycleOwner(), task -> {
            if (task == null) {
                return;
            }
            inputName.setText(task.getTaskName());
            inputDescription.setText(task.getDescription());
            inputDeadline.setText(parseLongToDate(task.getDeadline()));
            checkboxDone.setChecked(task.isDone());

            spinnerPriority.setSelection(task.getPriority().ordinal());
            spinnerDifficulty.setSelection(task.getDifficulty().ordinal());
        });


        buttonCancel.setOnClickListener(v ->
                requireActivity().getOnBackPressedDispatcher().onBackPressed()
        );

        buttonSave.setOnClickListener(v -> {
            taskEditViewModel.saveTask(
                    inputName.getText().toString(),
                    inputDescription.getText().toString(),
                    parseDateToLong(inputDeadline.getText().toString()),
                    ((Project) spinnerProjects.getSelectedItem()).getProjectName(),
                    (Priority) spinnerPriority.getSelectedItem(),
                    (Difficulty) spinnerDifficulty.getSelectedItem(),
                    checkboxDone.isChecked()
            );
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        });

        buttonDelete.setOnClickListener(v -> {
            taskEditViewModel.deleteTask();
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        });
    }

    private long parseDateToLong(String text) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy",
                    Locale.getDefault());
            return sdf.parse(text).getTime();
        } catch (Exception e) {
            return 0;
        }
    }
    // todo то же самое в фильтрации

    private String parseLongToDate(Long millis) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            return sdf.format(millis);
        } catch (Exception e) {
            return "";
        }
    }
}