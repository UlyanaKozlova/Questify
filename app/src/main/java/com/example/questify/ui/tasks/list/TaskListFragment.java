package com.example.questify.ui.tasks.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questify.R;
import com.example.questify.domain.model.Task;
import com.example.questify.ui.tasks.create.TaskCreateFragment;


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
        Button buttonSort = view.findViewById(R.id.buttonSort);
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
            }
        });

        recyclerView.setAdapter(taskListAdapter);

        taskListViewModel = new ViewModelProvider(this).get(TaskListViewModel.class);
        taskListViewModel.getTasks().observe(getViewLifecycleOwner(), taskListAdapter::submitList);


        buttonFilter.setOnClickListener(v -> {
            // todo фильтр
        });

        buttonSort.setOnClickListener(v -> {
            // todo сорт
        });

        buttonAdd.setOnClickListener(v -> {
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new TaskCreateFragment())
                    .addToBackStack(null)
                    .commit();
        });
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
