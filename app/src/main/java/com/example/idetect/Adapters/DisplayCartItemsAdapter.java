package com.example.idetect.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.idetect.CustomServiceCenterAutoPartsViewItems;
import com.example.idetect.FragmentServiceCenterAutoPartsCart;
import com.example.idetect.Models.CartItemsModel;
import com.example.idetect.Models.ItemsModel;
import com.example.idetect.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class DisplayCartItemsAdapter extends RecyclerView.Adapter<DisplayCartItemsAdapter.ViewHolder> {

    private Context context;
    private List<CartItemsModel> modelList;
    String shopID;

    public DisplayCartItemsAdapter(Context context, List<CartItemsModel> model) {
        this.context = context;
        this.modelList = model;
    }


    @NonNull
    @Override
    public DisplayCartItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_view, parent, false);
        return new ViewHolder(itemView);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull DisplayCartItemsAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        CartItemsModel model = modelList.get(position);

        holder.ItemCounter.setText(holder.Counter + "");
        FirebaseDatabase.getInstance().getReference().child("ITEMS")
                .child(model.getItemKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    ItemsModel itemsModel = snapshot.getValue(ItemsModel.class);
                    shopID = itemsModel.getShopUID();
                    holder.CItemName.setText(itemsModel.getItem_Name());
                    holder.CItemPrice.setText(itemsModel.getPrice());
                    holder.stocks = Integer.parseInt(itemsModel.getQty());
                    Glide.with(holder.CItemImg.getContext())
                            .load(itemsModel.getItem_Surl())
                            .placeholder(R.drawable.location_logo)
                            .error(R.drawable.location_logo)
                            .into(holder.CItemImg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.BuyNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String orderKey = FirebaseDatabase.getInstance().getReference().child("ORDERS").push().getKey();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("ID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                hashMap.put("ShopUID", shopID);
                hashMap.put("key", orderKey);
                hashMap.put("ItemKey", model.getItemKey());
                hashMap.put("Qty", ""+holder.Counter);
                hashMap.put("status", "pending");
                hashMap.put("seen", "new");

                String key = FirebaseDatabase.getInstance().getReference().child("AUTO_PARTS_NOTIFY").push().getKey();
                HashMap<String, Object> hashMap1 = new HashMap<>();
                hashMap1.put("shopID", shopID);
                hashMap1.put("ID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                hashMap1.put("key", key);
                hashMap1.put("feedback", "pending");
                hashMap1.put("seen", "new");

                FirebaseDatabase.getInstance().getReference().child("AUTO_PARTS_NOTIFY").child(key).setValue(hashMap1);

                FirebaseDatabase.getInstance().getReference().child("ORDERS").child(orderKey).setValue(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context, "Wait for approval", Toast.LENGTH_SHORT).show();
                            }
                        });

                FirebaseDatabase.getInstance().getReference().child("ITEM_CART").child(model.getCartKey())
                        .removeValue();
                modelList.remove(position);
                notifyItemRangeChanged(position, modelList.size());
                notifyItemRemoved(position);
                notifyDataSetChanged();
            }
        });

        holder.DelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if (position >= 0) {
                    FirebaseDatabase.getInstance().getReference().child("ITEM_CART")
                            .child(model.getCartKey()).removeValue();
                    notifyDataSetChanged();
                }*/
                AlertDialog.Builder AB = new AlertDialog.Builder(holder.CItemName.getContext());
                AB.setTitle("Want to delete the item "+holder.CItemName.getText().toString()+ " from cart?");
                AB.setMessage("This cannot be undo.");
                AB.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase.getInstance().getReference().child("ITEM_CART").child(model.getCartKey())
                                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(holder.CItemName.getContext(), "Item removed", Toast.LENGTH_SHORT).show();
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

        holder.PlusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.Counter++;
                if(holder.Counter >= holder.stocks){
                    Toast.makeText(view.getContext(), "You have reach the maximum item.", Toast.LENGTH_SHORT).show();
                    holder.Counter = holder.stocks;
                }
                holder.ItemCounter.setText(holder.Counter + "");
            }
        });
        holder.MinusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.Counter--;
                if(holder.Counter <= 1){
                    holder.Counter = 1;
                }
                holder.ItemCounter.setText(holder.Counter + "");
            }
        });

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView CItemName, CItemPrice, ItemCounter;
        ImageView CItemImg;
        private Button BuyNowBtn, DelBtn;
        private CardView PlusBtn, MinusBtn;
        int Counter = 1, stocks = 0;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            CItemName = itemView.findViewById(R.id.nameItemTB);
            CItemPrice = itemView.findViewById(R.id.priceItemTB);
            CItemImg = itemView.findViewById(R.id.cartItemImg);
            BuyNowBtn = itemView.findViewById(R.id.CartBuyNowBtn);
            DelBtn = itemView.findViewById(R.id.CartDeleteBtn);

            PlusBtn = itemView.findViewById(R.id.CardPlusBtn);
            MinusBtn = itemView.findViewById(R.id.CardMinusBtn);
            ItemCounter = itemView.findViewById(R.id.ItemCounterTxt);
        }
    }
}
