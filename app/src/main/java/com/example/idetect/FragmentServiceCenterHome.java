package com.example.idetect;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
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

import com.example.idetect.Adapters.DispOwnItemsAdapter;
import com.example.idetect.Models.ItemsModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;


public class FragmentServiceCenterHome extends Fragment {

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_STORAGE_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;

    private FirebaseStorage storage;
    private StorageReference storeReference;
    Uri imageUri;
    private ImageView ItemImageView;

    RecyclerView itemView;
    RecyclerView orderView;
    DispOwnItemsAdapter itemDispAdapter;

    String cameraPermission[];
    String storagePermission[];

    String MainID;
    Uri image_uri;

    private CardView customerCardBTN, storeCardBTN, orderCardBTN, updateServicesLayout;
    private LinearLayout customerServiceCardExpand, storeExpandable, addItemsLayout, orderLayoutExpandable;
    Button homeStoreAddItemBTN, updateServiceBTN, AddItemBTNSave, homeAddPicBTN;
    EditText AddItemNameTB, AddItemPriceTB, AddItemQtyTB;
    Spinner ItemCatSpin;
    TextView StoreCounter;

    //Firebabes
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private int Counter = 0;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View homeViewF = inflater.inflate(R.layout.fragment_service_center_home, container, false);

        //Buttons and expandable
        customerCardBTN = homeViewF.findViewById(R.id.customerServiceCard);
        customerServiceCardExpand = homeViewF.findViewById(R.id.customerServiceExpandable);
        orderCardBTN = homeViewF.findViewById(R.id.orderCardViewBTN);
        storeCardBTN = homeViewF.findViewById(R.id.storeCardBTN);
        storeExpandable = homeViewF.findViewById(R.id.storeDisplayExpandable);
        addItemsLayout = homeViewF.findViewById(R.id.addItemsLayout);
        orderLayoutExpandable = homeViewF.findViewById(R.id.orderLayoutExpandable);
        homeStoreAddItemBTN = homeViewF.findViewById(R.id.homeStoreAddItemBTN);
        updateServicesLayout = homeViewF.findViewById(R.id.updateServicesLayout);
        updateServiceBTN = homeViewF.findViewById(R.id.updateServiceBTN);
        homeAddPicBTN = homeViewF.findViewById(R.id.homeAddPicBTN);

        ItemImageView = homeViewF.findViewById(R.id.ItemImageView);

