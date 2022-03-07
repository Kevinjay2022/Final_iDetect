package com.example.idetect;

import static android.app.Activity.RESULT_OK;
import static com.google.firebase.storage.FirebaseStorage.getInstance;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;
import java.util.WeakHashMap;


public class Profile extends Fragment {
    StorageReference storageReference;
    String storagePath = "users_Profile_Imgs/";

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_STORAGE_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;

    String cameraPermission[];
    String storagePermission[];
    String profilePic;
    String uid;

    Uri image_uri;

    private FrameLayout mainProfile, subscriptions, updateProfile;
    private Button updateProfBTN, subscriptionBTN, subsBackBTN, updateBackBTN, editProfPic, profUpdteBtn, updateSubsBTN;
    Button logoutProf;
    ImageView profPic, updteProfIV;
    TextView servID;
    CardView freeSubs;
    private EditText updateName, updateAddress, updateEmail, updateContact, updateDteofBrth, upFirstN, upLastN;
    TextView profName, profAddrss, profEml, profCntct, profDOB;
    private LinearLayout FnameLName, WName;

    private FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference, dbRef;

    GoogleSignInClient mGoogleSignInClient;
    ProgressDialog progressDialog;
    public Profile(){
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View profileView = inflater.inflate(R.layout.profile, container, false);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Updating...");

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("USERS");
        storageReference = getInstance().getReference();
        uid = firebaseUser.getUid();
        dbRef = firebaseDatabase.getReference();

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        editProfPic = profileView.findViewById(R.id.editProfBtn);
        profName = profileView.findViewById(R.id.profile_name_ID);
        profAddrss = profileView.findViewById(R.id.profile_address);
        profEml = profileView.findViewById(R.id.profile_email);
        profCntct = profileView.findViewById(R.id.profile_contact);
        profDOB = profileView.findViewById(R.id.profile_DOB);
        profPic = profileView.findViewById(R.id.fragProfPic);
        updteProfIV = profileView.findViewById(R.id.UpdateProfile_logo);
        profUpdteBtn = profileView.findViewById(R.id.profSaveUpdateBTN);
        servID = profileView.findViewById(R.id.profServID);
        FnameLName = profileView.findViewById(R.id.FnameLName);
        WName = profileView.findViewById(R.id.nameDetailsProf);
        upFirstN = profileView.findViewById(R.id.UpdateProfNameTBFN);
        upLastN = profileView.findViewById(R.id.UpdateProfNameTBLN);

        Query query = databaseReference.orderByChild("email").equalTo(firebaseUser.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    String accType = ds.child("acctype").getValue(String.class);
                    //get data
                    String name = "" + ds.child("name").getValue();
                    String email = "" + ds.child("email").getValue();
                    String contact = "" + ds.child("phonenum").getValue();
                    String address = "" + ds.child("address").getValue();
                    String dateOfBrth = "" + ds.child("birthdate").getValue();
                    String frstName = "" + ds.child("firstname").getValue();
                    String lastName = "" + ds.child("lastname").getValue();
                    String image = "" + ds.child("image").getValue();

                    //set data
                    if(accType.equals("driver")){
                        FnameLName.setVisibility(View.VISIBLE);
                        subscriptionBTN.setVisibility(View.GONE);

                        profName.setText(frstName + " " + lastName);
                        profEml.setText(email);
                        profCntct.setText(contact);
                        profAddrss.setText(address);
                        profDOB.setText(dateOfBrth);
                        servID.setText(uid);
                        //Set data for update layout
                        WName.setVisibility(View.GONE);
                        upFirstN.setText(frstName);
                        upLastN.setText(lastName);
                        updateAddress.setText(address);
                        updateEmail.setText(email);
                        updateContact.setText(contact);

                        //if image is received then set
                        try {
                            Picasso.get().load(image).into(profPic);
                            Picasso.get().load(image).into(updteProfIV);
                        }
                        catch (Exception e){
                            Picasso.get().load(R.drawable.mechanics_logo_nav).into(profPic);
                            Picasso.get().load(R.drawable.mechanics_logo_nav).into(updteProfIV);
                        }
                    }
                    else if(accType.equals("serve_center")){
                        subscriptionBTN.setVisibility(View.VISIBLE);
                        FnameLName.setVisibility(View.GONE);
                        profName.setText(name);
                        profEml.setText(email);
                        profCntct.setText(contact);
                        profAddrss.setText(address);
                        servID.setText(uid);
                        //Set data for update layout
                        updateName.setText(name);
                        updateAddress.setText(address);
                        updateEmail.setText(email);
                        updateContact.setText(contact);
                        try {
                            Picasso.get().load(image).into(profPic);
                            Picasso.get().load(image).into(updteProfIV);
                        }
                        catch (Exception e){
                            Picasso.get().load(R.drawable.mechanics_logo_nav).into(profPic);
                            Picasso.get().load(R.drawable.mechanics_logo_nav).into(updteProfIV);
                        }
                    }
                    else if(accType.equals("auto_parts")){
                        subscriptionBTN.setVisibility(View.GONE);
                        FnameLName.setVisibility(View.GONE);
                        profName.setText(name);
                        profEml.setText(email);
                        profCntct.setText(contact);
                        profAddrss.setText(address);
                        servID.setText(uid);
                        //Set data for update layout
                        updateName.setText(name);
                        updateAddress.setText(address);
                        updateEmail.setText(email);
                        updateContact.setText(contact);
                        try {
                            Picasso.get().load(image).into(profPic);
                            Picasso.get().load(image).into(updteProfIV);
                        }
                        catch (Exception e){
                            Picasso.get().load(R.drawable.mechanics_logo_nav).into(profPic);
                            Picasso.get().load(R.drawable.mechanics_logo_nav).into(updteProfIV);
                        }
                    }
                    else if(accType.equals("mechanic")){
                        subscriptionBTN.setVisibility(View.GONE);
                        FnameLName.setVisibility(View.VISIBLE);

                        profName.setText(frstName + " " + lastName);
                        profEml.setText(email);
                        profCntct.setText(contact);
                        profAddrss.setText(address);
                        profDOB.setText(dateOfBrth);
                        servID.setText(uid);
                        //Set data for update layout
                        WName.setVisibility(View.GONE);
                        upFirstN.setText(frstName);
                        upLastN.setText(lastName);
                        updateAddress.setText(address);
                        updateEmail.setText(email);
                        updateContact.setText(contact);
                        //if image is received then set
                        try {
                            Picasso.get().load(image).into(profPic);
                            Picasso.get().load(image).into(updteProfIV);
                        }
                        catch (Exception e){
                            Picasso.get().load(R.drawable.mechanics_logo_nav).into(profPic);
                            Picasso.get().load(R.drawable.mechanics_logo_nav).into(updteProfIV);
                        }
                    }
                    else {
                        Toast.makeText(getActivity(), "Wrong Account", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        editProfPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profilePic = "image";
                String options [] = {"Camera", "Gallery"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Pick from");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            //CAMERA
                            if(!checkCameraPermission()){
                                requestCameraPermission();
                            }
                            else {
                                pickFromCamera();
                            }
                        }
                        else if(which == 1){
                            if(!checkStoragePermission()){
                                requestStoragePermission();
                            }
                            else {
                                pickFromStorage();
                            }
                        }
                    }
                });
                builder.create().show();
            }
        });

        updateName = profileView.findViewById(R.id.UpdateProfNameTB);
        updateAddress = profileView.findViewById(R.id.UpdateProfAddressTB);
        updateEmail = profileView.findViewById(R.id.UpdateProfEmailTB);
        updateContact = profileView.findViewById(R.id.UpdateProfContactTB);
        updateDteofBrth = profileView.findViewById(R.id.UpdateProfDAOBTB);

        mainProfile = profileView.findViewById(R.id.fragmentProfileLayout);
        subscriptions = profileView.findViewById(R.id.subscriptionLayout);
        updateProfile = profileView.findViewById(R.id.updateProfileLayout);

        updateProfBTN = profileView.findViewById(R.id.profileUpdateBTN);
        updateSubsBTN = profileView.findViewById(R.id.update_subscriptionBTN);
        freeSubs = profileView.findViewById(R.id.free_subscription);

        freeSubs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateSubsBTN.setVisibility(View.VISIBLE);
                long cutoff = new Date().getTime();
                HashMap<String, Object> result = new HashMap<>();
                result.put("subscription","28_Days");
                result.put("sub_status", "active");
                result.put("timestamps", cutoff);

                updateSubsBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        /*Date d = new Date(cutoff);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        String old_date = dateFormat.format(new Date(cutoff));*/

                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                databaseReference.child(firebaseUser.getUid()).updateChildren(result);
                                Toast.makeText(getActivity(), "Updated", Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        /*Query oldBug = dbRef.child(firebaseUser.getUid()).orderByChild("timestamp").endAt(cutoff);
                        oldBug.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()){
                                    ds.getRef().removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });*/

                    }
                });

            }
        });


        profUpdteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String accType = ds.child("acctype").getValue(String.class);
                            if(accType.equals("driver")) {
                                HashMap<String, Object> result = new HashMap<>();
                                result.put("firstname", updateName.getText().toString().trim());
                                result.put("address", updateAddress.getText().toString().trim());
                                result.put("phonenum", updateContact.getText().toString().trim());
                                result.put("birthdate", updateDteofBrth.getText().toString().trim());

                                databaseReference.child(firebaseUser.getUid()).updateChildren(result).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        progressDialog.dismiss();
                                        startActivity(new Intent(getActivity(), Profile.class));
                                        Toast.makeText(getActivity(), "Updated", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else if(accType.equals("serve_center")){
                                HashMap<String, Object> result = new HashMap<>();
                                result.put("name", updateName.getText().toString().trim());
                                result.put("address", updateAddress.getText().toString().trim());
                                result.put("phonenum", updateContact.getText().toString().trim());

                                databaseReference.child(firebaseUser.getUid()).updateChildren(result).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        progressDialog.dismiss();

                                        Toast.makeText(getActivity(), "Updated", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else if(accType.equals("auto_parts")){
                                HashMap<String, Object> result = new HashMap<>();
                                result.put("name", updateName.getText().toString().trim());
                                result.put("address", updateAddress.getText().toString().trim());
                                result.put("phonenum", updateContact.getText().toString().trim());

                                databaseReference.child(firebaseUser.getUid()).updateChildren(result).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        progressDialog.dismiss();

                                        Toast.makeText(getActivity(), "Updated", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else if(accType.equals("mechanic")) {
                                HashMap<String, Object> result = new HashMap<>();
                                result.put("firstname", updateName.getText().toString().trim());
                                result.put("address", updateAddress.getText().toString().trim());
                                result.put("phonenum", updateContact.getText().toString().trim());
                                result.put("birthdate", updateDteofBrth.getText().toString().trim());

                                databaseReference.child(firebaseUser.getUid()).updateChildren(result).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        progressDialog.dismiss();
                                        startActivity(new Intent(getActivity(), Profile.class));
                                        Toast.makeText(getActivity(), "Updated", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else {
                                Toast.makeText(getActivity(), "Wrong Account", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                            updateProfile.setVisibility(View.GONE);
                            subscriptions.setVisibility(View.GONE);
                            mainProfile.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        updateProfBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile.setVisibility(View.VISIBLE);
                subscriptions.setVisibility(View.GONE);
                mainProfile.setVisibility(View.GONE);
            }
        });

        subscriptionBTN = profileView.findViewById(R.id.profileSubsBTN);
        subscriptionBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile.setVisibility(View.GONE);
                subscriptions.setVisibility(View.VISIBLE);
                mainProfile.setVisibility(View.GONE);
            }
        });

        subsBackBTN = profileView.findViewById(R.id.back_BTNSubscription);
        subsBackBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile.setVisibility(View.GONE);
                subscriptions.setVisibility(View.GONE);
                mainProfile.setVisibility(View.VISIBLE);
            }
        });

        updateBackBTN = profileView.findViewById(R.id.updateBackButton);
        updateBackBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile.setVisibility(View.GONE);
                subscriptions.setVisibility(View.GONE);
                mainProfile.setVisibility(View.VISIBLE);
            }
        });
        logoutProf = profileView.findViewById(R.id.profileLogoutBTN);
        logoutProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), Login.class));
                getActivity().finish();
            }
        });
        return profileView;
    }

    boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    void requestStoragePermission(){
        ActivityCompat.requestPermissions(getActivity(), storagePermission, STORAGE_REQUEST_CODE);
    }

    boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    void requestCameraPermission(){
        ActivityCompat.requestPermissions(getActivity(), cameraPermission, CAMERA_REQUEST_CODE);
    }
    void pickFromCamera(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Tmp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Tmp description");
        //put image uri
        image_uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    void pickFromStorage() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_STORAGE_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length > 0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted){
                        pickFromCamera();
                    }
                    else {
                        Toast.makeText(getActivity(), "Please enable camera and storage permission", Toast.LENGTH_SHORT).show();
                    }

                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length > 0){
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted){
                        pickFromStorage();
                    }
                    else {
                        Toast.makeText(getActivity(), "Please enable storage permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)  {
        if (resultCode == RESULT_OK){
            if (requestCode== IMAGE_PICK_STORAGE_CODE){
                image_uri = data.getData();
                uploadProfilePhoto(image_uri);
            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE){
                uploadProfilePhoto(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfilePhoto(Uri uri) {
        progressDialog.show();
        String filePathAndName = storagePath + "" + profilePic + "_" +  firebaseUser.getUid();

        StorageReference storageReference2nd = storageReference.child(filePathAndName);
        storageReference2nd.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                final Uri downloadUri = uriTask.getResult();
                //check if image is uploaded or not and url is received
                if(uriTask.isSuccessful()){
                    //image uploaded
                    //add/update url is user's data
                    HashMap<String, Object> results = new HashMap<>();
                    results.put(profilePic, downloadUri.toString());

                    databaseReference.child(firebaseUser.getUid()).updateChildren(results).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Image Updated .....", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Error Updating ...", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                else {
                    //error
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Some error occured", Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}