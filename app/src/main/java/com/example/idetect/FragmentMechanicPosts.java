package com.example.idetect;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.util.Calendar;
import java.util.HashMap;

public class  FragmentMechanicPosts extends Fragment {

    Uri imageUri;
    private Button AddBTN, EditBTN, AddSaveBTN, CancelBTN, SelectIMG;
    private LinearLayout createLayout, displayLayout;
    private TextView createAddress, dispAddress, createNameCard, dispNameCard, dispSkill, upperTxt, createBdate, dispBdate;
    private EditText createSkill;
    ImageView calBTN, createCert, dispCert;

    String MechID, mechName, mechAddress, key;

    //Firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private int mYear, mMonth, mDay;
    public FragmentMechanicPosts() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragMechPost = inflater.inflate(R.layout.fragment_mechanic_posts, container, false);

        AddBTN = fragMechPost.findViewById(R.id.Add_postBTN);
        EditBTN = fragMechPost.findViewById(R.id.Edit_postBTN);
        CancelBTN = fragMechPost.findViewById(R.id.cancel_postBTN);
        SelectIMG = fragMechPost.findViewById(R.id.createPostSelectImage);
        AddSaveBTN = fragMechPost.findViewById(R.id.createSaveDataBTN);
        createLayout = fragMechPost.findViewById(R.id.createPostLayout);
        displayLayout = fragMechPost.findViewById(R.id.displayPostLayout);
        upperTxt = fragMechPost.findViewById(R.id.mechanicNameUpper);
        createNameCard = fragMechPost.findViewById(R.id.create_mechanicNameCard);
        dispNameCard = fragMechPost.findViewById(R.id.displayMechanicNameCard);
        createSkill = fragMechPost.findViewById(R.id.Create_mechanicDetailsInput);
        createAddress = fragMechPost.findViewById(R.id.Create_mechanicAddressInput);
        dispSkill = fragMechPost.findViewById(R.id.displayMechanicDetailsView);
        dispAddress = fragMechPost.findViewById(R.id.displayMechanicAddressDetailsView);
        calBTN = fragMechPost.findViewById(R.id.calendarBtn);
        createBdate = fragMechPost.findViewById(R.id.createTxtBdate);
        dispBdate = fragMechPost.findViewById(R.id.displayTxtBdate);
        createCert = fragMechPost.findViewById(R.id.createCertificateImage);
        dispCert = fragMechPost.findViewById(R.id.displayCertImg);

        //init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("USERS");
        MechID = firebaseUser.getUid();

