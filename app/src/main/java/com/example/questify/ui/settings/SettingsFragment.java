package com.example.questify.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.questify.R;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SettingsFragment extends Fragment {

    private static final String POSITIVE_RESPONSE = "Да";
    private static final String NEGATIVE_RESPONSE = "Отмена";

    private ActivityResultLauncher<String> createJsonLauncher;
    private ActivityResultLauncher<String> createIcsLauncher;
    private ActivityResultLauncher<String> createStatisticsJsonLauncher;
    private ActivityResultLauncher<String> createStatisticsPngLauncher;

    private SettingsViewModel settingsViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        Button buttonReset = view.findViewById(R.id.buttonResetProgress);
        Button buttonDeleteCompleted = view.findViewById(R.id.buttonDeleteCompleted);
        Button buttonExportJson = view.findViewById(R.id.buttonExportJson);
        Button buttonExportIcs = view.findViewById(R.id.buttonExportIcs);
        Button buttonExportStatisticsToJson = view.findViewById(R.id.buttonExportStatisticsToJson);
        Button buttonExportStatisticsToPng = view.findViewById(R.id.buttonExportStatisticsToPng);

        createJsonLauncher = registerForActivityResult(
                new ActivityResultContracts.CreateDocument("application/json"),
                uri -> {
                    if (uri != null) {
                        settingsViewModel.exportToJson(requireContext(), uri);
                    }
                }
        );

        createIcsLauncher = registerForActivityResult(
                new ActivityResultContracts.CreateDocument("text/calendar"),
                uri -> {
                    if (uri != null) {
                        settingsViewModel.exportToIcs(requireContext(), uri);
                    }
                }
        );

        createStatisticsJsonLauncher = registerForActivityResult(
                new ActivityResultContracts.CreateDocument("application/json"),
                uri -> {
                    if (uri != null) {
                        settingsViewModel.exportStatisticsToJson(requireContext(), uri);
                    }
                }
        );

        createStatisticsPngLauncher = registerForActivityResult(
                new ActivityResultContracts.CreateDocument("image/png"),
                uri -> {
                    if (uri != null) {
                        settingsViewModel.exportStatisticsToPng(requireContext(), uri);
                    }
                }
        );

        buttonExportJson.setOnClickListener(v ->
                createJsonLauncher.launch("tasks.json"));

        buttonExportIcs.setOnClickListener(v ->
                createIcsLauncher.launch("deadlines.ics"));

        buttonExportStatisticsToJson.setOnClickListener(v ->
                createStatisticsJsonLauncher.launch("statistics.json"));

        buttonExportStatisticsToPng.setOnClickListener(v ->
                createStatisticsPngLauncher.launch("statistics.png"));


        buttonReset.setOnClickListener(v -> showConfirmDialog(
                "Сброс прогресса",
                "Вы уверены, что хотите удалить весь прогресс?",
                () -> settingsViewModel.resetProgress()
        ));

        buttonDeleteCompleted.setOnClickListener(v -> showConfirmDialog(
                "Удаление задач",
                "Удалить все выполненные задачи?",
                () -> settingsViewModel.deleteCompletedTasks()
        ));
    }

    private void showConfirmDialog(String title, String message, Runnable onConfirm) {
        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(POSITIVE_RESPONSE, (dialog, which) -> onConfirm.run())
                .setNegativeButton(NEGATIVE_RESPONSE, null)
                .show();
    }
}