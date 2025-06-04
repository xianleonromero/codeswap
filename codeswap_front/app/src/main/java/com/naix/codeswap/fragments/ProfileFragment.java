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
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        // Cargar datos de perfil
        loadProfileData();

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

    private void loadProfileData() {

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Map<String, Object>> call = apiService.getProfile();

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                // Ocultar carga
                // progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    try {
                        Map<String, Object> data = response.body();
                        Map<String, Object> user = (Map<String, Object>) data.get("user");

                        // Datos básicos
                        String firstName = (String) user.get("first_name");
                        String lastName = (String) user.get("last_name");
                        String fullName = "";

                        if (firstName != null && !firstName.trim().isEmpty()) {
                            fullName = firstName.trim();
                        }
                        if (lastName != null && !lastName.trim().isEmpty()) {
                            if (!fullName.isEmpty()) {
                                fullName += " " + lastName.trim();
                            } else {
                                fullName = lastName.trim();
                            }
                        }

                        // Si no hay nombre completo, usar username
                        if (fullName.isEmpty()) {
                            fullName = (String) user.get("username");
                        }

                        tvUsername.setText(fullName);
                        tvUserRating.setVisibility(View.GONE);


                        // Bio (puede ser null)
                        String bio = "";
                        if (data.containsKey("bio") && data.get("bio") != null) {
                            bio = (String) data.get("bio");
                        }
                        tvBio.setText(bio);


                        // Habilidades ofrecidas
                        List<ProgrammingLanguage> offeredSkills = new ArrayList<>();
                        try {
                            if (data.containsKey("offered_skills")) {
                                List<Map<String, Object>> offered = (List<Map<String, Object>>) data.get("offered_skills");

                                for (Map<String, Object> skill : offered) {
                                    Map<String, Object> lang = (Map<String, Object>) skill.get("language");

                                    ProgrammingLanguage pl = new ProgrammingLanguage();
                                    if (lang.containsKey("id")) {
                                        double id = (double) lang.get("id");
                                        pl.setId((int) id);
                                    }
                                    pl.setName((String) lang.get("name"));

                                    offeredSkills.add(pl);
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("Error parsing offered skills: " + e.getMessage());
                        }

                        // Habilidades deseadas
                        List<ProgrammingLanguage> wantedSkills = new ArrayList<>();
                        try {
                            if (data.containsKey("wanted_skills")) {
                                List<Map<String, Object>> wanted = (List<Map<String, Object>>) data.get("wanted_skills");

                                for (Map<String, Object> skill : wanted) {
                                    Map<String, Object> lang = (Map<String, Object>) skill.get("language");

                                    ProgrammingLanguage pl = new ProgrammingLanguage();
                                    if (lang.containsKey("id")) {
                                        double id = (double) lang.get("id");
                                        pl.setId((int) id);
                                    }
                                    pl.setName((String) lang.get("name"));

                                    wantedSkills.add(pl);
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("Error parsing wanted skills: " + e.getMessage());
                        }

                        // Actualizar RecyclerViews
                        SkillAdapter offeredAdapter = new SkillAdapter(getContext(), offeredSkills);
                        recyclerSkillsOffered.setAdapter(offeredAdapter);

                        SkillAdapter wantedAdapter = new SkillAdapter(getContext(), wantedSkills);
                        recyclerSkillsWanted.setAdapter(wantedAdapter);

                    } catch (Exception e) {
                        System.out.println("Error parsing profile: " + e.getMessage());
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error al procesar datos del perfil", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        String error = response.errorBody() != null ? response.errorBody().string() : "";
                        System.out.println("Error profile: " + response.code() + " - " + error);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getContext(), "Error al cargar perfil: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                // progressBar.setVisibility(View.GONE);
                System.out.println("Network error: " + t.getMessage());
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}