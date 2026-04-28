package com.example.questify.domain.usecase.statistics;

import android.graphics.Bitmap;

import com.example.questify.data.export.StatisticsExporter;

import javax.inject.Inject;

public class ExportStatisticsToPngUseCase {

    private final StatisticsExporter exporter;

    @Inject
    public ExportStatisticsToPngUseCase(StatisticsExporter exporter) {
        this.exporter = exporter;
    }

    public boolean execute(Bitmap bitmap, String fileName) {
        return exporter.saveBitmapToGallery(bitmap, fileName);
    }
}
