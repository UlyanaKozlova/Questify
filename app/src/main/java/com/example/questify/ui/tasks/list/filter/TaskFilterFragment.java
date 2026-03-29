package com.example.questify.ui.tasks.list.filter;

import android.app.DatePickerDialog;
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
import com.example.questify.domain.model.enums.Difficulty;
import com.example.questify.domain.model.enums.Priority;
import com.example.questify.domain.model.enums.TaskStatus;
import com.example.questify.domain.usecase.plans.tasks.filter.TaskFilter;
import com.example.questify.ui.tasks.list.TaskListViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.IntStream;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TaskFilterFragment extends Fragment {

    private static final String ALL = "Все";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_filter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        TaskListViewModel viewModel =
                new ViewModelProvider(requireActivity()).get(TaskListViewModel.class);

        TaskFilter filter = viewModel.getCurrentFilter();

        Spinner spinnerPriority = view.findViewById(R.id.spinnerPriority);
        String[] priorityItems = new String[Priority.values().length + 1];
        priorityItems[0] = ALL;
        IntStream.range(0, Priority.values().length)
                .forEach(i -> priorityItems[i + 1] = Priority.values()[i].name());

        spinnerPriority.setAdapter(new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                priorityItems
        ));


        Spinner spinnerDifficulty = view.findViewById(R.id.spinnerDifficulty);
        String[] difficultyItems = new String[Difficulty.values().length + 1];
        difficultyItems[0] = ALL;

        IntStream.range(0, Difficulty.values().length)
                .forEach(i -> difficultyItems[i + 1] = Difficulty.values()[i].name());

        spinnerDifficulty.setAdapter(new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                difficultyItems
        ));


        Spinner spinnerStatus = view.findViewById(R.id.spinnerStatus);
        String[] statusItems = new String[TaskStatus.values().length];

        for (int i = 0; i < TaskStatus.values().length; i++) {
            statusItems[i] = TaskStatus.values()[i].toString();
        }

        spinnerStatus.setAdapter(new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                statusItems
        ));


        EditText inputStartDate = view.findViewById(R.id.inputStartDate);
        EditText inputEndDate = view.findViewById(R.id.inputEndDate);

        inputStartDate.setOnClickListener(v -> openDatePicker(inputStartDate));
        inputEndDate.setOnClickListener(v -> openDatePicker(inputEndDate));


        if (filter != null) {
            spinnerPriority.setSelection(
                    filter.getPriority() != null
                            ? filter.getPriority().ordinal() + 1
                            : 0
            );
            spinnerDifficulty.setSelection(
                    filter.getDifficulty() != null
                            ? filter.getDifficulty().ordinal() + 1
                            : 0
            );
            if (filter.getIsDone() == null) {
                spinnerStatus.setSelection(0);
            } else if (filter.getIsDone()) {
                spinnerStatus.setSelection(1);
            } else {
                spinnerStatus.setSelection(2);
            }

            if (filter.getStartDate() != null) {
                inputStartDate.setText(parseLongToDate(filter.getStartDate()));
            }

            if (filter.getEndDate() != null) {
                inputEndDate.setText(parseLongToDate(filter.getEndDate()));
            }
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

            Priority priority = spinnerPriority.getSelectedItem().equals(ALL)
                    ? null
                    : Priority.valueOf((String) spinnerPriority.getSelectedItem());

            Difficulty difficulty = spinnerDifficulty.getSelectedItem().equals(ALL)
                    ? null
                    : Difficulty.valueOf((String) spinnerDifficulty.getSelectedItem());

            TaskStatus status = TaskStatus.fromString(
                    (String) spinnerStatus.getSelectedItem()
            );

            Boolean isDone = status.getIsDone();
            Long startDate = parseDateToLong(inputStartDate.getText().toString().trim());
            Long endDate = parseDateToLong(inputEndDate.getText().toString().trim());
            if (startDate != null && endDate != null && startDate > endDate) {
                inputStartDate.setError("Начальная дата позже конечной");
                return;
            }
            viewModel.applyFilter(new TaskFilter(
                    priority,
                    difficulty,
                    startDate,
                    endDate,
                    isDone
            ));
            requireActivity().getSupportFragmentManager().popBackStack();
        });
        view.findViewById(R.id.buttonBack).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );
    }

    private Long parseDateToLong(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        try {
            return Objects
                    .requireNonNull(new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(text))
                    .getTime();
        } catch (Exception e) {
            return null;
        }
    }

    private String parseLongToDate(Long millis) {
        if (millis == null) {
            return "";
        }
        return new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(millis);
    }

    private void openDatePicker(EditText field) {
        Calendar calendar = Calendar.getInstance();

        Long existing = parseDateToLong(field.getText().toString());
        if (existing != null) {
            calendar.setTimeInMillis(existing);
        }

        new DatePickerDialog(
                requireContext(),
                (view, year, month, day) -> {
                    calendar.set(year, month, day);
                    field.setText(parseLongToDate(calendar.getTimeInMillis()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }
}