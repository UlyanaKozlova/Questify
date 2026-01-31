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
import com.example.questify.domain.model.Difficulty;
import com.example.questify.domain.model.Priority;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TaskEditFragment extends Fragment {

    private TaskEditViewModel viewModel;

    private EditText inputName;
    private EditText inputDescription;
    private EditText inputDeadline;
    private Spinner spinnerPriority;
    private Spinner spinnerDifficulty;
    private EditText inputProject;
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
        inputProject = view.findViewById(R.id.inputProject);
        checkboxDone = view.findViewById(R.id.checkboxDone);

        Button buttonCancel = view.findViewById(R.id.buttonCancel);
        Button buttonSave = view.findViewById(R.id.buttonSave);
        Button buttonDelete = view.findViewById(R.id.buttonDelete);


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


        viewModel = new ViewModelProvider(this).get(TaskEditViewModel.class);
        viewModel.loadTask(requireArguments().getString("taskGlobalId"));

        viewModel.getTask().observe(getViewLifecycleOwner(), task -> {
            if (task == null) {
                return;
            }
            inputName.setText(task.getTaskName());
            inputDescription.setText(task.getDescription());
            inputDeadline.setText(String.valueOf(task.getDeadline()));
            inputProject.setText(task.getProjectGlobalId());
            checkboxDone.setChecked(task.isDone());

            spinnerPriority.setSelection(task.getPriority().ordinal());
            spinnerDifficulty.setSelection(task.getDifficulty().ordinal());
        });


        buttonCancel.setOnClickListener(v ->
                requireActivity().getOnBackPressedDispatcher().onBackPressed()
        );

        buttonSave.setOnClickListener(v -> {
            viewModel.saveTask(
                    inputName.getText().toString(),
                    inputDescription.getText().toString(),
                    Long.parseLong(inputDeadline.getText().toString()),
                    inputProject.getText().toString(),
                    (Priority) spinnerPriority.getSelectedItem(),
                    (Difficulty) spinnerDifficulty.getSelectedItem(),
                    checkboxDone.isChecked()
            );
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        });

        buttonDelete.setOnClickListener(v -> {
            viewModel.deleteTask();
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        });
    }
}
