package com.example.questify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import com.example.questify.sync.WorkManagerScheduler;
import com.example.questify.ui.AppInitViewModel;
import com.example.questify.ui.main.PetFragment;
import com.example.questify.ui.projects.ProjectsFragment;
import com.example.questify.ui.statistics.StatisticsFragment;
import com.example.questify.ui.tasks.list.TaskListFragment;
import com.example.questify.ui.calendar.CalendarFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private static final String TAG_TASKS = "f_tasks";
    private static final String TAG_CALENDAR = "f_calendar";
    private static final String TAG_PROJECTS = "f_projects";
    private static final String TAG_STATS = "f_stats";
    private static final String TAG_PET = "f_pet";

    @Inject
    WorkManagerScheduler workManagerScheduler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        workManagerScheduler.schedulePeriodicSync(this);
        new ViewModelProvider(this).get(AppInitViewModel.class);

        if (savedInstanceState == null) {
            addAllFragments();
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnItemSelectedListener(item -> {
            String tag = tagForId(item.getItemId());
            return tag != null && showFragment(tag);
        });

        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_pet);
        }
    }

    private void addAllFragments() {
        Fragment tasks = new TaskListFragment();
        Fragment calendar = new CalendarFragment();
        Fragment projects = new ProjectsFragment();
        Fragment stats = new StatisticsFragment();
        Fragment pet = new PetFragment();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentContainer, tasks, TAG_TASKS).hide(tasks)
                .add(R.id.fragmentContainer, calendar, TAG_CALENDAR).hide(calendar)
                .add(R.id.fragmentContainer, projects, TAG_PROJECTS).hide(projects)
                .add(R.id.fragmentContainer, stats, TAG_STATS).hide(stats)
                .add(R.id.fragmentContainer, pet, TAG_PET).hide(pet)
                .commitNow();
    }

    private boolean showFragment(String tag) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment target = fm.findFragmentByTag(tag);
        if (target == null) {
            return false;
        }

        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        for (Fragment f : fm.getFragments()) {
            if (f != target) {
                fragmentTransaction.hide(f);
            }
        }
        fragmentTransaction.show(target).commit();
        return true;
    }

    private String tagForId(int itemId) {
        if (itemId == R.id.nav_tasks) return TAG_TASKS;
        if (itemId == R.id.nav_calendar) return TAG_CALENDAR;
        if (itemId == R.id.nav_projects) return TAG_PROJECTS;
        if (itemId == R.id.nav_stats) return TAG_STATS;
        if (itemId == R.id.nav_pet) return TAG_PET;
        return null;
    }
}
