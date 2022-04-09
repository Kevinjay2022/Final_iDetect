package com.example.idetect.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.idetect.CustomServiceCenterAutoPartsViewItems;
import com.example.idetect.Models.ItemsModel;
import com.example.idetect.Models.OrderModel;
import com.example.idetect.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class ItemOrderAdapter extends RecyclerView.Adapter<ItemOrderAdapter.ViewHolder> {

    public ItemOrderAdapter(Context context, List<OrderModel> model) {
        this.context = context;
        this.modelList = model;
    }

    private Context context;
    private List<OrderModel> modelList;

    @NonNull
    @Override
    public ItemOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_autoparts_list_customers, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemOrderAdapter.ViewHolder holder, int position) {
        OrderModel model = modelList.get(position);

        FirebaseDatabase.getInstance().getReference().child("ORDERS").child(model.getKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            OrderModel orderModel = snapshot.getValue(OrderModel.class);
                            holder.orderQty.setText(""+orderModel.getQty());

                            if (orderModel.getStatus().equals("pending"))
                                holder.customerStatus.setText("Processing");
                            else if (orderModel.getStatus().equals("accept")) {
                                holder.receiveBtn.setVisibility(View.VISIBLE);
                                holder.acceptBtn.setVisibility(View.GONE);
                                holder.cancelBtn.setVisibility(View.GONE);
                                holder.customerStatus.setText("Processed");
                            }else if (orderModel.getStatus().equals("complete")) {
                                holder.customerStatus.setText("Received");
                                holder.receiveBtn.setVisibility(View.GONE);
                                holder.acceptBtn.setVisibility(View.GONE);
                                holder.cancelBtn.setVisibility(View.GONE);
                            }else if (orderModel.getStatus().equals("cancel")) {
                                holder.customerStatus.setText("Not process");
                                holder.receiveBtn.setVisibility(View.GONE);
                                holder.acceptBtn.setVisibility(View.GONE);
                                holder.cancelBtn.setVisibility(View.GONE);
                            }

                            if (orderModel.getSeen().equals("new")){
                                holder.itemSeen.setText("New");
                                holder.itemSeen.setVisibility(View.VISIBLE);
                            }

                            FirebaseDatabase.getInstance().getReference().child("ITEMS").child(orderModel.getItemKey())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()){
                                                ItemsModel itemsModel = snapshot.getValue(ItemsModel.class);
                                                holder.orderName.setText(""+itemsModel.getItem_Name());
                                                holder.orderPrice.setText("₱ "+itemsModel.getPrice());
                                                holder.orderTotal.setText("₱ "+Float.parseFloat(itemsModel.getPrice()) * Integer.parseInt(orderModel.getQty()));
                                                holder.itemQty = Integer.parseInt(itemsModel.getQty());
                                                Glide.with(holder.orderImage.getContext())
                                                        .load(itemsModel.getItem_Surl())
                                                        .placeholder(R.drawable.location_logo)
                                                        .error(R.drawable.location_logo)
                                                        .into(holder.orderImage);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                            FirebaseDatabase.getInstance().getReference().child("USERS").child(orderModel.getID())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()){
                                                holder.shopNameTV.setText(""+snapshot.child("name").getValue().toString());
                                                holder.shopAddressTV.setText(""+snapshot.child("address").getValue().toString());
                                            }
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.expand.getVisibility() == View.GONE) {
                    holder.expand.setVisibility(View.VISIBLE);
                    holder.itemSeen.setVisibility(View.GONE);
                } else {
                    holder.expand.setVisibility(View.GONE);
                }

            }
        });
        holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.itemQty < Integer.parseInt(model.getQty()))
                    if(holder.itemQty == 0)
                        Toast.makeText(context, "You don't have any item left.", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(context, "You only have "+holder.itemQty +" left.", Toast.LENGTH_SHORT).show();
                else {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("status", "accept");

                    FirebaseDatabase.getInstance().getReference().child("ORDERS").child(model.getKey()).updateChildren(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    HashMap<String, Object> hashMap1 = new HashMap<>();
                                    hashMap1.put("Qty", "" + (holder.itemQty - Integer.parseInt(model.getQty())));
                                    FirebaseDatabase.getInstance().getReference().child("ITEMS").child(model.getItemKey()).updateChildren(hashMap1);

                                    holder.expand.setVisibility(View.GONE);
                                    holder.receiveBtn.setVisibility(View.VISIBLE);
                                    holder.acceptBtn.setVisibility(View.GONE);
                                    holder.cancelBtn.setVisibility(View.GONE);
                                    holder.customerStatus.setText("Processed");
                                }
                            });
                }
            }
        });
        holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("status", "cancel");

                FirebaseDatabase.getInstance().getReference().child("ORDERS").child(model.getKey()).updateChildren(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                holder.expand.setVisibility(View.GONE);
                                holder.customerStatus.setText("Not process");
                            }
                        });
            }
        });
        holder.receiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("status", "complete");

                FirebaseDatabase.getInstance().getReference().child("ORDERS").child(model.getKey()).updateChildren(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                holder.expand.setVisibility(View.GONE);
                                holder.customerStatus.setText("Received");
                            }
                        });

            }
        });

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView itemSeen, shopNameTV, shopAddressTV, customerStatus, orderName, orderQty, orderPrice, orderTotal;
        Button acceptBtn, cancelBtn, receiveBtn;
        LinearLayout expand;
        ImageView orderImage;
        int itemQty;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemSeen = itemView.findViewById(R.id.itemSeen);
            receiveBtn = itemView.findViewById(R.id.receivedOrderBTN);
            shopNameTV = itemView.findViewById(R.id.auto_partsShopName);
            shopAddressTV = itemView.findViewById(R.id.customerLocationTextView);
            customerStatus = itemView.findViewById(R.id.customerStatus);
            orderName = itemView.findViewById(R.id.orderName);
            orderQty = itemView.findViewById(R.id.orderQty);
            orderPrice = itemView.findViewById(R.id.orderPrice);
            orderTotal = itemView.findViewById(R.id.totalPriceOrder);
            acceptBtn = itemView.findViewById(R.id.acceptOrderBTN);
            cancelBtn = itemView.findViewById(R.id.cancelOrderBTN);
            expand = itemView.findViewById(R.id.customerItemExpandable);
            orderImage = itemView.findViewById(R.id.orderImg);
        }
    }
}
