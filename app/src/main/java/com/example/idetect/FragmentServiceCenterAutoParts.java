package com.example.idetect;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.idetect.Adapters.ServCentItemsAdapter;
import com.example.idetect.Models.ItemsModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class FragmentServiceCenterAutoParts extends Fragment {

    RecyclerView recyclerView;
    ServCentItemsAdapter servCentItemsAdapter;
    RelativeLayout cart_btn;
    int Counter = 0;
    TextView CartCounter;

    public FragmentServiceCenterAutoParts(){

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragServShop = inflater.inflate(R.layout.fragment_service_center_auto_parts, container, false);

        cart_btn = fragServShop.findViewById(R.id.cart_btn);
        CartCounter = fragServShop.findViewById(R.id.CartCounter);

        //For displaying the Items in grid
        recyclerView = (RecyclerView)fragServShop.findViewById(R.id.items_grid);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        FirebaseRecyclerOptions<ItemsModel> options = new FirebaseRecyclerOptions.Builder<ItemsModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("ITEMS"), ItemsModel.class)
                .build();
        servCentItemsAdapter = new ServCentItemsAdapter(options);
        recyclerView.setAdapter(servCentItemsAdapter);
        servCentItemsAdapter.startListening();
        //End for displaying the Items in grid


        cart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FragmentServiceCenterAutoPartsCart.class);
                startActivity(intent);
            }
        });

        FirebaseDatabase.getInstance().getReference().child("ITEM_CART").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    if(ds.child("ID").getValue().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        Counter++;
                    }
                }
                CartCounter.setText(Counter + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return fragServShop;
    }

    /**
    private void txtSearch(String str){
        FirebaseRecyclerOptions<ItemsModel> options = new FirebaseRecyclerOptions.Builder<ItemsModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("ITEMS").orderByChild("Item_Name").startAt(str).endAt(str + "~"), ItemsModel.class)
                .build();
        servCentItemsAdapter = new ServCentItemsAdapter(options);
        recyclerView.setAdapter(servCentItemsAdapter);
        servCentItemsAdapter.startListening();
    }
     */

}