        String[] CategoryNames = {"Tools", "2wheels", "4wheels"};
        ItemCatSpin = homeViewF.findViewById(R.id.AddItemCatSpinner);
        ArrayAdapter arrayAdapter = new ArrayAdapter(homeViewF.getContext(), android.R.layout.simple_spinner_item, CategoryNames);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ItemCatSpin.setAdapter(arrayAdapter);
        ItemCatSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) { }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        StoreCounter = homeViewF.findViewById(R.id.ItemAddedCounter);

        //Firebase Storage
        storage = FirebaseStorage.getInstance();
        storeReference = storage.getReference();


        // For add Items
        AddItemBTNSave = homeViewF.findViewById(R.id.AddItemBTNSave);
        AddItemNameTB = homeViewF.findViewById(R.id.AddItemNameTB);
        AddItemPriceTB = homeViewF.findViewById(R.id.AddItemPriceTB);
        AddItemQtyTB = homeViewF.findViewById(R.id.AddItemQtyTB);


        customerCardBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (customerServiceCardExpand.getVisibility() == View.GONE){
                    customerServiceCardExpand.setVisibility(View.VISIBLE);
                } else {
                    customerServiceCardExpand.setVisibility(View.GONE);
                }
            }
        });
        storeCardBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(storeExpandable.getVisibility() == View.GONE){
                    storeExpandable.setVisibility(View.VISIBLE);
                    addItemsLayout.setVisibility(View.GONE);
                }else{
                    storeExpandable.setVisibility(View.GONE);
                }
            }
        });
        orderCardBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (orderLayoutExpandable.getVisibility() == View.GONE) {
                    orderLayoutExpandable.setVisibility(View.VISIBLE);
                }else{
                    orderLayoutExpandable.setVisibility(View.GONE);
                }
            }
        });
        homeStoreAddItemBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storeExpandable.setVisibility(View.GONE);
                addItemsLayout.setVisibility(View.VISIBLE);
            }
        });
        updateServiceBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(updateServicesLayout.getVisibility() == View.GONE) {
                    updateServicesLayout.setVisibility(View.VISIBLE);
                }else{
                    updateServicesLayout.setVisibility(View.GONE);
                }
            }
        });


        //init firebase
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("USERS");
        MainID = firebaseUser.getUid();


        //For add Items
        AddItemBTNSave.setOnClickListener(new View.OnClickListener() {
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
                                String key = FirebaseDatabase.getInstance().getReference().child("ITEMS").push().getKey();
                                String Struri = uri.toString();
                                HashMap<Object, String > AshMap = new HashMap<>();
                                AshMap.put("Item_Name", AddItemNameTB.getText().toString());
                                AshMap.put("Price", AddItemPriceTB.getText().toString());
                                AshMap.put("Qty", AddItemQtyTB.getText().toString());
                                AshMap.put("Category", ItemCatSpin.getContext().toString());
                                AshMap.put("ID", MainID);
                                AshMap.put("Item_Surl", Struri);
                                AshMap.put("ItemKey", key);
                                AshMap.put("ShopUID", FirebaseAuth.getInstance().getCurrentUser().getUid());

                                FirebaseDatabase.getInstance().getReference().child("ITEMS").child(key).setValue(AshMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getActivity(), "Added Successfully...", Toast.LENGTH_SHORT).show();
                                        AddItemNameTB.setText("");AddItemPriceTB.setText("");AddItemQtyTB.setText("");
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

        //For display Items sa store
        itemView = (RecyclerView)homeViewF.findViewById(R.id.homeOrderGridView);
        itemView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        FirebaseRecyclerOptions<ItemsModel> options = new FirebaseRecyclerOptions.Builder<ItemsModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("ITEMS").orderByChild("ID").equalTo(MainID), ItemsModel.class)
                .build();
        itemDispAdapter = new DispOwnItemsAdapter(options);
        itemView.setAdapter(itemDispAdapter);
        itemDispAdapter.startListening();
        //End for displaying the Items sa store

        /**
        //For display store request order
        orderView = (RecyclerView)homeViewF.findViewById(R.id.OrderRecycleView);
        orderView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //FirebaseRecyclerOptions<ItemsModel> orderOption = new FirebaseRecyclerOptions.Builder<ItemsModel>()


         */

        homeAddPicBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String profilePic = "image";
                String options [] = {"Camera", "Gallery"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Pick from");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            //CAMERA
                            if(!checkCameraPermission()){
                                requestCameraPermission();
                            }
                            else {
                                pickFromCamera();
                            }
                        }
                        else if(which == 1){
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

        //Item Counter
        FirebaseDatabase.getInstance().getReference().child("ITEMS").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    if(ds.child("ID").getValue().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        Counter++;
                    }
                }
                StoreCounter.setText(Counter + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return homeViewF;
    }


    boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    void requestStoragePermission(){
        ActivityCompat.requestPermissions(getActivity(), storagePermission, STORAGE_REQUEST_CODE);
    }

    boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    void requestCameraPermission(){
        ActivityCompat.requestPermissions(getActivity(), cameraPermission, CAMERA_REQUEST_CODE);
    }

    void pickFromCamera(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Tmp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Tmp description");
        //put image uri
        image_uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
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
                ItemImageView.setImageURI(imageUri);
                //uploadImg();
                Picasso.get().load(imageUri).into(ItemImageView);
            }
            if(requestCode == IMAGE_PICK_CAMERA_CODE) {
                //uploadImg();

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
    private voiduploadImg() {
        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setTitle("Uploading Image...");
        pd.show();
        StorageReference ref = storeReference.child("image/*" + MainID);
        ref.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        Toast.makeText(getActivity(), "Added Image", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(getActivity(), "Error while add", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progresPerce = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        pd.setMessage("Percentage " + (int) progresPerce + " %");
                    }
                });
    }*/
}