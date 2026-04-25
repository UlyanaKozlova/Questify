package com.example.questify.sync;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import dagger.hilt.EntryPoint;
import dagger.hilt.InstallIn;
import dagger.hilt.android.EntryPointAccessors;
import dagger.hilt.components.SingletonComponent;

public class SyncWorker extends Worker {

    private static final String TAG = "SyncWorker";

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            SyncManager syncManager = getSyncManager();
            if (syncManager != null) {
                syncManager.syncAllToCloud(() -> Log.d(TAG, "Background sync completed"));
            } else {
                Log.e(TAG, "SyncManager is null");
                return Result.failure();
            }

            return Result.success();
        } catch (Exception e) {
            Log.e(TAG, "Background sync failed", e);
            return Result.retry();
        }
    }

    private SyncManager getSyncManager() {
        SyncWorkerEntryPoint entryPoint = EntryPointAccessors.fromApplication(
                getApplicationContext(),
                SyncWorkerEntryPoint.class
        );
        return entryPoint.getSyncManager();
    }
// todo пофиксить удаление с использованием isDeleted
    @EntryPoint
    @InstallIn(SingletonComponent.class)
    interface SyncWorkerEntryPoint {
        SyncManager getSyncManager();
    }
}