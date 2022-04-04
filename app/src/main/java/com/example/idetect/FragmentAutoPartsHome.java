package com.example.idetect;

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

    ItemOrderAdapter itemsAdapter;
    ArrayList<OrderModel> models;
    int totalQty = 0;
    float totalMoney = 0, price = 0;
    public FragmentAutoPartsHome(){

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragAutoPartsHome = inflater.inflate(R.layout.fragment_auto_parts_home, container, false);

        totalItem = fragAutoPartsHome.findViewById(R.id.totalItemSoldTextView);
        totalSalary = fragAutoPartsHome.findViewById(R.id.totalSaleTextViewMoney);
        customerList = fragAutoPartsHome.findViewById(R.id.customerListView);
        DashBoardBTN = fragAutoPartsHome.findViewById(R.id.DashboardCardBTN);
        CustomersBTN = fragAutoPartsHome.findViewById(R.id.customerCardBTN);
        DashOut = fragAutoPartsHome.findViewById(R.id.dashboardExpandable);
        CustOut = fragAutoPartsHome.findViewById(R.id.customerExpandableView);

        models = new ArrayList<>();
        customerList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        customerList.setLayoutManager(linearLayoutManager);

        DashBoardBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(DashOut.getVisibility()==View.GONE){
                    DashOut.setVisibility(View.VISIBLE);
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
                                                                totalSalary.setText(""+totalMoney);
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
                }else{
                    price = 0.00f;
                    totalQty = 0;
                    totalMoney = 0.00f;
                    DashOut.setVisibility(View.GONE);
                }
            }
        });
        CustomersBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CustOut.getVisibility()==View.GONE){
                    CustOut.setVisibility(View.VISIBLE);

                    FirebaseDatabase.getInstance().getReference().child("ORDERS")
                            .orderByChild("ShopUID").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    models.clear();
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        OrderModel model = ds.getValue(OrderModel.class);
                                        models.add(model);
                                    }
                                    if (models.size() == 0)
                                        Toast.makeText(getActivity(), "You don't have any orders.", Toast.LENGTH_SHORT).show();
                                    else {
                                        itemsAdapter = new ItemOrderAdapter(getActivity(), models);
                                        customerList.setAdapter(itemsAdapter);
                                        itemsAdapter.notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }else{
                    CustOut.setVisibility(View.GONE);
                    for (OrderModel re: models){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("seen", "old");
                        FirebaseDatabase.getInstance().getReference().child("ORDERS").child(re.getKey()).updateChildren(hashMap);

                    }

                }
            }
        });




        return fragAutoPartsHome;
    }

}