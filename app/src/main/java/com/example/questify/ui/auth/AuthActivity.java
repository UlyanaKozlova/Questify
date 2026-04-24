package com.example.questify.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.questify.MainActivity;
import com.example.questify.R;
import com.example.questify.sync.AuthenticationManager;
import com.example.questify.sync.SyncManager;
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
    private Button btnLogin, btnRegister, btnAnonymous;
    private ProgressBar progressBar;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        if (authManager.isAuthenticated()) {
            navigateToMain();
            return;
        }

        initViews();
        setupListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        btnAnonymous = findViewById(R.id.btnAnonymous);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> login());
        btnRegister.setOnClickListener(v -> register());
        btnAnonymous.setOnClickListener(v -> anonymousLogin());
    }

    private void login() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);
        authManager.signInWithEmail(email, password)
                .addOnSuccessListener(result -> syncAfterLogin())
                .addOnFailureListener(e -> {
                    setLoading(false);
                    showError("Ошибка входа: " + e.getMessage());
                });
        // todo вынести поля
    }

    private void register() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Пароль должен быть минимум 6 символов", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);
        authManager.signUpWithEmail(email, password)
                .addOnSuccessListener(result -> syncAfterLogin())
                .addOnFailureListener(e -> {
                    setLoading(false);
                    showError("Ошибка регистрации: " + e.getMessage());
                });
    }

    private void anonymousLogin() {
        setLoading(true);
        authManager.signInAnonymously()
                .addOnSuccessListener(result -> syncAfterLogin())
                .addOnFailureListener(e -> {
                    setLoading(false);
                    showError("Ошибка входа: " + e.getMessage());
                });
    }

    private void syncAfterLogin() {
        syncManager.syncAllFromCloud(() -> {
            mainHandler.post(() -> {
                setLoading(false);
                navigateToMain();
            });
        });
    }

    private void setLoading(boolean loading) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            mainHandler.post(() -> setLoading(loading));
            return;
        }

        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!loading);
        btnRegister.setEnabled(!loading);
        btnAnonymous.setEnabled(!loading);
    }

    private void showError(String message) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            mainHandler.post(() -> showError(message));
            return;
        }
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}