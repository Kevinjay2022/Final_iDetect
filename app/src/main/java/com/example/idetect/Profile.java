package com.example.idetect;

import static android.app.Activity.RESULT_OK;
import static com.google.firebase.storage.FirebaseStorage.getInstance;

import android.Manifest;
import android.annotation.SuppressLint;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;
import java.util.WeakHashMap;


public class Profile extends Fragment {
    StorageReference storageReference;
    String storagePath = "users_Profile_Imgs/";

    Uri imageUri;
    String profilePic;
    String uid;

    long due_date;

    private FrameLayout mainProfile, subscriptions, updateProfile;
    private Button updateProfBTN, subscriptionBTN, subsBackBTN, updateBackBTN, editProfPic, profUpdteBtn, updateSubsBTN;
    Button logoutProf;
    ImageView profPic, updteProfIV;
    TextView servID, freeTrial;
    private EditText updateName, updateAddress, updateContact, updateDteofBrth, upFirstN, upLastN;
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

        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        editProfPic = profileView.findViewById(R.id.editProfBtn);
        freeTrial = profileView.findViewById(R.id.freeTrial);
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

        Query query = databaseReference.child(firebaseUser.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String accType = snapshot.child("acctype").getValue(String.class);
                    //get data
                    String name = "" + snapshot.child("name").getValue();
                    String email = "" + snapshot.child("email").getValue();
                    String contact = "" + snapshot.child("phonenum").getValue();
                    String address = "" + snapshot.child("address").getValue();
                    String dateOfBrth = "" + snapshot.child("birthdate").getValue();
                    String frstName = "" + snapshot.child("firstname").getValue();
                    String lastName = "" + snapshot.child("lastname").getValue();
                    String image = "" + snapshot.child("image").getValue();

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
                    openFileChooser();
            }
        });

        updateName = profileView.findViewById(R.id.UpdateProfNameTB);
        updateAddress = profileView.findViewById(R.id.UpdateProfAddressTB);
        updateContact = profileView.findViewById(R.id.UpdateProfContactTB);
        updateDteofBrth = profileView.findViewById(R.id.UpdateProfDAOBTB);

        mainProfile = profileView.findViewById(R.id.fragmentProfileLayout);
        subscriptions = profileView.findViewById(R.id.subscriptionLayout);
        updateProfile = profileView.findViewById(R.id.updateProfileLayout);

        updateProfBTN = profileView.findViewById(R.id.profileUpdateBTN);
        updateSubsBTN = profileView.findViewById(R.id.update_subscriptionBTN);


        profUpdteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String acctype = snapshot.child("acctype").getValue(String.class);
                            if(imageUri == null){
                                assert acctype != null;
                                updateData(acctype);
                            }else
                                uploadProfilePhoto(acctype);

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
                checkStatus();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), Login.class));
                getActivity().finish();
            }
        });
        return profileView;
    }

    private void updateData(String acctype){

        HashMap<String, Object> result = new HashMap<>();
        switch (acctype) {
            case "driver":
            case "mechanic":
                result.put("firstname", upFirstN.getText().toString().trim());
                result.put("lastname", upLastN.getText().toString().trim());
                result.put("address", updateAddress.getText().toString().trim());
                result.put("phonenum", updateContact.getText().toString().trim());
                break;
            case "serve_center":
            case "auto_parts":
                result.put("name", updateName.getText().toString().trim());
                result.put("address", updateAddress.getText().toString().trim());
                result.put("phonenum", updateContact.getText().toString().trim());
                break;
            default:
                Toast.makeText(getActivity(), "Wrong Account", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                break;
        }

        databaseReference.child(firebaseUser.getUid()).updateChildren(result).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Updated", Toast.LENGTH_SHORT).show();
                updateProfile.setVisibility(View.GONE);
                subscriptions.setVisibility(View.GONE);
                mainProfile.setVisibility(View.VISIBLE);
                imageUri = null;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadProfilePhoto(String acctype) {
        String filePathAndName = storagePath + "" + profilePic + "_" +  firebaseUser.getUid();

        progressDialog.setMessage("Uploading...");
        progressDialog.show();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(filePathAndName).child(System.currentTimeMillis() + "");
        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String Struri = uri.toString();
                        HashMap<String, Object> result = new HashMap<>();
                        switch (acctype) {
                            case "driver":
                            case "mechanic":
                                result.put("firstname", upFirstN.getText().toString().trim());
                                result.put("lastname", upLastN.getText().toString().trim());
                                result.put("address", updateAddress.getText().toString().trim());
                                result.put("phonenum", updateContact.getText().toString().trim());
                                result.put("image", Struri);
                                break;
                            case "serve_center":
                            case "auto_parts":
                                result.put("name", updateName.getText().toString().trim());
                                result.put("address", updateAddress.getText().toString().trim());
                                result.put("phonenum", updateContact.getText().toString().trim());
                                result.put("image", Struri);
                                break;
                            default:
                                Toast.makeText(getActivity(), "Wrong Account", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                break;
                        }
                        databaseReference.child(firebaseUser.getUid()).updateChildren(result).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), "Updated", Toast.LENGTH_SHORT).show();
                                updateProfile.setVisibility(View.GONE);
                                subscriptions.setVisibility(View.GONE);
                                mainProfile.setVisibility(View.VISIBLE);
                                imageUri = null;
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progresPerce = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                progressDialog.setMessage("Uploading " + (int) progresPerce + " %");

            }
        });
    }
    private void checkStatus(){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", "offline");
        FirebaseDatabase.getInstance().getReference()
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .updateChildren(hashMap);
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(updteProfIV);
        }
    }
}