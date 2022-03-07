package com.example.idetect;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.idetect.Adapters.DispOwnItemsAdapter;
import com.example.idetect.Models.ItemsModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class FragmentAutoPartsStore extends Fragment {

    private static final int IMAGE_PICK_STORAGE_CODE = 300;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;
    private Uri imageUri;

    private Button AddItemBtn, saveItemBtn, addImgBtn;
    private RecyclerView itemView;
    private LinearLayout addItemLayout, storeLayout;
    private Spinner ItemCatSpin;
    private EditText ItemName, ItemQty, ItemPrice;
    private ImageView storeImg;
    DispOwnItemsAdapter itemDispAdapter;
    String storagePermission[];


    public FragmentAutoPartsStore(){ }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragAutoPartsStore = inflater.inflate(R.layout.fragment_auto_parts_store, container, false);

        AddItemBtn = fragAutoPartsStore.findViewById(R.id.AddItemBtn);
        addItemLayout = fragAutoPartsStore.findViewById(R.id.addItemLayout);
        storeLayout = fragAutoPartsStore.findViewById(R.id.storeLayout);

        storeImg = fragAutoPartsStore.findViewById(R.id.StoreItemImg);
        ItemName = fragAutoPartsStore.findViewById(R.id.storeItemName);
        ItemQty = fragAutoPartsStore.findViewById(R.id.storeItemQuantity);
        ItemPrice = fragAutoPartsStore.findViewById(R.id.storeItemPrice);
        saveItemBtn = fragAutoPartsStore.findViewById(R.id.SaveItemBtn);
        addImgBtn = fragAutoPartsStore.findViewById(R.id.addImgBtn);

        String[] CategoryNames = {"Tools", "2wheels", "4wheels"};
        ItemCatSpin = fragAutoPartsStore.findViewById(R.id.spinnerCatAuto);
        ArrayAdapter arrayAdapter = new ArrayAdapter(fragAutoPartsStore.getContext(), android.R.layout.simple_spinner_item, CategoryNames);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ItemCatSpin.setAdapter(arrayAdapter);
        ItemCatSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) { }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });


        //For display Items
        itemView = (RecyclerView)fragAutoPartsStore.findViewById(R.id.itemViewListStore);
        itemView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        FirebaseRecyclerOptions<ItemsModel> options = new FirebaseRecyclerOptions.Builder<ItemsModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("ITEMS").orderByChild("ID").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()), ItemsModel.class)
                .build();
        itemDispAdapter = new DispOwnItemsAdapter(options);
        itemView.setAdapter(itemDispAdapter);
        itemDispAdapter.startListening();
        //End for displaying the Items

        AddItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storeLayout.setVisibility(View.GONE);
                addItemLayout.setVisibility(View.VISIBLE);
            }
        });
        saveItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                                String Struri = uri.toString();
                                HashMap<Object, String > AshMap = new HashMap<>();
                                AshMap.put("Item_Name", ItemName.getText().toString());
                                AshMap.put("Price", ItemPrice.getText().toString());
                                AshMap.put("Qty", ItemQty.getText().toString());
                                AshMap.put("Category", ItemCatSpin.getContext().toString());
                                AshMap.put("ID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                AshMap.put("Item_Surl", Struri);

                                FirebaseDatabase.getInstance().getReference().child("ITEMS").push().setValue(AshMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getActivity(), "Added Successfully...", Toast.LENGTH_SHORT).show();
                                        ItemName.setText("");ItemPrice.setText("");ItemQty.setText("");
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
        });

        addImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String options [] = {"Camera", "Gallery"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Pick from");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    /**    if(which == 0){
                            //CAMERA
                            if(!checkCameraPermission()){
                                requestCameraPermission();
                            }
                            else {
                                pickFromCamera();
                            }
                        }
                        else*/if(which == 1){
                            if(!checkStoragePermission()){
                                requestStoragePermission();
                            }
                            else {
                                pickFromStorage();
                            }
                        }
                    }
                });
                builder.create().show();
            }
        });

        return fragAutoPartsStore;
    }

    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(getActivity(), storagePermission, STORAGE_REQUEST_CODE);
    }

    void pickFromStorage() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_STORAGE_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode==RESULT_OK){
            if(requestCode == IMAGE_PICK_STORAGE_CODE) {
                imageUri = data.getData();
                storeImg.setImageURI(imageUri);
                //uploadImg();
                Picasso.get().load(imageUri).into(storeImg);
            }
            if(requestCode == IMAGE_PICK_CAMERA_CODE) {
                //uploadImg();

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}