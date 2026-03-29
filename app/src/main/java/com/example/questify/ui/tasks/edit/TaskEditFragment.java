package com.example.questify.ui.tasks.edit;

import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.questify.R;
import com.example.questify.domain.model.Project;
import com.example.questify.domain.model.enums.Difficulty;
import com.example.questify.domain.model.enums.Priority;
import com.example.questify.util.DatePickerUtils;
import com.example.questify.util.DateUtils;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TaskEditFragment extends Fragment {

    private TaskEditViewModel viewModel;

    private EditText inputName, inputDescription, inputDeadline;
    private Spinner spinnerPriority, spinnerDifficulty, spinnerProjects;
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
        viewModel = new ViewModelProvider(this).get(TaskEditViewModel.class);

        inputName = view.findViewById(R.id.inputName);
        inputDescription = view.findViewById(R.id.inputDescription);
        inputDeadline = view.findViewById(R.id.inputDeadline);
        checkboxDone = view.findViewById(R.id.checkboxDone);

        DatePickerUtils.attach(inputDeadline, requireContext());

        spinnerPriority = view.findViewById(R.id.spinnerPriority);
        spinnerPriority.setAdapter(new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                Priority.values()
        ));

        spinnerDifficulty = view.findViewById(R.id.spinnerDifficulty);
        spinnerDifficulty.setAdapter(new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                Difficulty.values()
        ));

        spinnerProjects = view.findViewById(R.id.spinnerProjects);

        viewModel.projects.observe(getViewLifecycleOwner(), list -> {
            ArrayAdapter<Project> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    list
            );
            spinnerProjects.setAdapter(adapter);

            viewModel.getTask().observe(getViewLifecycleOwner(), task -> {
                if (task == null) return;

                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getGlobalId().equals(task.getProjectGlobalId())) {
                        spinnerProjects.setSelection(i);
                        break;
                    }
                }
            });
        });

        viewModel.loadTask(requireArguments().getString("taskGlobalId"));

        viewModel.getTask().observe(getViewLifecycleOwner(), task -> {
            if (task == null) return;

            inputName.setText(task.getTaskName());
            inputDescription.setText(task.getDescription());
            inputDeadline.setText(DateUtils.format(task.getDeadline()));
            checkboxDone.setChecked(task.isDone());

            spinnerPriority.setSelection(task.getPriority().ordinal());
            spinnerDifficulty.setSelection(task.getDifficulty().ordinal());
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
            }
        });

        view.findViewById(R.id.buttonCancel)
                .setOnClickListener(v ->
                        requireActivity().getOnBackPressedDispatcher().onBackPressed()
                );

        view.findViewById(R.id.buttonSave)
                .setOnClickListener(v -> {
                    viewModel.saveTask(
                            inputName.getText().toString(),
                            inputDescription.getText().toString(),
                            DateUtils.parseToMillis(inputDeadline.getText().toString()),
                            ((Project) spinnerProjects.getSelectedItem()).getProjectName(),
                            (Priority) spinnerPriority.getSelectedItem(),
                            (Difficulty) spinnerDifficulty.getSelectedItem(),
                            checkboxDone.isChecked()
                    );
                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
                });

        view.findViewById(R.id.buttonDelete)
                .setOnClickListener(v -> {
                    viewModel.deleteTask();
                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
                });
    }
}