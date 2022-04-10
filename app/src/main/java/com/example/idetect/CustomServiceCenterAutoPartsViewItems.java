package com.example.idetect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.idetect.Models.ItemsModel;
import com.example.idetect.Notify.Constant;
import com.example.idetect.Notify.Data;
import com.example.idetect.Notify.MyResponse;
import com.example.idetect.Notify.Sender;
import com.example.idetect.Notify.Token;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomServiceCenterAutoPartsViewItems extends AppCompatActivity {

    Button backBTN;
    LinearLayout addToCart, BuyNow;
    TextView ItemName, ItemPrice, itemCount, itemTotalPrice, itemDisplayRate;
    CardView minusBTN, plusBTN;

    RatingBar itemDisplayRateBar;
    String itemKey, imagePrice, imagePic, ShopUID;
    int Counter = 1, stocks = 0;
    float price = 0.2f;
    boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_service_center_auto_parts_view_items);

        addToCart = findViewById(R.id.itemAddCartBTN);
        BuyNow = findViewById(R.id.itemBuyNowBTN);
        itemDisplayRateBar = findViewById(R.id.itemDisplayRateBar);
        itemDisplayRate = findViewById(R.id.itemDisplayRate);

        minusBTN = findViewById(R.id.CardMinusBtn);
        plusBTN = findViewById(R.id.CardPlusBtn);
        itemTotalPrice = findViewById(R.id.itemTotalPrice);
        itemCount = findViewById(R.id.ItemCounterTxt);
        ItemName = findViewById(R.id.itemDisplayName);
        ItemPrice = findViewById(R.id.itemDisplayPrice);
        //ItemQty = findViewById(R.id.)

        if(getIntent().hasExtra("ItemKey")){
            itemKey = getIntent().getStringExtra("ItemKey");

            setImageB(itemKey);
        }

        plusBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Counter++;
                if(Counter >= stocks){
                    Toast.makeText(view.getContext(), "You have reach the maximum item.", Toast.LENGTH_SHORT).show();
                    Counter = stocks;
                }
                itemTotalPrice.setText(""+(price * Counter));
                itemCount.setText(Counter + "");
            }
        });
        minusBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Counter--;
                if(Counter <= 1){
                    Counter = 1;
                }
                itemTotalPrice.setText(""+(price * Counter));
                itemCount.setText(Counter + "");
            }
        });

        backBTN = findViewById(R.id.backItemBTN);
        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("ITEM_CART").orderByChild("ID").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean flag = false;
                        for (DataSnapshot ds : snapshot.getChildren()){
                            if (ds.child("ItemKey").getValue().toString().equals(itemKey)) {
                                flag = true;
                                Toast.makeText(getApplicationContext(), "Added to cart.", Toast.LENGTH_SHORT).show();
                                break;
                            }else
                                flag = false;
                        }
                        if (!flag){
                            String key = FirebaseDatabase.getInstance().getReference().child("ITEM_CART").push().getKey();

                            HashMap<String, Object> Map = new HashMap<>();
                            Map.put("ItemKey", itemKey);
                            Map.put("CartKey", key);
                            Map.put("ID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            //Integer.parseInt(imagePrice);

                            assert key != null;
                            FirebaseDatabase.getInstance().getReference().child("ITEM_CART").child(key).setValue(Map);
                            Toast.makeText(view.getContext(), "Added to cart.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        BuyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify = true;
                String orderKey = FirebaseDatabase.getInstance().getReference().child("ORDERS").push().getKey();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("ID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                hashMap.put("ShopUID", ShopUID);
                hashMap.put("key", orderKey);
                hashMap.put("ItemKey", itemKey);
                hashMap.put("Qty", ""+Counter);
                hashMap.put("status", "pending");
                hashMap.put("rate", "0");
                hashMap.put("seen", "new");

                String key = FirebaseDatabase.getInstance().getReference().child("AUTO_PARTS_NOTIFY").push().getKey();
                HashMap<String, Object> hashMap1 = new HashMap<>();
                hashMap1.put("shopID", ShopUID);
                hashMap1.put("ID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                hashMap1.put("key", key);
                hashMap1.put("feedback", "pending");
                hashMap1.put("seen", "new");

                FirebaseDatabase.getInstance().getReference().child("AUTO_PARTS_NOTIFY").child(key).setValue(hashMap1);

                FirebaseDatabase.getInstance().getReference().child("ORDERS").child(orderKey).setValue(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(CustomServiceCenterAutoPartsViewItems.this, "Wait for approval", Toast.LENGTH_SHORT).show();
                                FirebaseDatabase.getInstance().getReference().child("USERS").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    if (notify) {
                                                        String name = snapshot.child("name").getValue().toString();
                                                        FirebaseDatabase.getInstance().getReference().child("USERS").child(ShopUID)
                                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                        if (snapshot.exists()){
                                                                            sendNotification(ShopUID, name, "Want to buy your items.", snapshot.child("acctype").getValue().toString());
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                    }
                                                                });
                                                    }
                                                    notify = false;
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                onBackPressed();
                            }
                        });
            }
        });
    }

    private void setImageB(String itemKey){
        ImageView DispImage = findViewById(R.id.itemDisplayImage);
        TextView DispyName = findViewById(R.id.itemDisplayName);
        TextView DispPrice = findViewById(R.id.itemDisplayPrice);
        TextView DispQty = findViewById(R.id.itemDisplayStock);
        FirebaseDatabase.getInstance().getReference().child("ITEMS").child(itemKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    ItemsModel itemsModel = snapshot.getValue(ItemsModel.class);
                    imagePrice = itemsModel.getPrice();
                    imagePic = itemsModel.getItem_Surl();
                    Glide.with(getApplicationContext())
                            .asBitmap()
                            .load(itemsModel.getItem_Surl())
                            .into(DispImage);
                    DispyName.setText(itemsModel.getItem_Name());
                    price = Float.parseFloat(itemsModel.getPrice());
                    DispPrice.setText("₱ " + itemsModel.getPrice() + ".00");
                    stocks = Integer.parseInt(itemsModel.getQty());
                    DispQty.setText("Stock: " + stocks);
                    itemTotalPrice.setText(""+(price * Counter));
                    ShopUID = itemsModel.getShopUID();
                    itemDisplayRateBar.setRating(Float.parseFloat(itemsModel.getRate()));
                    itemDisplayRate.setText(itemsModel.getRate());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        this.finish();
        super.onBackPressed();
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