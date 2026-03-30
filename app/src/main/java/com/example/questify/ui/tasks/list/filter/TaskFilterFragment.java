package com.example.questify.ui.tasks.list.filter;

import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.questify.R;
import com.example.questify.domain.model.enums.*;
import com.example.questify.domain.usecase.plans.tasks.filter.TaskFilter;
import com.example.questify.ui.tasks.list.TaskListViewModel;
import com.example.questify.util.DatePickerUtils;
import com.example.questify.util.DateUtils;
import com.google.android.material.snackbar.Snackbar;

import java.util.stream.IntStream;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TaskFilterFragment extends Fragment {
    private View rootView;


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

        TaskListViewModel viewModel =
                new ViewModelProvider(requireActivity()).get(TaskListViewModel.class);

        TaskFilter filter = viewModel.getCurrentFilter();

        Spinner spinnerPriority = view.findViewById(R.id.spinnerPriority);
        Spinner spinnerDifficulty = view.findViewById(R.id.spinnerDifficulty);
        Spinner spinnerStatus = view.findViewById(R.id.spinnerStatus);

        EditText inputStartDate = view.findViewById(R.id.inputStartDate);
        EditText inputEndDate = view.findViewById(R.id.inputEndDate);

        DatePickerUtils.attach(inputStartDate, requireContext());
        DatePickerUtils.attach(inputEndDate, requireContext());

        String[] priorityItems = new String[Priority.values().length + 1];
        priorityItems[0] = getString(R.string.filter_all);
        IntStream.range(0, Priority.values().length)
                .forEach(i -> priorityItems[i + 1] = Priority.values()[i].name());
        spinnerPriority.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, priorityItems));


        String[] difficultyItems = new String[Difficulty.values().length + 1];
        difficultyItems[0] = getString(R.string.filter_all);
        IntStream.range(0, Difficulty.values().length)
                .forEach(i -> difficultyItems[i + 1] = Difficulty.values()[i].name());
        spinnerDifficulty.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, difficultyItems));


        String[] statusItems = new String[TaskStatus.values().length];
        for (int i = 0; i < TaskStatus.values().length; i++) {
            statusItems[i] = TaskStatus.values()[i].toString();
        }
        spinnerStatus.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, statusItems));


        if (filter != null) {
            spinnerPriority.setSelection(filter.getPriority() != null ? filter.getPriority().ordinal() + 1 : 0);
            spinnerDifficulty.setSelection(filter.getDifficulty() != null ? filter.getDifficulty().ordinal() + 1 : 0);

            if (filter.getIsDone() == null) spinnerStatus.setSelection(0);
            else if (filter.getIsDone()) spinnerStatus.setSelection(1);
            else spinnerStatus.setSelection(2);

            inputStartDate.setText(DateUtils.format(filter.getStartDate()));
            inputEndDate.setText(DateUtils.format(filter.getEndDate()));
        }

        view.findViewById(R.id.buttonReset).setOnClickListener(v -> {
            viewModel.applyFilter(new TaskFilter(null, null, null, null, null));
            spinnerPriority.setSelection(0);
            spinnerDifficulty.setSelection(0);
            spinnerStatus.setSelection(0);
            inputStartDate.setText("");
            inputEndDate.setText("");
        });

        view.findViewById(R.id.buttonApply).setOnClickListener(v -> {
            Priority priority = spinnerPriority.getSelectedItem().equals(getString(R.string.filter_all))
                    ? null : Priority.valueOf((String) spinnerPriority.getSelectedItem());

            Difficulty difficulty = spinnerDifficulty.getSelectedItem().equals(getString(R.string.filter_all))
                    ? null : Difficulty.valueOf((String) spinnerDifficulty.getSelectedItem());

            TaskStatus status = TaskStatus.fromString(
                    (String) spinnerStatus.getSelectedItem(), requireContext());

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
                    status.getIsDone()
            ));
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        view.findViewById(R.id.buttonBack)
                .setOnClickListener(v ->
                        requireActivity().getSupportFragmentManager().popBackStack());
    }
}