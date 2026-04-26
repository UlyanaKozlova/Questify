package com.example.questify.ui.calendar;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
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
    private Date selectedDate;
    private final GetAllTasksUseCase getAllTasksUseCase;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final MutableLiveData<List<Task>> allTasks = new MutableLiveData<>();
    private final MutableLiveData<List<Task>> tasksForSelectedDay = new MutableLiveData<>();
    private final MutableLiveData<Set<LocalDate>> datesWithDeadlines = new MutableLiveData<>(new HashSet<>());
    private final MutableLiveData<String> error = new MutableLiveData<>();

    @Inject
    public CalendarViewModel(GetAllTasksUseCase getAllTasksUseCase) {
        this.getAllTasksUseCase = getAllTasksUseCase;
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

    public void loadAllTasks() {
        executor.execute(() -> {
            try {
                List<Task> tasks = getAllTasksUseCase.execute();
                allTasks.postValue(tasks);

                Set<LocalDate> dates = new HashSet<>();
                for (Task task : tasks) {
                    LocalDate date = Instant.ofEpochMilli(task.getDeadline())
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    dates.add(date);
                }
                datesWithDeadlines.postValue(dates);

                if (selectedDate != null) {
                    filterTasksForDate(selectedDate, tasks);
                }
            } catch (Exception e) {
                error.postValue(e.getMessage());
            }
        });
    }

    public void loadTasksForDate(Date date) {
        selectedDate = date;
        List<Task> tasks = allTasks.getValue();
        if (tasks == null) {
            return;
        }
        filterTasksForDate(date, tasks);
    }

    private void filterTasksForDate(Date date, List<Task> tasks) {
        executor.execute(() -> {
            List<Task> result = new ArrayList<>();

            Calendar start = Calendar.getInstance();
            start.setTime(date);
            start.set(Calendar.HOUR_OF_DAY, 0);
            start.set(Calendar.MINUTE, 0);
            start.set(Calendar.SECOND, 0);
            start.set(Calendar.MILLISECOND, 0);

            long startTime = start.getTimeInMillis();
            start.add(Calendar.DAY_OF_MONTH, 1);
            long endTime = start.getTimeInMillis();

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