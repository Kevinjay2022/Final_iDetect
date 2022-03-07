package com.example.idetect;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.idetect.Adapters.ServCentCustHistAdapter;
import com.example.idetect.Models.ServCentCustHistModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class FragmentServiceCenterCustomers extends Fragment {

    String MainID;

    Button custAddBTN, AddCSBtn;
    LinearLayout mainLayOut, addcustLayout;
    EditText AddCName, AdCAddr ,AddCVehType,AddCVeMod;
    RecyclerView recyclerView;

    ServCentCustHistAdapter servCentCustHistAdapter;

    //Firebabes
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    public FragmentServiceCenterCustomers(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragServCst = inflater.inflate(R.layout.fragment_service_center_customers, container, false);

        custAddBTN = fragServCst.findViewById(R.id.customerAddBTN);

        mainLayOut = fragServCst.findViewById(R.id.customerMainLayout);
        addcustLayout = fragServCst.findViewById(R.id.addCustomerLayout);

        // add layout
        AddCSBtn = fragServCst.findViewById(R.id.AddCustomerSaveBTN);
        AddCName = fragServCst.findViewById(R.id.addCustomerNameTB);
        AdCAddr = fragServCst.findViewById(R.id.AddCustomerAddressTB);
        AddCVehType = fragServCst.findViewById(R.id.AddCustomerVehicleTypeTB);
        AddCVeMod = fragServCst.findViewById(R.id.AddCustomerVehicleModelTB);

        //init firebase
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("USERS");
        MainID = firebaseUser.getUid();
        //Save data to database
        AddCSBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(AddCName.getText().toString() == "" && AdCAddr.getText().toString() == "" && AddCVehType.getText().toString() == "" && AddCVeMod.getText().toString() == ""){
                    Toast.makeText(getActivity(), "Please Enter All Details", Toast.LENGTH_SHORT).show();
                }else{
                    HashMap<Object, String> Map = new HashMap<>();
                    Map.put("Customer_Name", AddCName.getText().toString());
                    Map.put("Customer_Address", AdCAddr.getText().toString());
                    Map.put("Vehicle_type", AddCVehType.getText().toString());
                    Map.put("Vehicle_Model", AddCVeMod.getText().toString());
                    Map.put("ID", MainID);
                    FirebaseDatabase.getInstance().getReference().child("SERVICE_CENT_CUSTOMERS").push().setValue(Map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getActivity(), "Added Successfully...", Toast.LENGTH_SHORT).show();
                            AddCName.setText("");AdCAddr.setText("");AddCVehType.setText("");AddCVeMod.setText("");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Error while add", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


        //For displaying the Customers in grid
        recyclerView = (RecyclerView)fragServCst.findViewById(R.id.CustomerRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        FirebaseRecyclerOptions<ServCentCustHistModel> options = new FirebaseRecyclerOptions.Builder<ServCentCustHistModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("SERVICE_CENT_CUSTOMERS").orderByChild("ID").equalTo(MainID), ServCentCustHistModel.class)
                .build();
        servCentCustHistAdapter = new ServCentCustHistAdapter(options);
        recyclerView.setAdapter(servCentCustHistAdapter);
        servCentCustHistAdapter.startListening();
        //End for displaying the Customers in grid




        custAddBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addcustLayout.setVisibility(View.VISIBLE);
                mainLayOut.setVisibility(View.GONE);
            }
        });

        return fragServCst;
    }
}