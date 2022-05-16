package com.example.idetect.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.idetect.Models.ItemsModel;
import com.example.idetect.Models.OrderModel;
import com.example.idetect.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DisplayCustomerHistoryAdapter extends RecyclerView.Adapter<DisplayCustomerHistoryAdapter.ViewHolder> {

    public DisplayCustomerHistoryAdapter(Context context, List<OrderModel> model) {
        this.context = context;
        this.modelList = model;
    }

    private Context context;
    private List<OrderModel> modelList;
    int totalQty;
    float price;

    @NonNull
    @Override
    public DisplayCustomerHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_autoparts_list_customers_items_history_names, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DisplayCustomerHistoryAdapter.ViewHolder holder, int position) {
        OrderModel model = modelList.get(position);

        FirebaseDatabase.getInstance().getReference().child("USERS").child(model.getID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            holder.histItemName.setText(""+snapshot.child("name").getValue().toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        FirebaseDatabase.getInstance().getReference().child("ORDERS").orderByChild("ID").equalTo(model.getID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            OrderModel model1 = ds.getValue(OrderModel.class);
                            if (model1.getStatus().equals("complete")&&
                                    model1.getShopUID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                totalQty = totalQty + Integer.parseInt(model1.getQty());
                        }
                        holder.histItemQty.setText(""+totalQty);
                        totalQty = 0;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.customerExpandableView.getVisibility() == View.GONE) {
                    holder.customerExpandableView.setVisibility(View.VISIBLE);
                } else {
                    holder.customerExpandableView.setVisibility(View.GONE);
                }

            }
        });

        FirebaseDatabase.getInstance().getReference().child("ORDERS").orderByChild("ID").equalTo(model.getID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        holder.totalPrice = 0;
                        holder.histOrder.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            OrderModel model1 = ds.getValue(OrderModel.class);
                            if (model1.getStatus().equals("complete")&&
                                model1.getShopUID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                holder.histOrder.add(model1);
                        }

                        for (OrderModel re : holder.histOrder){
                            FirebaseDatabase.getInstance().getReference().child("ITEMS").child(re.getItemKey())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()){
                                                ItemsModel itemsModel = snapshot.getValue(ItemsModel.class);
                                                price = Float.parseFloat(itemsModel.getPrice());
                                                holder.totalPrice = holder.totalPrice + (Integer.parseInt(re.getQty()) * price);
                                            }
                                            holder.histTotalMoney.setText("₱ "+holder.totalPrice);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                        }



                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        holder.adapter = new DisplayCustomerHistoryItemAdapter(context, holder.histOrder);
        holder.recyclerView.setAdapter(holder.adapter);
        holder.adapter.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView histItemName, histItemQty, histTotalMoney;
        LinearLayout customerExpandableView;
        RecyclerView recyclerView;
        ArrayList<OrderModel> histOrder;
        DisplayCustomerHistoryItemAdapter adapter;
        float totalPrice = 0.00f;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            histOrder = new ArrayList<>();
            histItemName = itemView.findViewById(R.id.customerHistoryNameTV);
            histItemQty = itemView.findViewById(R.id.customerHistoryItemQuantityTV);
            histTotalMoney = itemView.findViewById(R.id.totalMoney);
            customerExpandableView = itemView.findViewById(R.id.customerExpandableView);
            recyclerView = itemView.findViewById(R.id.recycleView);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            linearLayoutManager.setReverseLayout(true);
            linearLayoutManager.setStackFromEnd(true);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(linearLayoutManager);
        }
    }
}
