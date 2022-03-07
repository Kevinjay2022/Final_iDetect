package com.example.idetect.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import com.example.idetect.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.HashMap;

public class DispOwnItemsAdapter extends FirebaseRecyclerAdapter<ItemsModel, DispOwnItemsAdapter.myViewHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public DispOwnItemsAdapter(@NonNull FirebaseRecyclerOptions<ItemsModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull ItemsModel model) {
        holder.ItemName.setText(model.getItem_Name());
        holder.ItemPrice.setText("₱ " + model.getPrice() + ".00");
        holder.ItemQTY.setText(model.getQty() + " left");
        holder.ItemSold.setText(model.getSold() + " sold");

        Glide.with(holder.ItemImg.getContext())
                .load(model.getItem_Surl())
                .placeholder(R.drawable.location_logo)
                .error(R.drawable.location_logo)
                .into(holder.ItemImg);



        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogPlus dp = DialogPlus.newDialog(holder.ItemImg.getContext())
                        .setContentHolder(new ViewHolder(R.layout.custom_item_layout_dialog))
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
                        HashMap<String, Object> Update = new HashMap<>();
                        Update.put("Item_Name", Edtname.getText().toString());
                        Update.put("Price", EdtPrce.getText().toString());
                        Update.put("Qty", EdtQty.getText().toString());

                        FirebaseDatabase.getInstance().getReference().child("ITEMS").child(getRef(position).getKey())
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
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase.getInstance().getReference().child("ITEMS").child(getRef(position).getKey())
                                .removeValue();
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

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_service_center_grid_items_update_delete, parent, false);
        return new myViewHolder(itemView);
    }

    class myViewHolder extends RecyclerView.ViewHolder{
        TextView ItemName, ItemPrice, ItemSold, ItemQTY;
        ImageView ItemImg;
        CardView CardItemBTN; // sa button nga item
        Button editBtn, delBtn;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            ItemName = itemView.findViewById(R.id.ItemNameTB);
            ItemPrice = itemView.findViewById(R.id.itemPrice);
            ItemQTY = itemView.findViewById(R.id.item_quantity);
            ItemSold = itemView.findViewById(R.id.itemStockSold);
            ItemImg = itemView.findViewById(R.id.item_image);

            editBtn = itemView.findViewById(R.id.itemEditBtn);
            delBtn = itemView.findViewById(R.id.itemDeleteBtn);

            CardItemBTN = itemView.findViewById(R.id.CardItemBTN);// sa button nga item
        }
    }
}
