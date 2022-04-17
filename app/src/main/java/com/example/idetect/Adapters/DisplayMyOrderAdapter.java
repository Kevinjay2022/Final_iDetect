package com.example.idetect.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

public class DisplayMyOrderAdapter extends RecyclerView.Adapter<DisplayMyOrderAdapter.ViewHolder> {


    public DisplayMyOrderAdapter(Context context, List<OrderModel> model) {
        this.context = context;
        this.modelList = model;
    }

    private Context context;
    private List<OrderModel> modelList;

    @NonNull
    @Override
    public DisplayMyOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_autoparts_mylist_customers, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DisplayMyOrderAdapter.ViewHolder holder, int position) {
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
                                holder.cancelBtn.setVisibility(View.GONE);
                                holder.customerStatus.setText("Processed");
                            }else if (orderModel.getStatus().equals("complete")) {
                                holder.customerStatus.setText("Received");
                                holder.receiveBtn.setVisibility(View.GONE);
                                if (snapshot.child("rate").getValue().toString().equals("0"))
                                    holder.itemRate.setVisibility(View.VISIBLE);
                                holder.cancelBtn.setVisibility(View.GONE);
                            }else if (orderModel.getStatus().equals("cancel")) {
                                holder.customerStatus.setText("Not process");
                                holder.receiveBtn.setVisibility(View.GONE);
                                holder.cancelBtn.setVisibility(View.GONE);
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

                            FirebaseDatabase.getInstance().getReference().child("USERS").child(orderModel.getShopUID())
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

        holder.itemRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                LinearLayout layout = new LinearLayout(context);
                LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setLayoutParams(parms);

                layout.setGravity(Gravity.CENTER);
                layout.setPadding(2, 2, 2, 2);

                TextView tv = new TextView(context);
                tv.setText("Rate");
                tv.setPadding(40, 40, 40, 40);
                tv.setGravity(Gravity.START);
                tv.setTextSize(20);
                RatingBar r = new RatingBar(context);
                //how many starts you want to show,parent layout width must be wrap_content
                r.setNumStars(5);
                r.setRating(Float.parseFloat("1.0"));

                LinearLayout.LayoutParams parms1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layout.addView(r, parms1);
                r.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                        holder.rateValue = ratingBar.getRating();
                    }
                });
                alertDialogBuilder.setView(layout);
                alertDialogBuilder.setTitle("Rate me");
                alertDialogBuilder.setCustomTitle(tv);

                alertDialogBuilder.setCancelable(false);

                // Setting Negative "Cancel" Button
                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });

                // Setting Positive "OK" Button
                alertDialogBuilder.setPositiveButton("Rate", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("rate", ""+holder.rateValue);
                        FirebaseDatabase.getInstance().getReference().child("ORDERS").child(model.getKey()).updateChildren(hashMap);
                        FirebaseDatabase.getInstance().getReference().child("ORDERS").orderByChild("ItemKey").equalTo(model.getItemKey())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot ds1: snapshot.getChildren()){
                                            OrderModel orderModel = ds1.getValue(OrderModel.class);
                                            if(Float.parseFloat(orderModel.getRate()) != 0) {
                                                holder.getRateValue = holder.getRateValue + Float.parseFloat(orderModel.getRate());
                                                holder.rateCount++;
                                            }
                                        }
                                        String totalRate = String.valueOf(holder.getRateValue / holder.rateCount);
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("rate", totalRate);
                                        FirebaseDatabase.getInstance().getReference().child("ITEMS").child(model.getItemKey()).updateChildren(hashMap);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                        holder.itemRate.setVisibility(View.GONE);
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();

                try {
                    alertDialog.show();
                } catch (Exception e) {
                    // WindowManager$BadTokenException will be caught and the app would
                    // not display the 'Force Close' message
                    e.printStackTrace();
                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.expand.getVisibility() == View.GONE) {
                    holder.expand.setVisibility(View.VISIBLE);
                } else {
                    holder.expand.setVisibility(View.GONE);
                }

            }
        });
        holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.notify = true;
                String key = FirebaseDatabase.getInstance().getReference().child("AUTO_PARTS_NOTIFY").push().getKey();
                HashMap<String, Object> hashMap1 = new HashMap<>();
                hashMap1.put("shopID", model.getShopUID());
                hashMap1.put("ID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                hashMap1.put("key", key);
                hashMap1.put("feedback", "cancel");
                hashMap1.put("seen", "new");

                FirebaseDatabase.getInstance().getReference().child("AUTO_PARTS_NOTIFY").child(key).setValue(hashMap1);

                Toast.makeText(context, "Order cancelled", Toast.LENGTH_SHORT).show();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("status", "cancel");
                hashMap.put("seen", "old");

                FirebaseDatabase.getInstance().getReference().child("ORDERS").child(model.getKey()).updateChildren(hashMap);


                FirebaseDatabase.getInstance().getReference().child("USERS").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    if (holder.notify) {
                                        String name = snapshot.child("name").getValue().toString();
                                        FirebaseDatabase.getInstance().getReference().child("USERS").child(model.getShopUID())
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (snapshot.exists()){
                                                            sendNotification(model.getShopUID(), name, "Cancelled the order.", snapshot.child("acctype").getValue().toString());
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

            }
        });
        holder.receiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("status", "complete");
                hashMap.put("seen", "old");

                FirebaseDatabase.getInstance().getReference().child("ORDERS").child(model.getKey()).updateChildren(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                holder.expand.setVisibility(View.GONE);
                                holder.itemRate.setVisibility(View.VISIBLE);
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
        TextView shopNameTV, shopAddressTV, customerStatus, orderName, orderQty, orderPrice, orderTotal;
        Button cancelBtn, receiveBtn, itemRate;
        LinearLayout expand;
        ImageView orderImage;
        float rateValue = 0;
        float getRateValue = 0;
        int rateCount = 0;
        boolean notify = false;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemRate = itemView.findViewById(R.id.itemRate);
            receiveBtn = itemView.findViewById(R.id.receivedOrderBTN);
            shopNameTV = itemView.findViewById(R.id.auto_partsShopName);
            shopAddressTV = itemView.findViewById(R.id.customerLocationTextView);
            customerStatus = itemView.findViewById(R.id.customerStatus);
            orderName = itemView.findViewById(R.id.orderName);
            orderQty = itemView.findViewById(R.id.orderQty);
            orderPrice = itemView.findViewById(R.id.orderPrice);
            orderTotal = itemView.findViewById(R.id.totalPriceOrder);
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
                    Data data = new Data(FirebaseAuth.getInstance().getCurrentUser().getUid(), R.drawable.logo, msg, senderName, receiver, on);

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
