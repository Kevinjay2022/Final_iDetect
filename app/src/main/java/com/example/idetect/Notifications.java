package com.example.idetect;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Notifications extends AppCompatActivity {
    private final String CHANNEL_ID = "notify";
    private final int NOTIFICATION_ID = 1;
    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
    NotificationCompat.Builder builder1 = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);

    private void createNotification(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Orders";
            String desc = "There is an order";

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription(desc);

            //NotificationManager notificationManager = new NotificationManager(NotificationManager)
            //
        }
    }
    private void AddNotification(){
        FirebaseDatabase.getInstance().getReference().child("USERS").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String entity = snapshot.child("acctype").getValue().toString();
                    if(entity != null){
                        switch (entity){
                            case "serve_center":
                                ServiceCenterNotification();
                                break;
                            case "driver":
                                DriverNotification();
                                break;
                            case "mechanic":
                                MechanicNotification();
                                break;
                            case "auto_parts":
                                AutoPartsNotification();
                                break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ServiceCenterNotification() {
        builder.setSmallIcon(R.drawable.location_logo);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.location_logo));

        builder.setContentTitle("You have order.");
        builder.setContentText("Check the order");

        builder1.setSmallIcon(R.drawable.location_logo);
        builder1.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.location_logo));

        builder1.setContentTitle("You have request.");
        builder1.setContentText("Check the drivers");
    }

    private void DriverNotification(){
        builder.setSmallIcon(R.drawable.location_logo);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.location_logo));

        builder.setContentTitle("Responded.");
        builder.setContentText("Check the responded");
    }

    private void MechanicNotification(){
        builder.setSmallIcon(R.drawable.location_logo);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.location_logo));

        builder.setContentTitle("Request.");
        builder.setContentText("Service Center wants to hire you.");
    }

    private void AutoPartsNotification(){
        builder.setSmallIcon(R.drawable.location_logo);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.location_logo));

        builder.setContentTitle("You have order.");
        builder.setContentText("Check the order");
    }
}

