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

import com.example.idetect.Models.ServCentMechListModel;
import com.example.idetect.Models.ServCentMechOnCallModel;
import com.example.idetect.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ServCentMechOnCallAdapter extends FirebaseRecyclerAdapter<ServCentMechOnCallModel, ServCentMechOnCallAdapter.myViewHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ServCentMechOnCallAdapter(@NonNull FirebaseRecyclerOptions<ServCentMechOnCallModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull ServCentMechOnCallModel model) {
        holder.Name.setText(model.getFirstname());
        holder.Address.setText(model.getAddress());
        holder.Details.setText(model.getInput_Details());

        holder.CardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.ExpandView.getVisibility() == View.GONE) {
                    holder.ExpandView.setVisibility(View.VISIBLE);
                }else{
                    holder.ExpandView.setVisibility(View.GONE);
                }
            }
        });
        holder.HireBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list_service_center_on_call_mechanic, parent, false);
        return new myViewHolder(itemView);
    }

    class myViewHolder extends RecyclerView.ViewHolder{
        TextView Name, Address, Details;
        CardView CardBtn;
        Button HireBtn;
        LinearLayout ExpandView;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            Name = itemView.findViewById(R.id.mechanicOnCall_name);
            Address = itemView.findViewById(R.id.mechanicOnCall_adress);
            Details = itemView.findViewById(R.id.descriptionOnCall);

            CardBtn = itemView.findViewById(R.id.OnCallCardBTN);
            ExpandView = itemView.findViewById(R.id.expandableOnCallMechanic);

            HireBtn = itemView.findViewById(R.id.hire_mechBTN);
        }
    }
}
