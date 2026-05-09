package com.example.questify.ui.advice;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.questify.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AdviceFragment extends Fragment {

    private AdviceViewModel viewModel;
    private View cardAdvice;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (!granted) {
                    showMessage(getString(R.string.stats_permission_denied));
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_advice, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(AdviceViewModel.class);

        ImageView ivPet = view.findViewById(R.id.ivAdvicePet);

        TextView textAdvice = view.findViewById(R.id.textAdvice);
        ProgressBar progressAdvice = view.findViewById(R.id.progressAdvice);
        cardAdvice = view.findViewById(R.id.cardAdvice);
        MaterialButton buttonExport = view.findViewById(R.id.buttonExportAdvice);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
                ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        viewModel.getAdviceLoading().observe(getViewLifecycleOwner(), loading -> {
            progressAdvice.setVisibility(Boolean.TRUE.equals(loading) ? View.VISIBLE : View.GONE);
            textAdvice.setVisibility(Boolean.TRUE.equals(loading) ? View.GONE : View.VISIBLE);
        });

        viewModel.getAdvice().observe(getViewLifecycleOwner(), text -> {
            if (text == null || text.isEmpty()) {
                textAdvice.setText(R.string.stats_advice_empty);
            } else {
                textAdvice.setText(text);
            }
        });

        viewModel.getExportResult().observe(getViewLifecycleOwner(), resId -> {
            if (resId != null) {
                showMessage(getString(resId));
                viewModel.clearExportResult();
            }
        });

        viewModel.getPetImageRes().observe(getViewLifecycleOwner(), resId -> {
            if (resId != null && resId != 0) {
                ivPet.setImageResource(resId);
            } else {
                ivPet.setImageResource(R.drawable.pet_default);
            }
        });

        buttonExport.setOnClickListener(v -> viewModel.exportAsPng(cardAdvice));
    }

    private void showMessage(String message) {
        View root = getView();
        if (root != null) {
            Snackbar.make(root, message, Snackbar.LENGTH_SHORT).show();
        }
    }
}
