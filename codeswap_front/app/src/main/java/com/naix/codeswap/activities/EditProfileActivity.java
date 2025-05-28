package com.naix.codeswap.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.naix.codeswap.R;
import com.naix.codeswap.adapters.SkillAdapter;
import com.naix.codeswap.api.ApiClient;
import com.naix.codeswap.api.ApiService;
import com.naix.codeswap.models.ProgrammingLanguage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView ivProfilePicture;
    private EditText etFullName, etEmail, etBio;
    private Button btnChangePhoto, btnAddOfferedSkill, btnAddWantedSkill, btnSaveProfile;
    private RecyclerView recyclerSkillsOffered, recyclerSkillsWanted;

    private SkillAdapter offeredAdapter, wantedAdapter;
    private List<ProgrammingLanguage> offeredSkills, wantedSkills;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Configurar ActionBar con botón de retroceso
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Editar Perfil");
        }

        // Inicializar vistas
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etBio = findViewById(R.id.etBio);
        btnChangePhoto = findViewById(R.id.btnChangePhoto);
        btnAddOfferedSkill = findViewById(R.id.btnAddOfferedSkill);
        btnAddWantedSkill = findViewById(R.id.btnAddWantedSkill);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        recyclerSkillsOffered = findViewById(R.id.recyclerSkillsOffered);
        recyclerSkillsWanted = findViewById(R.id.recyclerSkillsWanted);

        // Configurar RecyclerViews
        recyclerSkillsOffered.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerSkillsWanted.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Inicializar listas y adaptadores
        offeredSkills = new ArrayList<>();
        wantedSkills = new ArrayList<>();

        offeredAdapter = new SkillAdapter(this, offeredSkills);
        wantedAdapter = new SkillAdapter(this, wantedSkills);

        recyclerSkillsOffered.setAdapter(offeredAdapter);
        recyclerSkillsWanted.setAdapter(wantedAdapter);

        // Inicializar API service
        apiService = ApiClient.getClient().create(ApiService.class);

        // Cargar datos del perfil
        loadProfileData();

        // Configurar listeners
        btnChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EditProfileActivity.this, "Cambiar foto de perfil", Toast.LENGTH_SHORT).show();
                // Aquí iría el código para seleccionar una foto
            }
        });

        btnAddOfferedSkill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSkillSelectionDialog(true);
            }
        });

        btnAddWantedSkill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSkillSelectionDialog(false);
            }
        });

        btnSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileChanges();
            }
        });
    }

    private void loadProfileData() {
        Call<Map<String, Object>> call = apiService.getProfile();

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        Map<String, Object> data = response.body();
                        Map<String, Object> user = (Map<String, Object>) data.get("user");

                        // Cargar datos básicos
                        String firstName = (String) user.get("first_name");
                        String lastName = (String) user.get("last_name");
                        String fullName = (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
                        etFullName.setText(fullName.trim());
                        etEmail.setText((String) user.get("email"));

                        String bio = "";
                        if (data.containsKey("bio") && data.get("bio") != null) {
                            bio = (String) data.get("bio");
                        }
                        etBio.setText(bio);

                        // Cargar habilidades ofrecidas
                        offeredSkills.clear();
                        if (data.containsKey("offered_skills")) {
                            List<Map<String, Object>> offered = (List<Map<String, Object>>) data.get("offered_skills");
                            for (Map<String, Object> skill : offered) {
                                Map<String, Object> lang = (Map<String, Object>) skill.get("language");
                                ProgrammingLanguage pl = new ProgrammingLanguage();
                                pl.setId(((Double) lang.get("id")).intValue());
                                pl.setName((String) lang.get("name"));
                                offeredSkills.add(pl);
                            }
                        }
                        offeredAdapter.notifyDataSetChanged();

                        // Cargar habilidades deseadas
                        wantedSkills.clear();
                        if (data.containsKey("wanted_skills")) {
                            List<Map<String, Object>> wanted = (List<Map<String, Object>>) data.get("wanted_skills");
                            for (Map<String, Object> skill : wanted) {
                                Map<String, Object> lang = (Map<String, Object>) skill.get("language");
                                ProgrammingLanguage pl = new ProgrammingLanguage();
                                pl.setId(((Double) lang.get("id")).intValue());
                                pl.setName((String) lang.get("name"));
                                wantedSkills.add(pl);
                            }
                        }
                        wantedAdapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        Toast.makeText(EditProfileActivity.this, "Error al procesar datos del perfil", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(EditProfileActivity.this, "Error al cargar perfil", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSkillSelectionDialog(boolean isOffered) {
        // En una app real, aquí mostrarías un diálogo para seleccionar una habilidad
        // Por ahora, simplemente agregamos una habilidad de ejemplo

        ProgrammingLanguage newSkill = new ProgrammingLanguage();
        newSkill.setId(isOffered ? 10 : 20);

        if (isOffered) {
            newSkill.setName("Nueva habilidad ofrecida");
            offeredSkills.add(newSkill);
            offeredAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Habilidad añadida a ofrecidas", Toast.LENGTH_SHORT).show();
        } else {
            newSkill.setName("Nueva habilidad buscada");
            wantedSkills.add(newSkill);
            wantedAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Habilidad añadida a buscadas", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveProfileChanges() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String bio = etBio.getText().toString().trim();

        // Simple validación
        if (fullName.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Por favor, completa los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        // Preparar datos para enviar
        Map<String, Object> profileData = new HashMap<>();

        String[] nameParts = fullName.split(" ", 2);
        profileData.put("first_name", nameParts[0]);
        if (nameParts.length > 1) {
            profileData.put("last_name", nameParts[1]);
        }

        profileData.put("email", email);
        profileData.put("bio", bio);

        Call<Map<String, Object>> call = apiService.updateProfile(profileData);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this, "Cambios guardados correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Error al guardar cambios", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}