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
import com.example.idetect.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class ServCentMechListAdapter extends FirebaseRecyclerAdapter<ServCentMechListModel, ServCentMechListAdapter.myViewHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ServCentMechListAdapter(@NonNull FirebaseRecyclerOptions<ServCentMechListModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull ServCentMechListModel model) {
        holder.Name.setText(model.getName());
        holder.Address.setText(model.getAddress());
        holder.Birth.setText(model.getBirth());
        holder.Skills.setText(model.getSkills());
        holder.Gender.setText(model.getGender());
        holder.Status.setText(model.getStatus());
        holder.Mech_Type.setText(model.getMech_type());


        // Sa item button
        holder.CardItemBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.DetailsLayout.getVisibility() == View.GONE){
                    holder.DetailsLayout.setVisibility(View.VISIBLE);
                }else{
                    holder.DetailsLayout.setVisibility(View.GONE);
                }
            }
        });
        holder.EditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Gi Click " + model.getName() ,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list_service_center_mechanics, parent, false);
        return new myViewHolder(itemView);
    }

    class myViewHolder extends RecyclerView.ViewHolder{
        TextView Name, Address, Birth, Skills, Gender, Status, Mech_Type;
        CardView CardItemBTN; // sa button nga item
        LinearLayout DetailsLayout;
        Button EditBtn, DisBtn;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            Name = itemView.findViewById(R.id.NameMechTV);
            Address = itemView.findViewById(R.id.addressMechTV);
            Birth = itemView.findViewById(R.id.birthMechTV);
            Skills = itemView.findViewById(R.id.skillsMechTV);
            Gender = itemView.findViewById(R.id.genderMechTV);
            Status = itemView.findViewById(R.id.statusMechTV);
            Mech_Type = itemView.findViewById(R.id.typeMechTV);

            EditBtn = itemView.findViewById(R.id.EditMechBTN);
            DisBtn = itemView.findViewById(R.id.DisableMechBTN);

            CardItemBTN = itemView.findViewById(R.id.mechCardBTN);// sa button nga item
            DetailsLayout = itemView.findViewById(R.id.detailsExpandMechLayout);
        }
    }
}
