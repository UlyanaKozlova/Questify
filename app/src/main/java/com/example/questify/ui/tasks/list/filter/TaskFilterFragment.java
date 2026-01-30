package com.example.questify.ui.tasks.list.filter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.questify.R;
import com.example.questify.domain.model.Difficulty;
import com.example.questify.domain.model.Priority;
import com.example.questify.domain.usecase.plans.filter.TaskFilter;
import com.example.questify.ui.tasks.list.TaskListViewModel;

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
        TaskListViewModel taskListViewModel =
                new ViewModelProvider(requireActivity()).get(TaskListViewModel.class);


        Spinner spinnerPriority = view.findViewById(R.id.spinnerPriority);
        String[] priorityItems = new String[Priority.values().length + 1];
        priorityItems[0] = ALL;
        IntStream.range(0, Priority.values().length)
                .forEach(i ->
                        priorityItems[i + 1] = Priority.values()[i].name());
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                priorityItems);
        spinnerPriority.setAdapter(priorityAdapter);


        Spinner spinnerDifficulty = view.findViewById(R.id.spinnerDifficulty);
        String[] difficultyItems = new String[Difficulty.values().length + 1];
        difficultyItems[0] = ALL;
        IntStream.range(0, Difficulty.values().length)
                .forEach(i ->
                        difficultyItems[i + 1] = Difficulty.values()[i].name());
        ArrayAdapter<String> difficultyAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                difficultyItems);
        spinnerDifficulty.setAdapter(difficultyAdapter);


        EditText inputDeadline = view.findViewById(R.id.inputDeadline);
        view.findViewById(R.id.buttonReset).setOnClickListener(v -> {
            taskListViewModel.applyFilter(new TaskFilter(
                    null,
                    null,
                    null));
            spinnerPriority.setSelection(0);
            spinnerDifficulty.setSelection(0);
            inputDeadline.setText("");
        });

        view.findViewById(R.id.buttonApply).setOnClickListener(v -> {
            String selectedPriority = (String) spinnerPriority.getSelectedItem();
            Priority priority = selectedPriority.equals(ALL)
                    ? null
                    : Priority.valueOf(selectedPriority);

            String selectedDifficulty = (String) spinnerDifficulty.getSelectedItem();
            Difficulty difficulty = selectedDifficulty.equals(ALL)
                    ? null
                    : Difficulty.valueOf(selectedDifficulty);

            Long deadline = null;
            try {
                String text = inputDeadline.getText().toString().trim();
                if (!text.isEmpty()) {
                    deadline = Long.parseLong(text);
                }
            } catch (Exception ignored) {
            }// todo плюс норм даты при добавлении


            taskListViewModel.applyFilter(new TaskFilter(
                    priority,
                    difficulty,
                    deadline));
            requireActivity()
                    .getSupportFragmentManager()
                    .popBackStack();
        });

        view.findViewById(R.id.buttonBack).setOnClickListener(v ->
                requireActivity()
                        .getSupportFragmentManager()
                        .popBackStack()
        );
    }
}
