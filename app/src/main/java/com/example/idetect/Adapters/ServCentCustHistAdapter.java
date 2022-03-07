package com.example.idetect.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.idetect.Models.ServCentCustHistModel;
import com.example.idetect.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.Objects;

public class ServCentCustHistAdapter extends FirebaseRecyclerAdapter<ServCentCustHistModel, ServCentCustHistAdapter.myViewHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ServCentCustHistAdapter(@NonNull FirebaseRecyclerOptions<ServCentCustHistModel> options) {
        super(Objects.requireNonNull(options));
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull ServCentCustHistModel model) {
        holder.CustName.setText(model.getCustomer_Name());
        holder.CustAdd.setText(model.getCustomer_Address());
        holder.CustVType.setText(model.getVehicle_type());
        holder.CustVModel.setText(model.getVehicle_Model());

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
                Toast.makeText(view.getContext(), "Imo gi click" + model.getCustomer_Name(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list_service_center_customer_order_history, parent, false);
        return new myViewHolder(itemView);
    }

    class myViewHolder extends RecyclerView.ViewHolder{
        TextView CustName, CustAdd, CustVType, CustVModel;
        CardView CardCustBTN; // sa button nga customer
        LinearLayout ExpandLayout;
        Button EditBtn;

        public myViewHolder(@NonNull View itemView) {
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
