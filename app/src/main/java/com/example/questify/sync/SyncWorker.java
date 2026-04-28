package com.example.questify.sync;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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
            if (syncManager == null) {
                Log.e(TAG, "SyncManager is null");
                return Result.failure();
            }

            CountDownLatch latch = new CountDownLatch(1);
            syncManager.syncAllToCloud(latch::countDown);
            boolean completed = latch.await(120, TimeUnit.SECONDS);
            if (!completed) {
                Log.w(TAG, "Sync timed out after 120 seconds");
            }

            Log.d(TAG, "Background sync completed");
            return Result.success();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Result.retry();
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
    @EntryPoint
    @InstallIn(SingletonComponent.class)
    interface SyncWorkerEntryPoint {
        SyncManager getSyncManager();
    }
}