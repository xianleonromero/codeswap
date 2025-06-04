package com.naix.codeswap.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.naix.codeswap.R;
import com.naix.codeswap.api.ApiClient;
import com.naix.codeswap.api.ApiService;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializar vistas
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        TextView tvRegister = findViewById(R.id.tvRegister);
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
    }

    private void attemptLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (username.isEmpty()) {
            Toast.makeText(this, "Ingresa tu usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Ingresa tu contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear objeto de login
        Map<String, String> loginData = new HashMap<>();
        loginData.put("username", username);
        loginData.put("password", password);

        System.out.println("DEBUG - Login data: " + loginData);

        // Hacer llamada a la API real
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Map<String, Object>> call = apiService.login(loginData);

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                System.out.println("DEBUG - Login response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    System.out.println("DEBUG - Login success: " + response.body());

                    // Guardar token y usuario
                    Map<String, Object> data = response.body();
                    String token = "";
                    if (data.containsKey("token")) {
                        token = (String) data.get("token");
                        saveAuthToken(token);
                        saveUsername(username);
                        // Guardar user_id si está disponible
                        if (data.containsKey("user_id")) {
                            Object userIdObj = data.get("user_id");
                            int userId = userIdObj instanceof Double ? ((Double) userIdObj).intValue() : (Integer) userIdObj;
                            saveUserId(userId);
                        }

                        System.out.println("DEBUG - Token guardado: " + token);
                    }

                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    String errorMsg = "Error " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            System.out.println("DEBUG - Login error: " + errorBody);
                            errorMsg += ": " + errorBody;
                        }
                    } catch (Exception e) {
                        System.out.println("DEBUG - Exception: " + e.getMessage());
                    }
                    Toast.makeText(LoginActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                System.out.println("DEBUG - Login network error: " + t.getMessage());
                Toast.makeText(LoginActivity.this, "Sin conexión a internet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserId(int userId) {
        SharedPreferences prefs = getSharedPreferences("CodeSwapPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("user_id", userId);
        editor.apply();
    }

    private void saveAuthToken(String token) {
        SharedPreferences prefs = getSharedPreferences("CodeSwapPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("auth_token", token);
        editor.apply();
    }

    private void saveUsername(String username) {
        SharedPreferences prefs = getSharedPreferences("CodeSwapPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("username", username);
        editor.apply();
    }
}