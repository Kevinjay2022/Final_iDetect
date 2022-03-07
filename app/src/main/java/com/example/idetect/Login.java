package com.example.idetect;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    GoogleSignInClient mGoogleSignInClient;

    Button loginBtn, googleLogBtn;
    EditText edtTextUserName, edtTextPass;
    TextView signUpTxtVw;

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

        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        //GOOGLE SIGN IN
        googleLogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Login....");

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        //NORMAL LOGIN
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        //SIGN UP
        signUpTxtVw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, RegistrationOfFourUsers.class);
                startActivity(intent);
            }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this,"" + e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                finish();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            progressDialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Login.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            finish();
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        Intent intent = new Intent(Login.this, FragmentServiceCenterNavigation.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void ref(){
        loginBtn = findViewById(R.id.login_button);
        googleLogBtn = findViewById(R.id.googlelogin_button);
        edtTextUserName = findViewById(R.id.editTextTextUserName);
        edtTextPass = findViewById(R.id.editTextTextPassword);
        signUpTxtVw = findViewById(R.id.signupTextView);
    }

}