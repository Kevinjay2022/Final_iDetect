package com.example.idetect;

import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.idetect.Adapters.ServCentMechListAdapter;
import com.example.idetect.Adapters.ServCentMechOnCallAdapter;
import com.example.idetect.Models.ServCentMechListModel;
import com.example.idetect.Models.ServCentMechOnCallModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.UUID;

public class FragmentServiceCenterMechanics extends Fragment {

    String MainID;

    private Button addMechBTN, OnCallMechBTN, ViewMechBtn;
    private FrameLayout mainMechLayout, addMechLayout, onCallMechLayout;

    EditText addMechNameEdt, addMechAddrssEdt, addMechBrthEdt, addMechSkllsEdt;
    Button saveMechBtn;
    RadioButton addRadioBtn;
    RadioGroup addGrpRdioBtn;

    RecyclerView VListMech, OnCallView;
    ServCentMechListAdapter servCentMechListAdapter;
    ServCentMechOnCallAdapter oncallAdapter;

    //Firebabes
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mechanicView = inflater.inflate(R.layout.fragment_service_center_mechanics, container, false);

        addMechBTN = mechanicView.findViewById(R.id.add_mechanic_btn);
        OnCallMechBTN = mechanicView.findViewById(R.id.on_call_mechanics);
        ViewMechBtn = mechanicView.findViewById(R.id.view_mechanic_btn);
        mainMechLayout = mechanicView.findViewById(R.id.mainMechanicLayout);
        addMechLayout = mechanicView.findViewById(R.id.addMechanicLayout);
        onCallMechLayout = mechanicView.findViewById(R.id.onCallMechanicLayout);

        addMechNameEdt = mechanicView.findViewById(R.id.addMechanicNameTB);
        addMechAddrssEdt = mechanicView.findViewById(R.id.addMechanicAddressTB);
        addMechBrthEdt = mechanicView.findViewById(R.id.addMechanicBirthDateTB);
        addMechSkllsEdt = mechanicView.findViewById(R.id.addMechanicSkillsTB);
        addGrpRdioBtn = mechanicView.findViewById(R.id.addMechanicR_group_gender);
        saveMechBtn = mechanicView.findViewById(R.id.addMechanicSaveBTN);

        //init firebase
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("USERS");
        MainID = firebaseUser.getUid();

        saveMechBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String uniqueID = UUID.randomUUID().toString();

                int radioId = addGrpRdioBtn.getCheckedRadioButtonId();
                addRadioBtn = mechanicView.findViewById(radioId);

                HashMap<Object, String > hashMap = new HashMap<>();
                hashMap.put("name", addMechNameEdt.getText().toString());
                hashMap.put("address", addMechAddrssEdt.getText().toString());
                hashMap.put("birth", addMechBrthEdt.getText().toString());
                hashMap.put("skills", addMechSkllsEdt.getText().toString());
                hashMap.put("gender", addRadioBtn.getText().toString());
                hashMap.put("status", "active");
                hashMap.put("mech_type", "in-house");
                hashMap.put("ID", MainID);

                //FirebaseDatabase database = FirebaseDatabase.getInstance();
               // DatabaseReference databaseReference = database.getReference("SERVICE_CENT_MECHANICS");

                FirebaseDatabase.getInstance().getReference().child("SERVICE_CENT_MECHANICS").push().setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getActivity(), "Added Successfully...", Toast.LENGTH_SHORT).show();
                        addMechNameEdt.setText("");addMechAddrssEdt.setText("");addMechBrthEdt.setText("");addMechSkllsEdt.setText("");addRadioBtn.setChecked(false);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Error while add", Toast.LENGTH_SHORT).show();
                    }
                });

                //databaseReference.child(MainID).setValue(hashMap);
                //Toast.makeText(getActivity(), "Added Successfully...", Toast.LENGTH_SHORT).show();
            }
        });

        //For displaying the Mechanics
        VListMech = (RecyclerView)mechanicView.findViewById(R.id.mechanic_list_view_all);
        VListMech.setLayoutManager(new LinearLayoutManager(getActivity()));

        FirebaseRecyclerOptions<ServCentMechListModel> MechList = new FirebaseRecyclerOptions.Builder<ServCentMechListModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("SERVICE_CENT_MECHANICS").orderByChild("ID").equalTo(MainID), ServCentMechListModel.class)
                .build();
        servCentMechListAdapter = new ServCentMechListAdapter(MechList);
        VListMech.setAdapter(servCentMechListAdapter);
        servCentMechListAdapter.startListening();
        //End for displaying the Mechanics

        //For On-call Mechanics
        OnCallView = (RecyclerView) mechanicView.findViewById(R.id.listViewOnCallMechanic);
        OnCallView.setLayoutManager(new LinearLayoutManager(getActivity()));
        FirebaseRecyclerOptions<ServCentMechOnCallModel> OncallMechs = new FirebaseRecyclerOptions.Builder<ServCentMechOnCallModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("USERS").orderByChild("acctype").equalTo("mechanic"), ServCentMechOnCallModel.class)
                .build();
        oncallAdapter = new ServCentMechOnCallAdapter(OncallMechs);
        OnCallView.setAdapter(oncallAdapter);
        oncallAdapter.startListening();
        //End For On-call Mechanics

        addMechBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMechLayout.setVisibility(View.VISIBLE);
                mainMechLayout.setVisibility(View.GONE);
                onCallMechLayout.setVisibility(View.GONE);
                ViewMechBtn.setVisibility(View.VISIBLE);
            }
        });
        OnCallMechBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMechLayout.setVisibility(View.GONE);
                mainMechLayout.setVisibility(View.GONE);
                onCallMechLayout.setVisibility(View.VISIBLE);
                ViewMechBtn.setVisibility(View.VISIBLE);
            }
        });
        ViewMechBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewMechBtn.setVisibility(View.GONE);
                mainMechLayout.setVisibility(View.VISIBLE);
                addMechLayout.setVisibility(View.GONE);
                onCallMechLayout.setVisibility(View.GONE);
            }
        });

        return mechanicView;
    }
}