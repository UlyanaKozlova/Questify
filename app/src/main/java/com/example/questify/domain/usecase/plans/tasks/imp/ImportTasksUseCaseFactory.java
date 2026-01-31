package com.example.questify.domain.usecase.plans.tasks.imp;

import com.example.questify.domain.usecase.plans.tasks.task.CreateTaskUseCase;

import javax.inject.Inject;

public class ImportTasksUseCaseFactory {

    @Inject
    public ImportTasksUseCaseFactory() {
    }

    public ImportTasksUseCase get(String fileName, CreateTaskUseCase createTaskUseCase) {

        if (fileName.endsWith(getFormat(ImportFormat.JSON))) {
            return new ImportTasksFromJsonUseCase(createTaskUseCase);
        }
        if (fileName.endsWith(getFormat(ImportFormat.CSV))) {
            return new ImportTasksFromCsvUseCase(createTaskUseCase);
        }
        throw new RuntimeException("WrongFormat");
    }

    private String getFormat(ImportFormat importFormat) {
        return "." + importFormat.name().toLowerCase();
    }

    enum ImportFormat {
        CSV,
        JSON
    }
}
