package com.example.questify.ui.tasks.list;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questify.R;
import com.example.questify.domain.model.Task;
import com.example.questify.domain.usecase.plans.tasks.sort.SortType;
import com.example.questify.ui.tasks.create.TaskCreateFragment;
import com.example.questify.ui.tasks.edit.TaskEditFragment;
import com.example.questify.ui.tasks.list.filter.TaskFilterFragment;
import com.google.android.material.snackbar.Snackbar;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TaskListFragment extends Fragment {
    private TaskListViewModel taskListViewModel;
    private ActivityResultLauncher<String> pickFileLauncher;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_task_list, container, false);
        return rootView;
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
        pickFileLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                taskListViewModel.importFromFile(requireContext(), uri, getFileName(uri));
            }
        });

        Button buttonAdd = view.findViewById(R.id.buttonAddTask);

        TaskListAdapter taskListAdapter = new TaskListAdapter(new TaskListAdapter.Listener() {
            @Override
            public void onTaskClicked(Task task) {
                Bundle args = new Bundle();
                args.putString("taskGlobalId", task.getGlobalId());

                requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, TaskEditFragment.class, args)
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onTaskChecked(Task task, boolean isChecked) {
                taskListViewModel.completeTask(task, isChecked);
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

        taskListViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                showSnackbar(error);
            }
        });

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

        buttonImport.setOnClickListener(v ->
                pickFileLauncher.launch("*/*"));
    }

    private void showSnackbar(String message) {
        if (rootView != null) {
            Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private String getFileName(Uri uri) {
        try (Cursor cursor = requireContext()
                .getContentResolver()
                .query(uri, null, null, null, null)) {

            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (index >= 0) {
                    return cursor.getString(index);
                }
            }
        }
        String fallback = uri.getLastPathSegment();
        return fallback != null ? fallback : "";
    }
}