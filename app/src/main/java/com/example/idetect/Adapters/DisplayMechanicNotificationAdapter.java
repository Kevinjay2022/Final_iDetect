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

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DisplayMechanicNotificationAdapter extends RecyclerView.Adapter<DisplayMechanicNotificationAdapter.ViewHolder> {

    private Context context;
    private List<ServCentCustomerService> modelList;
    String mechName, mechAddress, mechBirth, mechSkills, mechGender;

    public DisplayMechanicNotificationAdapter(Context context, List<ServCentCustomerService> modelList) {
        this.context = context;
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public DisplayMechanicNotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list_mechanic_notification, parent, false);
        return new DisplayMechanicNotificationAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull DisplayMechanicNotificationAdapter.ViewHolder holder, int position) {
        ServCentCustomerService model = modelList.get(position);

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

        FirebaseDatabase.getInstance().getReference().child("USERS").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            mechAddress = snapshot.child("address").getValue().toString();
                            mechName = snapshot.child("firstname").getValue().toString()+" "+ snapshot.child("lastname").getValue().toString();
                            mechGender = snapshot.child("gender").getValue().toString();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        FirebaseDatabase.getInstance().getReference().child("MECHANIC_POST").orderByChild("mechID").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ServCentMechOnCallModel model1 = ds.getValue(ServCentMechOnCallModel.class);
                            mechBirth = model1.getBirth();
                            mechSkills = model1.getSkills();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        if (model.getFeedback().equals("accept")){
            holder.shopApprove.setVisibility(View.GONE);
            holder.shopCancel.setVisibility(View.GONE);
            holder.notif_approve.setVisibility(View.VISIBLE);
        }

        holder.shopApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.notify = true;
                String key = FirebaseDatabase.getInstance().getReference().child("SERVICE_CENT_MECHANICS").push().getKey();
                HashMap<String, Object> hashMap2 = new HashMap<>();
                hashMap2.put("name", mechName);
                hashMap2.put("address", mechAddress);
                hashMap2.put("birth", mechBirth);
                hashMap2.put("skills", mechSkills);
                hashMap2.put("gender", mechGender);
                hashMap2.put("status", "active");
                hashMap2.put("mech_type", "on-call");
                hashMap2.put("mechID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                hashMap2.put("key", key);
                hashMap2.put("rate", "0");
                hashMap2.put("delete", false);
                hashMap2.put("employ", "hire");
                hashMap2.put("ID", model.getShopID());
                FirebaseDatabase.getInstance().getReference().child("SERVICE_CENT_MECHANICS").child(key).setValue(hashMap2);

                FirebaseDatabase.getInstance().getReference().child("USERS").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    if (holder.notify) {
                                        String name = snapshot.child("firstname").getValue().toString()+" "+snapshot.child("lastname").getValue().toString();
                                        sendNotification(model.getShopID(), name, "Approve your request.", "serve_center");
                                    }
                                    holder.notify = false;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("feedback", "accept");

                FirebaseDatabase.getInstance().getReference().child("MECHANIC_NOTIFY").child(model.getKey()).updateChildren(hashMap);

                HashMap<String, Object> hashMap1 = new HashMap<>();
                hashMap1.put("status", "hired");

                FirebaseDatabase.getInstance().getReference().child("MECHANIC_POST").orderByChild("mechID").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()){
                                    ServCentMechOnCallModel model1 = ds.getValue(ServCentMechOnCallModel.class);
                                    mechBirth = model1.getBirth();
                                    mechSkills = model1.getSkills();
                                    FirebaseDatabase.getInstance().getReference().child("MECHANIC_POST").child(model1.getKey()).updateChildren(hashMap1);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

            }
        });

        holder.shopCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseDatabase.getInstance().getReference().child("MECHANIC_NOTIFY").child(model.getKey()).removeValue();
                FirebaseDatabase.getInstance().getReference().child("USERS").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    if (holder.notify) {
                                        String name = snapshot.child("firstname").getValue().toString()+" "+snapshot.child("lastname").getValue().toString();
                                        sendNotification(model.getShopID(), name, "Cancelled request.", "serve_center");
                                    }
                                    holder.notify = false;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView shopName, shopApprove, shopCancel, notif_approve;
        boolean notify = false;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            notif_approve = itemView.findViewById(R.id.notif_approve);
            shopName = itemView.findViewById(R.id.shopName);
            shopApprove = itemView.findViewById(R.id.shopApprovedBTN);
            shopCancel = itemView.findViewById(R.id.shopCancelBTN);
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
