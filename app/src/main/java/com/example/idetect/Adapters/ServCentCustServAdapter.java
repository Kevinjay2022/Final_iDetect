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
import com.example.idetect.Models.ServCentCustomerService;
import com.example.idetect.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;

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
        FirebaseDatabase.getInstance().getReference().child("SERVICE_CENT_CUSTOMERS").orderByChild("ID").equalTo(model.getID())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
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
                FirebaseDatabase.getInstance().getReference().child("SERVICE_CENT_CUSTOMERS").orderByChild("ID").equalTo(model.getID())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
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

                String key = FirebaseDatabase.getInstance().getReference().child("DRIVER_NOTIFY").push().getKey();
                HashMap<String, Object> hashMap1 = new HashMap<>();
                hashMap1.put("feedback", "cancel");
                hashMap1.put("shopID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                hashMap1.put("ID", model.getID());
                hashMap1.put("key", model.getKey());
                hashMap1.put("seen", "new");

                FirebaseDatabase.getInstance().getReference().child("DRIVER_NOTIFY").child(key).setValue(hashMap1);

                FirebaseDatabase.getInstance().getReference().child("DRIVER_SERVICE_CENT_ISSUE").child(model.getKey()).removeValue();

                Toast.makeText(context, "Issue Cancelled", Toast.LENGTH_SHORT).show();


            }
        });
        holder.acceptBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("feedback", "accept");

                String key = FirebaseDatabase.getInstance().getReference().child("DRIVER_NOTIFY").push().getKey();
                HashMap<String, Object> hashMap1 = new HashMap<>();
                hashMap1.put("feedback", "accept");
                hashMap1.put("shopID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                hashMap1.put("ID", model.getID());
                hashMap1.put("key", key);
                hashMap1.put("seen", "new");

                FirebaseDatabase.getInstance().getReference().child("DRIVER_NOTIFY").child(key).setValue(hashMap1);

                FirebaseDatabase.getInstance().getReference().child("DRIVER_SERVICE_CENT_ISSUE").child(model.getKey()).updateChildren(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
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
                        .setExpanded(true, 700)
                        .create();

                View v = dp.getHolderView();
                TextView Edtname = v.findViewById(R.id.customerName);
                TextView EdtAddress = v.findViewById(R.id.customerAddress);
                EditText EdtType = v.findViewById(R.id.vehicleTypeEdt);
                EditText EdtModel = v.findViewById(R.id.vehicleModelEdt);
                Button UpdateBtnE = v.findViewById(R.id.EditSaveBtn);

                Edtname.setText(name);
                EdtAddress.setText(address);
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
}
