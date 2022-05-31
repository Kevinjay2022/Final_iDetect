package com.example.idetect.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.idetect.Models.ItemsModel;
import com.example.idetect.Models.ServCentCustHistModel;
import com.example.idetect.Models.ServCentCustomerService;
import com.example.idetect.Notify.Constant;
import com.example.idetect.Notify.Data;
import com.example.idetect.Notify.MyResponse;
import com.example.idetect.Notify.Sender;
import com.example.idetect.Notify.Token;
import com.example.idetect.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.orhanobut.dialogplus.DialogPlus;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServCentCustServAdapter extends RecyclerView.Adapter<ServCentCustServAdapter.ViewHolder> {

    private Context context;
    private List<ServCentCustomerService> modelList;
    String name, address;

    public ServCentCustServAdapter(Context context, List<ServCentCustomerService> modelList) {
        this.context = context;
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public ServCentCustServAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list_service_center_customer_services, parent, false);
        return new ServCentCustServAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ServCentCustServAdapter.ViewHolder holder, int position) {
        ServCentCustomerService model = modelList.get(position);

        holder.custIssue.setText(model.getIssue());
        FirebaseDatabase.getInstance().getReference().child("SERVICE_CENT_CUSTOMERS")
                .orderByChild("shopID").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ServCentCustomerService model1 = ds.getValue(ServCentCustomerService.class);
                            if(model1.getID().equals(model.getID()))
                                holder.sign.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        switch (model.getFeedback()){
            case "accept":
                holder.custViewAccept.setVisibility(View.VISIBLE);
                holder.acceptBTN.setVisibility(View.GONE);
                holder.cancelBTN.setVisibility(View.GONE);
                holder.finishBTN.setVisibility(View.VISIBLE);
                break;
            case "finish":
                holder.custViewAccept.setVisibility(View.VISIBLE);
                holder.custViewAccept.setText("Finished");
                FirebaseDatabase.getInstance().getReference().child("SERVICE_CENT_CUSTOMERS")
                        .orderByChild("shopID").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot ds: snapshot.getChildren()){
                                    ServCentCustHistModel histModel = ds.getValue(ServCentCustHistModel.class);
                                    if (histModel.getID().equals(model.getID())) {
                                        holder.histCheck = true;
                                        break;
                                    }

                                }
                                if(holder.histCheck){
                                    holder.addCustomerBTN.setVisibility(View.GONE);
                                }else
                                    holder.addCustomerBTN.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                break;
            case "pending":
                holder.acceptBTN.setVisibility(View.VISIBLE);
                holder.cancelBTN.setVisibility(View.VISIBLE);
                break;
        }

        FirebaseDatabase.getInstance().getReference().child("USERS").child(model.getID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            name = snapshot.child("firstname").getValue(String.class) +" "+snapshot.child("lastname").getValue(String.class);
                            address = snapshot.child("address").getValue(String.class);
                            holder.custName.setText(name);
                            holder.custAddress.setText(address);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.custServExpand.getVisibility() == View.GONE) {
                    holder.custServExpand.setVisibility(View.VISIBLE);
                    holder.clickContent.setText("Click to hide content");
                }else{
                    holder.custServExpand.setVisibility(View.GONE);
                    holder.clickContent.setText("Click to view content");
                }
            }
        });
        holder.cancelBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.notify = true;
                Date d = new Date(System.currentTimeMillis());
                Calendar c = Calendar.getInstance();
                c.setTime(d);
                String month = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
                String monthReg = month.substring(0, 3);
                String key = FirebaseDatabase.getInstance().getReference().child("DRIVER_NOTIFY").push().getKey();
                HashMap<String, Object> hashMap1 = new HashMap<>();
                hashMap1.put("feedback", "cancel");
                hashMap1.put("shopID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                hashMap1.put("ID", model.getID());
                hashMap1.put("key", model.getKey());
                hashMap1.put("monthReg", monthReg);
                hashMap1.put("seen", "new");

                FirebaseDatabase.getInstance().getReference().child("DRIVER_NOTIFY").child(key).setValue(hashMap1);
                FirebaseDatabase.getInstance().getReference().child("USERS").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    if (holder.notify) {
                                        String name = snapshot.child("name").getValue().toString();
                                        sendNotification(model.getID(), name, "Cancelled your request.", "driver");
                                    }
                                    holder.notify = false;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                FirebaseDatabase.getInstance().getReference().child("DRIVER_SERVICE_CENT_ISSUE").child(model.getKey()).removeValue();

                Toast.makeText(context, "Issue Cancelled", Toast.LENGTH_SHORT).show();


            }
        });
        holder.acceptBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.notify = true;
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("feedback", "accept");
                hashMap.put("seen", "old");

                Date d = new Date(System.currentTimeMillis());
                Calendar c = Calendar.getInstance();
                c.setTime(d);
                String month = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
                String monthReg = month.substring(0, 3);
                String key = FirebaseDatabase.getInstance().getReference().child("DRIVER_NOTIFY").push().getKey();
                HashMap<String, Object> hashMap1 = new HashMap<>();
                hashMap1.put("feedback", "accept");
                hashMap1.put("shopID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                hashMap1.put("ID", model.getID());
                hashMap1.put("key", key);
                hashMap1.put("monthReg", monthReg);
                hashMap1.put("seen", "new");

                FirebaseDatabase.getInstance().getReference().child("DRIVER_NOTIFY").child(key).setValue(hashMap1);

                FirebaseDatabase.getInstance().getReference().child("DRIVER_SERVICE_CENT_ISSUE").child(model.getKey()).updateChildren(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                FirebaseDatabase.getInstance().getReference().child("USERS").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    if (holder.notify) {
                                                        String name = snapshot.child("name").getValue().toString();
                                                        sendNotification(model.getID(), name, "Approve your request.", "driver");
                                                    }
                                                    holder.notify = false;
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                Toast.makeText(context, "Issue Accepted", Toast.LENGTH_SHORT).show();
                                holder.custViewAccept.setVisibility(View.VISIBLE);
                                holder.acceptBTN.setVisibility(View.GONE);
                                holder.cancelBTN.setVisibility(View.GONE);
                                holder.finishBTN.setVisibility(View.VISIBLE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Error!", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
        holder.finishBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("feedback", "finish");
                hashMap.put("seen", "old");

                FirebaseDatabase.getInstance().getReference().child("DRIVER_SERVICE_CENT_ISSUE").child(model.getKey()).updateChildren(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context, "Issue Accepted", Toast.LENGTH_SHORT).show();
                                holder.custViewAccept.setVisibility(View.VISIBLE);
                                holder.finishBTN.setVisibility(View.GONE);
                                holder.custViewAccept.setText("Finished");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Error!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        holder.addCustomerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogPlus dp = DialogPlus.newDialog(context)
                        .setContentHolder(new com.orhanobut.dialogplus.ViewHolder(R.layout.custom_add_to_customer_item_layout_dialog))
                        .setExpanded(true, 500)
                        .create();

                View v = dp.getHolderView();
                EditText EdtType = v.findViewById(R.id.vehicleTypeEdt);
                EditText EdtModel = v.findViewById(R.id.vehicleModelEdt);
                Button UpdateBtnE = v.findViewById(R.id.EditSaveBtn);

                dp.show();

                UpdateBtnE.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (EdtModel.getText().toString().isEmpty() | EdtType.getText().toString().isEmpty()) {
                            if (EdtModel.getText().toString().isEmpty())
                                EdtModel.setError("Field should not be empty");
                            if (EdtType.getText().toString().isEmpty())
                                EdtType.setError("Field should not be empty");
                            return;
                        } else {

                            String key = FirebaseDatabase.getInstance().getReference().child("SERVICE_CENT_CUSTOMERS").push().getKey();
                            HashMap<String, Object> Update = new HashMap<>();
                            Update.put("ID", model.getID());
                            Update.put("key", key);
                            Update.put("shopID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            Update.put("model", EdtModel.getText().toString());
                            Update.put("type", EdtType.getText().toString());

                            FirebaseDatabase.getInstance().getReference().child("SERVICE_CENT_CUSTOMERS").child(key).setValue(Update)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(context, "Customer added successfully", Toast.LENGTH_SHORT).show();
                                            holder.addCustomerBTN.setVisibility(View.GONE);
                                            dp.dismiss();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(view.getContext(), "Error while added", Toast.LENGTH_SHORT).show();
                                    dp.dismiss();
                                }
                            });
                        }
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
        TextView custName, custAddress, clickContent, custIssue, custViewAccept, custViewCancel;
        LinearLayout custServExpand;
        Button acceptBTN, cancelBTN, finishBTN, addCustomerBTN;
        ImageView sign;
        boolean notify = false, histCheck = false;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            sign = itemView.findViewById(R.id.customerSign);
            custName = itemView.findViewById(R.id.customerServiceName);
            custAddress = itemView.findViewById(R.id.customerServiceAddress);
            clickContent = itemView.findViewById(R.id.customerServiceClickContent);
            custIssue = itemView.findViewById(R.id.customerTextDescription);
            custServExpand = itemView.findViewById(R.id.customerServiceExpandable);
            custViewAccept = itemView.findViewById(R.id.CustomerViewAccepted);
            custViewCancel = itemView.findViewById(R.id.CustomerViewCanceled);
            acceptBTN = itemView.findViewById(R.id.CustomerServiceAcceptBTN);
            cancelBTN = itemView.findViewById(R.id.CustomerServiceCancelBTN);
            finishBTN = itemView.findViewById(R.id.CustomerServiceFinishBTN);
            addCustomerBTN = itemView.findViewById(R.id.addToCustomerBTN);
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
