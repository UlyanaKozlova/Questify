package com.example.questify.ui.projects;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questify.R;
import com.example.questify.data.repository.ProjectRepository;
import com.example.questify.domain.model.Project;
import com.example.questify.domain.usecase.plans.project.GetProjectStatisticsUseCase;
import com.example.questify.ui.projects.detail.ProjectDetailFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
@SuppressLint("NotifyDataSetChanged")
public class ProjectsFragment extends Fragment {

    private ProjectsViewModel viewModel;
    private ProjectsAdapter adapter;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private View rootView;
    private AlertDialog currentDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_projects, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(ProjectsViewModel.class);

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                showSnackbar(error);
            }
        });

        viewModel.getOperationSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                if (currentDialog != null && currentDialog.isShowing()) {
                    currentDialog.dismiss();
                    currentDialog = null;
                }
            }
        });

        RecyclerView recyclerProjects = view.findViewById(R.id.recyclerProjects);
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
                            .add(R.id.fragmentContainer, fragment)
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
            if (adapter != null) {
                adapter.updateStatsMap(statsMap);
            }
        });
    }

    private void showCreateProjectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(getString(R.string.project_create_title));

        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_create_project, null);

        EditText inputName = dialogView.findViewById(R.id.inputProjectName);
        inputName.setHint(getString(R.string.project_name_hint));

        View colorPicker = dialogView.findViewById(R.id.colorPicker);
        View selectedColorView = dialogView.findViewById(R.id.selectedColor);

        AtomicReference<String> selectedColor = new AtomicReference<>("#FF6200EE");
        ((GradientDrawable) selectedColorView.getBackground()).setColor(Color.parseColor(selectedColor.get()));

        colorPicker.setOnClickListener(v -> {
            String[] colors = {
                    getString(R.string.project_color_purple),
                    getString(R.string.project_color_red),
                    getString(R.string.project_color_green),
                    getString(R.string.project_color_yellow),
                    getString(R.string.project_color_blue),
                    getString(R.string.project_color_pink)
            };
            String[] colorHex = {"#FF6200EE", "#F44336", "#4CAF50", "#FFC107", "#2196F3", "#9C27B0"};
            // todo colors
            new AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.project_color_picker_title))
                    .setItems(colors, (dialog, which) -> {
                        selectedColor.set(colorHex[which]);
                        ((GradientDrawable) selectedColorView.getBackground())
                                .setColor(Color.parseColor(selectedColor.get()));
                    })
                    .show();
        });

        builder.setView(dialogView);

        builder.setPositiveButton(getString(R.string.project_create), null);
        builder.setNegativeButton(getString(R.string.project_cancel), (dialog, which) -> currentDialog = null);

        currentDialog = builder.create();
        currentDialog.show();

        currentDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String projectName = inputName.getText().toString().trim();

            if (projectName.isEmpty()) {
                showSnackbar(getString(R.string.project_name_empty));
                return;
            }
            if (projectName.length() < 3) {
                showSnackbar(getString(R.string.project_name_too_short));
                return;
            }

            v.setEnabled(false);

            viewModel.createProject(projectName, selectedColor.get(),
                    new ProjectsViewModel.OnProjectCreatedListener() {
                        @Override
                        public void onResult(boolean success) {
                            mainHandler.post(() -> {
                                v.setEnabled(true);
                                if (!success) {
                                    showSnackbar(getString(R.string.project_name_exists));
                                }
                            });
                        }

                        @Override
                        public void onError(String error) {
                            mainHandler.post(() -> {
                                v.setEnabled(true);
                                showSnackbar(error);
                            });
                        }
                    });
        });
    }

    private void showProjectActionsDialog(Project project) {
        boolean isDefault = viewModel.isDefaultProject(project);

        String[] actions;
        if (isDefault) {
            actions = new String[]{
                    getString(R.string.project_edit_color_only),
                    getString(R.string.project_export)
            };
        } else {
            actions = new String[]{
                    getString(R.string.project_edit),
                    getString(R.string.project_delete),
                    getString(R.string.project_export)
            };
        }

        new AlertDialog.Builder(requireContext())
                .setTitle(project.getProjectName())
                .setItems(actions, (dialog, which) -> {
                    String action = actions[which];
                    if (action.equals(getString(R.string.project_edit)) ||
                            action.equals(getString(R.string.project_edit_color_only))) {
                        showEditProjectDialog(project, isDefault);
                    } else if (action.equals(getString(R.string.project_delete))) {
                        showDeleteProjectDialog(project);
                    } else if (action.equals(getString(R.string.project_export))) {
                        showSnackbar(getString(R.string.project_export));
                    }
                })
                .show();
    }

    private void showEditProjectDialog(Project project, boolean isDefault) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(isDefault
                ? getString(R.string.project_edit_only_color_title)
                : getString(R.string.project_edit_title));

        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_create_project, null);

        EditText inputName = dialogView.findViewById(R.id.inputProjectName);
        inputName.setHint(getString(R.string.project_name_hint));

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
            String[] colors = {
                    getString(R.string.project_color_purple),
                    getString(R.string.project_color_red),
                    getString(R.string.project_color_green),
                    getString(R.string.project_color_yellow),
                    getString(R.string.project_color_blue),
                    getString(R.string.project_color_pink)
            };
            String[] colorHex = {"#FF6200EE", "#F44336", "#4CAF50", "#FFC107", "#2196F3", "#9C27B0"};

            new AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.project_color_picker_title))
                    .setItems(colors, (dialog, which) -> {
                        selectedColor.set(colorHex[which]);
                        ((GradientDrawable) selectedColorView.getBackground())
                                .setColor(Color.parseColor(selectedColor.get()));
                    })
                    .show();
        });

        builder.setView(dialogView);

        builder.setPositiveButton(getString(R.string.project_save), null);
        builder.setNegativeButton(getString(R.string.project_cancel), (dialog, which) -> currentDialog = null);

        currentDialog = builder.create();
        currentDialog.show();

        currentDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String newName = inputName.getText().toString().trim();

            if (!isDefault && newName.isEmpty()) {
                showSnackbar(getString(R.string.project_name_empty));
                return;
            }
            if (!isDefault && newName.length() < 3) {
                showSnackbar(getString(R.string.project_name_too_short));
                return;
            }

            v.setEnabled(false);

            viewModel.updateProject(project,
                    isDefault ? project.getProjectName() : newName,
                    selectedColor.get(),
                    new ProjectsViewModel.OnProjectUpdatedListener() {
                        @Override
                        public void onSuccess() {
                            mainHandler.post(() -> {
                                v.setEnabled(true);
                                if (currentDialog != null && currentDialog.isShowing()) {
                                    currentDialog.dismiss();
                                    currentDialog = null;
                                }
                                showSnackbar(getString(R.string.project_updated));
                            });
                        }

                        @Override
                        public void onError(String error) {
                            mainHandler.post(() -> {
                                v.setEnabled(true);
                                showSnackbar(error);
                            });
                        }
                    });
        });
    }

    private void showDeleteProjectDialog(Project project) {
        new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.project_delete_title))
                .setMessage(getString(R.string.project_delete_message,
                        project.getProjectName(),
                        ProjectRepository.DEFAULT_PROJECT_NAME))
                .setPositiveButton(getString(R.string.project_delete), (dialog, which) -> {
                    ProgressDialog progressDialog = new ProgressDialog(requireContext());
                    progressDialog.setMessage(getString(R.string.project_deleting));
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    viewModel.deleteProjectWithTasks(project, new ProjectsViewModel.OnProjectDeletedListener() {
                        @Override
                        public void onSuccess() {
                            mainHandler.post(() -> {
                                progressDialog.dismiss();
                                showSnackbar(getString(R.string.project_deleted_success));
                            });
                        }

                        @Override
                        public void onError(String error) {
                            mainHandler.post(() -> {
                                progressDialog.dismiss();
                                showSnackbar(error);
                            });
                        }
                    });
                })
                .setNegativeButton(getString(R.string.project_cancel), null)
                .show();
    }

    private void showSnackbar(String message) {
        if (rootView != null) {
            Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (currentDialog != null && currentDialog.isShowing()) {
            currentDialog.dismiss();
            currentDialog = null;
        }
    }

    private static class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.ProjectViewHolder> {
        private List<Project> projects = new ArrayList<>();
        private java.util.Map<String, GetProjectStatisticsUseCase.ProjectStatistics> statsMap = new java.util.HashMap<>();
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

        public void updateStatsMap(java.util.Map<String, GetProjectStatisticsUseCase.ProjectStatistics> map) {
            if (map != null) {
                this.statsMap = map;
                notifyDataSetChanged();
            }
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
            GetProjectStatisticsUseCase.ProjectStatistics stats = statsMap.get(project.getGlobalId());
            holder.bind(project, stats, clickListener, menuListener);
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

            void bind(Project project,
                      GetProjectStatisticsUseCase.ProjectStatistics statistics,
                      OnProjectClickListener clickListener,
                      OnProjectMenuListener menuListener) {
                textName.setText(project.getProjectName());

                try {
                    colorView.setBackgroundColor(Color.parseColor(project.getColor()));
                } catch (Exception e) {
                    colorView.setBackgroundColor(Color.parseColor("#FF6200EE"));
                }

                if (statistics != null) {
                    textStats.setText(itemView.getContext().getString(
                            R.string.project_stats_format, statistics.total, statistics.completed));
                    progressBar.setProgress(statistics.getProgressPercent());
                } else {
                    textStats.setText(itemView.getContext().getString(R.string.project_stats_loading));
                    progressBar.setProgress(0);
                }

                itemView.setOnClickListener(v -> clickListener.onProjectClick(project));
                buttonMenu.setOnClickListener(v -> menuListener.onMenuClick(project));
            }
        }
    }
}