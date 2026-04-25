package com.example.questify.sync;

import android.content.Context;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WorkManagerScheduler {
    @Inject
    public WorkManagerScheduler() {
    }

    public void schedulePeriodicSync(Context context) {
        PeriodicWorkRequest syncWorkRequest = new PeriodicWorkRequest.Builder(
                SyncWorker.class,
                1, TimeUnit.MINUTES)
                .setInitialDelay(1, TimeUnit.MINUTES)
                .build();

        WorkManager.getInstance(context)
                .enqueue(syncWorkRequest);
    }
}