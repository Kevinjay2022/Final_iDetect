package com.example.idetect;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.idetect.Adapters.DisplayCartItemsAdapter;
import com.example.idetect.Adapters.ItemOrderAdapter;
import com.example.idetect.Models.CartItemsModel;
import com.example.idetect.Models.OrderModel;
import com.example.idetect.Models.ServCentCustomerService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;


public class FragmentAutoPartsHome extends Fragment {

    private CardView DashBoardBTN, CustomersBTN;
    private LinearLayout DashOut, CustOut;
    RecyclerView customerList;
    TextView totalItem, totalSalary;
    TextView ordersBtn, completeBtn, cancelBtn;

    private ArrayList<OrderModel> orderModels;
    private ArrayList<OrderModel> completeOrderModels;
    private ArrayList<OrderModel> cancelOrderModels;
    ItemOrderAdapter itemsAdapter;
    int totalQty = 0, order_Counter = 0;
    float totalMoney = 0, price = 0;
    public FragmentAutoPartsHome(){

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragAutoPartsHome = inflater.inflate(R.layout.fragment_auto_parts_home, container, false);
        ordersBtn = fragAutoPartsHome.findViewById(R.id.OrdersBTN);
        completeBtn = fragAutoPartsHome.findViewById(R.id.CompleteBTN);
        cancelBtn = fragAutoPartsHome.findViewById(R.id.cancelBTN);
        totalItem = fragAutoPartsHome.findViewById(R.id.totalItemSoldTextView);
        totalSalary = fragAutoPartsHome.findViewById(R.id.totalSaleTextViewMoney);
        customerList = fragAutoPartsHome.findViewById(R.id.customerListView);
        //DashBoardBTN = fragAutoPartsHome.findViewById(R.id.DashboardCardBTN);
        CustomersBTN = fragAutoPartsHome.findViewById(R.id.customerCardBTN);
        DashOut = fragAutoPartsHome.findViewById(R.id.dashboardExpandable);
        CustOut = fragAutoPartsHome.findViewById(R.id.customerExpandableView);

        orderModels = new ArrayList<>();
        completeOrderModels = new ArrayList<>();
        cancelOrderModels = new ArrayList<>();
        customerList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        customerList.setLayoutManager(linearLayoutManager);
        FirebaseDatabase.getInstance().getReference().child("ORDERS")
                .orderByChild("ShopUID").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        totalQty = 0;
                        totalMoney = 0;
                        price = 0;
                        for (DataSnapshot ds : snapshot.getChildren()){
                            OrderModel model = ds.getValue(OrderModel.class);
                            if (model.getStatus().equals("complete")){
                                totalQty += Integer.parseInt(model.getQty());

                                FirebaseDatabase.getInstance().getReference().child("ITEMS").child(model.getItemKey())
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()){
                                                    price = Integer.parseInt(model.getQty()) * Float.parseFloat(snapshot.child("Price").getValue().toString());
                                                    totalMoney = totalMoney + price;
                                                    totalSalary.setText("₱ "+totalMoney);
                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                            }
                        }
                        totalItem.setText(""+totalQty);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
       /* DashBoardBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(DashOut.getVisibility()==View.GONE){
                    DashOut.setVisibility(View.VISIBLE);

                }else{
                    price = 0.00f;
                    totalQty = 0;
                    totalMoney = 0.00f;
                    DashOut.setVisibility(View.GONE);
                }
            }
        });*/
        CustomersBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (order_Counter == 0)
                    Toast.makeText(getActivity(), "No orders yet.", Toast.LENGTH_SHORT).show();
                else {
                    if (CustOut.getVisibility() == View.GONE) {
                        CustOut.setVisibility(View.VISIBLE);
                        FirebaseDatabase.getInstance().getReference().child("ORDERS")
                                .orderByChild("ShopUID").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        orderModels.clear();
                                        completeOrderModels.clear();
                                        cancelOrderModels.clear();
                                        for (DataSnapshot ds : snapshot.getChildren()) {
                                            OrderModel model = ds.getValue(OrderModel.class);
                                            assert model != null;
                                            if (model.getStatus().equals("pending") || model.getStatus().equals("accept")) {
                                                orderModels.add(model);
                                            }
                                            if (model.getStatus().equals("complete")) {
                                                completeOrderModels.add(model);
                                            }
                                            if (model.getStatus().equals("cancel")) {
                                                cancelOrderModels.add(model);
                                            }
                                        }
                                        itemsAdapter = new ItemOrderAdapter(getActivity(), orderModels);
                                        customerList.setAdapter(itemsAdapter);
                                        itemsAdapter.notifyDataSetChanged();

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                        ordersBtn.setBackgroundResource(R.color.teal_200);
                        completeBtn.setBackgroundResource(R.color.purple_700);
                        cancelBtn.setBackgroundResource(R.color.purple_700);

                    } else {
                        CustOut.setVisibility(View.GONE);
                        for (OrderModel re : orderModels) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("seen", "old");
                            FirebaseDatabase.getInstance().getReference().child("ORDERS").child(re.getKey()).updateChildren(hashMap);
                        }
                    }
                }
            }
        });

        ordersBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                ordersBtn.setBackgroundResource(R.color.teal_200);
                completeBtn.setBackgroundResource(R.color.purple_700);
                cancelBtn.setBackgroundResource(R.color.purple_700);
                itemsAdapter = new ItemOrderAdapter(getActivity(), orderModels);
                customerList.setAdapter(itemsAdapter);
                itemsAdapter.notifyDataSetChanged();
            }
        });
        completeBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                ordersBtn.setBackgroundResource(R.color.purple_700);
                completeBtn.setBackgroundResource(R.color.teal_200);
                cancelBtn.setBackgroundResource(R.color.purple_700);
                itemsAdapter = new ItemOrderAdapter(getActivity(), completeOrderModels);
                customerList.setAdapter(itemsAdapter);
                itemsAdapter.notifyDataSetChanged();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                ordersBtn.setBackgroundResource(R.color.purple_700);
                completeBtn.setBackgroundResource(R.color.purple_700);
                cancelBtn.setBackgroundResource(R.color.teal_200);
                itemsAdapter = new ItemOrderAdapter(getActivity(), cancelOrderModels);
                customerList.setAdapter(itemsAdapter);
                itemsAdapter.notifyDataSetChanged();
            }
        });
        FirebaseDatabase.getInstance().getReference().child("ORDERS")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        order_Counter = 0;
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            OrderModel orderModel = ds.getValue(OrderModel.class);
                            if(orderModel.getShopUID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                order_Counter++;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        return fragAutoPartsHome;
    }

}