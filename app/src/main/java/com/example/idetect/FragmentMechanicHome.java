package com.example.idetect;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FragmentMechanicHome extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_mechanic_home);

        BottomNavigationView bottomNav = findViewById(R.id.MechBottomNav);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.Mechanic_fragment_container_main, new FragmentMechanicPosts()).commit();
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()){
                case R.id.nav_home:
                    selectedFragment = new FragmentMechanicPosts();
                    break;
                case R.id.nav_notification:
                    selectedFragment = new FragmentMechanicNotification();
                    break;
                case R.id.nav_profile:
                    selectedFragment = new Profile();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.Mechanic_fragment_container_main, selectedFragment).commit();
            return true;
        }
    };
}
