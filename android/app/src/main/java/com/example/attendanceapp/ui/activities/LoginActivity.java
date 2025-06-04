package com.example.attendanceapp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.attendanceapp.R;
import com.example.attendanceapp.databinding.ActivityLoginBinding;
import com.example.attendanceapp.models.LoginResponse;
import com.example.attendanceapp.network.ApiClient;
import com.example.attendanceapp.network.ApiService;
import com.example.attendanceapp.utils.PreferenceManager;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private PreferenceManager preferenceManager;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(this);
        apiService = ApiClient.getClient();

        // Check if user is already logged in
        if (preferenceManager.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setupListeners();
    }

    private void setupListeners() {
        binding.btnLogin.setOnClickListener(v -> {
            if (validateInput()) {
                login();
            }
        });
    }

    private boolean validateInput() {
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (username.isEmpty()) {
            binding.tilUsername.setError(getString(R.string.username_required));
            return false;
        }

        if (password.isEmpty()) {
            binding.tilPassword.setError(getString(R.string.password_required));
            return false;
        }

        binding.tilUsername.setError(null);
        binding.tilPassword.setError(null);
        return true;
    }

    private void login() {
        showLoading(true);
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        apiService.login(username, password).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    if (loginResponse.isStatus()) {
                        handleLoginSuccess(loginResponse);
                    } else {
                        showError(loginResponse.getMessage());
                    }
                } else {
                    showError(getString(R.string.error_server));
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                showLoading(false);
                showError(getString(R.string.error_network));
            }
        });
    }

    private void handleLoginSuccess(LoginResponse response) {
        LoginResponse.UserData userData = response.getData();
        preferenceManager.saveAuthToken(userData.getToken());
        preferenceManager.saveUserId(userData.getUserId());
        preferenceManager.saveUserName(userData.getFullName());
        preferenceManager.setLoggedIn(true);

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void showLoading(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.btnLogin.setEnabled(!isLoading);
        binding.etUsername.setEnabled(!isLoading);
        binding.etPassword.setEnabled(!isLoading);
    }

    private void showError(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
