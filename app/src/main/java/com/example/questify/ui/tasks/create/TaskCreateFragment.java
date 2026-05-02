package com.example.questify.ui.tasks.create;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.questify.R;
import com.example.questify.domain.model.Project;
import com.example.questify.domain.model.enums.Difficulty;
import com.example.questify.domain.model.enums.Priority;
import com.example.questify.util.DatePickerUtils;
import com.example.questify.util.DateUtils;
import com.google.android.material.snackbar.Snackbar;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TaskCreateFragment extends Fragment {

    private TaskCreateViewModel viewModel;

    private EditText inputTitle;
    private EditText inputDescription;
    private EditText inputDeadline;

    private Spinner spinnerProjects;
    private Spinner spinnerDifficulty;
    private Spinner spinnerPriority;

    private String preSelectedProjectGlobalId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_create, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        if (getArguments() != null) {
            preSelectedProjectGlobalId = getArguments().getString("preSelectedProjectGlobalId");
        }

        viewModel = new ViewModelProvider(this).get(TaskCreateViewModel.class);

        viewModel.getError().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show();
            }
        });
        viewModel.getSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });

        inputTitle = view.findViewById(R.id.taskName);
        inputDescription = view.findViewById(R.id.inputDescription);
        inputDeadline = view.findViewById(R.id.inputDeadline);

        DatePickerUtils.attach(inputDeadline, requireContext());

        spinnerDifficulty = view.findViewById(R.id.spinnerDifficulty);
        spinnerDifficulty.setAdapter(new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                buildLocalizedNames(Difficulty.values())
        ));
        spinnerDifficulty.setSelection(Difficulty.MEDIUM.ordinal());

        spinnerPriority = view.findViewById(R.id.spinnerPriority);
        spinnerPriority.setAdapter(new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                buildLocalizedNames(Priority.values())
        ));
        spinnerPriority.setSelection(Priority.MEDIUM.ordinal());

        spinnerProjects = view.findViewById(R.id.spinnerProjects);
        viewModel.projects.observe(getViewLifecycleOwner(), list -> {
            int currentPosition = spinnerProjects.getSelectedItemPosition();
            ArrayAdapter<Project> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    list
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerProjects.setAdapter(adapter);
            if (currentPosition >= 0 && currentPosition < list.size()) {
                spinnerProjects.setSelection(currentPosition);
            }
            if (preSelectedProjectGlobalId != null && !list.isEmpty()) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getGlobalId().equals(preSelectedProjectGlobalId)) {
                        spinnerProjects.setSelection(i);
                        break;
                    }
                }
            }
        });

        view.findViewById(R.id.buttonCancel)
                .setOnClickListener(v -> requireActivity()
                        .getOnBackPressedDispatcher()
                        .onBackPressed());

        view.findViewById(R.id.buttonSave)
                .setOnClickListener(v -> saveTask());
    }

    @SuppressWarnings("unchecked")
    private <E extends Enum<E>> String[] buildLocalizedNames(E[] values) {
        String[] names = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            if (values[i] instanceof Priority) {
                names[i] = ((Priority) values[i]).getString(requireContext());
            } else if (values[i] instanceof Difficulty) {
                names[i] = ((Difficulty) values[i]).getString(requireContext());
            } else {
                names[i] = values[i].name();
            }
        }
        return names;
    }

    private void saveTask() {
        Project selectedProject = (Project) spinnerProjects.getSelectedItem();
        if (selectedProject == null) {
            Snackbar.make(requireView(), getString(R.string.choose_project), Snackbar.LENGTH_LONG).show();
            return;
        }

        viewModel.saveTask(
                inputTitle.getText().toString(),
                inputDescription.getText().toString(),
                DateUtils.parseToMillis(inputDeadline.getText().toString()),
                selectedProject.getProjectName(),
                Difficulty.values()[spinnerDifficulty.getSelectedItemPosition()],
                Priority.values()[spinnerPriority.getSelectedItemPosition()]
        );
    }
}