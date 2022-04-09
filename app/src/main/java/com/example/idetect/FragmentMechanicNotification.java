package com.example.idetect;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.idetect.Adapters.DisplayDriverNotificationAdapter;
import com.example.idetect.Adapters.DisplayMechanicNotificationAdapter;
import com.example.idetect.Models.ServCentCustomerService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class FragmentMechanicNotification extends Fragment {

    RecyclerView recyclerView;
    ArrayList<ServCentCustomerService> list;
    DisplayMechanicNotificationAdapter adapter;
    LinearLayout noNotif;

    public FragmentMechanicNotification(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragMechNot = inflater.inflate(R.layout.fragment_mechanic_notification, container, false);

        list = new ArrayList<>();
        noNotif = fragMechNot.findViewById(R.id.noNotifications);
        recyclerView = fragMechNot.findViewById(R.id.mechanicListViewNotification);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        FirebaseDatabase.getInstance().getReference().child("MECHANIC_NOTIFY")
                .orderByChild("ID").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ServCentCustomerService model = ds.getValue(ServCentCustomerService.class);
                            list.add(model);
                        }
                        if (list.size() == 0){
                            noNotif.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }else {
                            adapter = new DisplayMechanicNotificationAdapter(getActivity(), list);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        FirebaseDatabase.getInstance().getReference().child("MECHANIC_NOTIFY")
                .orderByChild("ID").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("seen", "old");
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ServCentCustomerService model = ds.getValue(ServCentCustomerService.class);
                            FirebaseDatabase.getInstance().getReference().child("MECHANIC_NOTIFY")
                                    .child(model.getKey()).updateChildren(hashMap);

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });




        return fragMechNot;
    }

}
