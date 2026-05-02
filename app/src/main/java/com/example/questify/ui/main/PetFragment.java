package com.example.questify.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.questify.R;
import com.example.questify.ui.advice.AdviceFragment;
import com.example.questify.ui.settings.SettingsFragment;
import com.example.questify.ui.shop.ShopFragment;
import com.google.android.material.button.MaterialButton;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PetFragment extends Fragment {

    private TextView textCoins;
    private TextView textLevel;
    private TextView tvCoinsToNextLevel;
    private ImageView ivPet;

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
        tvCoinsToNextLevel = view.findViewById(R.id.tvCoinsToNextLevel);
        ivPet = view.findViewById(R.id.ivPet);

        MaterialButton buttonAdvice = view.findViewById(R.id.buttonAdvice);
        MaterialButton buttonSettings = view.findViewById(R.id.buttonSettings);
        MaterialButton buttonShop = view.findViewById(R.id.buttonShop);

        PetViewModel petViewModel = new ViewModelProvider(requireActivity()).get(PetViewModel.class);

        buttonAdvice.setOnClickListener(v ->
                requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragmentContainer, new AdviceFragment())
                        .addToBackStack(null)
                        .commit()
        );
        buttonSettings.setOnClickListener(v ->
                requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragmentContainer, new SettingsFragment())
                        .addToBackStack(null)
                        .commit()
        );
        buttonShop.setOnClickListener(v ->
                requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragmentContainer, new ShopFragment())
                        .addToBackStack(null)
                        .commit()
        );

        petViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user == null) return;
            textCoins.setText(String.valueOf(user.getCoins()));
            textLevel.setText(String.valueOf(user.getLevel()));
        });

        petViewModel.getCoinsToNextLevel().observe(getViewLifecycleOwner(), coins ->
                tvCoinsToNextLevel.setText(String.valueOf(coins)));

        petViewModel.getCurrentPetImageRes().observe(getViewLifecycleOwner(), resId -> {
            if (resId != null && resId != 0) {
                ivPet.setImageResource(resId);
            } else {
                ivPet.setImageResource(R.drawable.pet_default);
            }
        });

    }
}
