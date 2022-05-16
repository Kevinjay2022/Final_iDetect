package com.example.idetect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.idetect.Models.PaypalClientIDConfigClass;
import com.example.idetect.Models.SubscriptionClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;

import java.math.BigDecimal;
import java.util.HashMap;

public class SubscriptionMethod extends AppCompatActivity {


    private static PayPalConfiguration paypalConfig = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(PaypalClientIDConfigClass.PAYPAL_CLIENT_ID);
    CardView threeHundredSub, oneThousand, fiveThousand;
    Button subsBackBTN;
    TextView freeTrial;
    long due_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_method);

        threeHundredSub = findViewById(R.id.threeHundredSub);
        oneThousand = findViewById(R.id.oneThousand);
        fiveThousand = findViewById(R.id.fiveThousand);

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);
        startService(intent);

        freeTrial = findViewById(R.id.freeTrial);
        subsBackBTN = findViewById(R.id.back_BTNSubscription);
        subsBackBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        FirebaseDatabase.getInstance().getReference().child("USERS").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String timeStamp = snapshot.child("subscribe").getValue().toString();
                            due_date = Long.parseLong(timeStamp);

                            if (System.currentTimeMillis() > due_date){
                                freeTrial.setText("Free trial expired");
                            }else {
                                long remaining = due_date - System.currentTimeMillis();
                                long seconds = remaining / 1000;
                                long minutes = seconds / 60;
                                long hours = minutes / 60;
                                long days = hours / 24;
                                long remainder = hours % 24;
                                freeTrial.setText("" + days + "d " + remainder + "hr(s) remaining.");
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        threeHundredSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    PayPalPaymentMethod(300, "30 days subscription.");
            }
        });
        oneThousand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PayPalPaymentMethod(1000, "6 months subscription.");
            }
        });
        fiveThousand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PayPalPaymentMethod(5000, "12 months subscription.");
            }
        });

    }

    private void PayPalPaymentMethod(int value, String text) {
        PayPalPayment payment = new PayPalPayment(new BigDecimal(value), "Php", text, PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        startActivityForResult(intent, 12);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 12){
            if (resultCode == Activity.RESULT_OK){
                Long yearStamp = Long.parseLong("31556952000");
                Long expiryTime = System.currentTimeMillis() + yearStamp;
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("subscribe", expiryTime);
                FirebaseDatabase.getInstance().getReference().child("USERS").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(hashMap);
                Toast.makeText(getApplicationContext(), "Payment made successfully!", Toast.LENGTH_LONG).show();
            }else
                Toast.makeText(getApplicationContext(), "Payment is unsuccessfully!", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onDestroy() {
        stopService(new Intent(getApplicationContext(), PayPalService.class));
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}