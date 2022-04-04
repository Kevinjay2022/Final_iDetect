package com.example.idetect.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.idetect.Models.ServCentCustHistModel;
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

public class DisplayServCentCustHistAdapter extends RecyclerView.Adapter<DisplayServCentCustHistAdapter.ViewHolder> {

    private Context context;
    private List<ServCentCustHistModel> modelList;
    String name, address;

    public DisplayServCentCustHistAdapter(Context context, List<ServCentCustHistModel> modelList) {
        this.context = context;
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public DisplayServCentCustHistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list_service_center_customer_order_history, parent, false);
        return new DisplayServCentCustHistAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DisplayServCentCustHistAdapter.ViewHolder holder, int position) {
        ServCentCustHistModel model = modelList.get(position);


        FirebaseDatabase.getInstance().getReference().child("USERS").child(model.getID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            name = snapshot.child("firstname").getValue(String.class) +" "+snapshot.child("lastname").getValue(String.class);
                            address = snapshot.child("address").getValue(String.class);
                            holder.CustName.setText(name);
                            holder.CustAdd.setText(address);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.CustVType.setText(model.getType());
        holder.CustVModel.setText(model.getModel());

        holder.CardCustBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.ExpandLayout.getVisibility() == View.GONE) {
                    holder.ExpandLayout.setVisibility(View.VISIBLE);
                }else{
                    holder.ExpandLayout.setVisibility(View.GONE);
                }
            }
        });

        holder.EditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogPlus dp = DialogPlus.newDialog(context)
                        .setContentHolder(new com.orhanobut.dialogplus.ViewHolder(R.layout.custom_add_to_customer_item_layout_dialog))
                        .setExpanded(true, 700)
                        .create();

                View v = dp.getHolderView();
                TextView label = v.findViewById(R.id.customerLabel);
                TextView Edtname = v.findViewById(R.id.customerName);
                TextView EdtAddress = v.findViewById(R.id.customerAddress);
                EditText EdtType = v.findViewById(R.id.vehicleTypeEdt);
                EditText EdtModel = v.findViewById(R.id.vehicleModelEdt);
                Button UpdateBtnE = v.findViewById(R.id.EditSaveBtn);

                label.setText("Update customer details");
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
                            Update.put("model", EdtModel.getText().toString());
                            Update.put("type", EdtType.getText().toString());

                            FirebaseDatabase.getInstance().getReference().child("SERVICE_CENT_CUSTOMERS").child(key).updateChildren(Update)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(context, "Customer added successfully", Toast.LENGTH_SHORT).show();
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
        TextView CustName, CustAdd, CustVType, CustVModel;
        CardView CardCustBTN; // sa button nga customer
        LinearLayout ExpandLayout;
        Button EditBtn;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            CustName = itemView.findViewById(R.id.CustomerName);
            CustAdd = itemView.findViewById(R.id.CustAddressTB);
            CustVType = itemView.findViewById(R.id.CustVTypeTB);
            CustVModel = itemView.findViewById(R.id.CustMTypeTB);

            CardCustBTN = itemView.findViewById(R.id.DisplayExpandLayout);// sa button nga item
            ExpandLayout = itemView.findViewById(R.id.CustomerHistoryLayout);
            EditBtn = itemView.findViewById(R.id.CustEditBTN);
        }
    }
}
