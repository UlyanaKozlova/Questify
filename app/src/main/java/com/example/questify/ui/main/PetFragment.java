package com.example.questify.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.questify.R;
import com.example.questify.ui.settings.SettingsFragment;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PetFragment extends Fragment {
    private PetViewModel petViewModel;
    private TextView textCoins;
    private TextView textLevel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        textCoins = view.findViewById(R.id.textCoins);
        textLevel = view.findViewById(R.id.textLevel);
        Button buttonSettings = view.findViewById(R.id.buttonSettings);
        petViewModel = new ViewModelProvider(this).get(PetViewModel.class);
        buttonSettings.setOnClickListener(v ->
                requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new SettingsFragment())
                        .addToBackStack(null)
                        .commit());
        petViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user == null) {
                return;
            }
            textCoins.setText("Coins: " + user.getCoins());
            textLevel.setText("Level: " + user.getLevel());
        });
    }
}