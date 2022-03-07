package com.example.idetect;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

import java.util.HashMap;

public class FragmentMechanicPosts extends Fragment {

    private Button AddBTN, EditBTN, AddSaveBTN;
    private LinearLayout createLayout, displayLayout;
    private TextView HeaderTxt, DetailsTV, AddressTV, upperTxt, SecHeadTxt;
    private EditText AddDetails, AddAddress;

    String MechID;

    //Firebabes
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    public FragmentMechanicPosts(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragMechPost = inflater.inflate(R.layout.fragment_mechanic_posts, container, false);

        AddBTN = fragMechPost.findViewById(R.id.Add_postBTN);
        EditBTN = fragMechPost.findViewById(R.id.Edit_postBTN);
        AddSaveBTN = fragMechPost.findViewById(R.id.createSaveDataBTN);
        createLayout = fragMechPost.findViewById(R.id.createPostLayout);
        displayLayout = fragMechPost.findViewById(R.id.displayPostLayout);
        upperTxt = fragMechPost.findViewById(R.id.mechanicNameUpper);
        HeaderTxt = fragMechPost.findViewById(R.id.create_mechanicNameCard);
        SecHeadTxt = fragMechPost.findViewById(R.id.mechanicNameCard);
        AddDetails = fragMechPost.findViewById(R.id.Create_mechanicDetailsInput);
        AddAddress = fragMechPost.findViewById(R.id.Create_mechanicAddressInput);
        DetailsTV = fragMechPost.findViewById(R.id.mechanicDetailsView);
        AddressTV = fragMechPost.findViewById(R.id.mechanicAddressDetailsView);


        //init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("USERS");
        MechID = firebaseUser.getUid();

        //Set data in details layout
        Query query = databaseReference.orderByChild("email").equalTo(firebaseUser.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    //get data
                    String posted = ds.child("post").getValue(String.class);
                    String dtails = "" + ds.child("Input_Details").getValue();
                    String adds = "" + ds.child("address").getValue();
                    String fname = "" + ds.child("firstname").getValue();
                    String lname = "" + ds.child("lastname").getValue();
                    //String addDet = "" + ds.child("Address_Details").getValue();

                    //set data
                    upperTxt.setText("Welcome " + "" + fname);
                    SecHeadTxt.setText(fname + " " + lname);
                    DetailsTV.setText(dtails);
                    AddressTV.setText(adds);
                    HeaderTxt.setText(fname + " " + lname);

                    //set data to edit text
                    AddDetails.setText("");
                    AddAddress.setText(adds);

                    if(posted.equals("no")){
                        EditBTN.setVisibility(View.GONE);
                        AddBTN.setVisibility(View.VISIBLE);
                    }else{
                        displayLayout.setVisibility(View.VISIBLE);
                        AddBTN.setVisibility(View.GONE);
                        EditBTN.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Add Details to the database
        AddSaveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            HashMap<String, Object> result = new HashMap<>();
                            result.put("Input_Details", AddDetails.getText().toString().trim());
                            result.put("Address_Details", AddAddress.getText().toString().trim());
                            result.put("post", "yes");

                            databaseReference.child(firebaseUser.getUid()).updateChildren(result).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getActivity(), "Created.", Toast.LENGTH_SHORT).show();
                                    AddBTN.setVisibility(View.GONE);
                                    createLayout.setVisibility(View.GONE);
                                    EditBTN.setVisibility(View.VISIBLE);
                                    displayLayout.setVisibility(View.VISIBLE);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        // Layouts
        AddBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createLayout.setVisibility(View.VISIBLE);
            }
        });

        EditBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayLayout.setVisibility(View.GONE);
                createLayout.setVisibility(View.VISIBLE);
            }
        });


        return fragMechPost;
    }
}