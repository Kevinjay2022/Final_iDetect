package com.example.idetect.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.idetect.Models.ItemsModel;
import com.example.idetect.Models.OrderModel;
import com.example.idetect.Models.ServCentMechListModel;
import com.example.idetect.Models.ServCentMechOnCallModel;
import com.example.idetect.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class DisplayServCentMechListAdapter extends RecyclerView.Adapter<DisplayServCentMechListAdapter.ViewHolder> {

    public DisplayServCentMechListAdapter(Context context, List<ServCentMechListModel> model) {
        this.context = context;
        this.modelList = model;
    }

    private Context context;
    private List<ServCentMechListModel> modelList;

    @NonNull
    @Override
    public DisplayServCentMechListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list_service_center_mechanics, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DisplayServCentMechListAdapter.ViewHolder holder, int position) {
        ServCentMechListModel model = modelList.get(position);
        holder.Name.setText(model.getName());
        holder.Address.setText(model.getAddress());
        holder.Birth.setText(model.getBirth());
        holder.Skills.setText(model.getSkills());
        holder.Gender.setText(model.getGender());
        holder.Status.setText(model.getStatus());
        if (model.getStatus().equals("active")) {
            holder.actBtn.setVisibility(View.GONE);
            holder.DisBtn.setVisibility(View.VISIBLE);
        }else{
            holder.actBtn.setVisibility(View.VISIBLE);
            holder.DisBtn.setVisibility(View.GONE);
        }

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
                final DialogPlus dp = DialogPlus.newDialog(context)
                        .setContentHolder(new com.orhanobut.dialogplus.ViewHolder(R.layout.custom_mechanic_update_dialog))
                        .setExpanded(true, 700)
                        .create();

                View v = dp.getHolderView();
                EditText Edtname = v.findViewById(R.id.updateaddMechanicNameTB);
                EditText EdtAddress = v.findViewById(R.id.updateaddMechanicAddressTB);
                TextView EdtBdate = v.findViewById(R.id.updateaddMechanicBirthDateTB);
                EditText EdtSkills = v.findViewById(R.id.updateMechanicSkillsTB);
                ImageView CalDate = v.findViewById(R.id.updatedateCalendar);
                Button UpdateBtnE = v.findViewById(R.id.updateMechanicSaveBTN);
                Edtname.setText(model.getName());
                EdtAddress.setText(model.getAddress());
                EdtBdate.setText(model.getBirth());
                EdtSkills.setText(model.getSkills());
                dp.show();

                CalDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Calendar c = Calendar.getInstance();
                        holder.mYear = c.get(Calendar.YEAR);
                        holder.mMonth = c.get(Calendar.MONTH);
                        holder.mDay = c.get(Calendar.DAY_OF_MONTH);


                        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                                new DatePickerDialog.OnDateSetListener() {

                                    @Override
                                    public void onDateSet(DatePicker view, int year,
                                                          int monthOfYear, int dayOfMonth) {

                                        EdtBdate.setText((monthOfYear + 1) + "-" + dayOfMonth + "-" +  year);

                                    }
                                }, holder.mYear, holder.mMonth, holder.mDay);
                        datePickerDialog.show();
                    }
                });

                UpdateBtnE.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (Edtname.getText().toString().isEmpty() | EdtAddress.getText().toString().isEmpty() | EdtSkills.getText().toString().isEmpty()) {
                            if (Edtname.getText().toString().isEmpty())
                                Edtname.setError("Field should not be empty");
                            if (EdtAddress.getText().toString().isEmpty())
                                EdtAddress.setError("Field should not be empty");
                            if (EdtSkills.getText().toString().isEmpty())
                                EdtSkills.setError("Field should not be empty");
                            return;
                        } else {
                            HashMap<String, Object> Update = new HashMap<>();
                            Update.put("name", Edtname.getText().toString());
                            Update.put("address", EdtAddress.getText().toString());
                            Update.put("birth", EdtBdate.getText().toString());
                            Update.put("skills", EdtSkills.getText().toString());

                            FirebaseDatabase.getInstance().getReference().child("SERVICE_CENT_MECHANICS").child(model.getKey())
                                    .updateChildren(Update)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show();
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
                AlertDialog.Builder AB = new AlertDialog.Builder(context);
                AB.setTitle("Want to delete?");
                AB.setMessage("This cannot be undo.");
                AB.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase.getInstance().getReference().child("SERVICE_CENT_MECHANICS").child(model.getKey())
                                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context, "Item removed", Toast.LENGTH_SHORT).show();
                                if (model.getMech_type().equals("on-call")) {
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("status", "not hired");
                                    FirebaseDatabase.getInstance().getReference().child("MECHANIC_POST").orderByChild("mechID").equalTo(model.getMechID())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                                        ServCentMechOnCallModel model1 = ds.getValue(ServCentMechOnCallModel.class);
                                                        FirebaseDatabase.getInstance().getReference().child("MECHANIC_POST").child(model1.getKey()).updateChildren(hashMap);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                }
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

        holder.DisBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("status", "in-active");
                FirebaseDatabase.getInstance().getReference().child("SERVICE_CENT_MECHANICS").child(model.getKey()).updateChildren(hashMap);

                holder.Status.setText("in-active");
                holder.actBtn.setVisibility(View.VISIBLE);
                holder.DisBtn.setVisibility(View.GONE);

            }
        });
        holder.actBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("status", "active");
                FirebaseDatabase.getInstance().getReference().child("SERVICE_CENT_MECHANICS").child(model.getKey()).updateChildren(hashMap);

                holder.Status.setText("active");
                holder.actBtn.setVisibility(View.GONE);
                holder.DisBtn.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView Name, Address, Birth, Skills, Gender, Status, Mech_Type;
        CardView CardItemBTN; // sa button nga item
        LinearLayout DetailsLayout;
        Button EditBtn, DisBtn, delBtn, actBtn;
        private int mYear, mMonth, mDay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            Name = itemView.findViewById(R.id.NameMechTV);
            Address = itemView.findViewById(R.id.addressMechTV);
            Birth = itemView.findViewById(R.id.birthMechTV);
            Skills = itemView.findViewById(R.id.skillsMechTV);
            Gender = itemView.findViewById(R.id.genderMechTV);
            Status = itemView.findViewById(R.id.statusMechTV);
            Mech_Type = itemView.findViewById(R.id.typeMechTV);

            delBtn = itemView.findViewById(R.id.DeleteMechBTN);
            EditBtn = itemView.findViewById(R.id.EditMechBTN);
            DisBtn = itemView.findViewById(R.id.DisableMechBTN);
            actBtn = itemView.findViewById(R.id.activeMechBTN);

            CardItemBTN = itemView.findViewById(R.id.mechCardBTN);// sa button nga item
            DetailsLayout = itemView.findViewById(R.id.detailsExpandMechLayout);
        }
    }


}
