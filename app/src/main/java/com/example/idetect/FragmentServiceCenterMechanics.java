package com.example.idetect;

import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.idetect.Adapters.DisplayMechOnCallAdapter;
import com.example.idetect.Adapters.DisplayServCentMechListAdapter;
import com.example.idetect.Adapters.DisplayStoreItemsAdapter;
import com.example.idetect.Models.ItemsModel;
import com.example.idetect.Models.ServCentMechListModel;
import com.example.idetect.Models.ServCentMechOnCallModel;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class FragmentServiceCenterMechanics extends Fragment {

    String MainID;

    ArrayList<ServCentMechListModel> MechList;
    ArrayList<ServCentMechOnCallModel> onMechList;
    private Button addMechBTN, OnCallMechBTN, ViewMechBtn;
    private FrameLayout mainMechLayout, addMechLayout, onCallMechLayout ;

    EditText addMechNameEdt, addMechAddrssEdt,addMechSkllsEdt;
    Button saveMechBtn;
    RadioButton addRadioBtn;
    RadioGroup addGrpRdioBtn;
    LinearLayout noNotif;
    TextView addMechBrthEdt;
    ImageView bdateBTN;

    androidx.appcompat.widget.SearchView searchView;
    RecyclerView VListMech, OnCallView;
    DisplayServCentMechListAdapter servCentMechListAdapter;
    DisplayMechOnCallAdapter oncallAdapter;

    //Firebabes
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private int mYear, mMonth, mDay;


    private void filter(String s) {
        List<ServCentMechOnCallModel> filtered = new ArrayList<>();
        filtered.clear();
        for (ServCentMechOnCallModel re : onMechList) {
            FirebaseDatabase.getInstance().getReference().child("USERS")
                    .child(re.getMechID())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                if (snapshot.child("address").getValue().toString().toLowerCase().contains(s.toLowerCase()))
                                    filtered.add(re);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

        }
        oncallAdapter = new DisplayMechOnCallAdapter(getActivity(), filtered);
        OnCallView.setAdapter(oncallAdapter);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mechanicView = inflater.inflate(R.layout.fragment_service_center_mechanics, container, false);

        MechList = new ArrayList<>();
        onMechList = new ArrayList<>();
        noNotif = mechanicView.findViewById(R.id.noNotifications);
        bdateBTN = mechanicView.findViewById(R.id.dateCalendar);
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

        searchView = mechanicView.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (s!=null)
                    filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s!=null)
                    filter(s);
                return false;
            }
        });


        saveMechBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mechName() | !mechBirth() | !mechAddress() | !mechSkills() | !mechGender(v))
                    return;
                else {
                    //String uniqueID = UUID.randomUUID().toString();
                    int radioId = addGrpRdioBtn.getCheckedRadioButtonId();
                    addRadioBtn = mechanicView.findViewById(radioId);
                    String key = FirebaseDatabase.getInstance().getReference().child("SERVICE_CENT_MECHANICS").push().getKey();
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("name", addMechNameEdt.getText().toString());
                    hashMap.put("address", addMechAddrssEdt.getText().toString());
                    hashMap.put("birth", addMechBrthEdt.getText().toString());
                    hashMap.put("skills", addMechSkllsEdt.getText().toString());
                    hashMap.put("gender", addRadioBtn.getText().toString());
                    hashMap.put("status", "active");
                    hashMap.put("mech_type", "in-house");
                    hashMap.put("mechID", "");
                    hashMap.put("key", key);
                    hashMap.put("rate", "0");
                    hashMap.put("delete", false);
                    hashMap.put("employ", "hire");
                    hashMap.put("ID", MainID);

                    FirebaseDatabase.getInstance().getReference().child("SERVICE_CENT_MECHANICS").child(key).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getActivity(), "Added Successfully...", Toast.LENGTH_SHORT).show();
                            addMechNameEdt.setText("");
                            addMechAddrssEdt.setText("");
                            addMechBrthEdt.setText("");
                            addMechSkllsEdt.setText("");
                            addRadioBtn.setChecked(false);
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

        //For displaying the Mechanics
        VListMech = (RecyclerView)mechanicView.findViewById(R.id.mechanic_list_view_all);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        VListMech.setLayoutManager(linearLayoutManager);
        FirebaseDatabase.getInstance().getReference().child("SERVICE_CENT_MECHANICS")
                .orderByChild("ID").equalTo(MainID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        MechList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ServCentMechListModel model = ds.getValue(ServCentMechListModel.class);
                            if (ds.child("delete").getValue().toString().equals("false"))
                                MechList.add(model);
                        }
                        if (MechList.size() == 0){
                            noNotif.setVisibility(View.VISIBLE);
                            VListMech.setVisibility(View.GONE);
                        }else {
                            servCentMechListAdapter = new DisplayServCentMechListAdapter(getActivity(), MechList);
                            VListMech.setAdapter(servCentMechListAdapter);
                            servCentMechListAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        //End for displaying the Mechanics

        //For On-call Mechanics
        OnCallView = (RecyclerView) mechanicView.findViewById(R.id.listViewOnCallMechanic);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        OnCallView.setLayoutManager(linearLayoutManager1);

        FirebaseDatabase.getInstance().getReference().child("MECHANIC_POST")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        onMechList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ServCentMechOnCallModel model = ds.getValue(ServCentMechOnCallModel.class);
                            if (model.getStatus().equals("not hired"))
                                onMechList.add(model);
                        }
                        sortByRatings();
                        oncallAdapter = new DisplayMechOnCallAdapter(getActivity(), onMechList);
                        OnCallView.setAdapter(oncallAdapter);
                        oncallAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


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

        FirebaseDatabase.getInstance().getReference().child("SERVICE_CENT_MECHANICS")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        MechList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ServCentMechListModel model = ds.getValue(ServCentMechListModel.class);
                            if (model.getID().equals(MainID) && !model.isDelete())
                                MechList.add(model);
                        }
                        servCentMechListAdapter = new DisplayServCentMechListAdapter(getActivity(), MechList);
                        VListMech.setAdapter(servCentMechListAdapter);
                        servCentMechListAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

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
        bdateBTN.setOnClickListener(new View.OnClickListener() {
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

                                addMechBrthEdt.setText((monthOfYear + 1) + "-" + dayOfMonth + "-" +  year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        return mechanicView;
    }

    private boolean mechGender(View v) {
        int radioId = addGrpRdioBtn.getCheckedRadioButtonId();
        addRadioBtn = v.findViewById(radioId);

        if (addGrpRdioBtn.getCheckedRadioButtonId() == -1)
        {
            // no radio buttons are checked
            Toast.makeText(getActivity(), "Pick your gender", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private boolean mechName() {
        String str = addMechNameEdt.getText().toString().trim();

        if (str.isEmpty()) {
            addMechNameEdt.setError("Field should not be empty.");
            return false;
        }
        return true;
    }
    private boolean mechAddress() {
        String str = addMechAddrssEdt.getText().toString().trim();

        if (str.isEmpty()) {
            addMechAddrssEdt.setError("Field should not be empty.");
            return false;
        }
        return true;
    }
    private boolean mechBirth() {
        String str = addMechBrthEdt.getText().toString().trim();

        if (str.isEmpty()) {
            addMechBrthEdt.setError("Field should not be empty.");
            return false;
        }
        return true;
    }
    private boolean mechSkills() {
        String str = addMechSkllsEdt.getText().toString().trim();

        if (str.isEmpty()) {
            addMechSkllsEdt.setError("Field should not be empty.");
            return false;
        }
        return true;
    }

    private void sortByRatings(){
        Collections.sort(onMechList, (l1, l2) -> {
            float r1 = Float.parseFloat(l1.getRate());
            float r2 = Float.parseFloat(l2.getRate());
            if (r2 > r1) return 1;
            else if(r2 < r1) return -1;
            else return 0;
        });
    }
}