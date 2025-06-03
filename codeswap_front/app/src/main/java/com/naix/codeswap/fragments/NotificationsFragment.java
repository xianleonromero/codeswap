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

import com.naix.codeswap.R;
import com.naix.codeswap.adapters.SessionRequestAdapter;
import com.naix.codeswap.api.ApiClient;
import com.naix.codeswap.api.ApiService;
import com.naix.codeswap.models.SessionRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsFragment extends Fragment implements SessionRequestAdapter.OnRequestActionListener {

    private RecyclerView recyclerView;
    private SessionRequestAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvNoNotifications;

    private List<SessionRequest> sessionRequests = new ArrayList<>();
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializar vistas
        recyclerView = view.findViewById(R.id.recyclerNotifications);
        progressBar = view.findViewById(R.id.progressBar);
        tvNoNotifications = view.findViewById(R.id.tvNoNotifications);

        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SessionRequestAdapter(getContext(), sessionRequests, this);
        recyclerView.setAdapter(adapter);

        // Inicializar API service
        apiService = ApiClient.getClient().create(ApiService.class);

        // Cargar notificaciones
        loadNotifications();
    }

    private void loadNotifications() {
        showLoading(true);

        Call<List<Map<String, Object>>> call = apiService.getPendingRequests();
        call.enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    sessionRequests.clear();
                    for (Map<String, Object> requestData : response.body()) {
                        sessionRequests.add(SessionRequest.fromMap(requestData));
                    }
                    adapter.notifyDataSetChanged();
                    updateEmptyState();
                } else {
                    Toast.makeText(getContext(), "Error al cargar notificaciones", Toast.LENGTH_SHORT).show();
                    updateEmptyState();
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                updateEmptyState();
            }
        });
    }

    private void updateEmptyState() {
        if (sessionRequests.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvNoNotifications.setVisibility(View.VISIBLE);
            tvNoNotifications.setText("No tienes notificaciones pendientes");
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvNoNotifications.setVisibility(View.GONE);
        }
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            tvNoNotifications.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAcceptRequest(SessionRequest request) {
        respondToRequest(request, "accept", "Aceptando solicitud...");
    }

    @Override
    public void onRejectRequest(SessionRequest request) {
        respondToRequest(request, "reject", "Rechazando solicitud...");
    }

    private void respondToRequest(SessionRequest request, String action, String loadingMessage) {
        Toast.makeText(getContext(), loadingMessage, Toast.LENGTH_SHORT).show();

        Map<String, String> responseData = new HashMap<>();
        responseData.put("action", action);

        Call<Map<String, Object>> call = apiService.respondToRequest(request.getId(), responseData);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String message = (String) response.body().get("message");
                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();

                    // Eliminar la solicitud de la lista
                    sessionRequests.remove(request);
                    adapter.notifyDataSetChanged();
                    updateEmptyState();
                } else {
                    Toast.makeText(getContext(), "Error al responder solicitud", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadNotifications();
    }
}