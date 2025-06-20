package com.naix.codeswap.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.naix.codeswap.R;
import com.naix.codeswap.fragments.ConversationsFragment;
import com.naix.codeswap.fragments.HomeFragment;
import com.naix.codeswap.fragments.MatchesFragment;
import com.naix.codeswap.fragments.ProfileFragment;
import com.naix.codeswap.fragments.SessionsFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.naix.codeswap.api.ApiClient;
import com.naix.codeswap.api.ApiService;
import com.naix.codeswap.fragments.NotificationsFragment;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private TextView notificationCountView;
    private View notificationBadge;
    private TextView messageCountView;
    private View messageBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);
        showCurrentUser();

        // Cargar el fragmento inicial (Home)
        loadFragment(new HomeFragment());
    }

    private void showCurrentUser() {
        // Obtener username guardado
        SharedPreferences prefs = getSharedPreferences("CodeSwapPrefs", MODE_PRIVATE);
        String username = prefs.getString("username", "");

        if (!username.isEmpty() && getSupportActionBar() != null) {
            getSupportActionBar().setTitle("CodeSwap");
        }
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        int itemId = item.getItemId();
        if (itemId == R.id.nav_home) {
            fragment = new HomeFragment();
        } else if (itemId == R.id.nav_matches) {
            fragment = new MatchesFragment();
        } else if (itemId == R.id.nav_sessions) {
            fragment = new SessionsFragment();
        } else if (itemId == R.id.nav_profile) {
            fragment = new ProfileFragment();
        }

        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dashboard_menu, menu);

        // Configurar badge de mensajes
        MenuItem messageItem = menu.findItem(R.id.action_messages);
        View messageActionView = messageItem.getActionView();
        if (messageActionView != null) {
            messageBadge = messageActionView;
            messageCountView = messageActionView.findViewById(R.id.tv_message_count);
            messageActionView.setOnClickListener(v -> openMessages());
        }

        // Configurar badge de notificaciones (código existente)
        MenuItem notificationItem = menu.findItem(R.id.action_notifications);
        View actionView = notificationItem.getActionView();
        if (actionView != null) {
            notificationBadge = actionView;
            notificationCountView = actionView.findViewById(R.id.tv_notification_count);
            actionView.setOnClickListener(v -> openNotifications());
        }

        // Cargar contadores
        updateNotificationCount();
        updateMessageCount();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_notifications) {
            openNotifications();
            return true;
        } else if (item.getItemId() == R.id.action_messages) {
            openMessages();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void openMessages() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new ConversationsFragment())
                .addToBackStack(null)
                .commit();
    }

    private void updateMessageCount() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Map<String, Object>> call = apiService.getMessagesCount();

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Object countObj = response.body().get("count");
                    int count = 0;

                    if (countObj instanceof Double) {
                        count = ((Double) countObj).intValue();
                    } else if (countObj instanceof Integer) {
                        count = (Integer) countObj;
                    }

                    updateMessageBadgeCount(count);
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                updateMessageBadgeCount(0);
            }
        });
    }

    private void updateMessageBadgeCount(int count) {
        if (messageCountView != null) {
            if (count > 0) {
                messageCountView.setVisibility(View.VISIBLE);
                messageCountView.setText(count > 99 ? "99+" : String.valueOf(count));
            } else {
                messageCountView.setVisibility(View.GONE);
            }
        }
    }

    private void openNotifications() {
        // Abrir fragment de notificaciones
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new NotificationsFragment())
                .addToBackStack(null)
                .commit();
    }

    private void updateNotificationCount() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Map<String, Object>> call = apiService.getNotificationsCount();

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Object countObj = response.body().get("count");
                    int count = 0;

                    if (countObj instanceof Double) {
                        count = ((Double) countObj).intValue();
                    } else if (countObj instanceof Integer) {
                        count = (Integer) countObj;
                    }

                    updateBadgeCount(count);
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                // Si falla, ocultar badge
                updateBadgeCount(0);
            }
        });
    }

    private void updateBadgeCount(int count) {
        if (notificationCountView != null) {
            if (count > 0) {
                notificationCountView.setVisibility(View.VISIBLE);
                notificationCountView.setText(count > 99 ? "99+" : String.valueOf(count));
            } else {
                notificationCountView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateNotificationCount();
        updateMessageCount();
    }
}