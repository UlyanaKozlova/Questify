package com.example.questify.ui.tasks.edit;

import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.annotation.*;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questify.R;
import com.example.questify.domain.model.Project;
import com.example.questify.domain.model.Subtask;
import com.example.questify.domain.model.enums.Difficulty;
import com.example.questify.domain.model.enums.Priority;
import com.example.questify.util.DatePickerUtils;
import com.example.questify.util.DateUtils;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TaskEditFragment extends Fragment {

    private TaskEditViewModel viewModel;

    private EditText inputName, inputDescription, inputDeadline;
    private Spinner spinnerPriority, spinnerDifficulty, spinnerProjects;
    private CheckBox checkboxDone;
    private SubtaskListAdapter subtaskAdapter;

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

        setupSubtasksList(view);
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

        viewModel.getError().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show();
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
                            checkboxDone.isChecked(),
                            requireContext()
                    );
                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
                });

        view.findViewById(R.id.buttonDelete)
                .setOnClickListener(v -> {
                    viewModel.deleteTask();
                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
                });
    }

    private void setupSubtasksList(View view) {
        subtaskAdapter = new SubtaskListAdapter(new SubtaskListAdapter.Callbacks() {
            @Override
            public void onToggle(Subtask subtask, boolean isDone) {
                viewModel.toggleSubtask(subtask, isDone);
            }

            @Override
            public void onDelete(Subtask subtask) {
                viewModel.deleteSubtask(subtask);
            }

            @Override
            public void onEditRequest(Subtask subtask) {
                showEditSubtaskDialog(subtask);
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.subtasksRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(subtaskAdapter);
        recyclerView.setNestedScrollingEnabled(false);

        viewModel.getSubtasks().observe(getViewLifecycleOwner(), list ->
                subtaskAdapter.submitList(list != null
                        ? list
                        : new ArrayList<>())
        );

        EditText newSubtaskInput = view.findViewById(R.id.newSubtaskInput);
        view.findViewById(R.id.addSubtaskButton).setOnClickListener(v -> {
            String text = newSubtaskInput.getText().toString().trim();
            if (!text.isEmpty()) {
                viewModel.addSubtask(text);
                newSubtaskInput.setText("");
                newSubtaskInput.clearFocus();
            }
        });
    }

    private void showEditSubtaskDialog(Subtask subtask) {
        EditText input = new EditText(requireContext());
        input.setText(subtask.getSubtaskName());
        input.setSelection(input.getText().length());

        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.subtask_edit_title)
                .setView(input)
                .setPositiveButton(R.string.saveButton, (dialog, which) -> {
                    String newName = input.getText().toString().trim();
                    if (!newName.isEmpty()) {
                        viewModel.updateSubtask(subtask, newName);
                    }
                })
                .setNegativeButton(R.string.cancelButton, null)
                .show();
    }
}
