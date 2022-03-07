package com.example.idetect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class RegistrationOfFourUsers extends AppCompatActivity {

    Button bckBtn, servCentRegBtn, drvrRegBtn, autoPrtsRegBtn, mechRegBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_of_four_users);
        ref();
        bckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationOfFourUsers.this, Login.class);
                startActivity(intent);
            }
        });
        servCentRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationOfFourUsers.this, RegistrationServiceCenter.class);
                startActivity(intent);
            }
        });
        drvrRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationOfFourUsers.this, RegistrationDriver.class);
                startActivity(intent);
            }
        });
        autoPrtsRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationOfFourUsers.this, RegistrationAutoParts.class);
                startActivity(intent);
            }
        });
        mechRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationOfFourUsers.this, RegistrationMechanic.class);
                startActivity(intent);
            }
        });
    }

    private void ref() {
        bckBtn = findViewById(R.id.back_button);
        servCentRegBtn = findViewById(R.id.service_center_btn);
        drvrRegBtn = findViewById(R.id.driver_btn);
        autoPrtsRegBtn = findViewById(R.id.auto_parts_btn);
        mechRegBtn = findViewById(R.id.mechanic_btn);
    }
}