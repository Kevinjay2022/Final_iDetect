package com.example.idetect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.idetect.Models.ItemsModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class CustomServiceCenterAutoPartsViewItems extends AppCompatActivity {

    Button backBTN;
    LinearLayout addToCart, BuyNow;
    TextView ItemName, ItemPrice;

    String itemKey, imagePrice, imagePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_service_center_auto_parts_view_items);

        addToCart = findViewById(R.id.itemAddCartBTN);
        BuyNow = findViewById(R.id.itemBuyNowBTN);

        ItemName = findViewById(R.id.itemDisplayName);
        ItemPrice = findViewById(R.id.itemDisplayPrice);
        //ItemQty = findViewById(R.id.)

        backBTN = findViewById(R.id.backItemBTN);
        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if(getIntent().hasExtra("ItemKey")){
            itemKey = getIntent().getStringExtra("ItemKey");

            setImageB(itemKey);
        }

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("ITEM_CART").orderByChild("ItemKey").equalTo(itemKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(!snapshot.exists()){
                            String key = FirebaseDatabase.getInstance().getReference().child("ITEM_CART").push().getKey();

                            HashMap<Object, String> Map = new HashMap<>();
                            Map.put("ItemKey", itemKey);
                            Map.put("CartKey", key);
                            Map.put("ID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            //Integer.parseInt(imagePrice);

                            FirebaseDatabase.getInstance().getReference().child("ITEM_CART").child(key).setValue(Map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(view.getContext(), "Added to cart.", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(view.getContext(), "Error", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
                            Toast.makeText(getApplicationContext(), "Item already added.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

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
                    DispPrice.setText("₱ " + itemsModel.getPrice() + ".00");
                    DispQty.setText("Stock: " + itemsModel.getQty());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}