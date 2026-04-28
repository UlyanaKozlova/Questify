package com.example.questify.domain.usecase.statistics;

import com.example.questify.data.export.StatisticsExporter;
import com.example.questify.domain.model.helpers.ProjectTaskCount;
import com.example.questify.domain.model.helpers.TaskStatistics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class ExportStatisticsToJsonUseCase {

    private final StatisticsExporter exporter;

    @Inject
    public ExportStatisticsToJsonUseCase(StatisticsExporter exporter) {
        this.exporter = exporter;
    }

    public boolean execute(TaskStatistics stats, List<ProjectTaskCount> chartData, String fileName) {
        Map<String, Object> map = new HashMap<>();
        map.put("total", stats.total);
        map.put("completed", stats.completed);
        map.put("overdue", stats.overdue);
        map.put("completionPercent", stats.getCompletionPercent());
        map.put("overduePercent", stats.getOverduePercent());
        map.put("byProject", chartData);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return exporter.saveJsonToDownloads(gson.toJson(map), fileName);
    }
}
