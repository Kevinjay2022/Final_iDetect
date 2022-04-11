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

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

public class RegistrationDriver extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseUser firebaseUser;

    RadioGroup drvrGnder;
    RadioButton addRadioBtn;
    EditText drvrFName, drvrLName, drvrEmlAdd, drvrPhneNum, drvrPass, drvrAddrss;
    Button regDrvrDtls;

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

                    Date d = new Date(System.currentTimeMillis());
                    Calendar c = Calendar.getInstance();
                    c.setTime(d);
                    String month = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
                    String monthReg = month.substring(0, 3);

                    int radioId = drvrGnder.getCheckedRadioButtonId();
                    addRadioBtn = findViewById(radioId);

                    HashMap<Object, String> hashMap = new HashMap<>();
                    hashMap.put("firstname", drvrFName.getText().toString().trim());
                    hashMap.put("lastname", drvrLName.getText().toString().trim());
                    hashMap.put("email", email);
                    hashMap.put("phonenum", drvrPhneNum.getText().toString().trim());
                    hashMap.put("password", drvrPass.getText().toString().trim());
                    hashMap.put("address", drvrAddrss.getText().toString().trim());
                    hashMap.put("gender", addRadioBtn.getText().toString().trim());
                    hashMap.put("uid", uid);
                    hashMap.put("status", "offline");
                    hashMap.put("monthReg", monthReg);
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

    private void ref() {

        drvrFName = findViewById(R.id.regMechNameEdtTxt);
        drvrLName = findViewById(R.id.regMechLastNameEdtTxt);
        drvrEmlAdd = findViewById(R.id.regMechEmailEdtTxt);
        drvrPhneNum = findViewById(R.id.regMechPhoneEdtTxt);
        drvrPass = findViewById(R.id.regMechPassEdtTxttTxt);
        drvrAddrss = findViewById(R.id.regMechAddressEdtTxt);
        drvrGnder = findViewById(R.id.regDrvrGenderRadioGroup);
        regDrvrDtls = findViewById(R.id.regMechBTN);
    }
}
