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
import com.example.questify.domain.model.Difficulty;
import com.example.questify.domain.model.Priority;

import dagger.hilt.android.AndroidEntryPoint;

import android.widget.Spinner;
import android.widget.ArrayAdapter;

@AndroidEntryPoint
public class TaskCreateFragment extends Fragment {
    private TaskCreateViewModel viewModel;
    private EditText inputTitle;
    private EditText inputDescription;
    private EditText inputDeadline;
    private EditText inputNewProject;
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
        spinnerDifficulty = view.findViewById(R.id.spinnerDifficulty);
        ArrayAdapter<Difficulty> difficultyAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                Difficulty.values()
        );
        difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDifficulty.setAdapter(difficultyAdapter);
        spinnerDifficulty.setSelection(Difficulty.MEDIUM.ordinal());

        spinnerPriority = view.findViewById(R.id.spinnerPriority);
        ArrayAdapter<Priority> priorityAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                Priority.values()
        );
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(priorityAdapter);
        spinnerPriority.setSelection(Priority.MEDIUM.ordinal());

        inputTitle = view.findViewById(R.id.taskName);
        inputDescription = view.findViewById(R.id.inputDescription);
        inputDeadline = view.findViewById(R.id.inputDeadline);
        inputNewProject = view.findViewById(R.id.inputNewProject);
        // todo список проектов просто

        Button buttonCancel = view.findViewById(R.id.buttonCancel);
        Button buttonSave = view.findViewById(R.id.buttonSave);

        viewModel = new ViewModelProvider(this).get(TaskCreateViewModel.class);

        buttonCancel.setOnClickListener(v -> requireActivity().onBackPressed());
        buttonSave.setOnClickListener(v -> saveTask());
    }


    private void saveTask() {
        viewModel.saveTask(inputTitle.getText().toString(),
                inputDescription.getText().toString(),
                Long.parseLong(inputDeadline.getText().toString()),
                inputNewProject.getText().toString(),
                (Difficulty) spinnerDifficulty.getSelectedItem(),
                (Priority) spinnerPriority.getSelectedItem());
        requireActivity().onBackPressed();
    }
}
