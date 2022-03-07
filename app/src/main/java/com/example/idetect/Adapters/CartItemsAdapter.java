package com.example.idetect.Adapters;

import android.annotation.SuppressLint;
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
import com.example.idetect.Models.CartItemsModel;
import com.example.idetect.Models.ItemsModel;
import com.example.idetect.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class CartItemsAdapter extends FirebaseRecyclerAdapter<CartItemsModel, CartItemsAdapter.myViewHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public CartItemsAdapter(@NonNull FirebaseRecyclerOptions<CartItemsModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull CartItemsModel model) {
        FirebaseDatabase.getInstance().getReference().child("ITEMS").child(model.getItemKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    ItemsModel itemsModel = snapshot.getValue(ItemsModel.class);
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
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("ORDER");
                String orderKey = ref.push().getKey();
                HashMap<String , Object> map = new HashMap<>();
                map.put("ordeyKey", orderKey);
                map.put("ID", model.getID());
                map.put("itemKey", model.getItemKey());
                map.put("qty", holder.Counter);

                ref.child(orderKey).setValue(map);
            }
        });
        holder.DelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("ITEM_CART").child(model.getCartKey())
                        .removeValue();
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

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_view, parent, false);
        return new myViewHolder(itemView);
    }

    class myViewHolder extends RecyclerView.ViewHolder{
        TextView CItemName, CItemPrice, ItemCounter;
        ImageView CItemImg;
        private Button BuyNowBtn, DelBtn;
        private CardView PlusBtn, MinusBtn;
        int Counter = 1, stocks = 0;

        public myViewHolder(@NonNull View itemView) {
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
