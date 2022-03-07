package com.example.idetect;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

//import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
//import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class RegistrationServiceCenter extends AppCompatActivity {
    private static final int RC_SIGN_IN = 100;


    LocationManager lm;
    boolean gps_enabled = false;
    boolean network_enabled = false;
    //Map variable
    Button btnPicker, back;
    TextView login;
    RelativeLayout mapLayout;
    LinearLayout regLayout;
    String myLocation = "", mLat, mLng;
    SupportMapFragment mapFragment;
    private Marker currentMarker;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    LatLng currentLatLng;
    //CheckConnection connection;
    //GoogleSignInClient mGoogleSignInClient;

    private FirebaseAuth mAuth;
    FirebaseUser firebaseUser;

    Button regstrBtn, addressMap;
    CardView logGmailBtn;
    TextView addRss;
    EditText srvceCntr, emailAdd, phneNum, passWrd;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_service_center);
        ref();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.regMap);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        checkMyLocationEnabled(lm);
        /*GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);*/

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering...");

        addressMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!gps_enabled && !network_enabled){
                    buildAlertDialogMessage();
                }else {
                    mapLayout.setVisibility(View.VISIBLE);
                    regLayout.setVisibility(View.GONE);
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistrationServiceCenter.this, Login.class));
                finish();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regLayout.setVisibility(View.VISIBLE);
                mapLayout.setVisibility(View.GONE);
            }
        });
        btnPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapLayout.setVisibility(View.GONE);
                regLayout.setVisibility(View.VISIBLE);
                addRss.setText(""+myLocation);
            }
        });

        regstrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serviceCenter = srvceCntr.getText().toString().trim();
                String emailAddrss = emailAdd.getText().toString().trim();
                String phoneNum = phneNum.getText().toString().trim();
                String passWord = passWrd.getText().toString().trim();
                String addRess = addRss.getText().toString().trim();
                if (!Patterns.EMAIL_ADDRESS.matcher(emailAddrss).matches()) {
                    emailAdd.setError("Invalid Email");
                    emailAdd.setFocusable(true);
                } else {
                    registerDriver(serviceCenter, emailAddrss, phoneNum, passWord, addRess);
                }
            }
        });

        /*logGmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);*/


        /**
         //-------------------------------------------------------
         addressMap.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
        if(!connection.isConnected(RegistrationServiceCenter.this))
        registerNetworkBroadcast();
        else {
        //--------------
        }
        }


        });
         */
    }

    private void buildAlertDialogMessage() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void registerDriver(String serviceCenter, String emailAddrss, String phoneNum, String passWord, String addRess) {
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(emailAddrss, passWord).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    firebaseUser = mAuth.getCurrentUser();
                    //String reference fore data
                    String email = firebaseUser.getEmail();
                    String uid = firebaseUser.getUid();
                    HashMap<Object, String> hashMap = new HashMap<>();
                    hashMap.put("name", srvceCntr.getText().toString().trim());
                    hashMap.put("email", email);
                    hashMap.put("phonenum", phneNum.getText().toString().trim());
                    hashMap.put("password", passWord);
                    hashMap.put("address", addRss.getText().toString().trim());
                    hashMap.put("uid", uid);
                    hashMap.put("acctype", "serve_center");

                    HashMap<String, Object> hashMap2 = new HashMap<>();
                    hashMap2.put("longitude", mLng);
                    hashMap2.put("latitude", mLat);

                    FirebaseDatabase database = FirebaseDatabase.getInstance();

                    DatabaseReference reference = database.getReference("USERS");
                    reference.child(uid).setValue(hashMap);
                    DatabaseReference reference2 = database.getReference("LAT_LNG");
                    reference2.child(uid).setValue(hashMap2);

                    Toast.makeText(RegistrationServiceCenter.this, "Register Successful!...\n" + firebaseUser.getEmail(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RegistrationServiceCenter.this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();}
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        /**if (requestCode == RC_SIGN_IN) {
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
        }*/
    }

    /**private void firebaseAuthWithGoogle(String idToken) {
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
                            Toast.makeText(RegistrationServiceCenter.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            finish();
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        Intent intent = new Intent(RegistrationServiceCenter.this, FragmentServiceCenterNavigation.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }*/

    private void ref() {
        logGmailBtn = findViewById(R.id.regMechGoogleLoginCV);
        regstrBtn = findViewById(R.id.regMechBTN);
        srvceCntr = findViewById(R.id.regNameAutPrtsEdtTxt);
        emailAdd = findViewById(R.id.regMechEmailEdtTxt);
        phneNum = findViewById(R.id.regMechPhoneEdtTxt);
        passWrd = findViewById(R.id.regMechPassEdtTxttTxt);
        addRss = findViewById(R.id.srvcCntrAddress);
        addressMap = findViewById(R.id.OpenAddMapServ);
        btnPicker = findViewById(R.id.pick_address);
        back = findViewById(R.id.back_button);
        regLayout = findViewById(R.id.regLayout);
        mapLayout = findViewById(R.id.mapLayout);
        login = findViewById(R.id.loginDrvrTxtView);


    }

    private void checkMyLocationEnabled(LocationManager lm) {
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception exception) {
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception exception) {
        }

    }

    @Override
    protected void onResume() {
        checkMyLocationEnabled(lm);
        if (gps_enabled && network_enabled)
            fetchLocation();
        super.onResume();
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){

                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {
                            mMap = googleMap;
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                            currentLatLng = latLng;
                            List<Address> addresses = new ArrayList<>();
                            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                            try{
                                addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                            }catch (IOException e){
                                e.printStackTrace();
                            }
                            if (currentMarker != null){
                                currentMarker.remove();
                            }
                            if (addresses.size() > 0) {
                                Address address = addresses.get(0);
                                MarkerOptions options = new MarkerOptions().position(latLng)
                                        .title("Select location")
                                        .snippet(address.getAddressLine(0));
                                myLocation = address.getAddressLine(0);
                                mLat = String.valueOf(latLng.latitude);
                                mLng = String.valueOf(latLng.longitude);
                                currentMarker = mMap.addMarker(options);
                            }
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                            assert currentMarker != null;
                            currentMarker.showInfoWindow();

                            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                @Override
                                public void onMapClick(@NonNull LatLng latLng) {
                                    currentLatLng = latLng;
                                    List<Address> addresses = new ArrayList<>();
                                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                    try{
                                        addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                                    }catch (IOException e){
                                        e.printStackTrace();
                                    }
                                    if (currentMarker != null){
                                        currentMarker.remove();
                                    }
                                    if (addresses.size() > 0) {
                                        Address address = addresses.get(0);
                                        MarkerOptions options = new MarkerOptions().position(latLng)
                                                .title("You are here")
                                                .snippet(address.getAddressLine(0));
                                        myLocation = address.getAddressLine(0);
                                        mLat = String.valueOf(latLng.latitude);
                                        mLng = String.valueOf(latLng.longitude);
                                        currentMarker = mMap.addMarker(options);
                                    }
                                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                                    assert currentMarker != null;
                                    currentMarker.showInfoWindow();
                                }
                            });
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);

                        }
                    });
                }
            }
        });
    }
    // Code here

}