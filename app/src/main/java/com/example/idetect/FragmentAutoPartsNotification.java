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

import com.example.idetect.Adapters.DisplayAutoPartsNotificationAdapter;
import com.example.idetect.Adapters.DisplayDriverNotificationAdapter;
import com.example.idetect.Models.ServCentCustomerService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;


public class FragmentAutoPartsNotification extends Fragment {

    RecyclerView recyclerView;
    DisplayAutoPartsNotificationAdapter notifyDisplayAdapter;
    private ArrayList<ServCentCustomerService> notifyList;
    LinearLayout noNotif;

    public FragmentAutoPartsNotification(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragAutoPartsNotification = inflater.inflate(R.layout.fragment_auto_parts_notification, container, false);

        notifyList = new ArrayList<>();

        noNotif = fragAutoPartsNotification.findViewById(R.id.noNotifications);
        recyclerView = (RecyclerView) fragAutoPartsNotification.findViewById(R.id.notificationListView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        FirebaseDatabase.getInstance().getReference().child("AUTO_PARTS_NOTIFY")
                .orderByChild("shopID").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("seen", "old");
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ServCentCustomerService model = ds.getValue(ServCentCustomerService.class);
                            FirebaseDatabase.getInstance().getReference().child("AUTO_PARTS_NOTIFY")
                                    .child(model.getKey()).updateChildren(hashMap);

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        FirebaseDatabase.getInstance().getReference().child("AUTO_PARTS_NOTIFY")
                .orderByChild("shopID").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        notifyList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ServCentCustomerService model = ds.getValue(ServCentCustomerService.class);
                            notifyList.add(model);
                        }
                        if (notifyList.size() == 0){
                            recyclerView.setVisibility(View.GONE);
                            noNotif.setVisibility(View.VISIBLE);
                        }else {
                            notifyDisplayAdapter = new DisplayAutoPartsNotificationAdapter(getActivity(), notifyList);
                            recyclerView.setAdapter(notifyDisplayAdapter);
                            notifyDisplayAdapter.notifyDataSetChanged();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        return fragAutoPartsNotification;
    }

}