        databaseReference.child(MechID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    upperTxt.setText("Welcome "+snapshot.child("firstname").getValue().toString());
                    mechAddress = snapshot.child("address").getValue().toString();
                    mechName = snapshot.child("firstname").getValue().toString()+" "+snapshot.child("lastname").getValue().toString();
                    createAddress.setText(mechAddress);
                    dispAddress.setText(mechAddress);
                    createNameCard.setText(mechName);
                    dispNameCard.setText(mechName);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("MECHANIC_POST").orderByChild("mechID").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            EditBTN.setVisibility(View.VISIBLE);
                            displayLayout.setVisibility(View.VISIBLE);
                            for (DataSnapshot ds: snapshot.getChildren()){
                                dispSkill.setText(ds.child("skills").getValue().toString());
                                dispBdate.setText(ds.child("birth").getValue().toString());
                                key = ds.child("key").getValue().toString();
                                Picasso.get().load(Uri.parse(ds.child("certificate").getValue().toString())).into(dispCert);
                            }
                        }else{
                            AddBTN.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        calBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                createBdate.setText((monthOfYear + 1) + "-" + dayOfMonth + "-" +  year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        AddSaveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mechSkills() | !mechBdate() | !itemImg()) {
                    return;
                } else {
                    FirebaseDatabase.getInstance().getReference().child("MECHANIC_POST").orderByChild("mechID").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        updatePost();
                                    }else{
                                        createPost();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            }
        });

        // Layouts
        AddBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createLayout.setVisibility(View.VISIBLE);
                AddBTN.setVisibility(View.GONE);
                CancelBTN.setVisibility(View.VISIBLE);
            }
        });

        EditBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditBTN.setVisibility(View.GONE);
                CancelBTN.setVisibility(View.VISIBLE);
                displayLayout.setVisibility(View.GONE);
                createLayout.setVisibility(View.VISIBLE);
            }
        });

        CancelBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createLayout.setVisibility(View.GONE);
                FirebaseDatabase.getInstance().getReference().child("MECHANIC_POST").orderByChild("mechID").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    EditBTN.setVisibility(View.VISIBLE);
                                    CancelBTN.setVisibility(View.GONE);
                                    displayLayout.setVisibility(View.VISIBLE);
                                }else{
                                    AddBTN.setVisibility(View.VISIBLE);
                                    CancelBTN.setVisibility(View.GONE);
                                    displayLayout.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });

        SelectIMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        return fragMechPost;
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
            Picasso.get().load(imageUri).into(createCert);
        }
    }

    private boolean mechSkills() {
        String str = createSkill.getText().toString().trim();

        if (str.isEmpty()) {
            createSkill.setError("Field should not be empty.");
            return false;
        }
        return true;
    }
    private boolean mechBdate() {
        String str = createBdate.getText().toString().trim();

        if (str.isEmpty()) {
            createBdate.setError("Field should not be empty.");
            return false;
        }
        return true;
    }
    private boolean itemImg() {

        if (imageUri == null) {
            Toast.makeText(getActivity(), "Put an image for your item", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private void createPost(){
        ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setMessage("Uploading...");
        pd.show();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Item_Img").child(System.currentTimeMillis() + "");
        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String key = FirebaseDatabase.getInstance().getReference().child("ITEMS").push().getKey();
                        String Struri = uri.toString();
                        HashMap<Object, String> AshMap = new HashMap<>();
                        AshMap.put("skills", createSkill.getText().toString());
                        AshMap.put("birth", createBdate.getText().toString());
                        AshMap.put("certificate", Struri);
                        AshMap.put("key", key);
                        AshMap.put("rate", "0");
                        AshMap.put("status", "not hired");
                        AshMap.put("mechID", FirebaseAuth.getInstance().getCurrentUser().getUid());

                        FirebaseDatabase.getInstance().getReference().child("MECHANIC_POST").child(key).setValue(AshMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getActivity(), "Added Successfully...", Toast.LENGTH_SHORT).show();
                                        EditBTN.setVisibility(View.VISIBLE);
                                        createLayout.setVisibility(View.GONE);
                                        displayLayout.setVisibility(View.VISIBLE);
                                        dispBdate.setText(createBdate.getText().toString());
                                        Picasso.get().load(imageUri).into(dispCert);
                                        imageUri = null;
                                        pd.dismiss();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Error while add", Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                        });
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progresPerce = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                pd.setMessage("Uploading " + (int) progresPerce + " %");
            }
        });
    }
    private void updatePost(){
        ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setMessage("Uploading...");
        pd.show();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Item_Img").child(System.currentTimeMillis() + "");
        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String Struri = uri.toString();
                        HashMap<String, Object> AshMap = new HashMap<>();
                        AshMap.put("skills", createSkill.getText().toString());
                        AshMap.put("birth", createBdate.getText().toString());
                        AshMap.put("certificate", Struri);

                        FirebaseDatabase.getInstance().getReference().child("MECHANIC_POST").child(key).updateChildren(AshMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getActivity(), "Added Successfully...", Toast.LENGTH_SHORT).show();
                                        EditBTN.setVisibility(View.VISIBLE);
                                        createLayout.setVisibility(View.GONE);
                                        displayLayout.setVisibility(View.VISIBLE);
                                        dispBdate.setText(createBdate.getText().toString());
                                        Picasso.get().load(imageUri).into(dispCert);
                                        CancelBTN.setVisibility(View.GONE);
                                        createBdate.setText("");
                                        createSkill.setText("");
                                        Picasso.get().load(R.color.fui_transparent).into(createCert);
                                        imageUri = null;
                                        pd.dismiss();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Error while add", Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                        });
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progresPerce = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                pd.setMessage("Uploading " + (int) progresPerce + " %");
            }
        });
    }
}