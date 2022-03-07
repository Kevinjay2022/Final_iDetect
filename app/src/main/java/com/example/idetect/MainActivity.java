package com.example.idetect;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loginStatus();
            }
        }, 1000);


    }

    private void loginStatus() {
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseDatabase.getInstance().getReference("USERS").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String entity = snapshot.child("acctype").getValue().toString();
                                if (entity != null) {
                                    switch (entity) {
                                        case "serve_center":
                                            startActivity(new Intent(MainActivity.this, FragmentServiceCenterNavigation.class));
                                            finish();
                                            break;
                                        case "driver":
                                            startActivity(new Intent(MainActivity.this, FragmentDriverNavigationHome.class));
                                            finish();
                                            break;
                                        case "mechanic":
                                            startActivity(new Intent(MainActivity.this, FragmentMechanicHome.class));
                                            finish();
                                            break;
                                        case "auto_parts":
                                            startActivity(new Intent(MainActivity.this, FragmentAutoPartsNavigation.class));
                                            finish();
                                            break;
                                    }
                                }

                            } else {
                                Toast.makeText(MainActivity.this, "Unapproved account", Toast.LENGTH_SHORT).show();
                                FirebaseAuth.getInstance().signOut();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }else {
            startActivity(new Intent(MainActivity.this, Login.class));
        }
    }
}