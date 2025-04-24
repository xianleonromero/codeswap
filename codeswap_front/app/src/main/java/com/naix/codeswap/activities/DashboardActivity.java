package com.naix.codeswap.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.naix.codeswap.R;
import com.naix.codeswap.fragments.HomeFragment;
import com.naix.codeswap.fragments.MatchesFragment;
import com.naix.codeswap.fragments.ProfileFragment;
import com.naix.codeswap.fragments.SessionsFragment;

public class DashboardActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        // Cargar el fragmento inicial (Home)
        loadFragment(new HomeFragment());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        if (item.getItemId() == R.id.nav_home) {
            fragment = new HomeFragment();
        } else if (item.getItemId() == R.id.nav_matches) {
            fragment = new MatchesFragment();
        } else if (item.getItemId() == R.id.nav_sessions) {
            fragment = new SessionsFragment();
        } else if (item.getItemId() == R.id.nav_profile) {
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