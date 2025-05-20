package com.naix.codeswap.activities;

import android.content.Intent;
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
import com.naix.codeswap.models.ProgrammingLanguage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private Button btnRegister;
    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicializar vistas
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        // Configurar listener para el botón de registro
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        // Configurar listener para volver a login
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        testBackend();
    }

    private void registerUser() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        // Validación básica
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        // Datos exactos que espera tu backend
        Map<String, String> registerData = new HashMap<>();
        registerData.put("username", username);
        registerData.put("email", email);
        registerData.put("password", password);

        // Debug: ver qué datos se envían
        System.out.println("DEBUG - Datos a enviar: " + registerData.toString());
        System.out.println("DEBUG - URL completa: " + ApiClient.getClient().baseUrl() + "auth/registration/");

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Map<String, Object>> call = apiService.register(registerData);

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                System.out.println("DEBUG - Response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    System.out.println("DEBUG - Success response: " + response.body().toString());
                    Toast.makeText(RegisterActivity.this, "Registro exitoso. Puedes iniciar sesión.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String errorMsg = "Error " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            System.out.println("DEBUG - Error body: " + errorBody);
                            errorMsg = "Error: " + errorBody;
                        }
                    } catch (Exception e) {
                        System.out.println("DEBUG - Exception reading error: " + e.getMessage());
                    }
                    Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                System.out.println("DEBUG - Network failure: " + t.getMessage());
                t.printStackTrace();
                Toast.makeText(RegisterActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    private void testBackend() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<ProgrammingLanguage>> call = apiService.getProgrammingLanguages();

        call.enqueue(new Callback<List<ProgrammingLanguage>>() {
            @Override
            public void onResponse(Call<List<ProgrammingLanguage>> call, Response<List<ProgrammingLanguage>> response) {
                System.out.println("TEST - Languages response: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(RegisterActivity.this, "Backend OK - " + response.body().size() + " lenguajes", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegisterActivity.this, "Backend error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ProgrammingLanguage>> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "No conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}