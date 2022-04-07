package com.example.idetect.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.idetect.CustomServiceCenterAutoPartsViewItems;
import com.example.idetect.Models.ItemsModel;
import com.example.idetect.Models.OrderModel;
import com.example.idetect.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;

import java.util.HashMap;
import java.util.List;

public class DisplayStoreItemsAdapter extends RecyclerView.Adapter<DisplayStoreItemsAdapter.ViewHolder> {

    public DisplayStoreItemsAdapter(Context context, List<ItemsModel> model) {
        this.context = context;
        this.modelList = model;
    }

    private Context context;
    private List<ItemsModel> modelList;

    @NonNull
    @Override
    public DisplayStoreItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_service_center_grid_items, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DisplayStoreItemsAdapter.ViewHolder holder, int position) {
        ItemsModel model = modelList.get(position);


        holder.ItemName.setText(model.getItem_Name());
        holder.ItemPrice.setText("₱ " + model.getPrice() + ".00");
        holder.ItemQTY.setText(model.getQty() + " left");

        FirebaseDatabase.getInstance().getReference().child("ORDERS").orderByChild("ItemKey").equalTo(model.getItemKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            OrderModel orderModel = ds.getValue(OrderModel.class);
                            holder.itemSold = holder.itemSold + Integer.parseInt(orderModel.getQty());
                        }
                        holder.ItemSold.setText(holder.itemSold+ " sold");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        Glide.with(holder.ItemImg.getContext())
                .load(model.getItem_Surl())
                .placeholder(R.drawable.location_logo)
                .error(R.drawable.location_logo)
                .into(holder.ItemImg);

        // Sa item button
        holder.CardItemBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(view.getContext(), "name "+ model.getItem_Name(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(view.getContext(), CustomServiceCenterAutoPartsViewItems.class);
                intent.putExtra("ItemKey", model.getItemKey());
                view.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView ItemName, ItemPrice, ItemSold, ItemQTY;
        ImageView ItemImg;
        CardView CardItemBTN;
        int itemSold = 0;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ItemName = itemView.findViewById(R.id.ItemNameTB);
            ItemPrice = (TextView)itemView.findViewById(R.id.itemPrice);
            ItemQTY = (TextView)itemView.findViewById(R.id.item_quantity);
            ItemSold = (TextView)itemView.findViewById(R.id.itemStockSold);
            ItemImg = itemView.findViewById(R.id.item_image);

            CardItemBTN = itemView.findViewById(R.id.CardItemBTN);
        }
    }
}
