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
    public void onRequestSessionClick(Match match) {
        // Crear diálogo para seleccionar fecha y hora
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Solicitar sesión con " + match.getUser2().getUsername());

        // Crear vista personalizada para el diálogo
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_request_session, null);

        // Referencias a los elementos del diálogo
        android.widget.DatePicker datePicker = dialogView.findViewById(R.id.datePicker);
        android.widget.TimePicker timePicker = dialogView.findViewById(R.id.timePicker);
        android.widget.Spinner spinnerLanguage = dialogView.findViewById(R.id.spinnerLanguage);
        android.widget.Spinner spinnerDuration = dialogView.findViewById(R.id.spinnerDuration);

        // Configurar selector de lenguaje - USAR USER2_WANTS (lo que el otro busca)
        List<String> languageNames = new ArrayList<>();
        List<ProgrammingLanguage> availableLanguages = new ArrayList<>();

        for (ProgrammingLanguage lang : match.getUser2Wants()) {
            languageNames.add(lang.getName());
            availableLanguages.add(lang);
        }

        // Si no hay coincidencias, usar user2_offers como alternativa
        if (languageNames.isEmpty()) {
            for (ProgrammingLanguage lang : match.getUser2Offers()) {
                languageNames.add(lang.getName());
                availableLanguages.add(lang);
            }
        }

        if (languageNames.isEmpty()) {
            Toast.makeText(getContext(), "No hay lenguajes disponibles para enseñar", Toast.LENGTH_SHORT).show();
            return;
        }

        android.widget.ArrayAdapter<String> languageAdapter = new android.widget.ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, languageNames);
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(languageAdapter);

        // Configurar selector de duración
        String[] durations = {"30 minutos", "45 minutos", "60 minutos", "90 minutos"};
        android.widget.ArrayAdapter<String> durationAdapter = new android.widget.ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, durations);
        durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDuration.setAdapter(durationAdapter);
        spinnerDuration.setSelection(2); // 60 minutos por defecto

        // Configurar fecha mínima (mañana)
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        datePicker.setMinDate(tomorrow.getTimeInMillis());

        // Configurar hora (formato 24h)
        timePicker.setIs24HourView(true);
        timePicker.setHour(14); // 2 PM por defecto
        timePicker.setMinute(0);

        builder.setView(dialogView);
        builder.setPositiveButton("Solicitar", (dialog, which) -> {
            // Obtener valores seleccionados
            int year = datePicker.getYear();
            int month = datePicker.getMonth();
            int day = datePicker.getDayOfMonth();
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();

            int selectedLanguageIndex = spinnerLanguage.getSelectedItemPosition();
            int selectedDurationIndex = spinnerDuration.getSelectedItemPosition();

            if (selectedLanguageIndex < 0 || selectedLanguageIndex >= availableLanguages.size()) {
                Toast.makeText(getContext(), "Error: Selecciona un lenguaje válido", Toast.LENGTH_SHORT).show();
                return;
            }

            // Crear fecha
            Calendar sessionDate = Calendar.getInstance();
            sessionDate.set(year, month, day, hour, minute, 0);

            // Crear datos para la sesión
            Map<String, Object> sessionData = new HashMap<>();
            sessionData.put("receiver_id", match.getUser2().getId());
            sessionData.put("language_id", availableLanguages.get(selectedLanguageIndex).getId());

            // Formato ISO para Django
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
            isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            sessionData.put("date_time", isoFormat.format(sessionDate.getTime()));

            // Duración
            int[] durationMinutes = {30, 45, 60, 90};
            sessionData.put("duration_minutes", durationMinutes[selectedDurationIndex]);

            // Crear sesión
            createSessionRequest(sessionData, match.getUser2().getUsername());
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void createSessionRequest(Map<String, Object> sessionData, String receiverName) {
        // Añadir mensaje directamente a sessionData
        sessionData.put("message", "Solicitud de sesión de programación");

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Map<String, Object>> call = apiService.requestSession(sessionData);

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), "¡Solicitud enviada a " + receiverName + "! Recibirá una notificación.", Toast.LENGTH_LONG).show();
                    loadMatches();
                } else {
                    Toast.makeText(getContext(), "Error al enviar solicitud: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}