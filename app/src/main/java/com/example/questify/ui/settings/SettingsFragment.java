package com.example.questify.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.questify.R;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SettingsFragment extends Fragment {

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
        Button buttonExport = view.findViewById(R.id.buttonExportTasks);

        buttonReset.setOnClickListener(v ->
                settingsViewModel.resetProgress());
        buttonDeleteCompleted.setOnClickListener(v ->
                settingsViewModel.deleteCompletedTasks());
        buttonExport.setOnClickListener(v ->
                settingsViewModel.exportTasks());
    }
}
