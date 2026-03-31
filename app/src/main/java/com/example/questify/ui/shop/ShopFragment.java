package com.example.questify.ui.shop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.questify.R;
import com.example.questify.domain.model.Clothing;
import com.example.questify.domain.model.User;
import com.google.android.material.snackbar.Snackbar;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ShopFragment extends Fragment {

    private ShopViewModel viewModel;

    private ImageView imagePet;
    private TextView textName;
    private TextView textPrice;
    private Button buttonPrev;
    private Button buttonNext;
    private Button buttonAction;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shop, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(ShopViewModel.class);

        initViews(view);
        observeData();
        setupClickListeners();
    }

    private void initViews(View view) {
        imagePet = view.findViewById(R.id.imagePet);
        textName = view.findViewById(R.id.textName);
        textPrice = view.findViewById(R.id.textPrice);
        buttonPrev = view.findViewById(R.id.buttonPrev);
        buttonNext = view.findViewById(R.id.buttonNext);
        buttonAction = view.findViewById(R.id.buttonAction);
    }

    private void observeData() {
        viewModel.getAllClothes().observe(getViewLifecycleOwner(), clothes -> {
            if (clothes != null && !clothes.isEmpty()) {
                if (viewModel.getCurrentIndex().getValue() == null) {
                    viewModel.setCurrentIndex(0);
                }
            }
            updateDisplay();
        });

        viewModel.getCurrentIndex().observe(getViewLifecycleOwner(), index -> {
            if (index != null) {
                updateDisplay();
            }
        });

        viewModel.getUser().observe(getViewLifecycleOwner(), user -> updateDisplay());
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Snackbar.make(requireView(), error, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void updateDisplay() {
        Clothing clothing = viewModel.getCurrentDisplayClothing();
        if (clothing == null) {
            return;
        }
        imagePet.setImageResource(clothing.getImageResId());
        textName.setText(clothing.getName());

        User user = viewModel.getUser().getValue();

        boolean isBought = viewModel.isBought(clothing);
        boolean isCurrent = viewModel.isCurrentClothing(clothing);

        if (isCurrent) {
            buttonAction.setText(R.string.shop_wearing);
            buttonAction.setEnabled(false);
            textPrice.setText(R.string.shop_wearing);
        } else if (isBought) {
            buttonAction.setText(R.string.shop_wear);
            buttonAction.setEnabled(true);
            textPrice.setText(R.string.shop_already_bought);
        } else {
            buttonAction.setText(getString(R.string.shop_buy_format, clothing.getPrice()));
            textPrice.setText(getString(R.string.shop_price_format, clothing.getPrice()));

            buttonAction.setEnabled(user != null && user.getCoins() >= clothing.getPrice());
        }
    }

    private void setupClickListeners() {
        buttonPrev.setOnClickListener(v -> viewModel.previous());
        buttonNext.setOnClickListener(v -> viewModel.next());

        buttonAction.setOnClickListener(v -> {
            Clothing clothing = viewModel.getCurrentDisplayClothing();
            if (clothing == null) {
                return;
            }

            if (viewModel.isBought(clothing)) {
                viewModel.wearCurrentClothing(requireContext());
            } else {
                viewModel.buyCurrentClothing(requireContext());
            }
        });
    }
}