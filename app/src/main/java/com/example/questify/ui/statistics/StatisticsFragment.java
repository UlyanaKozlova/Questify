package com.example.questify.ui.statistics;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import com.github.mikephil.charting.animation.Easing;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.questify.R;
import com.example.questify.domain.model.helpers.ProjectTaskCount;
import com.example.questify.domain.model.helpers.TaskStatistics;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class StatisticsFragment extends Fragment {

    private StatisticsViewModel viewModel;

    private TextView textTotal;
    private TextView textCompleted;
    private TextView textOverdue;
    private TextView tvStatsUsername;
    private TextView tvStatsLevel;
    private PieChart pieChart;
    private View cardStats;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (!granted) {
                    showMessage(getString(R.string.stats_permission_denied));
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(StatisticsViewModel.class);

        textTotal = view.findViewById(R.id.textTotal);
        textCompleted = view.findViewById(R.id.textCompleted);
        textOverdue = view.findViewById(R.id.textOverdue);
        tvStatsUsername = view.findViewById(R.id.tvStatsUsername);
        tvStatsLevel = view.findViewById(R.id.tvStatsLevel);
        pieChart = view.findViewById(R.id.pieChart);
        view.findViewById(R.id.chipGroupExportFormat);
        cardStats = view.findViewById(R.id.cardStats);

        MaterialButton buttonExportStats = view.findViewById(R.id.buttonExportStats);

        setupPieChart();
        requestStoragePermissionIfNeeded();

        viewModel.getTaskStatistics().observe(getViewLifecycleOwner(), this::updateStatsUI);
        viewModel.getProjectChartData().observe(getViewLifecycleOwner(), this::updateChart);
        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user == null) return;
            tvStatsUsername.setText(user.getUsername());
            tvStatsLevel.setText(getString(R.string.stats_user_level, user.getLevel()));
        });
        viewModel.getExportResult().observe(getViewLifecycleOwner(), resId -> {
            if (resId != null) {
                showMessage(getString(resId));
            }
        });

        buttonExportStats.setOnClickListener(v -> {
            if (isJsonSelected()) {
                viewModel.exportStatsAsJson();
            } else {
                viewModel.exportStatsAsPng(cardStats);
            }
        });
    }

    private void setupPieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(42f);
        int surfaceColor = requireContext().getResources().getColor(R.color.bg_surface, requireContext().getTheme());
        int secondaryTextColor = requireContext().getResources().getColor(R.color.text_secondary, requireContext().getTheme());
        pieChart.setHoleColor(surfaceColor);
        pieChart.setTransparentCircleRadius(47f);
        pieChart.setTransparentCircleColor(surfaceColor);
        pieChart.setDrawCenterText(false);
        pieChart.setRotationEnabled(false);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.getLegend().setEnabled(true);
        pieChart.getLegend().setWordWrapEnabled(true);
        pieChart.getLegend().setTextColor(secondaryTextColor);
        pieChart.setNoDataText(getString(R.string.stats_chart_no_data));
        pieChart.setNoDataTextColor(secondaryTextColor);
        pieChart.setBackgroundColor(Color.TRANSPARENT);
    }

    private void updateStatsUI(TaskStatistics stats) {
        if (stats == null) {
            return;
        }
        textTotal.setText(String.valueOf(stats.total));
        textCompleted.setText(String.valueOf(stats.completed));
        textOverdue.setText(String.valueOf(stats.overdue));
    }

    private void updateChart(List<ProjectTaskCount> data) {
        if (data == null || data.isEmpty()) {
            pieChart.clear();
            pieChart.invalidate();
            return;
        }

        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        for (ProjectTaskCount item : data) {
            if (item.taskCount > 0) {
                entries.add(new PieEntry(item.taskCount, item.projectName));
                try {
                    colors.add(Color.parseColor(item.color));
                } catch (IllegalArgumentException e) {
                    colors.add(Color.GRAY);
                }
            }
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(6f);
        dataSet.setValueTextSize(11f);
        dataSet.setValueTextColor(Color.WHITE);

        PieData pieData = new PieData(dataSet);
        pieData.setValueFormatter(new PercentFormatter(pieChart));
        pieData.setValueTextSize(11f);

        pieChart.setData(pieData);
        pieChart.animateY(800, Easing.EaseInOutQuad);
        pieChart.invalidate();
    }

    private boolean isJsonSelected() {
        Chip chipJson = requireView().findViewById(R.id.chipJson);
        return chipJson.isChecked();
    }

    private void requestStoragePermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        }
    }

    private void showMessage(String message) {
        View root = getView();
        if (root != null) {
            Snackbar.make(root, message, Snackbar.LENGTH_SHORT).show();
        }
    }
}
