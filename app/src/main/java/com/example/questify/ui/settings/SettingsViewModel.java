package com.example.questify.ui.settings;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.questify.domain.model.Project;
import com.example.questify.domain.usecase.plans.project.DeleteProjectWithTasksUseCase;
import com.example.questify.domain.usecase.plans.project.GetAllProjectsUseCase;
import com.example.questify.domain.usecase.plans.tasks.exp.ExportStatisticsToJsonUseCase;
import com.example.questify.domain.usecase.plans.tasks.exp.ExportStatisticsToPngUseCase;
import com.example.questify.domain.usecase.plans.tasks.exp.ExportToIcsUseCase;
import com.example.questify.domain.usecase.plans.tasks.exp.ExportToJsonUseCase;
import com.example.questify.domain.usecase.user.DeleteCompletedTasksUseCase;
import com.example.questify.domain.usecase.user.DeleteProgressUseCase;
import com.example.questify.sync.AuthenticationManager;
import com.example.questify.sync.SyncManager;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SettingsViewModel extends ViewModel {

    private final DeleteProgressUseCase deleteProgressUseCase;
    private final DeleteCompletedTasksUseCase deleteCompletedTasksUseCase;
    private final SyncManager syncManager;
    private final ExportToJsonUseCase exportToJsonUseCase;
    private final ExportToIcsUseCase exportToIcsUseCase;
    private final ExportStatisticsToPngUseCase exportStatisticsToPngUseCase;
    private final ExportStatisticsToJsonUseCase exportStatisticsToJsonUseCase;
    private final GetAllProjectsUseCase getAllProjectsUseCase;
    private final DeleteProjectWithTasksUseCase deleteProjectWithTasksUseCase;
    private final AuthenticationManager authManager;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    private final MutableLiveData<String> successMessage = new MutableLiveData<>();

    public LiveData<List<Project>> getProjects() {
        return getAllProjectsUseCase.executeLive();
    }

    public String getCurrentAccountEmail() {
        FirebaseUser user = authManager.getCurrentUser();
        if (user == null) return null;
        return user.isAnonymous() ? null : user.getEmail();
    }

    @Inject
    public SettingsViewModel(DeleteProgressUseCase deleteProgressUseCase,
                             DeleteCompletedTasksUseCase deleteCompletedTasksUseCase,
                             ExportToJsonUseCase exportToJsonUseCase,
                             ExportToIcsUseCase exportToIcsUseCase,
                             ExportStatisticsToPngUseCase exportStatisticsToPngUseCase,
                             ExportStatisticsToJsonUseCase exportStatisticsToJsonUseCase,
                             SyncManager syncManager,
                             GetAllProjectsUseCase getAllProjectsUseCase,
                             DeleteProjectWithTasksUseCase deleteProjectWithTasksUseCase,
                             AuthenticationManager authManager) {
        this.deleteProgressUseCase = deleteProgressUseCase;
        this.deleteCompletedTasksUseCase = deleteCompletedTasksUseCase;
        this.exportToJsonUseCase = exportToJsonUseCase;
        this.exportToIcsUseCase = exportToIcsUseCase;
        this.exportStatisticsToPngUseCase = exportStatisticsToPngUseCase;
        this.exportStatisticsToJsonUseCase = exportStatisticsToJsonUseCase;
        this.syncManager = syncManager;
        this.getAllProjectsUseCase = getAllProjectsUseCase;
        this.deleteProjectWithTasksUseCase = deleteProjectWithTasksUseCase;
        this.authManager = authManager;
    }

    public void sendPasswordResetEmail(String email, Runnable onSuccess, java.util.function.Consumer<String> onError) {
        com.google.firebase.auth.FirebaseAuth.getInstance()
                .sendPasswordResetEmail(email)
                .addOnSuccessListener(unused -> onSuccess.run())
                .addOnFailureListener(e -> onError.accept(e.getMessage()));
    }

    public void signOut() {
        authManager.signOut();
    }

    public void deleteProject(Project project) {
        executor.execute(() -> {
            try {
                deleteProjectWithTasksUseCase.execute(project);
                syncManager.scheduleSyncSoon();
            } catch (Exception e) {
                errorMessage.postValue(e.getMessage());
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdownNow();
    }

    public void resetProgress() {
        executor.execute(() -> {
            try {
                deleteProgressUseCase.execute();
                syncManager.scheduleSyncSoon();
            } catch (Exception e) {
                errorMessage.postValue(e.getMessage());
            }
        });
    }

    public void deleteCompletedTasks() {
        executor.execute(() -> {
            try {
                deleteCompletedTasksUseCase.execute();
                syncManager.scheduleSyncSoon();
            } catch (Exception e) {
                errorMessage.postValue(e.getMessage());
            }
        });
    }

    public void exportToJson(Context context, Uri uri) {
        executor.execute(() -> {
            try {
                exportToJsonUseCase.execute(context, uri);
            } catch (Exception e) {
                errorMessage.postValue(e.getMessage());
            }
        });
    }

    public void exportToIcs(Context context, Uri uri) {
        executor.execute(() -> {
            try {
                exportToIcsUseCase.execute(context, uri);
            } catch (Exception e) {
                errorMessage.postValue(e.getMessage());
            }
        });
    }

    public void exportStatisticsToPng(Context context, Uri uri) {
        executor.execute(() -> {
            try {
                exportStatisticsToPngUseCase.execute(context, uri);
            } catch (Exception e) {
                errorMessage.postValue(e.getMessage());
            }
        });
    }

    public void exportStatisticsToJson(Context context, Uri uri) {
        executor.execute(() -> {
            try {
                exportStatisticsToJsonUseCase.execute(context, uri);
            } catch (Exception e) {
                errorMessage.postValue(e.getMessage());
            }
        });
    }
}