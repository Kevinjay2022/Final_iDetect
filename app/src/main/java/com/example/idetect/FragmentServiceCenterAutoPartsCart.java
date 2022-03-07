package com.example.idetect;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.idetect.Adapters.CartItemsAdapter;
import com.example.idetect.Models.CartItemsModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class FragmentServiceCenterAutoPartsCart extends AppCompatActivity {

    Button backBtnCart;
    RecyclerView recyclerView;
    CartItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_service_center_auto_parts_cart);

        backBtnCart = findViewById(R.id.backBtnCart);

        backBtnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //For displaying the Items in grid
        recyclerView = (RecyclerView)findViewById(R.id.cartRecycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<CartItemsModel> options = new FirebaseRecyclerOptions.Builder<CartItemsModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("ITEM_CART").orderByChild("ID").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()), CartItemsModel.class)
                .build();
        itemsAdapter = new CartItemsAdapter(options);
        recyclerView.setAdapter(itemsAdapter);
        itemsAdapter.startListening();
        //End for displaying the Items in grid
    }
}