package com.example.idetect;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.annotation.NonNull;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class FragmentServiceCenterNavigation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_service_center_navigation);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_main, new FragmentServiceCenterHome()).commit();
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId()){
                case R.id.nav_home:
                    selectedFragment = new FragmentServiceCenterHome();
                    break;
                case R.id.nav_shop:
                    selectedFragment = new FragmentServiceCenterAutoParts();
                    break;
                case R.id.nav_mechanic:
                    selectedFragment = new FragmentServiceCenterMechanics();
                    break;
                case R.id.nav_customers:
                    selectedFragment = new FragmentServiceCenterCustomers();
                    break;
                case R.id.nav_profile:
                    selectedFragment = new Profile();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_main, selectedFragment).commit();
            return true;
        }
    };
}