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
import com.example.idetect.Notify.Constant;
import com.example.idetect.Notify.Data;
import com.example.idetect.Notify.MyResponse;
import com.example.idetect.Notify.Sender;
import com.example.idetect.Notify.Token;
import com.example.idetect.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                    holder.notify = true;
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("status", "accept");

                    FirebaseDatabase.getInstance().getReference().child("ORDERS").child(model.getKey()).updateChildren(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    HashMap<String, Object> hashMap1 = new HashMap<>();
                                    hashMap1.put("Qty", "" + (holder.itemQty - Integer.parseInt(model.getQty())));
                                    FirebaseDatabase.getInstance().getReference().child("ITEMS").child(model.getItemKey()).updateChildren(hashMap1);

                                    FirebaseDatabase.getInstance().getReference().child("USERS").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.exists()) {
                                                        if (holder.notify) {
                                                            String name = snapshot.child("name").getValue().toString();
                                                            FirebaseDatabase.getInstance().getReference().child("USERS").child(model.getID())
                                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                            if (snapshot.exists()){
                                                                                sendNotification(model.getID(), name, "Accept your order.", snapshot.child("acctype").getValue().toString());
                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                                        }
                                                                    });
                                                        }
                                                        holder.notify = false;
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
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
                holder.notify = true;
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("status", "cancel");

                FirebaseDatabase.getInstance().getReference().child("ORDERS").child(model.getKey()).updateChildren(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                FirebaseDatabase.getInstance().getReference().child("USERS").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                if (holder.notify) {
                                                    String name = snapshot.child("name").getValue().toString();
                                                    FirebaseDatabase.getInstance().getReference().child("USERS").child(model.getID())
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    if (snapshot.exists()){
                                                                        sendNotification(model.getID(), name, "Cancelled the order.", snapshot.child("acctype").getValue().toString());
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                }
                                                            });
                                                }
                                                holder.notify = false;
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
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
        boolean notify = false;



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
    private void sendNotification(String receiver, String senderName, String msg, String on) {

        Query query = Constant.tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(FirebaseAuth.getInstance().getCurrentUser().getUid(), R.drawable.home_logo, msg, senderName, receiver, on);

                    assert token != null;
                    Sender sender = new Sender(data, token.getToken());

                    Constant.apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(@NotNull Call<MyResponse> call, @NotNull Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        assert response.body() != null;
                                        if(response.body().success != 1){}
                                        //Toast.makeText(context, "Failed", Toast.).show();
                                    }
                                }
                                @Override
                                public void onFailure(@NotNull Call<MyResponse> call, @NotNull Throwable t) {
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
