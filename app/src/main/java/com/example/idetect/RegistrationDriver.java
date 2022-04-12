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
import android.widget.TextView;
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
    TextView loginBtn;

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

        regDrvrDtls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = drvrEmlAdd.getText().toString().trim();
                String password = drvrPass.getText().toString().trim();
                int radioID = drvrGnder.getCheckedRadioButtonId();
                if(!validateEmail() | !validateFname() | !validateLname() | !validatePass() | !validatePhone() | !validateAddress() | !validateGender(radioID)) {
                    return;
                } else {
                    registerUser(email, password);
                }
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistrationDriver.this, Login.class));
                finish();
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
    private Boolean validateGender(int radioID){
        if(radioID == -1){
            Toast.makeText(this, "Please select your gender", Toast.LENGTH_LONG).show();
            return false;
        }else{
            return true;
        }
    }
    private Boolean validateEmail() {
        String cPass = drvrEmlAdd.getText().toString().trim();

        if (cPass.isEmpty()) {
            drvrEmlAdd.setError("Field cannot be empty");
            return false;
        } else if(!Patterns.EMAIL_ADDRESS.matcher(cPass).matches()){
            drvrEmlAdd.setError("Invalid email");
            drvrEmlAdd.setFocusable(true);
            return true;
        } else {
            drvrEmlAdd.setError(null);
            drvrEmlAdd.setFocusable(true);
            return true;
        }
    }
    private Boolean validateFname() {
        String cPass = drvrFName.getText().toString().trim();

        if (cPass.isEmpty()) {
            drvrFName.setError("Field cannot be empty");
            return false;
        } else {
            drvrFName.setError(null);
            drvrFName.setFocusable(true);
            return true;
        }
    }
    private Boolean validateLname() {
        String cPass = drvrLName.getText().toString().trim();

        if (cPass.isEmpty()) {
            drvrLName.setError("Field cannot be empty");
            return false;
        } else {
            drvrLName.setError(null);
            drvrLName.setFocusable(true);
            return true;
        }
    }
    private Boolean validatePhone() {
        String cPass = drvrPhneNum.getText().toString().trim();

        if (cPass.isEmpty()) {
            drvrPhneNum.setError("Field cannot be empty");
            return false;
        } else {
            drvrPhneNum.setError(null);
            drvrPhneNum.setFocusable(true);
            return true;
        }
    }
    private Boolean validatePass() {
        String cPass = drvrPass.getText().toString().trim();

        if (cPass.isEmpty()) {
            drvrPass.setError("Field cannot be empty");
            return false;
        } else {
            drvrPass.setError(null);
            drvrPass.setFocusable(true);
            return true;
        }
    }
    private Boolean validateAddress() {
        String cPass = drvrAddrss.getText().toString().trim();

        if (cPass.isEmpty()) {
            drvrAddrss.setError("Field cannot be empty");
            return false;
        } else {
            drvrAddrss.setError(null);
            drvrAddrss.setFocusable(true);
            return true;
        }
    }
    private void ref() {

        loginBtn = findViewById(R.id.loginDrvrTxtView);
        drvrFName = findViewById(R.id.regMechNameEdtTxt);
        drvrLName = findViewById(R.id.regMechLastNameEdtTxt);
        drvrEmlAdd = findViewById(R.id.regMechEmailEdtTxt);
        drvrPhneNum = findViewById(R.id.regMechPhoneEdtTxt);
        drvrPass = findViewById(R.id.regMechPassEdtTxttTxt);
        drvrAddrss = findViewById(R.id.regMechAddressEdtTxt);
        drvrGnder = findViewById(R.id.regDrvrGenderRadioGroup);
        regDrvrDtls = findViewById(R.id.regMechBTN);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
