package com.naix.codeswap.activities;

import android.annotation.SuppressLint;
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
        TextView tvDemo = findViewById(R.id.tvDemo);
        tvDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Simular login en modo demo
                Toast.makeText(LoginActivity.this, "Entrando en modo demo...", Toast.LENGTH_SHORT).show();

                // aquí se podrá establecer una bandera o token especial que indique que la app está en modo demo

                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                intent.putExtra("IS_DEMO_MODE", true);
                startActivity(intent);
                finish();
            }
        });
    }

    private void attemptLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear objeto de login
        Map<String, String> loginData = new HashMap<>();
        loginData.put("username", username);
        loginData.put("password", password);

        // Hacer llamada a la API real
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Map<String, Object>> call = apiService.login(loginData);

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(LoginActivity.this, "Login exitoso", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}