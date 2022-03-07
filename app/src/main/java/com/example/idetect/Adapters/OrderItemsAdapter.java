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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.idetect.Models.CartItemsModel;
import com.example.idetect.Models.ItemsModel;
import com.example.idetect.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class OrderItemsAdapter extends FirebaseRecyclerAdapter<CartItemsModel, OrderItemsAdapter.myViewHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public OrderItemsAdapter(@NonNull FirebaseRecyclerOptions<CartItemsModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull CartItemsModel model) {

        holder.cardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.expand.getVisibility() == View.GONE){
                    holder.expand.setVisibility(View.VISIBLE);
                }else{
                    holder.expand.setVisibility(View.GONE);
                }
            }
        });
        holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list_service_center_customer_orders, parent, false);
        return new myViewHolder(itemView);
    }

    class myViewHolder extends RecyclerView.ViewHolder{
        private TextView NameTV;
        private Button acceptBtn, cancelBtn;
        private ConstraintLayout expand;
        private CardView cardBtn;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            NameTV = itemView.findViewById(R.id.CustNameTB);

            cardBtn = itemView.findViewById(R.id.orderCardBtn);
            expand = itemView.findViewById(R.id.expandableItemLists);

            acceptBtn = itemView.findViewById(R.id.acceptOrderBtn);
            cancelBtn = itemView.findViewById(R.id.cancelOrderBtn);
        }
    }
}
