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
import com.naix.codeswap.adapters.SessionAdapter;
import com.naix.codeswap.api.ApiClient;
import com.naix.codeswap.api.ApiService;
import com.naix.codeswap.models.ProgrammingLanguage;
import com.naix.codeswap.models.Session;
import com.naix.codeswap.models.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SessionsFragment extends Fragment implements SessionAdapter.OnSessionActionListener {

    private RecyclerView recyclerView;
    private SessionAdapter adapter;
    private TabLayout tabLayout;
    private ProgressBar progressBar;
    private TextView tvNoSessions;

    private List<Session> upcomingSessions = new ArrayList<>();
    private List<Session> pastSessions = new ArrayList<>();
    private ApiService apiService;
    private boolean isShowingUpcoming = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sessions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializar vistas
        recyclerView = view.findViewById(R.id.recyclerSessions);
        tabLayout = view.findViewById(R.id.tabLayout);
        progressBar = view.findViewById(R.id.progressBar);
        tvNoSessions = view.findViewById(R.id.tvNoSessions);

        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SessionAdapter(getContext(), new ArrayList<>(), isShowingUpcoming, this);
        recyclerView.setAdapter(adapter);

        // Inicializar API service
        apiService = ApiClient.getClient().create(ApiService.class);

        // Cargar los datos iniciales (upcoming sessions)
        loadUpcomingSessions();

        // Configurar el cambio de tabs
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    // Mostrar sesiones próximas
                    isShowingUpcoming = true;
                    adapter = new SessionAdapter(getContext(), upcomingSessions, isShowingUpcoming, SessionsFragment.this);
                    recyclerView.setAdapter(adapter);
                    updateEmptyState(upcomingSessions);
                } else {
                    // Mostrar sesiones pasadas
                    isShowingUpcoming = false;
                    adapter = new SessionAdapter(getContext(), pastSessions, isShowingUpcoming, SessionsFragment.this);
                    recyclerView.setAdapter(adapter);
                    updateEmptyState(pastSessions);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // No es necesario hacer nada
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        adapter.updateData(upcomingSessions);
        updateEmptyState(upcomingSessions);
    }

    private void loadUpcomingSessions() {
        showLoading(true);

        apiService.getUpcomingSessions().enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    upcomingSessions = new ArrayList<>();
                    for (Map<String, Object> sessionData : response.body()) {
                        upcomingSessions.add(Session.fromMap(sessionData));
                    }
                    adapter.updateData(upcomingSessions);
                    updateEmptyState(upcomingSessions);
                } else {
                    Toast.makeText(getContext(), "Error al cargar las sesiones", Toast.LENGTH_SHORT).show();
                    upcomingSessions = new ArrayList<>();
                    adapter.updateData(upcomingSessions);
                    updateEmptyState(upcomingSessions);
                }
                // Cargar sesiones pasadas después
                loadPastSessions();
            }
            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                upcomingSessions = new ArrayList<>();
                adapter.updateData(upcomingSessions);
                updateEmptyState(upcomingSessions);
                loadPastSessions();
            }
        });
    }

    private void loadPastSessions() {
        apiService.getPastSessions().enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    pastSessions = new ArrayList<>();
                    for (Map<String, Object> sessionData : response.body()) {
                        pastSessions.add(Session.fromMap(sessionData));
                    }
                    // Si estamos en la pestaña de sesiones pasadas, actualizar la vista
                    if (tabLayout.getSelectedTabPosition() == 1) {
                        adapter.updateData(pastSessions);
                        updateEmptyState(pastSessions);
                    }
                } else {
                    Toast.makeText(getContext(), "Error al cargar las sesiones pasadas", Toast.LENGTH_SHORT).show();
                    pastSessions = new ArrayList<>();
                    if (tabLayout.getSelectedTabPosition() == 1) {
                        adapter.updateData(pastSessions);
                        updateEmptyState(pastSessions);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                pastSessions = new ArrayList<>();
                if (tabLayout.getSelectedTabPosition() == 1) {
                    adapter.updateData(pastSessions);
                    updateEmptyState(pastSessions);
                }
            }
        });
    }


    private void updateEmptyState(List<Session> sessions) {
        if (sessions.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvNoSessions.setVisibility(View.VISIBLE);
            if (isShowingUpcoming) {
                tvNoSessions.setText("No tienes sesiones programadas");
            } else {
                tvNoSessions.setText("No tienes sesiones pasadas");
            }
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvNoSessions.setVisibility(View.GONE);
        }
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            tvNoSessions.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void updateSessionStatus(Session session, String newStatus) {
        Map<String, String> statusUpdate = new HashMap<>();
        statusUpdate.put("status", newStatus);

        apiService.updateSessionStatus(session.getId(), statusUpdate).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Actualizar la sesión en la lista local
                    session.setStatus(newStatus);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Sesión actualizada correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Error al actualizar la sesión", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAcceptSession(Session session) {
        updateSessionStatus(session, Session.STATUS_CONFIRMED);
    }

    @Override
    public void onRejectSession(Session session) {
        updateSessionStatus(session, Session.STATUS_CANCELLED);
    }

    @Override
    public void onRateSession(Session session) {
        Toast.makeText(getContext(), "Valorar sesión", Toast.LENGTH_SHORT).show();
        // Aquí se abriría un dialog o una nueva actividad para valorar la sesión
    }
}