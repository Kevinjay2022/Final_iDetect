package com.example.idetect;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.idetect.Adapters.DisplayOwnItemsAdapter;
import com.example.idetect.Models.ItemsModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class FragmentAutoPartsStore extends Fragment {
    private Uri imageUri;

    private Button AddItemBtn, saveItemBtn, addImgBtn, cancelItemBtn;
    private RecyclerView itemView;
    private LinearLayout addItemLayout, storeLayout;
    private Spinner ItemCatSpin;
    private EditText ItemName, ItemQty, ItemPrice;
    private ImageView storeImg;
    DisplayOwnItemsAdapter itemDispAdapter;


    private ArrayList<ItemsModel> fourItemsModels;
    private ArrayList<ItemsModel> sixItemsModels;
    private ArrayList<ItemsModel> toolsItemsModels;

    public FragmentAutoPartsStore(){ }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragAutoPartsStore = inflater.inflate(R.layout.fragment_auto_parts_store, container, false);

        cancelItemBtn = fragAutoPartsStore.findViewById(R.id.cancelItemBtn);
        AddItemBtn = fragAutoPartsStore.findViewById(R.id.AddItemBtn);
        addItemLayout = fragAutoPartsStore.findViewById(R.id.addItemLayout);
        storeLayout = fragAutoPartsStore.findViewById(R.id.storeLayout);

        TextView fourwheels = fragAutoPartsStore.findViewById(R.id.homeOrderBTN);
        TextView sixwheels = fragAutoPartsStore.findViewById(R.id.homeOrderBTN2);
        TextView tools = fragAutoPartsStore.findViewById(R.id.homeOrderBTN3);
        storeImg = fragAutoPartsStore.findViewById(R.id.StoreItemImg);
        ItemName = fragAutoPartsStore.findViewById(R.id.storeItemName);
        ItemQty = fragAutoPartsStore.findViewById(R.id.storeItemQuantity);
        ItemPrice = fragAutoPartsStore.findViewById(R.id.storeItemPrice);
        saveItemBtn = fragAutoPartsStore.findViewById(R.id.SaveItemBtn);
        addImgBtn = fragAutoPartsStore.findViewById(R.id.addImgBtn);

        String[] CategoryNames = {"Tools", "4 wheels", "6 wheels"};
        ItemCatSpin = fragAutoPartsStore.findViewById(R.id.AddItemCatSpinner);
        ArrayAdapter arrayAdapter = new ArrayAdapter(fragAutoPartsStore.getContext(), android.R.layout.simple_spinner_item, CategoryNames);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ItemCatSpin.setAdapter(arrayAdapter);
        ItemCatSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) { }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        AddItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storeLayout.setVisibility(View.GONE);
                addItemLayout.setVisibility(View.VISIBLE);
            }
        });
        cancelItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storeLayout.setVisibility(View.VISIBLE);
                addItemLayout.setVisibility(View.GONE);
                ItemName.setError(null);
                ItemPrice.setError(null);
                ItemQty.setError(null);
            }
        });
        saveItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!itemName() | !itemPrice() | !itemQty() | !itemImg()) {
                    return;
                } else {
                    ProgressDialog pd = new ProgressDialog(getActivity());
                    pd.setMessage("Uploading...");
                    pd.show();
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Item_Img").child(System.currentTimeMillis() + "");
                    storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String key = FirebaseDatabase.getInstance().getReference().child("ITEMS").push().getKey();
                                    String Struri = uri.toString();
                                    HashMap<Object, String> AshMap = new HashMap<>();
                                    AshMap.put("Item_Name", ItemName.getText().toString());
                                    AshMap.put("Price", ItemPrice.getText().toString());
                                    AshMap.put("Qty", ItemQty.getText().toString());
                                    AshMap.put("Category", ItemCatSpin.getSelectedItem().toString());
                                    AshMap.put("Item_Surl", Struri);
                                    AshMap.put("ItemKey", key);
                                    AshMap.put("rate", "0");
                                    AshMap.put("ShopUID", FirebaseAuth.getInstance().getCurrentUser().getUid());

                                    FirebaseDatabase.getInstance().getReference().child("ITEMS").child(key).setValue(AshMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(getActivity(), "Added Successfully...", Toast.LENGTH_SHORT).show();
                                            ItemName.setText("");
                                            ItemPrice.setText("");
                                            ItemQty.setText("");
                                            storeLayout.setVisibility(View.VISIBLE);
                                            addItemLayout.setVisibility(View.GONE);
                                            imageUri = null;
                                            Picasso.get().load(android.R.color.transparent).into(storeImg);
                                            fourwheels.setBackgroundResource(R.color.teal_200);
                                            sixwheels.setBackgroundResource(R.color.purple_700);
                                            tools.setBackgroundResource(R.color.purple_700);
                                            pd.dismiss();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getActivity(), "Error while add", Toast.LENGTH_SHORT).show();
                                            pd.dismiss();
                                        }
                                    });
                                }
                            });
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progresPerce = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            pd.setMessage("Uploading " + (int) progresPerce + " %");
                        }
                    });
                }
            }
        });
        addImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        fourItemsModels = new ArrayList<>();
        sixItemsModels = new ArrayList<>();
        toolsItemsModels = new ArrayList<>();

        itemView = (RecyclerView)fragAutoPartsStore.findViewById(R.id.itemViewListStore);
        itemView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        FirebaseDatabase.getInstance().getReference().child("ITEMS")
                .orderByChild("ShopUID").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        fourItemsModels.clear();
                        sixItemsModels.clear();
                        toolsItemsModels.clear();
                        for (DataSnapshot ds : snapshot.getChildren()){
                            ItemsModel model = ds.getValue(ItemsModel.class);
                            if (ds.child("Category").getValue().toString().equals("4 wheels")){
                                fourItemsModels.add(model);
                            }
                            if (ds.child("Category").getValue().toString().equals("6 wheels")){
                                sixItemsModels.add(model);
                            }
                            if (ds.child("Category").getValue().toString().equals("Tools")){
                                toolsItemsModels.add(model);
                            }
                        }
                        fourwheels.setBackgroundResource(R.color.teal_200);
                        sixwheels.setBackgroundResource(R.color.purple_700);
                        tools.setBackgroundResource(R.color.purple_700);
                        itemDispAdapter = new DisplayOwnItemsAdapter(getActivity(), fourItemsModels);
                        itemView.setAdapter(itemDispAdapter);
                        itemDispAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        fourwheels.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                fourwheels.setBackgroundResource(R.color.teal_200);
                sixwheels.setBackgroundResource(R.color.purple_700);
                tools.setBackgroundResource(R.color.purple_700);
                itemDispAdapter = new DisplayOwnItemsAdapter(getActivity(), fourItemsModels);
                itemDispAdapter.notifyItemRangeChanged(0, fourItemsModels.size());
                itemView.setAdapter(itemDispAdapter);
            }
        });
        sixwheels.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                fourwheels.setBackgroundResource(R.color.purple_700);
                sixwheels.setBackgroundResource(R.color.teal_200);
                tools.setBackgroundResource(R.color.purple_700);
                itemDispAdapter = new DisplayOwnItemsAdapter(getActivity(), sixItemsModels);
                itemDispAdapter.notifyItemRangeChanged(0, sixItemsModels.size());
                itemView.setAdapter(itemDispAdapter);
            }
        });
        tools.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                fourwheels.setBackgroundResource(R.color.purple_700);
                sixwheels.setBackgroundResource(R.color.purple_700);
                tools.setBackgroundResource(R.color.teal_200);
                itemDispAdapter = new DisplayOwnItemsAdapter(getActivity(), toolsItemsModels);
                itemDispAdapter.notifyItemRangeChanged(0, toolsItemsModels.size());
                itemView.setAdapter(itemDispAdapter);
            }
        });

        return fragAutoPartsStore;
    }
    private boolean itemName() {
        String str = ItemName.getText().toString().trim();

        if (str.isEmpty()) {
            ItemName.setError("Field should not be empty.");
            return false;
        }
        return true;
    }
    private boolean itemPrice() {
        String str = ItemPrice.getText().toString().trim();

        if (str.isEmpty()) {
            ItemPrice.setError("Field should not be empty.");
            return false;
        }
        return true;
    }
    private boolean itemQty() {
        String str = ItemQty.getText().toString().trim();

        if (str.isEmpty()) {
            ItemQty.setError("Field should not be empty.");
            return false;
        }
        return true;
    }
    private boolean itemImg() {

        if (imageUri == null) {
            Toast.makeText(getActivity(), "Put an image for your item", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(storeImg);
        }
    }
}