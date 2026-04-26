package com.example.questify.ui.calendar;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questify.R;
import com.example.questify.domain.model.Task;
import com.example.questify.ui.tasks.edit.TaskEditFragment;
import com.example.questify.ui.tasks.list.TaskListAdapter;
import com.example.questify.ui.tasks.list.TaskListViewModel;
import com.kizitonwose.calendar.view.CalendarView;
import com.kizitonwose.calendar.view.MonthDayBinder;
import com.kizitonwose.calendar.core.CalendarDay;
import com.kizitonwose.calendar.core.DayPosition;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CalendarFragment extends Fragment {

    private CalendarView calendarView;
    private TextView textMonth;
    private TextView textSelectedDate;
    private TextView textEmpty;
    private RecyclerView recyclerTasks;
    private ImageButton btnPrev, btnNext;

    private CalendarViewModel viewModel;
    private TaskListViewModel taskListViewModel;
    private TaskListAdapter adapter;

    private YearMonth currentMonth;
    private Set<LocalDate> datesWithDeadlines = new HashSet<>();

    private final DateTimeFormatter monthFormatter =
            DateTimeFormatter.ofPattern("LLLL yyyy", new Locale("ru"));

    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("ru"));

    public CalendarFragment() {
        super(R.layout.fragment_calendar);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
        taskListViewModel = new ViewModelProvider(requireActivity()).get(TaskListViewModel.class);

        calendarView = view.findViewById(R.id.calendarView);
        textMonth = view.findViewById(R.id.textMonth);
        textSelectedDate = view.findViewById(R.id.textSelectedDate);
        textEmpty = view.findViewById(R.id.textEmpty);
        recyclerTasks = view.findViewById(R.id.recyclerTasks);
        btnPrev = view.findViewById(R.id.btnPrev);
        btnNext = view.findViewById(R.id.btnNext);

        setupRecycler();
        setupCalendar();
        observe();

        viewModel.loadAllTasks();
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.loadAllTasks();
    }

    private void setupRecycler() {
        adapter = new TaskListAdapter(new TaskListAdapter.Listener() {
            @Override
            public void onTaskClicked(Task task) {
                Bundle args = new Bundle();
                args.putString("taskGlobalId", task.getGlobalId());

                requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, TaskEditFragment.class, args)
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onTaskChecked(Task task, boolean isChecked) {
                taskListViewModel.completeTask(task, isChecked);
            }
        });

        recyclerTasks.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerTasks.setAdapter(adapter);
    }

    private void setupCalendar() {
        currentMonth = YearMonth.now();

        calendarView.setup(
                currentMonth.minusMonths(12),
                currentMonth.plusMonths(12),
                DayOfWeek.MONDAY
        );

        calendarView.scrollToMonth(currentMonth);
        textMonth.setText(currentMonth.format(monthFormatter));

        calendarView.setDayBinder(new DayBinderImpl());

        calendarView.setMonthScrollListener(month -> {
            currentMonth = month.getYearMonth();
            textMonth.setText(currentMonth.format(monthFormatter));
            return null;
        });

        btnPrev.setOnClickListener(v -> {
            currentMonth = currentMonth.minusMonths(1);
            calendarView.scrollToMonth(currentMonth);
        });

        btnNext.setOnClickListener(v -> {
            currentMonth = currentMonth.plusMonths(1);
            calendarView.scrollToMonth(currentMonth);
        });
    }

    private class DayBinderImpl implements MonthDayBinder<DayViewContainer> {

        @NonNull
        @Override
        public DayViewContainer create(@NonNull View view) {
            return new DayViewContainer(view);
        }

        @Override
        public void bind(@NonNull DayViewContainer container, @NonNull CalendarDay day) {
            container.textView.setText(String.valueOf(day.getDate().getDayOfMonth()));

            if (day.getPosition() == DayPosition.MonthDate) {
                container.textView.setAlpha(1f);
                boolean hasDeadline = datesWithDeadlines.contains(day.getDate());
                container.dotDeadline.setVisibility(hasDeadline ? View.VISIBLE : View.INVISIBLE);

                container.view.setOnClickListener(v -> {
                    textSelectedDate.setText(day.getDate().format(dateFormatter));

                    viewModel.loadTasksForDate(
                            java.util.Date.from(day
                                    .getDate()
                                    .atStartOfDay(ZoneId.systemDefault())
                                    .toInstant()
                            )
                    );
                });
            } else {
                container.textView.setAlpha(0.3f);
                container.dotDeadline.setVisibility(View.INVISIBLE);
                container.view.setOnClickListener(null);
            }
        }
    }

    private void observe() {
        viewModel.getTasksForSelectedDay().observe(getViewLifecycleOwner(), tasks -> {
            adapter.submitList(tasks);
            textEmpty.setVisibility((tasks == null || tasks.isEmpty()) ? View.VISIBLE : View.GONE);
        });

        viewModel.getDatesWithDeadlines().observe(getViewLifecycleOwner(), dates -> {
            datesWithDeadlines = dates != null ? dates : new HashSet<>();
            calendarView.notifyCalendarChanged();
        });
    }
}