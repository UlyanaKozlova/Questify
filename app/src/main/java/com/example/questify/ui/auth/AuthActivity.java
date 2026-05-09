package com.example.questify.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import com.example.questify.ui.utils.AppPreferences;

import java.util.Locale;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.questify.MainActivity;
import com.example.questify.R;
import com.example.questify.sync.AuthenticationManager;
import com.example.questify.sync.SyncManager;
import com.example.questify.ui.AppInitViewModel;
import com.google.android.material.snackbar.Snackbar;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AuthActivity extends AppCompatActivity {

    @Inject
    AuthenticationManager authManager;

    @Inject
    SyncManager syncManager;

    private EditText etEmail, etPassword;
    private Button btnLogin, btnRegister;
    private ProgressBar progressBar;

    @Override
    protected void attachBaseContext(Context newBase) {
        String lang = AppPreferences.getLanguage(newBase);
        if (!lang.isEmpty()) {
            Locale locale = new Locale(lang);
            Configuration config = new Configuration(newBase.getResources().getConfiguration());
            config.setLocale(locale);
            super.attachBaseContext(newBase.createConfigurationContext(config));
        } else {
            super.attachBaseContext(newBase);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        AppInitViewModel appInitViewModel = new ViewModelProvider(this).get(AppInitViewModel.class);

        initViews();
        setupListeners();

        appInitViewModel.getIsInitialized().observe(this, isInitialized -> {
            if (isInitialized) {
                showAuthScreen();
            }
        });
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> login());
        btnRegister.setOnClickListener(v -> register());
    }

    private void showAuthScreen() {
        if (authManager.isAuthenticated()) {
            navigateToMain();
        }
    }

    private void login() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.auth_fill_all_fields, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, R.string.auth_invalid_email, Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);
        authManager.signInWithEmail(email, password)
                .addOnSuccessListener(result -> {
                    if (!isFinishing() && !isDestroyed()) navigateToMain();
                })
                .addOnFailureListener(e -> {
                    if (isFinishing() || isDestroyed()) return;
                    setLoading(false);
                    showError(getString(R.string.auth_error_login, e.getMessage()));
                });
    }

    private void register() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.auth_fill_all_fields, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, R.string.auth_invalid_email, Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, R.string.auth_password_too_short, Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);
        authManager.signUpWithEmail(email, password)
                .addOnSuccessListener(result -> {
                    if (!isFinishing() && !isDestroyed()) navigateToMain();
                })
                .addOnFailureListener(e -> {
                    if (isFinishing() || isDestroyed()) return;
                    setLoading(false);
                    showError(getString(R.string.auth_error_register, e.getMessage()));
                });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!loading);
        btnRegister.setEnabled(!loading);
    }

    private void showError(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}