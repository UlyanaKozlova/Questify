package com.example.questify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import com.example.questify.ui.AppInitViewModel;
import com.example.questify.ui.main.PetFragment;
import com.example.questify.ui.projects.ProjectsFragment;
import com.example.questify.ui.statistics.StatisticsFragment;
import com.example.questify.ui.tasks.list.TaskListFragment;
import com.example.questify.ui.calendar.CalendarFragment;
import com.example.questify.ui.shop.ShopFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.Map;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private Map<Integer, Fragment> navMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new ViewModelProvider(this).get(AppInitViewModel.class);

        initNavMap();

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment = navMap.get(item.getItemId());
            if (fragment != null) {
                openFragment(fragment);
                return true;
            }
            return false;
        });

        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_pet);
        }
    }

    private void initNavMap() {
        navMap = new HashMap<>();
        navMap.put(R.id.nav_tasks, new TaskListFragment());
        navMap.put(R.id.nav_calendar, new CalendarFragment());
        navMap.put(R.id.nav_projects, new ProjectsFragment());
        navMap.put(R.id.nav_stats, new StatisticsFragment());
        navMap.put(R.id.nav_pet, new PetFragment());
    }

    private void openFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}
