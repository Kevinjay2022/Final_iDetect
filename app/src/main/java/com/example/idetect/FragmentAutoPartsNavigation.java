package com.example.idetect;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FragmentAutoPartsNavigation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_auto_parts_navigation);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_main, new FragmentAutoPartsHome()).commit();
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId()){
                case R.id.nav_home:
                    selectedFragment = new FragmentAutoPartsHome();
                    break;
                case R.id.nav_shop:
                    selectedFragment = new FragmentAutoPartsStore();
                    break;
                case R.id.nav_notification:
                    selectedFragment = new FragmentAutoPartsNotification();
                    break;
                case R.id.nav_customers:
                    selectedFragment = new FragmentAutoPartsCustomerHistory();
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