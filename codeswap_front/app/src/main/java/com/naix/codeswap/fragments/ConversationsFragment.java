package com.naix.codeswap.fragments;

import android.content.Intent;
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
import com.naix.codeswap.adapters.ConversationAdapter;
import com.naix.codeswap.api.ApiClient;
import com.naix.codeswap.api.ApiService;
import com.naix.codeswap.models.Conversation;
import com.naix.codeswap.activities.ChatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConversationsFragment extends Fragment implements ConversationAdapter.OnConversationClickListener {

    private RecyclerView recyclerView;
    private ConversationAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvNoConversations;

    private List<Conversation> conversations = new ArrayList<>();
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_conversations, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerConversations);
        progressBar = view.findViewById(R.id.progressBar);
        tvNoConversations = view.findViewById(R.id.tvNoConversations);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ConversationAdapter(getContext(), conversations, this);
        recyclerView.setAdapter(adapter);

        apiService = ApiClient.getClient().create(ApiService.class);

        loadConversations();
    }

    private void loadConversations() {
        showLoading(true);

        Call<List<Map<String, Object>>> call = apiService.getConversations();
        call.enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(@NonNull Call<List<Map<String, Object>>> call, @NonNull Response<List<Map<String, Object>>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    conversations.clear();
                    for (Map<String, Object> convData : response.body()) {
                        conversations.add(Conversation.fromMap(convData));
                    }
                    adapter.updateData(conversations);
                    updateEmptyState();
                } else {
                    Toast.makeText(getContext(), "Error al cargar conversaciones", Toast.LENGTH_SHORT).show();
                    updateEmptyState();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Map<String, Object>>> call, @NonNull Throwable t) {
                showLoading(false);
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                updateEmptyState();
            }
        });
    }

    private void updateEmptyState() {
        if (conversations.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvNoConversations.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvNoConversations.setVisibility(View.GONE);
        }
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            tvNoConversations.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onConversationClick(Conversation conversation) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("conversation_id", conversation.getId());
        intent.putExtra("other_user_name", conversation.getOtherUser().getUsername());
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadConversations();
    }
}