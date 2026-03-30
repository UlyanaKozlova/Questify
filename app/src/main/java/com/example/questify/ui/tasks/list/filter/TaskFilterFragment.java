package com.example.questify.ui.tasks.list.filter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.questify.R;
import com.example.questify.domain.model.Project;
import com.example.questify.domain.model.enums.Difficulty;
import com.example.questify.domain.model.enums.Priority;
import com.example.questify.domain.model.enums.TaskStatus;
import com.example.questify.domain.usecase.plans.tasks.filter.TaskFilter;
import com.example.questify.ui.tasks.list.TaskListViewModel;
import com.example.questify.util.DatePickerUtils;
import com.example.questify.util.DateUtils;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TaskFilterFragment extends Fragment {
    private View rootView;
    private TaskListViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_task_filter, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(TaskListViewModel.class);

        TaskFilter filter = viewModel.getCurrentFilter();

        Spinner spinnerPriority = view.findViewById(R.id.spinnerPriority);
        Spinner spinnerDifficulty = view.findViewById(R.id.spinnerDifficulty);
        Spinner spinnerStatus = view.findViewById(R.id.spinnerStatus);
        Spinner spinnerProject = view.findViewById(R.id.spinnerProject);

        EditText inputStartDate = view.findViewById(R.id.inputStartDate);
        EditText inputEndDate = view.findViewById(R.id.inputEndDate);

        DatePickerUtils.attach(inputStartDate, requireContext());
        DatePickerUtils.attach(inputEndDate, requireContext());

        List<String> priorityItems = new ArrayList<>();
        priorityItems.add(getString(R.string.filter_all));
        for (Priority priority : Priority.values()) {
            priorityItems.add(priority.getString(requireContext()));
        }
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                priorityItems);
        spinnerPriority.setAdapter(priorityAdapter);

        List<String> difficultyItems = new ArrayList<>();
        difficultyItems.add(getString(R.string.filter_all));
        for (Difficulty difficulty : Difficulty.values()) {
            difficultyItems.add(difficulty.getString(requireContext()));
        }
        ArrayAdapter<String> difficultyAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                difficultyItems);
        spinnerDifficulty.setAdapter(difficultyAdapter);

        List<String> statusItems = new ArrayList<>();
        for (TaskStatus status : TaskStatus.values()) {
            statusItems.add(status.getString(requireContext()));
        }
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                statusItems);
        spinnerStatus.setAdapter(statusAdapter);

        List<String> projectItems = new ArrayList<>();
        projectItems.add(getString(R.string.filter_all_projects));

        viewModel.getProjects().observe(getViewLifecycleOwner(), projects -> {
            projectItems.clear();
            projectItems.add(getString(R.string.filter_all_projects));
            for (Project project : projects) {
                projectItems.add(project.getProjectName());
            }
            ArrayAdapter<String> projectAdapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    projectItems);
            spinnerProject.setAdapter(projectAdapter);

            if (filter != null && filter.getProjectGlobalId() != null) {
                for (int i = 0; i < projects.size(); i++) {
                    if (projects.get(i).getGlobalId().equals(filter.getProjectGlobalId())) {
                        spinnerProject.setSelection(i + 1);
                        break;
                    }
                }
            }
        });

        if (filter != null) {
            if (filter.getPriority() != null) {
                int position = priorityItems.indexOf(filter.getPriority().getString(requireContext()));
                spinnerPriority.setSelection(Math.max(position, 0));
            } else {
                spinnerPriority.setSelection(0);
            }

            if (filter.getDifficulty() != null) {
                int position = difficultyItems.indexOf(filter.getDifficulty().getString(requireContext()));
                spinnerDifficulty.setSelection(Math.max(position, 0));
            } else {
                spinnerDifficulty.setSelection(0);
            }

            if (filter.getIsDone() == null) {
                spinnerStatus.setSelection(0);
            } else if (filter.getIsDone()) {
                spinnerStatus.setSelection(1);
            } else {
                spinnerStatus.setSelection(2);
            }

            inputStartDate.setText(DateUtils.format(filter.getStartDate()));
            inputEndDate.setText(DateUtils.format(filter.getEndDate()));
        } else {
            spinnerPriority.setSelection(0);
            spinnerDifficulty.setSelection(0);
            spinnerStatus.setSelection(0);
        }

        view.findViewById(R.id.buttonReset).setOnClickListener(v -> {
            viewModel.applyFilter(new TaskFilter(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null)
            );
            spinnerPriority.setSelection(0);
            spinnerDifficulty.setSelection(0);
            spinnerStatus.setSelection(0);
            spinnerProject.setSelection(0);
            inputStartDate.setText("");
            inputEndDate.setText("");
        });

        view.findViewById(R.id.buttonApply).setOnClickListener(v -> {
            Priority priority = null;
            int priorityPos = spinnerPriority.getSelectedItemPosition();
            if (priorityPos > 0) {
                priority = Priority.values()[priorityPos - 1];
            }

            Difficulty difficulty = null;
            int difficultyPos = spinnerDifficulty.getSelectedItemPosition();
            if (difficultyPos > 0) {
                difficulty = Difficulty.values()[difficultyPos - 1];
            }

            Boolean isDone = null;
            int statusPos = spinnerStatus.getSelectedItemPosition();
            if (statusPos > 0) {
                TaskStatus status = TaskStatus.values()[statusPos - 1];
                isDone = status.getIsDone();
            }

            String projectGlobalId = null;
            int projectPos = spinnerProject.getSelectedItemPosition();
            if (projectPos > 0) {
                List<Project> projects = viewModel.getProjects().getValue();
                if (projects != null && projectPos - 1 < projects.size()) {
                    projectGlobalId = projects.get(projectPos - 1).getGlobalId();
                }
            }

            Long start = DateUtils.parseToMillis(inputStartDate.getText().toString());
            Long end = DateUtils.parseToMillis(inputEndDate.getText().toString());

            if (start != null && end != null && start > end) {
                Snackbar.make(rootView, getString(R.string.filter_error_start_after_end), Snackbar.LENGTH_LONG).show();
                return;
            }

            viewModel.applyFilter(new TaskFilter(
                    priority,
                    difficulty,
                    start,
                    end,
                    isDone,
                    projectGlobalId
            ));
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        view.findViewById(R.id.buttonBack)
                .setOnClickListener(v ->
                        requireActivity().getSupportFragmentManager().popBackStack());
    }
}