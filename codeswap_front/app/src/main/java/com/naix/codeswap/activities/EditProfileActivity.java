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
import java.util.List;

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
        // En una app real, cargaríamos los datos del usuario actual desde la API
        // Por ahora, usamos datos simulados
        etFullName.setText("Juan Pérez");
        etEmail.setText("juan.dev@example.com");
        etBio.setText("Desarrollador full-stack con 5 años de experiencia. Especializado en Java y Spring Boot, con interés en aprender nuevas tecnologías frontend.");

        // Cargar habilidades ofrecidas
        ProgrammingLanguage java = new ProgrammingLanguage();
        java.setId(1);
        java.setName("Java");

        ProgrammingLanguage python = new ProgrammingLanguage();
        python.setId(2);
        python.setName("Python");

        ProgrammingLanguage spring = new ProgrammingLanguage();
        spring.setId(3);
        spring.setName("Spring Boot");

        offeredSkills.add(java);
        offeredSkills.add(python);
        offeredSkills.add(spring);

        offeredAdapter.notifyDataSetChanged();

        // Cargar habilidades buscadas
        ProgrammingLanguage react = new ProgrammingLanguage();
        react.setId(4);
        react.setName("React");

        ProgrammingLanguage angular = new ProgrammingLanguage();
        angular.setId(5);
        angular.setName("Angular");

        wantedSkills.add(react);
        wantedSkills.add(angular);

        wantedAdapter.notifyDataSetChanged();
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
        // En una app real, aquí enviaríamos los cambios a la API
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String bio = etBio.getText().toString().trim();

        // Simple validación
        if (fullName.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Por favor, completa los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Cambios guardados correctamente", Toast.LENGTH_SHORT).show();
        finish();
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