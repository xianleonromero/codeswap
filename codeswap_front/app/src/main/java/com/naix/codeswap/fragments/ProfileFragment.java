package com.naix.codeswap.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.naix.codeswap.R;
import com.naix.codeswap.activities.EditProfileActivity;
import com.naix.codeswap.activities.LoginActivity;
import com.naix.codeswap.adapters.SkillAdapter;
import com.naix.codeswap.api.ApiClient;
import com.naix.codeswap.api.ApiService;
import com.naix.codeswap.models.ProgrammingLanguage;
import com.naix.codeswap.models.User;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private ImageView ivProfilePicture;
    private TextView tvUsername, tvUserRating, tvBio;
    private RecyclerView recyclerSkillsOffered, recyclerSkillsWanted;
    private Button btnEditProfile, btnLogout;

    private ApiService apiService;
    private User currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializar vistas
        ivProfilePicture = view.findViewById(R.id.ivProfilePicture);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvUserRating = view.findViewById(R.id.tvUserRating);
        tvBio = view.findViewById(R.id.tvBio);
        recyclerSkillsOffered = view.findViewById(R.id.recyclerSkillsOffered);
        recyclerSkillsWanted = view.findViewById(R.id.recyclerSkillsWanted);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnLogout = view.findViewById(R.id.btnLogout);

        // Configurar RecyclerViews
        recyclerSkillsOffered.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerSkillsWanted.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Inicializar API service
        apiService = ApiClient.getClient().create(ApiService.class);

        // Cargar datos de perfil simulados
        loadMockProfileData();

        // Configurar listeners
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            // Simular cierre de sesión
            Toast.makeText(getContext(), "Cerrando sesión...", Toast.LENGTH_SHORT).show();

            // Navegar a la pantalla de login
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void loadMockProfileData() {
        // Datos simulados del perfil
        tvUsername.setText("JuanDev");
        tvUserRating.setText("Valoración: 4.5/5.0");
        tvBio.setText("Desarrollador full-stack con 5 años de experiencia. Especializado en Java y Spring Boot, con interés en aprender nuevas tecnologías frontend.");

        // Simular habilidades ofrecidas
        List<ProgrammingLanguage> offeredSkills = new ArrayList<>();

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

        // Usar el adaptador para las habilidades ofrecidas
        SkillAdapter offeredAdapter = new SkillAdapter(getContext(), offeredSkills);
        recyclerSkillsOffered.setAdapter(offeredAdapter);

        // Simular habilidades buscadas
        List<ProgrammingLanguage> wantedSkills = new ArrayList<>();

        ProgrammingLanguage react = new ProgrammingLanguage();
        react.setId(4);
        react.setName("React");

        ProgrammingLanguage angular = new ProgrammingLanguage();
        angular.setId(5);
        angular.setName("Angular");

        wantedSkills.add(react);
        wantedSkills.add(angular);

        // Usar el adaptador para las habilidades buscadas
        SkillAdapter wantedAdapter = new SkillAdapter(getContext(), wantedSkills);
        recyclerSkillsWanted.setAdapter(wantedAdapter);
    }
}