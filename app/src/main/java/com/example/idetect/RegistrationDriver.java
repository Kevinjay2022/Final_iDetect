package com.example.idetect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class RegistrationDriver extends AppCompatActivity {
    private static final int RC_SIGN_IN = 100;

    GoogleSignInClient mGoogleSignInClient;

    private FirebaseAuth mAuth;
    FirebaseUser firebaseUser;

    RadioGroup drvrGnder;
    RadioButton addRadioBtn;
    EditText drvrFName, drvrLName, drvrEmlAdd, drvrPhneNum, drvrPass, drvrAddrss, drvrBrthDte;
    Button regDrvrDtls;
    CardView logGmailBtn;

    ProgressDialog progressDialog, progressDialog2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_driver);

        ref();

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering");
        progressDialog2 = new ProgressDialog(this);
        progressDialog2.setMessage("Logging in...");

        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        logGmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        regDrvrDtls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = drvrEmlAdd.getText().toString().trim();
                String password = drvrPass.getText().toString().trim();
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    drvrEmlAdd.setError("Invalid email");
                    drvrEmlAdd.setFocusable(true);
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

                    int radioId = drvrGnder.getCheckedRadioButtonId();
                    addRadioBtn = findViewById(radioId);

                    HashMap<Object, String> hashMap = new HashMap<>();
                    hashMap.put("firstname", drvrFName.getText().toString().trim());
                    hashMap.put("lastname", drvrLName.getText().toString().trim());
                    hashMap.put("email", email);
                    hashMap.put("phonenum", drvrPhneNum.getText().toString().trim());
                    hashMap.put("password", drvrPass.getText().toString().trim());
                    hashMap.put("address", drvrAddrss.getText().toString().trim());
                    hashMap.put("birthdate", drvrBrthDte.getText().toString().trim());
                    hashMap.put("gender", addRadioBtn.getText().toString().trim());
                    hashMap.put("uid", uid);
                    hashMap.put("uniqeid", uniqueID);
                    hashMap.put("acctype", "driver");

                    FirebaseDatabase database = FirebaseDatabase.getInstance();

                    DatabaseReference reference = database.getReference("USERS");
                    reference.child(uid).setValue(hashMap);

                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(RegistrationDriver.this, "Register Successful!...\n" + firebaseUser.getEmail(), Toast.LENGTH_SHORT).show();
                    finish();


                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(RegistrationDriver.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        progressDialog2.show();
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
                progressDialog2.dismiss();
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
                            progressDialog2.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegistrationDriver.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                            progressDialog2.dismiss();
                            finish();
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        Intent intent = new Intent(RegistrationDriver.this, FragmentServiceCenterNavigation.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void ref() {

        drvrFName = findViewById(R.id.regMechNameEdtTxt);
        drvrLName = findViewById(R.id.regMechLastNameEdtTxt);
        drvrEmlAdd = findViewById(R.id.regMechEmailEdtTxt);
        drvrPhneNum = findViewById(R.id.regMechPhoneEdtTxt);
        drvrPass = findViewById(R.id.regMechPassEdtTxttTxt);
        drvrAddrss = findViewById(R.id.regMechAddressEdtTxt);
        drvrBrthDte = findViewById(R.id.regMechBirthEdtTxt);
        drvrGnder = findViewById(R.id.regDrvrGenderRadioGroup);
        logGmailBtn = findViewById(R.id.regMechGoogleLoginCV);
        regDrvrDtls = findViewById(R.id.regMechBTN);
    }
}
