package com.example.idetect;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.idetect.Adapters.DisplayCustomerHistoryAdapter;
import com.example.idetect.Adapters.DisplayDriverNotificationAdapter;
import com.example.idetect.Models.ItemsModel;
import com.example.idetect.Models.OrderModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class FragmentAutoPartsCustomerHistory extends Fragment {

    RecyclerView recyclerView;
    DisplayCustomerHistoryAdapter adapter;
    ArrayList<OrderModel> orderModels;
    boolean flag = false;

    public FragmentAutoPartsCustomerHistory(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragCustHst = inflater.inflate(R.layout.fragment_auto_parts_customer_history, container, false);

        orderModels = new ArrayList<>();
        recyclerView = fragCustHst.findViewById(R.id.recycleView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        FirebaseDatabase.getInstance().getReference().child("ORDERS").orderByChild("ShopUID")
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot ds: snapshot.getChildren()){
                            OrderModel model = ds.getValue(OrderModel.class);
                            if (model.getStatus().equals("complete")) {
                                for (OrderModel re : orderModels) {
                                    if (re.getID().equals(model.getID())){
                                        flag = true;
                                        break;
                                    }else
                                        flag = false;
                                }
                                if (!flag) {
                                    orderModels.add(model);
                                }
                            }
                        }
                        adapter = new DisplayCustomerHistoryAdapter(getActivity(), orderModels);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



        return fragCustHst;
    }
}