package com.naix.codeswap.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.naix.codeswap.R;
import com.naix.codeswap.adapters.MatchAdapter;
import com.naix.codeswap.api.ApiClient;
import com.naix.codeswap.api.ApiService;
import com.naix.codeswap.models.Match;
import com.naix.codeswap.models.ProgrammingLanguage;
import com.naix.codeswap.models.Session;
import com.naix.codeswap.models.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class MatchesFragment extends Fragment implements MatchAdapter.OnMatchClickListener {

    private RecyclerView recyclerView;
    private MatchAdapter adapter;
    private TabLayout tabLayout;
    private ProgressBar progressBar;
    private TextView tvNoMatches;

    private List<Match> potentialMatches = new ArrayList<>();
    private List<Match> normalMatches = new ArrayList<>();
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_matches, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializar vistas
        recyclerView = view.findViewById(R.id.recyclerMatches);
        tabLayout = view.findViewById(R.id.tabLayout);
        progressBar = view.findViewById(R.id.progressBar);
        tvNoMatches = view.findViewById(R.id.tvNoMatches);

        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MatchAdapter(getContext(), new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        // Inicializar API service
        apiService = ApiClient.getClient().create(ApiService.class);

        // Cargar los datos iniciales (potential matches)
        loadMatches();

        // Configurar el cambio de tabs
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    // Mostrar matches potenciales
                    adapter.updateData(potentialMatches);
                    updateEmptyState(potentialMatches);
                } else {
                    // Mostrar matches normales
                    adapter.updateData(normalMatches);
                    updateEmptyState(normalMatches);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // No es necesario hacer nada
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // No es necesario hacer nada
            }
        });
    }


    private void loadMatches() {
        showLoading(true);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        // Cargar matches potenciales
        Call<List<Map<String, Object>>> call = apiService.getPotentialMatches();
        call.enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    potentialMatches = new ArrayList<>();
                    for (Map<String, Object> matchData : response.body()) {
                        potentialMatches.add(Match.fromMap(matchData));
                    }
                    if (tabLayout.getSelectedTabPosition() == 0) {
                        adapter.updateData(potentialMatches);
                        updateEmptyState(potentialMatches);
                    }
                } else {
                    System.out.println("Error loading potential matches: " + response.code());
                    potentialMatches = new ArrayList<>();
                    if (tabLayout.getSelectedTabPosition() == 0) {
                        adapter.updateData(potentialMatches);
                        updateEmptyState(potentialMatches);
                    }
                }
                loadNormalMatchesFromApi();
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                System.out.println("Network error: " + t.getMessage());
                potentialMatches = new ArrayList<>();
                if (tabLayout.getSelectedTabPosition() == 0) {
                    adapter.updateData(potentialMatches);
                    updateEmptyState(potentialMatches);
                }
                loadNormalMatchesFromApi();
            }
        });
    }

    private void loadNormalMatchesFromApi() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<Map<String, Object>>> call = apiService.getNormalMatches();
        call.enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    normalMatches = new ArrayList<>();
                    for (Map<String, Object> matchData : response.body()) {
                        normalMatches.add(Match.fromMap(matchData));
                    }
                    if (tabLayout.getSelectedTabPosition() == 1) {
                        adapter.updateData(normalMatches);
                        updateEmptyState(normalMatches);
                    }
                } else {
                    System.out.println("Error loading normal matches: " + response.code());
                    normalMatches = new ArrayList<>();
                    if (tabLayout.getSelectedTabPosition() == 1) {
                        adapter.updateData(normalMatches);
                        updateEmptyState(normalMatches);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                showLoading(false);
                System.out.println("Network error: " + t.getMessage());
                normalMatches = new ArrayList<>();
                if (tabLayout.getSelectedTabPosition() == 1) {
                    adapter.updateData(normalMatches);
                    updateEmptyState(normalMatches);
                }
            }
        });
    }

    private void updateEmptyState(List<Match> matches) {
        if (matches.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvNoMatches.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvNoMatches.setVisibility(View.GONE);
        }
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            tvNoMatches.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }


    @Override
    public void onViewDetailsClick(Match match) {
        Toast.makeText(getContext(), "Ver detalles de " + match.getUser2().getUsername(), Toast.LENGTH_SHORT).show();
        // Aquí navegarías a la pantalla de detalles del usuario
    }

    @Override
    public void onRequestSessionClick(Match match) {
        // Crear datos para la sesión
        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put("student_id", match.getUser2().getId());

        // Usar el primer lenguaje que ofrecemos
        if (!match.getUser1Offers().isEmpty()) {
            sessionData.put("language_id", match.getUser1Offers().get(0).getId());
        } else {
            Toast.makeText(getContext(), "Error: No se encontró lenguaje para enseñar", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fecha para mañana a las 15:00
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 15);
        calendar.set(Calendar.MINUTE, 0);
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        sessionData.put("date_time", isoFormat.format(calendar.getTime()));
        sessionData.put("duration_minutes", 60);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Map<String, Object>> call = apiService.createSession(sessionData);

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), "¡Sesión solicitada con éxito!", Toast.LENGTH_SHORT).show();
                    // Recargar matches para actualizar el estado
                    loadMatches();
                } else {
                    Toast.makeText(getContext(), "Error al crear sesión: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}