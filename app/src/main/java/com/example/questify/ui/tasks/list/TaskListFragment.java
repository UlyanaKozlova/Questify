package com.example.questify.ui.tasks.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questify.R;
import com.example.questify.domain.model.Task;
import com.example.questify.domain.usecase.plans.sort.SortType;
import com.example.questify.ui.tasks.create.TaskCreateFragment;
import com.example.questify.ui.tasks.list.filter.TaskFilterFragment;


import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TaskListFragment extends Fragment {
    private TaskListViewModel taskListViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        Button buttonFilter = view.findViewById(R.id.buttonFilter);

        Spinner spinnerSort = view.findViewById(R.id.spinnerSort);
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.sort_options,
                android.R.layout.simple_spinner_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(sortAdapter);

        Button buttonImport = view.findViewById(R.id.buttonImport);
        Button buttonAdd = view.findViewById(R.id.buttonAddTask);

        TaskListAdapter taskListAdapter = new TaskListAdapter(new TaskListAdapter.Listener() {
            @Override
            public void onTaskClicked(Task task) {
                // todo редактировать задачу
            }

            @Override
            public void onTaskChecked(Task task, boolean isChecked) {
                taskListViewModel.completeTask(task, isChecked);
                //todo почему не меняется

            }
        });

        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    return;
                }
                SortType type = SortType.getType(position);
                taskListViewModel.sort(type);
                spinnerSort.setSelection(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        recyclerView.setAdapter(taskListAdapter);

        taskListViewModel = new ViewModelProvider(requireActivity()).get(TaskListViewModel.class);
        taskListViewModel.getTasks().observe(getViewLifecycleOwner(), taskListAdapter::submitList);


        buttonFilter.setOnClickListener(v -> requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, new TaskFilterFragment())
                .addToBackStack(null)
                .commit());

        buttonAdd.setOnClickListener(v -> requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, new TaskCreateFragment())
                .addToBackStack(null)
                .commit());
        // todo навигация??


        buttonImport.setOnClickListener(v -> {
            // todo импорт
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        taskListViewModel.loadTasks();
    }
}
