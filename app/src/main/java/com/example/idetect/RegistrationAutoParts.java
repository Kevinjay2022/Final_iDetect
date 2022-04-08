package com.example.idetect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.UUID;

public class RegistrationAutoParts extends AppCompatActivity {
    Button regAtPrtsBtn;


    private FirebaseAuth mAuth;
    FirebaseUser firebaseUser;

    ProgressDialog progressDialog, progressDialog2;

    EditText regNmeShp, regEmlShp, regPhneShp, regPassShp, regAddrssShp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_auto_parts);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering");
        progressDialog2 = new ProgressDialog(this);
        progressDialog2.setMessage("Logging in...");

        ref();

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        regAtPrtsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = regEmlShp.getText().toString().trim();
                String password = regPassShp.getText().toString().trim();
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    regEmlShp.setError("Invalid email");
                    regEmlShp.setFocusable(true);
                }
                else {
                    registerUser(email, password);
                }
            }
        });
    }

    private void registerUser(String email, String password) {
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    firebaseUser = mAuth.getCurrentUser();
                    String email = firebaseUser.getEmail();
                    String uid = firebaseUser.getUid();
                    String uniqueID = UUID.randomUUID().toString();

                    HashMap<Object, String> hashMap = new HashMap<>();
                    hashMap.put("name", regNmeShp.getText().toString().trim());
                    hashMap.put("email", email);
                    hashMap.put("phonenum", regPhneShp.getText().toString().trim());
                    hashMap.put("password", regPassShp.getText().toString().trim());
                    hashMap.put("address", regAddrssShp.getText().toString().trim());
                    hashMap.put("uid", uid);
                    hashMap.put("uniqeid", uniqueID);
                    hashMap.put("acctype", "auto_parts");

                    FirebaseDatabase database = FirebaseDatabase.getInstance();

                    DatabaseReference reference = database.getReference("USERS");
                    reference.child(uid).setValue(hashMap);

                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(RegistrationAutoParts.this, "Register Successful!...\n" + firebaseUser.getEmail(), Toast.LENGTH_SHORT).show();
                    finish();


                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(RegistrationAutoParts.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void ref() {
        regNmeShp = findViewById(R.id.regNameAutPrtsEdtTxt);
        regEmlShp = findViewById(R.id.regMechEmailEdtTxt);
        regPhneShp = findViewById(R.id.regMechPhoneEdtTxt);
        regPassShp = findViewById(R.id.regMechPassEdtTxttTxt);
        regAddrssShp = findViewById(R.id.regMechAddressEdtTxt);
        regAtPrtsBtn = findViewById(R.id.regMechBTN);
    }
}
