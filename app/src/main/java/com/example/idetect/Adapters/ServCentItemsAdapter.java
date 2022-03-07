package com.example.idetect.Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.idetect.CustomServiceCenterAutoPartsViewItems;
import com.example.idetect.Models.ItemsModel;
import com.example.idetect.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class ServCentItemsAdapter extends FirebaseRecyclerAdapter<ItemsModel, ServCentItemsAdapter.myViewHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ServCentItemsAdapter(@NonNull FirebaseRecyclerOptions<ItemsModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull ItemsModel model) {
        holder.ItemName.setText(model.getItem_Name());
        holder.ItemPrice.setText("₱ " + model.getPrice() + ".00");
        holder.ItemQTY.setText(model.getQty() + " left");
        holder.ItemSold.setText(model.getSold() + " sold");

        Glide.with(holder.ItemImg.getContext())
                .load(model.getItem_Surl())
                .placeholder(R.drawable.location_logo)
                .error(R.drawable.location_logo)
                .into(holder.ItemImg);

        // Sa item button
        holder.CardItemBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(view.getContext(), "name "+ model.getItem_Name(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(view.getContext(), CustomServiceCenterAutoPartsViewItems.class);
                intent.putExtra("ItemKey", model.getItemKey());
                view.getContext().startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_service_center_grid_items, parent, false);
        return new myViewHolder(itemView);
    }

    class myViewHolder extends RecyclerView.ViewHolder{
        TextView ItemName, ItemPrice, ItemSold, ItemQTY;
        ImageView ItemImg;
        CardView CardItemBTN; // sa button nga item

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            ItemName = itemView.findViewById(R.id.ItemNameTB);
            ItemPrice = (TextView)itemView.findViewById(R.id.itemPrice);
            ItemQTY = (TextView)itemView.findViewById(R.id.item_quantity);
            ItemSold = (TextView)itemView.findViewById(R.id.itemStockSold);
            ItemImg = itemView.findViewById(R.id.item_image);

            CardItemBTN = itemView.findViewById(R.id.CardItemBTN);// sa button nga item
        }
    }
}
