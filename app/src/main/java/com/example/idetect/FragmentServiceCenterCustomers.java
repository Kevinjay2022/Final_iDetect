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

import com.example.idetect.Adapters.DisplayServCentCustHistAdapter;
import com.example.idetect.Models.ServCentCustHistModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class FragmentServiceCenterCustomers extends Fragment {

    String MainID;
    ArrayList<ServCentCustHistModel> models;
    RecyclerView recyclerView;
    DisplayServCentCustHistAdapter servCentCustHistAdapter;

    //Firebabes
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    LinearLayout noNotif;

    public FragmentServiceCenterCustomers(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragServCst = inflater.inflate(R.layout.fragment_service_center_customers, container, false);

        models = new ArrayList<>();

        //init firebase
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("USERS");
        MainID = firebaseUser.getUid();

        //For displaying the Customers in grid
        noNotif = fragServCst.findViewById(R.id.noNotifications);
        recyclerView = (RecyclerView)fragServCst.findViewById(R.id.CustomerRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        FirebaseDatabase.getInstance().getReference().child("SERVICE_CENT_CUSTOMERS").orderByChild("shopID").equalTo(MainID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ServCentCustHistModel model = ds.getValue(ServCentCustHistModel.class);
                            models.add(model);
                        }
                        if (models.size() == 0){
                            noNotif.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }else {
                            servCentCustHistAdapter = new DisplayServCentCustHistAdapter(getActivity(), models);
                            recyclerView.setAdapter(servCentCustHistAdapter);
                            servCentCustHistAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        return fragServCst;
    }
}