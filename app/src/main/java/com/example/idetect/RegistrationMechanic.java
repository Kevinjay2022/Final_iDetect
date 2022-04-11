package com.example.idetect;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

public class RegistrationMechanic extends AppCompatActivity {

    EditText regMchFNme, regMchLNme, regMchEml, regMchPhne, regMchPass, regMchAddrss;
    Button regMchDtls;
    RadioGroup regMchGndr;
    RadioButton addRadioBtn;

    private FirebaseAuth mAuth;
    FirebaseUser firebaseUser;

    ProgressDialog progressDialog, progressDialog2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_mechanic);

        ref();

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering");
        progressDialog2 = new ProgressDialog(this);
        progressDialog2.setMessage("Logging in...");

        regMchDtls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = regMchEml.getText().toString().trim();
                String password = regMchPass.getText().toString().trim();
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    regMchEml.setError("Invalid email");
                    regMchEml.setFocusable(true);
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

                    int radioId = regMchGndr.getCheckedRadioButtonId();
                    addRadioBtn = findViewById(radioId);

                    Date d = new Date(System.currentTimeMillis());
                    Calendar c = Calendar.getInstance();
                    c.setTime(d);
                    String month = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
                    String monthReg = month.substring(0, 3);

                    HashMap<Object, String> hashMap = new HashMap<>();
                    hashMap.put("firstname", regMchFNme.getText().toString().trim());
                    hashMap.put("lastname", regMchLNme.getText().toString().trim());
                    hashMap.put("email", email);
                    hashMap.put("phonenum", regMchPhne.getText().toString().trim());
                    hashMap.put("password", regMchPass.getText().toString().trim());
                    hashMap.put("address", regMchAddrss.getText().toString().trim());
                    hashMap.put("gender", addRadioBtn.getText().toString().trim());
                    hashMap.put("uid", uid);
                    hashMap.put("rate", "0");
                    hashMap.put("status", "offline");
                    hashMap.put("monthReg", monthReg);
                    hashMap.put("acctype", "mechanic");

                    FirebaseDatabase database = FirebaseDatabase.getInstance();

                    DatabaseReference reference = database.getReference().child("USERS");
                    reference.child(uid).setValue(hashMap);

                    FirebaseAuth.getInstance().signOut();

                    Toast.makeText(RegistrationMechanic.this, "Register Successful!...\n" + firebaseUser.getEmail(), Toast.LENGTH_SHORT).show();
                    finish();


                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(RegistrationMechanic.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void ref() {

        regMchFNme = findViewById(R.id.regMechNameEdtTxt);
        regMchLNme = findViewById(R.id.regMechLastNameEdtTxt);
        regMchEml = findViewById(R.id.regMechEmailEdtTxt);
        regMchPhne = findViewById(R.id.regMechPhoneEdtTxt);
        regMchPass = findViewById(R.id.regMechPassEdtTxttTxt);
        regMchAddrss = findViewById(R.id.regMechAddressEdtTxt);
        regMchGndr = findViewById(R.id.regMechGenderRBGrp);
        regMchDtls = findViewById(R.id.regMechBTN);

    }
}
