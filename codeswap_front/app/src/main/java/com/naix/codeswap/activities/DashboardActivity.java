package com.naix.codeswap.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.naix.codeswap.R;
import com.naix.codeswap.fragments.HomeFragment;
import com.naix.codeswap.fragments.MatchesFragment;
import com.naix.codeswap.fragments.ProfileFragment;
import com.naix.codeswap.fragments.SessionsFragment;

public class DashboardActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean isDemoMode = getIntent().getBooleanExtra("IS_DEMO_MODE", false);
        if (isDemoMode) {
            Toast.makeText(this, "Modo demo activado. Algunas funciones están limitadas.", Toast.LENGTH_LONG).show();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

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
            getSupportActionBar().setTitle("CodeSwap - " + username);
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
}