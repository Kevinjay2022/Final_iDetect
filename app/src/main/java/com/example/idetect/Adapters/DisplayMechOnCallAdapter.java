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
import com.example.idetect.Notify.Constant;
import com.example.idetect.Notify.Data;
import com.example.idetect.Notify.MyResponse;
import com.example.idetect.Notify.Sender;
import com.example.idetect.Notify.Token;
import com.example.idetect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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


        FirebaseDatabase.getInstance().getReference().child("MECHANIC_REQUEST")
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
                holder.notify = true;
                String key = FirebaseDatabase.getInstance().getReference().child("MECHANIC_NOTIFY").push().getKey();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("ID", model.getMechID());
                hashMap.put("shopID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                hashMap.put("feedback", "pending");
                hashMap.put("seen", "new");
                hashMap.put("issue", "");
                hashMap.put("key", key);
                FirebaseDatabase.getInstance().getReference().child("MECHANIC_NOTIFY").child(key).setValue(hashMap);

                String key1 = FirebaseDatabase.getInstance().getReference().child("MECHANIC_REQUEST").push().getKey();
                HashMap<String, Object> hashMap1 = new HashMap<>();
                hashMap1.put("ID", model.getMechID());
                hashMap1.put("shopID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                hashMap1.put("feedback", "pending");
                hashMap1.put("seen", "new");
                hashMap1.put("issue", "");
                hashMap1.put("key", key1);
                FirebaseDatabase.getInstance().getReference().child("MECHANIC_REQUEST").child(key1).setValue(hashMap1);


                FirebaseDatabase.getInstance().getReference().child("USERS").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    if (holder.notify) {
                                        String name = snapshot.child("name").getValue().toString();
                                        sendNotification(model.getMechID(), name, "Want to hire you.", "mechanic");
                                    }
                                    holder.notify = false;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                holder.cancelBtn.setVisibility(View.VISIBLE);
                holder.HireBtn.setVisibility(View.GONE);
            }
        });
        holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.notify = true;
                String key = FirebaseDatabase.getInstance().getReference().child("MECHANIC_NOTIFY").push().getKey();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("ID", model.getMechID());
                hashMap.put("shopID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                hashMap.put("feedback", "cancel");
                hashMap.put("seen", "new");
                hashMap.put("issue", "");
                hashMap.put("key", key);
                FirebaseDatabase.getInstance().getReference().child("MECHANIC_NOTIFY").child(key).setValue(hashMap);

                FirebaseDatabase.getInstance().getReference().child("MECHANIC_REQUEST").orderByChild("shopID").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()){
                                    ServCentCustomerService model1 = ds.getValue(ServCentCustomerService.class);
                                    if (model1.getID().equals(model.getMechID())){
                                        FirebaseDatabase.getInstance().getReference().child("MECHANIC_REQUEST").child(model1.getKey()).removeValue();
                                    }
                                }
                                FirebaseDatabase.getInstance().getReference().child("USERS").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    if (holder.notify) {
                                                        String name = snapshot.child("name").getValue().toString();
                                                        sendNotification(model.getMechID(), name, "Cancelled request.", "mechanic");
                                                    }
                                                    holder.notify = false;
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                holder.cancelBtn.setVisibility(View.GONE);
                holder.HireBtn.setVisibility(View.VISIBLE);
                holder.ExpandView.setVisibility(View.GONE);

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
        boolean notify = false;

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
