package com.example.questify.ui.projects;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questify.R;
import com.example.questify.domain.model.Project;
import com.example.questify.domain.usecase.plans.project.GetProjectStatisticsUseCase;
import com.example.questify.ui.projects.detail.ProjectDetailFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProjectsFragment extends Fragment {

    private ProjectsViewModel viewModel;
    private ProjectsAdapter adapter;
    private RecyclerView recyclerProjects;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_projects, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(ProjectsViewModel.class);

        recyclerProjects = view.findViewById(R.id.recyclerProjects);
        recyclerProjects.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new ProjectsAdapter(
                project -> {
                    Bundle args = new Bundle();
                    args.putString("projectGlobalId", project.getGlobalId());
                    args.putString("projectName", project.getProjectName());

                    ProjectDetailFragment fragment = new ProjectDetailFragment();
                    fragment.setArguments(args);

                    requireActivity()
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentContainer, fragment)
                            .addToBackStack(null)
                            .commit();
                },
                this::showProjectActionsDialog
        );

        recyclerProjects.setAdapter(adapter);

        FloatingActionButton fabAdd = view.findViewById(R.id.fabAddProject);
        fabAdd.setOnClickListener(v -> showCreateProjectDialog());

        viewModel.getAllProjects().observe(getViewLifecycleOwner(), projects -> {
            if (projects != null) {
                adapter.submitList(projects);
                for (Project project : projects) {
                    viewModel.loadProjectStatistics(project.getGlobalId());
                }
            }
        });

        viewModel.getProjectStatisticsMap().observe(getViewLifecycleOwner(), statsMap -> {
            if (statsMap != null && adapter != null) {
                for (int i = 0; i < adapter.getItemCount(); i++) {
                    Project project = adapter.getProjectAt(i);
                    if (project != null) {
                        GetProjectStatisticsUseCase.ProjectStatistics stats = statsMap.get(project.getGlobalId());
                        if (stats != null) {
                            RecyclerView.ViewHolder holder = recyclerProjects.findViewHolderForAdapterPosition(i);
                            if (holder instanceof ProjectsAdapter.ProjectViewHolder) {
                                ((ProjectsAdapter.ProjectViewHolder) holder).updateStats(
                                        stats.total, stats.completed, stats.getProgressPercent());
                            }
                        }
                    }
                }
            }
        });
    }

    private void showCreateProjectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Создать проект");

        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_create_project, null);

        EditText inputName = dialogView.findViewById(R.id.inputProjectName);
        View colorPicker = dialogView.findViewById(R.id.colorPicker);
        View selectedColorView = dialogView.findViewById(R.id.selectedColor);

        AtomicReference<String> selectedColor = new AtomicReference<>("#FF6200EE");
        ((GradientDrawable) selectedColorView.getBackground()).setColor(Color.parseColor(selectedColor.get()));

        colorPicker.setOnClickListener(v -> {
            String[] colors = {"#FF6200EE", "#F44336", "#4CAF50", "#FFC107", "#2196F3", "#9C27B0"};
            String[] colorNames = {"Фиолетовый", "Красный", "Зеленый", "Желтый", "Синий", "Розовый"};

            new AlertDialog.Builder(requireContext())
                    .setTitle("Выберите цвет")
                    .setItems(colorNames, (dialog, which) -> {
                        selectedColor.set(colors[which]);
                        ((GradientDrawable) selectedColorView.getBackground())
                                .setColor(Color.parseColor(selectedColor.get()));
                    })
                    .show();
        });

        builder.setView(dialogView);

        builder.setPositiveButton("Создать", (dialog, which) -> {
            String projectName = inputName.getText().toString().trim();


            viewModel.createProject(projectName, selectedColor.get(),
                    new ProjectsViewModel.OnProjectCreatedListener() {
                        @Override
                        public void onResult(boolean success) {
                            mainHandler.post(() -> {
                                if (!success) {
                                    showToast("Проект с таким названием уже существует");
                                }
                            });
                        }

                        @Override
                        public void onError(String error) {
                            mainHandler.post(() -> showToast(error));
                        }
                    },
                    requireContext());
        });

        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    private void showProjectActionsDialog(Project project) {
        boolean isDefault = viewModel.isDefaultProject(project);

        String[] actions;
        if (isDefault) {
            actions = new String[]{
                    "Редактировать цвет",
                    "Экспорт"
            };
        } else {
            actions = new String[]{
                    "Редактировать",
                    "Удалить",
                    "Экспорт"
            };
        }

        new AlertDialog.Builder(requireContext())
                .setTitle(project.getProjectName())
                .setItems(actions, (dialog, which) -> {
                    String action = actions[which];
                    switch (action) {
                        case "Редактировать":
                        case "Редактировать цвет":
                            showEditProjectDialog(project, isDefault);
                            break;
                        case "Удалить":
                            showDeleteProjectDialog(project);
                            break;
                        case "Экспорт":
                            showToast("Экспорт");
                            break;
                    }
                })
                .show();
    }

    private void showEditProjectDialog(Project project, boolean isDefault) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(isDefault
                ? "Редактировать проект цвет"
                : "Редактировать проект");

        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_create_project, null);

        EditText inputName = dialogView.findViewById(R.id.inputProjectName);
        View colorPicker = dialogView.findViewById(R.id.colorPicker);
        View selectedColorView = dialogView.findViewById(R.id.selectedColor);

        if (isDefault) {
            inputName.setText(project.getProjectName());
            inputName.setEnabled(false);
            inputName.setAlpha(0.5f);
        } else {
            inputName.setText(project.getProjectName());
        }

        AtomicReference<String> selectedColor = new AtomicReference<>(project.getColor());
        ((GradientDrawable) selectedColorView.getBackground())
                .setColor(Color.parseColor(selectedColor.get()));

        colorPicker.setOnClickListener(v -> {
            String[] colors = {"#FF6200EE", "#F44336", "#4CAF50", "#FFC107", "#2196F3", "#9C27B0"};
            String[] colorNames = {"Фиолетовый", "Красный", "Зеленый", "Желтый", "Синий", "Розовый"};

            new AlertDialog.Builder(requireContext())
                    .setTitle("Выберите цвет")
                    .setItems(colorNames, (dialog, which) -> {
                        selectedColor.set(colors[which]);
                        ((GradientDrawable) selectedColorView.getBackground())
                                .setColor(Color.parseColor(selectedColor.get()));
                    })
                    .show();
        });

        builder.setView(dialogView);

        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            String newName = inputName.getText().toString().trim();
            if (!isDefault && newName.isEmpty()) {
                showToast("Введите название проекта");
                return;
            }
            if (!isDefault && newName.length() < 3) {
                showToast("Название должно быть не менее 3 символов");
                return;
            }

            viewModel.updateProject(project,
                    isDefault ? project.getProjectName() : newName,
                    selectedColor.get(),
                    new ProjectsViewModel.OnProjectUpdatedListener() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError(String error) {
                            mainHandler.post(() -> showToast(error));
                        }
                    },
                    requireContext());
        });

        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    private void showDeleteProjectDialog(Project project) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Удаление проекта")
                .setMessage("Удалить проект \"" + project.getProjectName() + "\"?\n\n" +
                        "Все задачи этого проекта будут перенесены в проект \"" +
                        com.example.questify.data.repository.ProjectRepository.DEFAULT_PROJECT_NAME + "\"")
                .setPositiveButton("Удалить", (dialog, which) -> {
                    ProgressDialog progressDialog = new ProgressDialog(requireContext());
                    progressDialog.setMessage("Удаление проекта...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    viewModel.deleteProjectWithTasks(project, new ProjectsViewModel.OnProjectDeletedListener() {
                        @Override
                        public void onSuccess() {
                            mainHandler.post(() -> {
                                progressDialog.dismiss();
                                showToast("Проект удален, задачи перенесены в стандартный проект");
                            });
                        }

                        @Override
                        public void onError(String error) {
                            mainHandler.post(() -> {
                                progressDialog.dismiss();
                                showToast("Ошибка: " + error);
                            });
                        }
                    });
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private static class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.ProjectViewHolder> {
        private List<Project> projects = new ArrayList<>();
        private final OnProjectClickListener clickListener;
        private final OnProjectMenuListener menuListener;

        interface OnProjectClickListener {
            void onProjectClick(Project project);
        }

        interface OnProjectMenuListener {
            void onMenuClick(Project project);
        }

        public ProjectsAdapter(OnProjectClickListener clickListener,
                               OnProjectMenuListener menuListener) {
            this.clickListener = clickListener;
            this.menuListener = menuListener;
        }

        public void submitList(List<Project> newProjects) {
            this.projects = newProjects != null ? newProjects : new ArrayList<>();
            notifyDataSetChanged();
        }

        public Project getProjectAt(int position) {
            if (position >= 0 && position < projects.size()) {
                return projects.get(position);
            }
            return null;
        }

        @Override
        public int getItemCount() {
            return projects.size();
        }

        @NonNull
        @Override
        public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_project, parent, false);
            return new ProjectViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
            Project project = projects.get(position);
            holder.bind(project, clickListener, menuListener);
        }

        static class ProjectViewHolder extends RecyclerView.ViewHolder {
            View colorView;
            TextView textName;
            TextView textStats;
            ProgressBar progressBar;
            ImageView buttonMenu;

            public ProjectViewHolder(@NonNull View itemView) {
                super(itemView);
                colorView = itemView.findViewById(R.id.viewProjectColor);
                textName = itemView.findViewById(R.id.textProjectName);
                textStats = itemView.findViewById(R.id.textProjectStats);
                progressBar = itemView.findViewById(R.id.progressProject);
                buttonMenu = itemView.findViewById(R.id.buttonProjectMenu);
            }

            void bind(Project project, OnProjectClickListener clickListener,
                      OnProjectMenuListener menuListener) {
                textName.setText(project.getProjectName());

                try {
                    colorView.setBackgroundColor(Color.parseColor(project.getColor()));
                } catch (Exception e) {
                    colorView.setBackgroundColor(Color.parseColor("#FF6200EE"));
                }

                textStats.setText("Загрузка...");
                progressBar.setProgress(0);

                itemView.setOnClickListener(v -> clickListener.onProjectClick(project));
                buttonMenu.setOnClickListener(v -> menuListener.onMenuClick(project));
            }

            void updateStats(int total, int completed, int percent) {
                textStats.setText(String.format("Задач: %d | Выполнено: %d", total, completed));
                progressBar.setProgress(percent);
            }
        }
    }
}