package com.example.idetect.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.idetect.CustomServiceCenterAutoPartsViewItems;
import com.example.idetect.Models.ItemsModel;
import com.example.idetect.Models.ServCentCustomerService;
import com.example.idetect.Models.ServCentMechOnCallModel;
import com.example.idetect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class DisplayMechOnCallAdapter extends RecyclerView.Adapter<DisplayMechOnCallAdapter.ViewHolder> {

    public DisplayMechOnCallAdapter(Context context, List<ServCentMechOnCallModel> model) {
        this.context = context;
        this.modelList = model;
    }

    private Context context;
    private List<ServCentMechOnCallModel> modelList;

    @NonNull
    @Override
    public DisplayMechOnCallAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list_service_center_on_call_mechanic, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DisplayMechOnCallAdapter.ViewHolder holder, int position) {
        ServCentMechOnCallModel model = modelList.get(position);


        FirebaseDatabase.getInstance().getReference().child("MECHANIC_NOTIFY")
                .orderByChild("shopID").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ServCentCustomerService model1 = ds.getValue(ServCentCustomerService.class);
                            if (model1.getID().equals(model.getMechID())) {
                                if (model1.getFeedback().equals("pending")) {
                                    holder.HireBtn.setVisibility(View.GONE);
                                    holder.cancelBtn.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        FirebaseDatabase.getInstance().getReference().child("USERS").child(model.getMechID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            holder.Name.setText(snapshot.child("firstname").getValue().toString() + " " + snapshot.child("lastname").getValue().toString());
                            holder.Address.setText(snapshot.child("address").getValue().toString());
                            holder.mechRatings.setText(snapshot.child("rate").getValue().toString());
                            holder.mechRatingBar.setRating(Float.parseFloat(snapshot.child("rate").getValue().toString()));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.Details.setText(model.getSkills());
        Picasso.get().load(Uri.parse(model.getCertificate())).into(holder.certImg);

        holder.CardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.ExpandView.getVisibility() == View.GONE) {
                    holder.ExpandView.setVisibility(View.VISIBLE);
                } else {
                    holder.ExpandView.setVisibility(View.GONE);
                }
            }
        });
        holder.HireBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String key = FirebaseDatabase.getInstance().getReference().child("MECHANIC_NOTIFY").push().getKey();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("ID", model.getMechID());
                hashMap.put("shopID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                hashMap.put("feedback", "pending");
                hashMap.put("seen", "new");
                hashMap.put("issue", "");
                hashMap.put("key", key);

                FirebaseDatabase.getInstance().getReference().child("MECHANIC_NOTIFY").child(key).setValue(hashMap);

                holder.cancelBtn.setVisibility(View.VISIBLE);
                holder.HireBtn.setVisibility(View.GONE);
            }
        });
        holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("MECHANIC_NOTIFY").orderByChild("shopID").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()){
                                    ServCentCustomerService model1 = ds.getValue(ServCentCustomerService.class);
                                    if (model1.getID().equals(model.getMechID())){
                                        FirebaseDatabase.getInstance().getReference().child("MECHANIC_NOTIFY").child(model1.getKey()).removeValue();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                holder.cancelBtn.setVisibility(View.GONE);
                holder.HireBtn.setVisibility(View.VISIBLE);

            }
        });
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView Name, Address, Details, mechRatings;
        CardView CardBtn;
        RatingBar mechRatingBar;
        Button HireBtn, cancelBtn;
        LinearLayout ExpandView;
        ImageView certImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mechRatingBar = itemView.findViewById(R.id.itemDisplayRateBar);
            mechRatings = itemView.findViewById(R.id.itemDisplayRate);
            certImg = itemView.findViewById(R.id.onCallImage);
            Name = itemView.findViewById(R.id.mechanicOnCall_name);
            Address = itemView.findViewById(R.id.mechanicOnCall_adress);
            Details = itemView.findViewById(R.id.descriptionOnCall);

            CardBtn = itemView.findViewById(R.id.OnCallCardBTN);
            ExpandView = itemView.findViewById(R.id.expandableOnCallMechanic);

            HireBtn = itemView.findViewById(R.id.hire_mechBTN);
            cancelBtn = itemView.findViewById(R.id.cancel_mechBTN);
        }
    }
}
