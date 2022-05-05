package com.example.idetect.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.example.idetect.Models.ItemsModel;
import com.example.idetect.Models.OrderModel;
import com.example.idetect.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.HashMap;
import java.util.List;

public class DisplayOwnItemsAdapter extends RecyclerView.Adapter<DisplayOwnItemsAdapter.ViewHolder> {

    public DisplayOwnItemsAdapter(Context context, List<ItemsModel> model) {
        this.context = context;
        this.modelList = model;
    }

    private Context context;
    private List<ItemsModel> modelList;
    int sold_counter = 0;

    @NonNull
    @Override
    public DisplayOwnItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_service_center_grid_items_update_delete, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DisplayOwnItemsAdapter.ViewHolder holder, int position) {
        ItemsModel model = modelList.get(position);

        holder.ItemName.setText(model.getItem_Name());
        holder.ItemPrice.setText("₱ " + model.getPrice() + ".00");
        holder.ItemQTY.setText(model.getQty() + " left");

        FirebaseDatabase.getInstance().getReference().child("ORDERS")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        sold_counter = 0;
                        for (DataSnapshot ds: snapshot.getChildren()){
                            OrderModel model1 = ds.getValue(OrderModel.class);
                            if (model1.getItemKey().equals(model.getItemKey()) &&
                                    (model1.getStatus().equals("accept") || model1.getStatus().equals("complete")))
                                sold_counter = sold_counter + Integer.parseInt(model1.getQty());
                        }

                        if (sold_counter == 0){
                            holder.ItemSold.setText("0 sold");
                        }else
                            holder.ItemSold.setText(sold_counter + " sold");
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



        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogPlus dp = DialogPlus.newDialog(holder.ItemImg.getContext())
                        .setContentHolder(new com.orhanobut.dialogplus.ViewHolder(R.layout.custom_item_layout_dialog))
                        .setExpanded(true, 700)
                        .create();

                View v = dp.getHolderView();
                EditText Edtname = v.findViewById(R.id.itemNameEdit);
                EditText EdtPrce = v.findViewById(R.id.itemPriceEdit);
                EditText EdtQty = v.findViewById(R.id.itemQtyEdit);
                Button UpdateBtnE = v.findViewById(R.id.EditSaveBtn);
                Edtname.setText(model.getItem_Name());
                EdtPrce.setText(model.getPrice());
                EdtQty.setText(model.getQty());
                dp.show();

                UpdateBtnE.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (Edtname.getText().toString().isEmpty() | EdtPrce.getText().toString().isEmpty() | EdtQty.getText().toString().isEmpty()) {
                            if (Edtname.getText().toString().isEmpty())
                                Edtname.setError("Field should not be empty");
                            if (EdtPrce.getText().toString().isEmpty())
                                EdtPrce.setError("Field should not be empty");
                            if (EdtQty.getText().toString().isEmpty())
                                EdtQty.setError("Field should not be empty");
                            return;
                        } else {
                            HashMap<String, Object> Update = new HashMap<>();
                            Update.put("Item_Name", Edtname.getText().toString());
                            Update.put("Price", EdtPrce.getText().toString());
                            Update.put("Qty", EdtQty.getText().toString());

                            FirebaseDatabase.getInstance().getReference().child("ITEMS").child(model.getItemKey())
                                    .updateChildren(Update)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(holder.ItemName.getContext(), "Updated Successfully", Toast.LENGTH_SHORT).show();
                                            dp.dismiss();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(view.getContext(), "Error while update", Toast.LENGTH_SHORT).show();
                                    dp.dismiss();
                                }
                            });
                        }
                    }
                });
            }
        });
        holder.delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder AB = new AlertDialog.Builder(holder.ItemName.getContext());
                AB.setTitle("want to delete?");
                AB.setMessage("This cannot be undo.");
                AB.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase.getInstance().getReference().child("ITEMS").child(model.getItemKey())
                                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context, "Item removed", Toast.LENGTH_SHORT).show();
                                notifyDataSetChanged();
                            }
                        });

                        FirebaseDatabase.getInstance().getReference().child("ORDERS")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot ds: snapshot.getChildren()){
                                            OrderModel orderModel = ds.getValue(OrderModel.class);
                                            if (orderModel.getItemKey().equals(model.getItemKey())){
                                                FirebaseDatabase.getInstance().getReference().child("ORDERS").child(orderModel.getKey()).removeValue();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }
                });
                AB.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AB.show();

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
        CardView CardItemBTN; // sa button nga item
        Button editBtn, delBtn;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ItemName = itemView.findViewById(R.id.ItemNameTB);
            ItemPrice = itemView.findViewById(R.id.itemPrice);
            ItemQTY = itemView.findViewById(R.id.item_quantity);
            ItemSold = itemView.findViewById(R.id.itemStockSold);
            ItemImg = itemView.findViewById(R.id.item_image);

            editBtn = itemView.findViewById(R.id.itemEditBtn);
            delBtn = itemView.findViewById(R.id.itemDeleteBtn);

            CardItemBTN = itemView.findViewById(R.id.CardItemBTN);
        }
    }
}
