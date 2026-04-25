package com.example.questify.ui;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.questify.UserSession;
import com.example.questify.domain.usecase.game.pet.InitPetUseCase;
import com.example.questify.domain.usecase.plans.project.InitProjectUseCase;
import com.example.questify.domain.usecase.user.InitUserUseCase;
import com.example.questify.sync.AuthenticationManager;
import com.example.questify.sync.SyncManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AppInitViewModel extends ViewModel {

    private static final String TAG = "AppInitViewModel";

    private final MutableLiveData<Boolean> isInitialized = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isSyncComplete = new MutableLiveData<>(false);

    private final InitUserUseCase initUserUseCase;
    private final InitProjectUseCase initProjectUseCase;
    private final InitPetUseCase initPetUseCase;
    private final UserSession userSession;
    private final AuthenticationManager authManager;
    private final SyncManager syncManager;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Inject
    public AppInitViewModel(InitUserUseCase initUserUseCase,
                            InitProjectUseCase initProjectUseCase,
                            InitPetUseCase initPetUseCase,
                            UserSession userSession,
                            AuthenticationManager authManager,
                            SyncManager syncManager) {
        this.initUserUseCase = initUserUseCase;
        this.initProjectUseCase = initProjectUseCase;
        this.initPetUseCase = initPetUseCase;
        this.userSession = userSession;
        this.authManager = authManager;
        this.syncManager = syncManager;

        initializeApp();
    }

    private void initializeApp() {
        executor.execute(() -> {
            try {
                initUserUseCase.execute();
                initProjectUseCase.execute();
                initPetUseCase.execute();

                if (authManager.isAuthenticated()) {
                    String firebaseUserId = authManager.getCurrentUserId();
                    userSession.setFirebaseUserId(firebaseUserId);

                    syncManager.syncAllFromCloud(() -> {
                        isSyncComplete.postValue(true);
                        isInitialized.postValue(true);
                        Log.d(TAG, "Sync completed after init");
                    });
                } else {
                    isInitialized.postValue(true);
                    Log.d(TAG, "Local init completed, no auth");
                }

            } catch (Exception e) {
                Log.e(TAG, "Init failed", e);
                error.postValue(e.getMessage());
            }
        });
    }

    public LiveData<Boolean> getIsInitialized() {
        return isInitialized;
    }

    public LiveData<String> getError() {
        return error;
    }
    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}