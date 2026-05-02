package com.example.questify.ui.calendar;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.questify.domain.model.Task;
import com.example.questify.domain.usecase.plans.tasks.task.GetAllTasksUseCase;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CalendarViewModel extends ViewModel {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final LiveData<List<Task>> allTasksLive;
    private final LiveData<Set<LocalDate>> datesWithDeadlines;

    private final MutableLiveData<Date> selectedDateLive = new MutableLiveData<>();
    private final MediatorLiveData<List<Task>> tasksForSelectedDay = new MediatorLiveData<>();

    private final MutableLiveData<String> error = new MutableLiveData<>();

    @Inject
    public CalendarViewModel(GetAllTasksUseCase getAllTasksUseCase) {
        allTasksLive = getAllTasksUseCase.executeLive();

        datesWithDeadlines = Transformations.map(allTasksLive, tasks -> {
            Set<LocalDate> dates = new HashSet<>();
            if (tasks != null) {
                for (Task task : tasks) {
                    if (task.getDeadline() > 0) {
                        dates.add(Instant.ofEpochMilli(task.getDeadline())
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate());
                    }
                }
            }
            return dates;
        });

        tasksForSelectedDay.addSource(allTasksLive, tasks -> recomputeSelectedDay());
        tasksForSelectedDay.addSource(selectedDateLive, date -> recomputeSelectedDay());
    }

    public LiveData<List<Task>> getTasksForSelectedDay() {
        return tasksForSelectedDay;
    }

    public LiveData<Set<LocalDate>> getDatesWithDeadlines() {
        return datesWithDeadlines;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void loadTasksForDate(Date date) {
        selectedDateLive.setValue(date);
    }

    private void recomputeSelectedDay() {
        List<Task> tasks = allTasksLive.getValue();
        Date date = selectedDateLive.getValue();
        if (tasks == null || date == null) {
            return;
        }

        executor.execute(() -> {
            Calendar start = Calendar.getInstance();
            start.setTime(date);
            start.set(Calendar.HOUR_OF_DAY, 0);
            start.set(Calendar.MINUTE, 0);
            start.set(Calendar.SECOND, 0);
            start.set(Calendar.MILLISECOND, 0);

            long startTime = start.getTimeInMillis();
            start.add(Calendar.DAY_OF_MONTH, 1);
            long endTime = start.getTimeInMillis();

            List<Task> result = new ArrayList<>();
            for (Task task : tasks) {
                if (task.getDeadline() >= startTime && task.getDeadline() < endTime) {
                    result.add(task);
                }
            }

            result.sort(Comparator.comparingLong(Task::getDeadline));
            tasksForSelectedDay.postValue(result);
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}