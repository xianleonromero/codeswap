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
    private Button btnAddOfferedSkill, btnAddWantedSkill, btnSaveProfile;
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
        btnAddOfferedSkill = findViewById(R.id.btnAddOfferedSkill);
        btnAddWantedSkill = findViewById(R.id.btnAddWantedSkill);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        recyclerSkillsOffered = findViewById(R.id.recyclerSkillsOffered);
        recyclerSkillsWanted = findViewById(R.id.recyclerSkillsWanted);

        // Configurar RecyclerViews
        recyclerSkillsOffered.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerSkillsWanted.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Inicializar listas y adaptadores
// Inicializar listas y adaptadores
        offeredSkills = new ArrayList<>();
        wantedSkills = new ArrayList<>();

        offeredAdapter = new SkillAdapter(this, offeredSkills, skill -> {
            // Eliminar habilidad ofrecida
            offeredSkills.remove(skill);
            offeredAdapter.notifyDataSetChanged();
        });

        wantedAdapter = new SkillAdapter(this, wantedSkills, skill -> {
            // Eliminar habilidad buscada
            wantedSkills.remove(skill);
            wantedAdapter.notifyDataSetChanged();
        });

        recyclerSkillsOffered.setAdapter(offeredAdapter);
        recyclerSkillsWanted.setAdapter(wantedAdapter);

        // Inicializar API service
        apiService = ApiClient.getClient().create(ApiService.class);

        // Cargar datos del perfil
        loadProfileData();

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
                        Toast.makeText(EditProfileActivity.this, "Error al cargar perfil", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(EditProfileActivity.this, "No se pudo cargar tu perfil", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Sin conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSkillSelectionDialog(boolean isOffered) {
        // Cargar lenguajes disponibles desde la API
        Call<List<Map<String, Object>>> call = apiService.getProgrammingLanguages();

        call.enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                System.out.println("DEBUG - Languages response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    List<Map<String, Object>> languagesData = response.body();
                    System.out.println("DEBUG - Languages data size: " + languagesData.size());
                    System.out.println("DEBUG - Languages data: " + languagesData.toString());

                    if (languagesData.isEmpty()) {
                        Toast.makeText(EditProfileActivity.this, "No hay lenguajes disponibles en el servidor", Toast.LENGTH_LONG).show();
                        return;
                    }
                    List<ProgrammingLanguage> availableLanguages = new ArrayList<>();

                    // Convertir datos de la API a objetos ProgrammingLanguage
                    for (Map<String, Object> langData : languagesData) {
                        ProgrammingLanguage lang = new ProgrammingLanguage();
                        lang.setId(((Double) langData.get("id")).intValue());
                        lang.setName((String) langData.get("name"));
                        if (langData.containsKey("icon")) {
                            lang.setIcon((String) langData.get("icon"));
                        }
                        availableLanguages.add(lang);
                    }

                    // Crear array de nombres para el diálogo
                    String[] languageNames = new String[availableLanguages.size()];
                    for (int i = 0; i < availableLanguages.size(); i++) {
                        languageNames[i] = availableLanguages.get(i).getName();
                    }

                    // Mostrar diálogo de selección
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(EditProfileActivity.this);
                    builder.setTitle(isOffered ? "Seleccionar habilidad ofrecida" : "Seleccionar habilidad buscada");
                    builder.setItems(languageNames, (dialog, which) -> {
                        ProgrammingLanguage selectedLanguage = availableLanguages.get(which);

                        // Verificar si ya está en la lista
                        List<ProgrammingLanguage> targetList = isOffered ? offeredSkills : wantedSkills;
                        boolean alreadyExists = false;
                        for (ProgrammingLanguage existing : targetList) {
                            if (existing.getId() == selectedLanguage.getId()) {
                                alreadyExists = true;
                                break;
                            }
                        }

                        if (alreadyExists) {
                            Toast.makeText(EditProfileActivity.this, "Esta habilidad ya está en tu lista", Toast.LENGTH_SHORT).show();
                        } else {
                            // Añadir a la lista
                            targetList.add(selectedLanguage);
                            if (isOffered) {
                                offeredAdapter.notifyDataSetChanged();
                            } else {
                                wantedAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                    builder.setNegativeButton("Cancelar", null);
                    builder.show();

                } else {
                    Toast.makeText(EditProfileActivity.this, "Error al cargar lenguajes: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfileChanges() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String bio = etBio.getText().toString().trim();

        // Simple validación
        if (fullName.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Añade tu nombre", Toast.LENGTH_SHORT).show();
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
// Preparar habilidades ofrecidas
        List<Map<String, Object>> offeredSkillsData = new ArrayList<>();
        for (ProgrammingLanguage skill : offeredSkills) {
            Map<String, Object> skillData = new HashMap<>();
            skillData.put("language_id", skill.getId());
            skillData.put("level", 4); // Nivel por defecto
            offeredSkillsData.add(skillData);
        }
        profileData.put("offered_skills", offeredSkillsData);

// Preparar habilidades buscadas
        List<Map<String, Object>> wantedSkillsData = new ArrayList<>();
        for (ProgrammingLanguage skill : wantedSkills) {
            Map<String, Object> skillData = new HashMap<>();
            skillData.put("language_id", skill.getId());
            wantedSkillsData.add(skillData);
        }
        profileData.put("wanted_skills", wantedSkillsData);
        Call<Map<String, Object>> call = apiService.updateProfile(profileData);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this, "¡Perfil actualizado!", Toast.LENGTH_SHORT).show();
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