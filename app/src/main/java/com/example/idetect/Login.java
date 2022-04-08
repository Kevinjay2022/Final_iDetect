package com.example.idetect;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.*;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Login extends AppCompatActivity {
    private static final int RC_SIGN_IN = 100;

    String usrName, usrPass;

    Button loginBtn;
    EditText edtTextUserName, edtTextPass;
    TextView signUpTxtVw, forgotPassword;

    private FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    DatabaseReference dbRef;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        ref();
        mAuth = FirebaseAuth.getInstance();
        //mAuth.signOut();
        firebaseUser = mAuth.getCurrentUser();
        dbRef = FirebaseDatabase.getInstance().getReference("USERS");

        progressDialog = new ProgressDialog(this);

        //NORMAL LOGIN
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Login....");
                usrName = edtTextUserName.getText().toString().trim();
                usrPass = edtTextPass.getText().toString().trim();

                if(!Patterns.EMAIL_ADDRESS.matcher(usrName).matches()){
                    edtTextUserName.setError("Invalid email");
                    edtTextUserName.setFocusable(true);
                }
                else {
                    loginUser();
                }
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRecoverPasswordDialog();
            }
        });

        //SIGN UP
        signUpTxtVw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, RegistrationOfFourUsers.class);
                startActivity(intent);
            }
        });

    }
    private void showRecoverPasswordDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");

        LinearLayout linearLayout = new LinearLayout(this);
        EditText emailEt = new EditText(this);
        emailEt.setHint("Email");
        emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailEt.setMinEms(16);
        linearLayout.addView(emailEt);
        linearLayout.setPadding(10, 10, 10, 10);
        builder.setView(linearLayout);

        builder.setPositiveButton("Recover", (dialogInterface, i) -> {
            String email = emailEt.getText().toString().trim();
            beginRecovery(email);
        });
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
        builder.create().show();

    }
    private void beginRecovery(String email) {
        progressDialog.setMessage("Sending email...");
        progressDialog.show();
        FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()){
                Toast.makeText(Login.this, "Email sent", Toast.LENGTH_LONG).show();
            }else
                Toast.makeText(Login.this, "Failed...", Toast.LENGTH_LONG).show();
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(Login.this, ""+e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }


    private void loginUser(){
        progressDialog.setMessage("Logging In");
        progressDialog.show();

        FirebaseAuth.getInstance().signInWithEmailAndPassword(usrName, usrPass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            dbRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                String entity = snapshot.child("acctype").getValue().toString();
                                                if (entity != null) {
                                                    switch (entity) {
                                                        case "serve_center":
                                                            progressDialog.dismiss();
                                                            startActivity(new Intent(Login.this, FragmentServiceCenterNavigation.class));
                                                            finish();
                                                            break;
                                                        case "driver":
                                                            progressDialog.dismiss();
                                                            startActivity(new Intent(Login.this, FragmentDriverNavigationHome.class));
                                                            finish();
                                                            break;
                                                        case "mechanic":
                                                            progressDialog.dismiss();
                                                            startActivity(new Intent(Login.this, FragmentMechanicHome.class));
                                                            finish();
                                                            break;
                                                        case "auto_parts":
                                                            progressDialog.dismiss();
                                                            startActivity(new Intent(Login.this, FragmentAutoPartsNavigation.class));
                                                            finish();
                                                            break;
                                                    }
                                                }
                                            } else {
                                                progressDialog.dismiss();
                                                Toast.makeText(Login.this, "Unapproved account", Toast.LENGTH_SHORT).show();
                                                FirebaseAuth.getInstance().signOut();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(Login.this, "Incorrect email/password", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(Login.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void ref(){
        forgotPassword = findViewById(R.id.forgotPassword);
        loginBtn = findViewById(R.id.login_button);
        edtTextUserName = findViewById(R.id.editTextTextUserName);
        edtTextPass = findViewById(R.id.editTextTextPassword);
        signUpTxtVw = findViewById(R.id.signupTextView);
    }

}