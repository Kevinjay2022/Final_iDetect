package com.example.idetect.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.idetect.FragmentDriverVisitShop;
import com.example.idetect.Models.ServCentCustomerService;
import com.example.idetect.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class DisplayAutoPartsNotificationAdapter extends RecyclerView.Adapter<DisplayAutoPartsNotificationAdapter.ViewHolder> {

    private Context context;
    private List<ServCentCustomerService> modelList;

    public DisplayAutoPartsNotificationAdapter(Context context, List<ServCentCustomerService> modelList) {
        this.context = context;
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public DisplayAutoPartsNotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_autoparts_list_notifications, parent, false);
        return new DisplayAutoPartsNotificationAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull DisplayAutoPartsNotificationAdapter.ViewHolder holder, int position) {
        ServCentCustomerService model = modelList.get(position);

        FirebaseDatabase.getInstance().getReference().child("USERS").child(model.getID())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String name = snapshot.child("name").getValue(String.class);
                            holder.shopName.setText(name);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        if (model.getFeedback().equals("pending"))
            holder.notifMsg.setText("Wants to buy items");
        else
            holder.notifMsg.setText("Cancelled the order");
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView shopName, notifMsg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            notifMsg = itemView.findViewById(R.id.notifMsg);
            shopName = itemView.findViewById(R.id.notificationCustomerNameTV);
        }
    }

}
