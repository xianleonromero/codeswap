package com.naix.codeswap.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.naix.codeswap.R;
import com.naix.codeswap.adapters.ChatAdapter;
import com.naix.codeswap.api.ApiClient;
import com.naix.codeswap.api.ApiService;
import com.naix.codeswap.models.ChatMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.os.Handler;
import android.os.Looper;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerChat;
    private ChatAdapter adapter;
    private EditText etMessage;
    private ImageButton btnSend;

    private int conversationId;
    private String otherUserName;
    private List<ChatMessage> messages = new ArrayList<>();
    private ApiService apiService;
    private Handler refreshHandler;
    private Runnable refreshRunnable;
    private static final int REFRESH_INTERVAL = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Obtener datos del intent
        conversationId = getIntent().getIntExtra("conversation_id", -1);
        otherUserName = getIntent().getStringExtra("other_user_name");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(otherUserName);
        }

        // Inicializar vistas
        recyclerChat = findViewById(R.id.recyclerChat);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        // Configurar RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerChat.setLayoutManager(layoutManager);
        adapter = new ChatAdapter(this, messages);
        recyclerChat.setAdapter(adapter);

        apiService = ApiClient.getClient().create(ApiService.class);

        // Cargar mensajes
        loadMessages();

        // Configurar envío de mensajes
        btnSend.setOnClickListener(v -> sendMessage());
        // Configurar auto-refresh de mensajes
        refreshHandler = new Handler(Looper.getMainLooper());
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                loadMessages();
                refreshHandler.postDelayed(this, REFRESH_INTERVAL);
            }
        };
    }

    private void loadMessages() {
        Call<List<Map<String, Object>>> call = apiService.getConversationMessages(conversationId);
        call.enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(@NonNull Call<List<Map<String, Object>>> call, @NonNull Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int oldSize = messages.size();
                    boolean wasAtBottom = isScrollAtBottom();
                    messages.clear();
                    for (Map<String, Object> msgData : response.body()) {
                        messages.add(ChatMessage.fromMap(msgData));
                    }
                    adapter.notifyDataSetChanged();

                    // Solo hacer scroll si había pocos mensajes antes O si estaba al final
                    if (oldSize == 0 || wasAtBottom || messages.size() > oldSize) {
                        scrollToBottom();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {}
        });
    }
    private boolean isScrollAtBottom() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerChat.getLayoutManager();
        if (layoutManager == null) return true;

        int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
        int totalItems = layoutManager.getItemCount();

        return lastVisiblePosition >= totalItems - 2; // Considerar "cerca del final"
    }

    private void sendMessage() {
        String content = etMessage.getText().toString().trim();
        if (content.isEmpty()) return;

        Map<String, String> messageData = new HashMap<>();
        messageData.put("content", content);

        Call<Map<String, Object>> call = apiService.sendMessage(conversationId, messageData);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, Object>> call, @NonNull Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    etMessage.setText("");
                    // Refresh inmediato después de enviar
                    refreshHandler.removeCallbacks(refreshRunnable);
                    loadMessages();
                    refreshHandler.postDelayed(refreshRunnable, REFRESH_INTERVAL);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, Object>> call, @NonNull Throwable t) {
                Toast.makeText(ChatActivity.this, "Error al enviar mensaje", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void scrollToBottom() {
        if (!messages.isEmpty()) {
            recyclerChat.smoothScrollToPosition(messages.size() - 1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Iniciar auto-refresh cuando la actividad es visible
        refreshHandler.postDelayed(refreshRunnable, REFRESH_INTERVAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Detener auto-refresh cuando la actividad no es visible
        refreshHandler.removeCallbacks(refreshRunnable);
    }
}