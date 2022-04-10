package com.example.idetect;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.idetect.Models.ServCentCustomerService;
import com.example.idetect.Notify.Token;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class FragmentMechanicHome extends AppCompatActivity {
    TextView textView;
    int counter = 0;
    CardView notifCard;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_mechanic_home);

        updateToken(FirebaseInstanceId.getInstance().getToken());
        textView = findViewById(R.id.notif_counter);
        notifCard = findViewById(R.id.notif_counterCard);

        FirebaseDatabase.getInstance().getReference().child("MECHANIC_NOTIFY")
                .orderByChild("ID").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        counter = 0;
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ServCentCustomerService model = ds.getValue(ServCentCustomerService.class);
                            if (model.getSeen().equals("new"))
                                counter++;
                        }
                        if (counter == 0)
                            notifCard.setVisibility(View.GONE);
                        else {
                            notifCard.setVisibility(View.VISIBLE);
                            textView.setText(""+counter);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

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
    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token1);
    }
}
