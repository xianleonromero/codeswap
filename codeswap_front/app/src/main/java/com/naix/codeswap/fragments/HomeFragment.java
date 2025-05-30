package com.naix.codeswap.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.naix.codeswap.R;
import com.naix.codeswap.api.ApiClient;
import com.naix.codeswap.api.ApiService;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private TextView tvPotentialMatchesCount;
    private TextView tvNormalMatchesCount;
    private TextView tvCompletedSessionsCount;
    private Button btnFindMatches;

    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializar vistas
        tvPotentialMatchesCount = view.findViewById(R.id.tvPotentialMatchesCount);
        tvNormalMatchesCount = view.findViewById(R.id.tvNormalMatchesCount);
        tvCompletedSessionsCount = view.findViewById(R.id.tvCompletedSessionsCount);
        btnFindMatches = view.findViewById(R.id.btnFindMatches);

        // Inicializar API service
        apiService = ApiClient.getClient().create(ApiService.class);

        loadRealData();

        btnFindMatches.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Buscando nuevos matches...", Toast.LENGTH_SHORT).show();
            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<Void> call = apiService.refreshMatches();

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "¡Nuevos matches encontrados!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Error al buscar matches", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
    private void loadRealData() {
        // Mostrar 0 inicialmente
        tvPotentialMatchesCount.setText("0");
        tvNormalMatchesCount.setText("0");
        tvCompletedSessionsCount.setText("0");

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        // Cargar matches potenciales
        Call<List<Map<String, Object>>> potentialCall = apiService.getPotentialMatches();
        potentialCall.enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tvPotentialMatchesCount.setText(String.valueOf(response.body().size()));
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                // Mantener en 0
            }
        });

        // Cargar matches normales
        Call<List<Map<String, Object>>> normalCall = apiService.getNormalMatches();
        normalCall.enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tvNormalMatchesCount.setText(String.valueOf(response.body().size()));
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                // Mantener en 0
            }
        });

        // Cargar sesiones pasadas para contar las completadas
        Call<List<Map<String, Object>>> sessionsCall = apiService.getPastSessions();
        sessionsCall.enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int completedCount = 0;
                    for (Map<String, Object> sessionData : response.body()) {
                        if ("COMPLETED".equals(sessionData.get("status"))) {
                            completedCount++;
                        }
                    }
                    tvCompletedSessionsCount.setText(String.valueOf(completedCount));
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                // Mantener en 0
            }
        });
    }
}