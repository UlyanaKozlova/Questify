package com.example.questify.ui.settings;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.questify.R;
import com.example.questify.domain.model.Project;
import com.example.questify.ui.auth.AuthActivity;
import com.example.questify.ui.utils.AppPreferences;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SettingsFragment extends Fragment {

    public static final String RU = "ru";
    public static final String EN = "en";
    private ActivityResultLauncher<String> createJsonLauncher;
    private ActivityResultLauncher<String> createIcsLauncher;
    private ActivityResultLauncher<String> createStatisticsJsonLauncher;
    private ActivityResultLauncher<String> createStatisticsPngLauncher;

    private SettingsViewModel settingsViewModel;
    private View rootView;
    private List<Project> projectList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        settingsViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                showSnackbar(error);
            }
        });
        setupAccountSection(view);
        setupAppearanceSection(view);
        setupProgressSection(view);
        setupProjectsSection(view);
        setupExportSection(view);
    }

    private void setupAccountSection(View view) {
        TextView tvEmail = view.findViewById(R.id.tvAccountEmail);
        MaterialButton btnResetPassword = view.findViewById(R.id.buttonResetPassword);
        MaterialButton btnSignOut = view.findViewById(R.id.buttonSignOut);

        String email = settingsViewModel.getCurrentAccountEmail();
        if (email != null) {
            tvEmail.setText(getString(R.string.settings_account_email, email));
            btnResetPassword.setVisibility(View.VISIBLE);
        } else {
            tvEmail.setText(R.string.settings_account_guest);
            btnResetPassword.setVisibility(View.GONE);
        }

        btnResetPassword.setOnClickListener(v -> {
            String currentEmail = settingsViewModel.getCurrentAccountEmail();
            if (currentEmail == null) return;
            settingsViewModel.sendPasswordResetEmail(
                    currentEmail,
                    () -> {
                        if (isAdded()) {
                            requireActivity().runOnUiThread(() ->
                                    showSnackbar(getString(R.string.settings_reset_password_sent)));
                        }
                    },
                    error -> {
                        if (isAdded()) {
                            requireActivity().runOnUiThread(() ->
                                    showSnackbar(getString(R.string.settings_reset_password_error, error)));
                        }
                    }
            );
        });

        btnSignOut.setOnClickListener(v -> showConfirmDialog(
                getString(R.string.settings_sign_out),
                getString(R.string.settings_sign_out_confirm),
                () -> {
                    settingsViewModel.signOut();
                    Intent intent = new Intent(requireActivity(), AuthActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
        ));
    }

    private void setupAppearanceSection(View view) {
        SwitchMaterial switchTheme = view.findViewById(R.id.switchTheme);
        MaterialButton btnRu = view.findViewById(R.id.btnLanguageRu);
        MaterialButton btnEn = view.findViewById(R.id.btnLanguageEn);

        int savedMode = AppPreferences.getNightMode(requireContext());
        switchTheme.setOnCheckedChangeListener(null);
        switchTheme.setChecked(savedMode == AppCompatDelegate.MODE_NIGHT_YES);
        switchTheme.setOnCheckedChangeListener((btn, isChecked) -> {
            int mode = isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
            AppPreferences.saveNightMode(requireContext(), mode);
            AppCompatDelegate.setDefaultNightMode(mode);
        });

        String savedLang = AppPreferences.getLanguage(requireContext());
        String currentLang = savedLang.isEmpty() ? getCurrentLanguage() : savedLang;
        updateLanguageButtons(btnRu, btnEn, currentLang);

        btnRu.setOnClickListener(v -> {
            AppPreferences.saveLanguage(requireContext(), RU);
            setAppLanguage(RU);
            updateLanguageButtons(btnRu, btnEn, RU);
        });
        btnEn.setOnClickListener(v -> {
            AppPreferences.saveLanguage(requireContext(), EN);
            setAppLanguage(EN);
            updateLanguageButtons(btnRu, btnEn, EN);
        });
    }

    private void setupProgressSection(View view) {
        MaterialButton buttonReset = view.findViewById(R.id.buttonResetProgress);
        MaterialButton buttonDeleteCompleted = view.findViewById(R.id.buttonDeleteCompleted);

        buttonReset.setOnClickListener(v -> showConfirmDialog(
                getString(R.string.settings_reset_progress),
                getString(R.string.settings_reset_progress_confirm),
                () -> settingsViewModel.resetProgress()
        ));

        buttonDeleteCompleted.setOnClickListener(v -> showConfirmDialog(
                getString(R.string.settings_delete_completed),
                getString(R.string.settings_delete_completed_confirm),
                () -> settingsViewModel.deleteCompletedTasks()
        ));
    }

    private void setupProjectsSection(View view) {
        Spinner spinnerProjects = view.findViewById(R.id.spinnerProjects);
        MaterialButton buttonDeleteProject = view.findViewById(R.id.buttonDeleteProject);

        ArrayAdapter<String> projectAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, new ArrayList<>());
        projectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProjects.setAdapter(projectAdapter);

        settingsViewModel.getProjects().observe(getViewLifecycleOwner(), projects -> {
            projectList = projects != null ? projects : new ArrayList<>();
            projectAdapter.clear();
            for (Project p : projectList) {
                projectAdapter.add(p.getProjectName());
            }
            projectAdapter.notifyDataSetChanged();
        });

        buttonDeleteProject.setOnClickListener(v -> {
            int pos = spinnerProjects.getSelectedItemPosition();
            if (pos < 0 || pos >= projectList.size()) return;
            Project selected = projectList.get(pos);
            showConfirmDialog(
                    getString(R.string.settings_delete_project_title),
                    getString(R.string.settings_delete_project_confirm, selected.getProjectName()),
                    () -> settingsViewModel.deleteProject(selected)
            );
        });
    }

    private void setupExportSection(View view) {
        createJsonLauncher = registerForActivityResult(
                new ActivityResultContracts.CreateDocument("application/json"),
                uri -> {
                    if (uri != null) settingsViewModel.exportToJson(requireContext(), uri);
                }
        );
        createIcsLauncher = registerForActivityResult(
                new ActivityResultContracts.CreateDocument("text/calendar"),
                uri -> {
                    if (uri != null) settingsViewModel.exportToIcs(requireContext(), uri);
                }
        );
        createStatisticsJsonLauncher = registerForActivityResult(
                new ActivityResultContracts.CreateDocument("application/json"),
                uri -> {
                    if (uri != null)
                        settingsViewModel.exportStatisticsToJson(requireContext(), uri);
                }
        );
        createStatisticsPngLauncher = registerForActivityResult(
                new ActivityResultContracts.CreateDocument("image/png"),
                uri -> {
                    if (uri != null) settingsViewModel.exportStatisticsToPng(requireContext(), uri);
                }
        );

        view.findViewById(R.id.buttonExportJson).setOnClickListener(v ->
                createJsonLauncher.launch("tasks.json"));
        view.findViewById(R.id.buttonExportIcs).setOnClickListener(v ->
                createIcsLauncher.launch("deadlines.ics"));
        view.findViewById(R.id.buttonExportStatisticsToJson).setOnClickListener(v ->
                createStatisticsJsonLauncher.launch("statistics.json"));
        view.findViewById(R.id.buttonExportStatisticsToPng).setOnClickListener(v ->
                createStatisticsPngLauncher.launch("statistics.png"));
    }

    private String getCurrentLanguage() {
        Configuration config = requireContext().getResources().getConfiguration();
        Locale locale = config.getLocales().get(0);
        return locale != null ? locale.getLanguage() : "ru";
    }

    private void setAppLanguage(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration(requireContext().getResources().getConfiguration());
        config.setLocale(locale);
        requireContext().getApplicationContext().createConfigurationContext(config);
        requireActivity().recreate();
    }

    private void updateLanguageButtons(MaterialButton btnRu, MaterialButton btnEn, String lang) {
        boolean ruSelected = RU.equals(lang);
        btnRu.setAlpha(ruSelected ? 1.0f : 0.5f);
        btnEn.setAlpha(ruSelected ? 0.5f : 1.0f);
    }

    private void showSnackbar(String message) {
        if (rootView != null) {
            Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show();
        }
    }

    private void showConfirmDialog(String title, String message, Runnable onConfirm) {
        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(getString(R.string.settings_yes), (dialog, which) -> onConfirm.run())
                .setNegativeButton(getString(R.string.settings_no), null)
                .show();
    }
}