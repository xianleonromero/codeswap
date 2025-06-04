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

        // Validación campos vacíos
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validación email
        if (!isValidEmail(email)) {
            Toast.makeText(this, "Email inválido. Debe tener @ y terminar en .com, .es, .org, etc.", Toast.LENGTH_LONG).show();
            return;
        }

        // Validación contraseña
        if (password.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validación contraseñas iguales
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Las contraseñas no son iguales", Toast.LENGTH_SHORT).show();
            return;
        }

        // Datos exactos que espera tu backend
        Map<String, String> registerData = new HashMap<>();
        registerData.put("username", username);
        registerData.put("email", email);
        registerData.put("password", password);

        System.out.println("DEBUG - Datos a enviar: " + registerData.toString());

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Map<String, Object>> call = apiService.register(registerData);

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(RegisterActivity.this, "¡Cuenta creada! Ya puedes iniciar sesión", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Error al crear la cuenta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Sin conexión a internet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        email = email.trim();

        if (!email.contains("@") || email.indexOf("@") != email.lastIndexOf("@")) {
            return false;
        }

        String[] parts = email.split("@");
        if (parts.length != 2 || parts[0].isEmpty() || parts[1].isEmpty()) {
            return false;
        }

        String[] validDomains = {".com", ".es", ".org", ".net", ".edu", ".gov", ".co", ".mx", ".ar", ".cl", ".pe"};

        for (String domain : validDomains) {
            if (parts[1].toLowerCase().endsWith(domain)) {
                return true;
            }
        }

        return false;
    }

    private void testBackend() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<Map<String, Object>>> call = apiService.getProgrammingLanguages();

        call.enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                System.out.println("TEST - Languages response: " + response.code());
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
            }
        });
    }
}