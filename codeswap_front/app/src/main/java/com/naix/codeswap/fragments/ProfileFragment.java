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
import com.naix.codeswap.activities.LoginActivity;
import com.naix.codeswap.api.ApiClient;
import com.naix.codeswap.api.ApiService;
import com.naix.codeswap.models.User;

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
            Toast.makeText(getContext(), "Editar perfil", Toast.LENGTH_SHORT).show();
            // Aquí navegarías a la pantalla de edición de perfil
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
        // En una app real, cargarías los datos del usuario actual
        tvUsername.setText("JuanDev");
        tvUserRating.setText("Valoración: 4.5/5.0");
        tvBio.setText("Desarrollador full-stack con 5 años de experiencia. Especializado en Java y Spring Boot, con interés en aprender nuevas tecnologías frontend.");

        // Aquí cargarías también las habilidades ofrecidas y buscadas
        // y las mostrarías en los RecyclerViews
    }
}