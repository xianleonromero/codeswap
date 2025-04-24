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

        // Cargar datos simulados
        loadMockData();

        // Configurar listeners
        btnFindMatches.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Buscando nuevos matches...", Toast.LENGTH_SHORT).show();
            // En una app real, aquí harías una llamada a la API
            // apiService.refreshMatches().enqueue(...);
        });
    }

    private void loadMockData() {
        // Aquí cargaríamos datos de la API en una app real
        // Para demo, usar valores simulados
        tvPotentialMatchesCount.setText("3");
        tvNormalMatchesCount.setText("5");
        tvCompletedSessionsCount.setText("2");
    }
}