package com.example.questify.sync;

import android.content.Context;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WorkManagerScheduler {
    private static final String SYNC_WORK_NAME = "questify_periodic_sync";

    @Inject
    public WorkManagerScheduler() {
    }

    public void schedulePeriodicSync(Context context) {
        PeriodicWorkRequest syncWorkRequest = new PeriodicWorkRequest.Builder(
                SyncWorker.class,
                15, TimeUnit.MINUTES)
                .build();

        WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                        SYNC_WORK_NAME,
                        ExistingPeriodicWorkPolicy.KEEP,
                        syncWorkRequest);
    }
}
