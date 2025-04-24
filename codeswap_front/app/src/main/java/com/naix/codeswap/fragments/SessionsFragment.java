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
                // No es necesario hacer nada
            }
        });

        // Asegurarse de mostrar los datos iniciales
        // Esta línea es clave, añádela justo después de cargar los datos
        adapter.updateData(upcomingSessions);
        updateEmptyState(upcomingSessions);
    }

    private void loadUpcomingSessions() {
        showLoading(true);

        // En una aplicación real, esta llamada usaría la API real
        // Para demo, usamos datos simulados
        simulateUpcomingSessions();

        // Ejemplo de cómo sería con la API real:
        /*
        apiService.getUpcomingSessions().enqueue(new Callback<List<Session>>() {
            @Override
            public void onResponse(Call<List<Session>> call, Response<List<Session>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    upcomingSessions = response.body();
                    adapter.updateData(upcomingSessions);
                    updateEmptyState(upcomingSessions);
                } else {
                    Toast.makeText(getContext(), "Error al cargar las sesiones", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Session>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
        */
    }

    private void loadPastSessions() {
        showLoading(true);

        // En una aplicación real, esta llamada usaría la API real
        // Para demo, usamos datos simulados
        simulatePastSessions();

        // Ejemplo de cómo sería con la API real:
        /*
        apiService.getPastSessions().enqueue(new Callback<List<Session>>() {
            @Override
            public void onResponse(Call<List<Session>> call, Response<List<Session>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    pastSessions = response.body();
                    // Si estamos en la pestaña de sesiones pasadas, actualizar la vista
                    if (tabLayout.getSelectedTabPosition() == 1) {
                        adapter.updateData(pastSessions);
                        updateEmptyState(pastSessions);
                    }
                } else {
                    Toast.makeText(getContext(), "Error al cargar las sesiones", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Session>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
        */
    }

    private void simulateUpcomingSessions() {
        // Simulamos algunas sesiones próximas
        upcomingSessions = new ArrayList<>();

        User currentUser = new User();
        currentUser.setId(1);
        currentUser.setUsername("JuanDev");
        currentUser.setFullName("Juan Pérez");

        for (int i = 1; i <= 3; i++) {
            Session session = new Session();
            session.setId(i);

            // Alternamos entre ser profesor y estudiante
            if (i % 2 == 0) {
                User otherUser = new User();
                otherUser.setId(i + 100);
                otherUser.setUsername("Estudiante" + i);
                otherUser.setFullName("Estudiante Número " + i);

                session.setTeacher(currentUser);
                session.setStudent(otherUser);
            } else {
                User otherUser = new User();
                otherUser.setId(i + 200);
                otherUser.setUsername("Profesor" + i);
                otherUser.setFullName("Profesor Número " + i);

                session.setTeacher(otherUser);
                session.setStudent(currentUser);
            }

            // Crear lenguaje para la sesión
            ProgrammingLanguage language = new ProgrammingLanguage();
            language.setId(i);

            switch (i) {
                case 1:
                    language.setName("JavaScript");
                    break;
                case 2:
                    language.setName("Python");
                    break;
                case 3:
                    language.setName("Java");
                    break;
                default:
                    language.setName("Kotlin");
            }

            session.setLanguage(language);

            // Configurar fecha y estado
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, i + 1);  // Próximos días
            calendar.set(Calendar.HOUR_OF_DAY, 14 + i);  // Diferentes horas
            session.setDateTime(calendar.getTime());

            session.setDurationMinutes(60);

            // Diferentes estados
            if (i == 1) {
                session.setStatus(Session.STATUS_PENDING);
            } else if (i == 2) {
                session.setStatus(Session.STATUS_CONFIRMED);
            } else {
                session.setStatus(Session.STATUS_PENDING);
            }

            upcomingSessions.add(session);
        }

        showLoading(false);
        adapter.updateData(upcomingSessions);
        updateEmptyState(upcomingSessions);

        loadPastSessions();
    }

    private void simulatePastSessions() {
        // Simulamos sesiones pasadas
        pastSessions = new ArrayList<>();

        User currentUser = new User();
        currentUser.setId(1);
        currentUser.setUsername("JuanDev");
        currentUser.setFullName("Juan Pérez");

        for (int i = 1; i <= 2; i++) {
            Session session = new Session();
            session.setId(i + 100);

            if (i % 2 == 0) {
                User otherUser = new User();
                otherUser.setId(i + 300);
                otherUser.setUsername("ExEstudiante" + i);
                otherUser.setFullName("Estudiante Pasado " + i);

                session.setTeacher(currentUser);
                session.setStudent(otherUser);
            } else {
                User otherUser = new User();
                otherUser.setId(i + 400);
                otherUser.setUsername("ExProfesor" + i);
                otherUser.setFullName("Profesor Pasado " + i);

                session.setTeacher(otherUser);
                session.setStudent(currentUser);
            }

            // Crear lenguaje para la sesión
            ProgrammingLanguage language = new ProgrammingLanguage();
            language.setId(i + 10);

            if (i == 1) {
                language.setName("C#");
            } else {
                language.setName("Swift");
            }

            session.setLanguage(language);

            // Configurar fecha pasada y estado
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -i * 3);  // Días pasados
            session.setDateTime(calendar.getTime());

            session.setDurationMinutes(45 + i * 15);

            // Diferentes estados para sesiones pasadas
            if (i == 1) {
                session.setStatus(Session.STATUS_COMPLETED);
            } else {
                session.setStatus(Session.STATUS_CANCELLED);
            }

            pastSessions.add(session);
        }

        showLoading(false);

        // Si estamos en la pestaña de sesiones pasadas, actualizar la vista
        if (tabLayout != null && tabLayout.getSelectedTabPosition() == 1) {
            adapter.updateData(pastSessions);
            updateEmptyState(pastSessions);
        }
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

        // En una app real, harías la llamada a la API
        // Ejemplo:
        /*
        apiService.updateSessionStatus(session.getId(), statusUpdate).enqueue(new Callback<Session>() {
            @Override
            public void onResponse(Call<Session> call, Response<Session> response) {
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
            public void onFailure(Call<Session> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
        */

        // Para la demo, simplemente actualizamos la UI
        Toast.makeText(getContext(), "Sesión " + (newStatus.equals(Session.STATUS_CONFIRMED) ? "aceptada" : "rechazada"), Toast.LENGTH_SHORT).show();
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