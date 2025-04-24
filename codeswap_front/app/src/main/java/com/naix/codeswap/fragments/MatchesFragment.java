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

import java.util.ArrayList;
import java.util.List;

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
        loadPotentialMatches();

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

    private void loadPotentialMatches() {
        showLoading(true);

        // En una aplicación real, esta llamada usaría la API real
        // Para demo, usamos datos simulados
        simulatePotentialMatches();

        // Ejemplo de cómo sería con la API real:
        /*
        apiService.getPotentialMatches().enqueue(new Callback<List<Match>>() {
            @Override
            public void onResponse(Call<List<Match>> call, Response<List<Match>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    potentialMatches = response.body();
                    adapter.updateData(potentialMatches);
                    updateEmptyState(potentialMatches);
                } else {
                    Toast.makeText(getContext(), "Error al cargar los matches", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Match>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
        */
    }

    private void loadNormalMatches() {
        showLoading(true);

        // En una aplicación real, esta llamada usaría la API real
        // Para demo, usamos datos simulados
        simulateNormalMatches();

        // Ejemplo de cómo sería con la API real:
        /*
        apiService.getNormalMatches().enqueue(new Callback<List<Match>>() {
            @Override
            public void onResponse(Call<List<Match>> call, Response<List<Match>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    normalMatches = response.body();
                    // Si estamos en la pestaña de matches normales, actualizar la vista
                    if (tabLayout.getSelectedTabPosition() == 1) {
                        adapter.updateData(normalMatches);
                        updateEmptyState(normalMatches);
                    }
                } else {
                    Toast.makeText(getContext(), "Error al cargar los matches", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Match>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
        */
    }

    // Para simular datos en la demostración
    private void simulatePotentialMatches() {
        // Aquí crearías datos simulados para probar
        // Por ahora, solo mostramos el estado de carga/vacío
        showLoading(false);
        potentialMatches = new ArrayList<>();
        adapter.updateData(potentialMatches);
        updateEmptyState(potentialMatches);

        // No olvides cargar también los matches normales
        loadNormalMatches();
    }

    private void simulateNormalMatches() {
        // Aquí crearías datos simulados para probar
        showLoading(false);
        normalMatches = new ArrayList<>();
        // No actualizamos el adaptador aquí porque depende de qué pestaña esté seleccionada
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
        Toast.makeText(getContext(), "Solicitar sesión con " + match.getUser2().getUsername(), Toast.LENGTH_SHORT).show();
        // Aquí navegarías a la pantalla de solicitud de sesión
    }
}