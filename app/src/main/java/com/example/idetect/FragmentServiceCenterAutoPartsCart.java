package com.example.idetect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.idetect.Adapters.DisplayCartItemsAdapter;
import com.example.idetect.Models.CartItemsModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FragmentServiceCenterAutoPartsCart extends AppCompatActivity {

    Button backBtnCart;
    RecyclerView recyclerView;
    DisplayCartItemsAdapter itemsAdapter;
    ArrayList<CartItemsModel> models;


    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_service_center_auto_parts_cart);

        backBtnCart = findViewById(R.id.backBtnCart);
        models = new ArrayList<>();

        backBtnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //For displaying the Items
        recyclerView = (RecyclerView)findViewById(R.id.cartRecycleView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        FirebaseDatabase.getInstance().getReference().child("ITEM_CART")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        models.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            CartItemsModel model = ds.getValue(CartItemsModel.class);
                            if (model.getID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                models.add(model);
                        }
                        itemsAdapter = new DisplayCartItemsAdapter(FragmentServiceCenterAutoPartsCart.this, models);
                        recyclerView.setAdapter(itemsAdapter);
                        itemsAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    @Override
    public void onBackPressed() {
        this.finish();
        super.onBackPressed(); //replaced

    }
}