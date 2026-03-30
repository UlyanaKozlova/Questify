package com.example.questify.ui.projects.detail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questify.R;
import com.example.questify.domain.model.Task;
import com.example.questify.ui.tasks.create.TaskCreateFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProjectDetailFragment extends Fragment {

    private ProjectDetailViewModel viewModel;
    private String projectGlobalId;
    private String projectName;
    private TasksInProjectAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_project_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            projectGlobalId = getArguments().getString("projectGlobalId");
            projectName = getArguments().getString("projectName");
        }

        viewModel = new ViewModelProvider(this).get(ProjectDetailViewModel.class);
        TextView textTitle = view.findViewById(R.id.textProjectTitle);
        TextView textStats = view.findViewById(R.id.textProjectStats);
        TextView textProgress = view.findViewById(R.id.textProgress);
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        RecyclerView recyclerTasks = view.findViewById(R.id.recyclerProjectTasks);
        Button buttonAddTask = view.findViewById(R.id.buttonAddTaskToProject);

        textTitle.setText(projectName);

        adapter = new TasksInProjectAdapter(
                task -> {
                    Bundle args = new Bundle();
                    args.putString("taskGlobalId", task.getGlobalId());

                    com.example.questify.ui.tasks.edit.TaskEditFragment fragment =
                            new com.example.questify.ui.tasks.edit.TaskEditFragment();
                    fragment.setArguments(args);

                    requireActivity()
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentContainer, fragment)
                            .addToBackStack(null)
                            .commit();
                },
                (task, isChecked) -> viewModel.updateTaskDone(task, isChecked)
        );
        recyclerTasks.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerTasks.setAdapter(adapter);

        viewModel.getProjectTasks().observe(getViewLifecycleOwner(), tasks -> {
            if (tasks != null) {
                adapter.submitList(tasks);
            }
        });

        viewModel.getProjectStatistics().observe(getViewLifecycleOwner(), stats -> {
            if (stats != null) {
                textStats.setText(String.format("Задач: %d | Выполнено: %d | Просрочено: %d",
                        stats.total, stats.completed, stats.overdue));

                int percent = stats.getProgressPercent();
                textProgress.setText(String.format("Прогресс: %d%%", percent));
                progressBar.setProgress(percent);
            }
        });

        buttonAddTask.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("preSelectedProjectGlobalId", projectGlobalId);
            args.putString("preSelectedProjectName", projectName);

            TaskCreateFragment fragment = new TaskCreateFragment();
            fragment.setArguments(args);

            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        viewModel.loadProjectData(projectGlobalId);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adapter = null;
    }

    private static class TasksInProjectAdapter extends RecyclerView.Adapter<TasksInProjectAdapter.TaskViewHolder> {
        private List<Task> tasks = new ArrayList<>();
        private final OnTaskClickListener clickListener;
        private final OnTaskCheckListener checkListener;
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

        interface OnTaskClickListener {
            void onTaskClick(Task task);
        }

        interface OnTaskCheckListener {
            void onTaskChecked(Task task, boolean isChecked);
        }

        public TasksInProjectAdapter(OnTaskClickListener clickListener,
                                     OnTaskCheckListener checkListener) {
            this.clickListener = clickListener;
            this.checkListener = checkListener;
        }

        public void submitList(List<Task> newTasks) {
            this.tasks = newTasks != null ? newTasks : new ArrayList<>();
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_task, parent, false);
            return new TaskViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
            Task task = tasks.get(position);
            holder.bind(task, clickListener, checkListener, dateFormat);
        }

        @Override
        public int getItemCount() {
            return tasks.size();
        }

        static class TaskViewHolder extends RecyclerView.ViewHolder {
            CheckBox checkboxDone;
            TextView textTaskTitle;
            TextView textTaskDeadline;

            TaskViewHolder(@NonNull View itemView) {
                super(itemView);
                checkboxDone = itemView.findViewById(R.id.checkboxDone);
                textTaskTitle = itemView.findViewById(R.id.textTaskTitle);
                textTaskDeadline = itemView.findViewById(R.id.textTaskDeadline);
            }

            void bind(Task task, OnTaskClickListener clickListener,
                      OnTaskCheckListener checkListener, SimpleDateFormat dateFormat) {
                textTaskTitle.setText(task.getTaskName());

                String deadlineText = "Дедлайн: " + dateFormat.format(new Date(task.getDeadline()));
                if (!task.isDone() && task.getDeadline() < System.currentTimeMillis()) {
                    textTaskDeadline.setTextColor(0xFFFF0000);
                    deadlineText += " (Просрочено)";
                } else {
                    textTaskDeadline.setTextColor(0xFF888888);
                }
                textTaskDeadline.setText(deadlineText);

                checkboxDone.setChecked(task.isDone());

                checkboxDone.setOnCheckedChangeListener(null);
                checkboxDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    checkListener.onTaskChecked(task, isChecked);
                });

                itemView.setOnClickListener(v -> clickListener.onTaskClick(task));
            }
        }
    }
}