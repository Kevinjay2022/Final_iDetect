package com.example.idetect.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.idetect.Models.ItemsModel;
import com.example.idetect.Models.OrderModel;
import com.example.idetect.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class DisplayCustomerHistoryItemAdapter extends RecyclerView.Adapter<DisplayCustomerHistoryItemAdapter.ViewHolder> {

    public DisplayCustomerHistoryItemAdapter(Context context, List<OrderModel> model) {
        this.context = context;
        this.modelList = model;
    }

    private Context context;
    private List<OrderModel> modelList;

    @NonNull
    @Override
    public DisplayCustomerHistoryItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_autoparts_list_customers_items_history, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DisplayCustomerHistoryItemAdapter.ViewHolder holder, int position) {
        OrderModel model = modelList.get(position);

        holder.histItemQty.setText(""+model.getQty());
        FirebaseDatabase.getInstance().getReference().child("ITEMS").child(model.getItemKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            ItemsModel itemsModel = snapshot.getValue(ItemsModel.class);
                            holder.histItemName.setText(""+itemsModel.getItem_Name());
                            holder.histPrice.setText("₱ "+itemsModel.getPrice());

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView histItemName, histItemQty, histPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            histItemName = itemView.findViewById(R.id.customerHistoryItemNameTV);
            histItemQty = itemView.findViewById(R.id.customerHistoryItemQtyTV);
            histPrice = itemView.findViewById(R.id.customerHistoryItemPriceTV);
        }
    }
}
