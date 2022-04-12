package com.example.idetect.Adapters;

import static android.graphics.Color.RED;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.idetect.FragmentDriverVisitShop;
import com.example.idetect.Models.ServCentCustomerService;
import com.example.idetect.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class DisplayDriverNotificationAdapter extends RecyclerView.Adapter<DisplayDriverNotificationAdapter.ViewHolder> {

    private Context context;
    private List<ServCentCustomerService> modelList;

    public DisplayDriverNotificationAdapter(Context context, List<ServCentCustomerService> modelList) {
        this.context = context;
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public DisplayDriverNotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_driver_list_notification, parent, false);
        return new DisplayDriverNotificationAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull DisplayDriverNotificationAdapter.ViewHolder holder, int position) {
        ServCentCustomerService model = modelList.get(position);

        String feedback = model.getFeedback();

        if (feedback.equals("accept")) {
            holder.shopRespond.setText("APPROVED");
        }else{
            holder.shopRespond.setText("CANCELLED");
        }
        holder.shopRespond.setTextColor(RED);

        FirebaseDatabase.getInstance().getReference().child("USERS").child(model.getShopID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, FragmentDriverVisitShop.class);
                i.putExtra("shopID", model.getShopID());
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView shopName, shopRespond;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            shopName = itemView.findViewById(R.id.shopName);
            shopRespond = itemView.findViewById(R.id.shopRespond);
        }
    }

}
