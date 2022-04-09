package com.example.idetect;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
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

import com.example.idetect.Adapters.DisplayMyOrderAdapter;
import com.example.idetect.Adapters.DisplayOwnItemsAdapter;
import com.example.idetect.Adapters.ItemOrderAdapter;
import com.example.idetect.Adapters.ServCentCustServAdapter;
import com.example.idetect.Models.ItemsModel;
import com.example.idetect.Models.OrderModel;
import com.example.idetect.Models.ServCentCustomerService;
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

import java.util.ArrayList;
import java.util.HashMap;


public class FragmentServiceCenterHome extends Fragment {

    private FirebaseStorage storage;
    Uri imageUri;
    private ImageView ItemImageView;

    RecyclerView itemView;
    RecyclerView orderView;
    RecyclerView myOrderView;
    RecyclerView custServView;
    DisplayOwnItemsAdapter itemDispAdapter;
    ServCentCustServAdapter custServDispAdapter;
    ItemOrderAdapter itemsAdapter;
    DisplayMyOrderAdapter myOrderAdapter;
    private ArrayList<ItemsModel> fourItemsModels;
    private ArrayList<ItemsModel> sixItemsModels;
    private ArrayList<ItemsModel> toolsItemsModels;
    private ArrayList<ServCentCustomerService> servModel;
    private ArrayList<OrderModel> orderModels;
    private ArrayList<OrderModel> completeOrderModels;
    private ArrayList<OrderModel> cancelOrderModels;
    private ArrayList<OrderModel> myOrderModels;
    private ArrayList<OrderModel> myComplete;
    private ArrayList<OrderModel> myCancel;

    String MainID;

    private CardView customerCardBTN, storeCardBTN, orderCardBTN, myOrderCardBTN, updateServicesLayout, newOrderCardView, newIssueCardView;
    private LinearLayout customerServiceCardExpand, storeExpandable, addItemsLayout, orderLayoutExpandable, myOrderLayoutExpandable;
    Button homeStoreAddItemBTN, updateServiceBTN, AddItemBTNSave, homeAddPicBTN, saveBTN;
    EditText AddItemNameTB, AddItemPriceTB, AddItemQtyTB, UpdateServiceEditText;
    Spinner ItemCatSpin;
    TextView StoreCounter, CustomerCounter, OrderCounter, MyOrderCounter, totalItem, totalSalary, newOrder, newIssue, myOrdersBtn, myCompleteBtn, ordersBtn, completeBtn, cancelBtn, myCancelBtn;

    int totalQty = 0;
    float totalMoney = 0, price = 0;
    long due_date;

    //Firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private int store_Counter = 0, customerServCounter = 0, order_Counter = 0, myOrder_Counter = 0, newOrderCounter = 0, newIssueCounter = 0;


    @SuppressLint("NotifyDataSetChanged")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View homeViewF = inflater.inflate(R.layout.fragment_service_center_home, container, false);


        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("USERS");

        MainID = firebaseUser.getUid();
        fourItemsModels = new ArrayList<>();
        sixItemsModels = new ArrayList<>();
        toolsItemsModels = new ArrayList<>();
        myOrderModels = new ArrayList<>();
        myComplete = new ArrayList<>();
        servModel = new ArrayList<>();
        orderModels = new ArrayList<>();
        completeOrderModels = new ArrayList<>();
        cancelOrderModels = new ArrayList<>();
        myCancel = new ArrayList<>();

        //Buttons and expandable
        newIssue = homeViewF.findViewById(R.id.newIssueCounter);
        ordersBtn = homeViewF.findViewById(R.id.OrdersBTN);
        completeBtn = homeViewF.findViewById(R.id.CompleteBTN);
        cancelBtn = homeViewF.findViewById(R.id.cancelBTN);
        myOrdersBtn = homeViewF.findViewById(R.id.myOrdersBTN);
        myCompleteBtn = homeViewF.findViewById(R.id.myCompleteBTN);
        myCancelBtn = homeViewF.findViewById(R.id.myCancelBTN);
        newIssueCardView = homeViewF.findViewById(R.id.newIssueCard);
        newOrderCardView = homeViewF.findViewById(R.id.newOrderCard);
        newOrder = homeViewF.findViewById(R.id.newOrdersCounter);
        saveBTN = homeViewF.findViewById(R.id.btnSave);
        totalItem = homeViewF.findViewById(R.id.totalItemSoldTextView);
        totalSalary = homeViewF.findViewById(R.id.totalSaleTextViewMoney);
        CustomerCounter = homeViewF.findViewById(R.id.customerServiceCounter);
        UpdateServiceEditText = homeViewF.findViewById(R.id.ServicesTextViewLine);
        customerCardBTN = homeViewF.findViewById(R.id.customerServiceCard);
        customerServiceCardExpand = homeViewF.findViewById(R.id.customerServiceExpandable);
        orderCardBTN = homeViewF.findViewById(R.id.orderCardViewBTN);
        OrderCounter = homeViewF.findViewById(R.id.OrdersCounter);
        storeCardBTN = homeViewF.findViewById(R.id.storeCardBTN);
        myOrderCardBTN = homeViewF.findViewById(R.id.myOrderCardViewBTN);
        storeExpandable = homeViewF.findViewById(R.id.storeDisplayExpandable);
        addItemsLayout = homeViewF.findViewById(R.id.addItemsLayout);
        orderLayoutExpandable = homeViewF.findViewById(R.id.orderLayoutExpandable);
        myOrderLayoutExpandable = homeViewF.findViewById(R.id.myOrderLayoutExpandable);
        homeStoreAddItemBTN = homeViewF.findViewById(R.id.homeStoreAddItemBTN);
        updateServicesLayout = homeViewF.findViewById(R.id.updateServicesLayout);
        updateServiceBTN = homeViewF.findViewById(R.id.updateServiceBTN);
        homeAddPicBTN = homeViewF.findViewById(R.id.homeAddPicBTN);
        ItemImageView = homeViewF.findViewById(R.id.ItemImageView);
        myOrderView = homeViewF.findViewById(R.id.myOrderRecycleView);
        TextView fourwheels = homeViewF.findViewById(R.id.homeOrderBTN);
        TextView sixwheels = homeViewF.findViewById(R.id.homeOrderBTN2);
        TextView tools = homeViewF.findViewById(R.id.homeOrderBTN3);
        StoreCounter = homeViewF.findViewById(R.id.ItemAddedCounter);
        MyOrderCounter = homeViewF.findViewById(R.id.myOrdersCounter);
        ItemCatSpin = homeViewF.findViewById(R.id.AddItemCatSpinner);
        AddItemBTNSave = homeViewF.findViewById(R.id.AddItemBTNSave);
        AddItemNameTB = homeViewF.findViewById(R.id.AddItemNameTB);
        AddItemPriceTB = homeViewF.findViewById(R.id.AddItemPriceTB);
        AddItemQtyTB = homeViewF.findViewById(R.id.AddItemQtyTB);
        orderView = homeViewF.findViewById(R.id.OrderRecycleView);
        custServView = homeViewF.findViewById(R.id.CustomerServiceListviewExpand);
        itemView = homeViewF.findViewById(R.id.homeOrderGridView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getActivity());
        linearLayoutManager1.setReverseLayout(true);
        linearLayoutManager1.setStackFromEnd(true);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getActivity());
        linearLayoutManager2.setReverseLayout(true);
        linearLayoutManager2.setStackFromEnd(true);
        itemView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        orderView.setHasFixedSize(true);
        myOrderView.setHasFixedSize(true);
        orderView.setLayoutManager(linearLayoutManager1);
        myOrderView.setLayoutManager(linearLayoutManager2);
        custServView.setLayoutManager(linearLayoutManager);

        String[] CategoryNames = {"Tools", "4 wheels", "6 wheels"};
        ArrayAdapter arrayAdapter = new ArrayAdapter(homeViewF.getContext(), android.R.layout.simple_spinner_item, CategoryNames);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ItemCatSpin.setAdapter(arrayAdapter);
        ItemCatSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) { }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        //Firebase Storage
        storage = FirebaseStorage.getInstance();

        //Statistics
        FirebaseDatabase.getInstance().getReference().child("ORDERS")
                .orderByChild("ShopUID").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        totalQty = 0;
                        totalMoney = 0;
                        price = 0;
                        for (DataSnapshot ds : snapshot.getChildren()){
                            OrderModel model = ds.getValue(OrderModel.class);
                            if (model.getStatus().equals("complete")){
                                totalQty += Integer.parseInt(model.getQty());

                                FirebaseDatabase.getInstance().getReference().child("ITEMS").child(model.getItemKey())
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()){
                                                    price = Integer.parseInt(model.getQty()) * Float.parseFloat(snapshot.child("Price").getValue().toString());
                                                    totalMoney = totalMoney + price;
                                                    totalSalary.setText("₱ "+totalMoney);
                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                            }
                        }
                        totalItem.setText(""+totalQty);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        updateServiceBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(updateServicesLayout.getVisibility() == View.GONE) {
                    updateServicesLayout.setVisibility(View.VISIBLE);
                    customerServiceCardExpand.setVisibility(View.GONE);
                    storeExpandable.setVisibility(View.GONE);
                    orderLayoutExpandable.setVisibility(View.GONE);
                    myOrderLayoutExpandable.setVisibility(View.GONE);
                    FirebaseDatabase.getInstance().getReference().child("SERVICE_CENT_DESC_SERVICES").child(MainID)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        String desc = snapshot.child("description").getValue().toString();
                                        UpdateServiceEditText.setText(desc);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }else{
                    updateServicesLayout.setVisibility(View.GONE);
                    UpdateServiceEditText.setError(null);

                }
            }
        });

        saveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String ed_updateService = UpdateServiceEditText.getText().toString().trim();
                if (ed_updateService.isEmpty()){
                    UpdateServiceEditText.setError("Field should not be empty!");
                    return;
                }else{
                    ProgressDialog pd = new ProgressDialog(getActivity());
                    pd.setMessage("Updating...");
                    pd.show();
                    String key = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("description", ed_updateService);
                    hashMap.put("ID", FirebaseAuth.getInstance().getCurrentUser().getUid());

                    assert key != null;
                    FirebaseDatabase.getInstance().getReference().child("SERVICE_CENT_DESC_SERVICES").child(key).setValue(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getActivity(), "Update Successfully...", Toast.LENGTH_SHORT).show();
                            pd.dismiss();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Error while updating...", Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        }
                    });
                }
            }
        });
        customerCardBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                FirebaseDatabase.getInstance().getReference().child("USERS").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    String timeStamp = snapshot.child("subscribe").getValue().toString();
                                    due_date = Long.parseLong(timeStamp);

                                    if (System.currentTimeMillis() > due_date){
                                        Toast.makeText(getActivity(), "Your subscription had expired.\nPlease avail subscription to open this features.", Toast.LENGTH_SHORT).show();
                                    }else {
                                        if (customerServCounter == 0){
                                            Toast.makeText(getActivity(), "No customer yet.", Toast.LENGTH_SHORT).show();
                                        }else{
                                            if (customerServiceCardExpand.getVisibility() == View.GONE){
                                                customerServiceCardExpand.setVisibility(View.VISIBLE);
                                                storeExpandable.setVisibility(View.GONE);
                                                orderLayoutExpandable.setVisibility(View.GONE);
                                                myOrderLayoutExpandable.setVisibility(View.GONE);
                                                newIssueCardView.setVisibility(View.GONE);

                                                FirebaseDatabase.getInstance().getReference().child("DRIVER_SERVICE_CENT_ISSUE").orderByChild("shopID").equalTo(MainID)
                                                        .addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                servModel.clear();
                                                                for (DataSnapshot ds : snapshot.getChildren()) {
                                                                    ServCentCustomerService model = ds.getValue(ServCentCustomerService.class);
                                                                    servModel.add(model);
                                                                }
                                                                custServDispAdapter = new ServCentCustServAdapter(getActivity(), servModel);
                                                                custServView.setAdapter(custServDispAdapter);
                                                                custServDispAdapter.notifyDataSetChanged();
                                                            }
                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });

                                            } else {
                                                newIssueCardView.setVisibility(View.GONE);
                                                customerServiceCardExpand.setVisibility(View.GONE);
                                                for (ServCentCustomerService re: servModel){
                                                    HashMap<String, Object> hashMap = new HashMap<>();
                                                    hashMap.put("seen", "old");
                                                    FirebaseDatabase.getInstance().getReference().child("DRIVER_SERVICE_CENT_ISSUE").child(re.getKey()).updateChildren(hashMap);

                                                }
                                        }
                                        }
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

            }
        });

        orderCardBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("USERS").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    String timeStamp = snapshot.child("subscribe").getValue().toString();
                                    due_date = Long.parseLong(timeStamp);

                                    if (System.currentTimeMillis() > due_date){
                                        Toast.makeText(getActivity(), "Your subscription had expired.\nPlease avail subscription to open this features.", Toast.LENGTH_SHORT).show();
                                    }else {
                                        if(order_Counter == 0){
                                            Toast.makeText(getActivity(), "No orders yet.", Toast.LENGTH_SHORT).show();
                                        }else{
                                            if (orderLayoutExpandable.getVisibility() == View.GONE) {
                                            //Orders Display
                                            FirebaseDatabase.getInstance().getReference().child("ORDERS")
                                                    .orderByChild("ShopUID").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            orderModels.clear();
                                                            completeOrderModels.clear();
                                                            cancelOrderModels.clear();
                                                            for (DataSnapshot ds : snapshot.getChildren()){
                                                                OrderModel model = ds.getValue(OrderModel.class);
                                                                assert model != null;
                                                                if (model.getStatus().equals("pending") || model.getStatus().equals("accept")){
                                                                    orderModels.add(model);
                                                                }
                                                                if (model.getStatus().equals("complete")){
                                                                    completeOrderModels.add(model);
                                                                }
                                                                if (model.getStatus().equals("cancel")){
                                                                    cancelOrderModels.add(model);
                                                                }
                                                            }
                                                            itemsAdapter = new ItemOrderAdapter(getActivity(), orderModels);
                                                            orderView.setAdapter(itemsAdapter);
                                                            itemsAdapter.notifyDataSetChanged();
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                ordersBtn.setBackgroundResource(R.color.teal_200);
                                                completeBtn.setBackgroundResource(R.color.purple_700);
                                                cancelBtn.setBackgroundResource(R.color.purple_700);
                                                orderLayoutExpandable.setVisibility(View.VISIBLE);
                                                customerServiceCardExpand.setVisibility(View.GONE);
                                                storeExpandable.setVisibility(View.GONE);
                                                myOrderLayoutExpandable.setVisibility(View.GONE);
                                                newOrderCardView.setVisibility(View.GONE);


                                            }else{
                                                newOrderCardView.setVisibility(View.GONE);
                                                orderLayoutExpandable.setVisibility(View.GONE);
                                                for (OrderModel re: orderModels){
                                                    HashMap<String, Object> hashMap = new HashMap<>();
                                                    hashMap.put("seen", "old");
                                                    FirebaseDatabase.getInstance().getReference().child("ORDERS").child(re.getKey()).updateChildren(hashMap);

                                                }
                                            }
                                        }

                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

            }
        });
        ordersBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                ordersBtn.setBackgroundResource(R.color.teal_200);
                completeBtn.setBackgroundResource(R.color.purple_700);
                cancelBtn.setBackgroundResource(R.color.purple_700);
                itemsAdapter = new ItemOrderAdapter(getActivity(), orderModels);
                orderView.setAdapter(itemsAdapter);
                itemsAdapter.notifyDataSetChanged();
            }
        });
        completeBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                ordersBtn.setBackgroundResource(R.color.purple_700);
                completeBtn.setBackgroundResource(R.color.teal_200);
                cancelBtn.setBackgroundResource(R.color.purple_700);
                itemsAdapter = new ItemOrderAdapter(getActivity(), completeOrderModels);
                orderView.setAdapter(itemsAdapter);
                itemsAdapter.notifyDataSetChanged();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                ordersBtn.setBackgroundResource(R.color.purple_700);
                completeBtn.setBackgroundResource(R.color.purple_700);
                cancelBtn.setBackgroundResource(R.color.teal_200);
                itemsAdapter = new ItemOrderAdapter(getActivity(), cancelOrderModels);
                orderView.setAdapter(itemsAdapter);
                itemsAdapter.notifyDataSetChanged();
            }
        });

        homeStoreAddItemBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storeExpandable.setVisibility(View.GONE);
                addItemsLayout.setVisibility(View.VISIBLE);
            }
        });


        myOrderCardBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myOrder_Counter == 0){
                    Toast.makeText(getActivity(), "You don't take some orders.", Toast.LENGTH_SHORT).show();
                }else{
                    if (myOrderLayoutExpandable.getVisibility() == View.GONE) {
                        //My orders display
                        FirebaseDatabase.getInstance().getReference().child("ORDERS")
                                .orderByChild("ID").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        myOrderModels.clear();
                                        myComplete.clear();
                                        myCancel.clear();
                                        for (DataSnapshot ds : snapshot.getChildren()){
                                            OrderModel model = ds.getValue(OrderModel.class);
                                            assert model != null;
                                            if (model.getStatus().equals("pending") || model.getStatus().equals("accept")){
                                                myOrderModels.add(model);
                                            }
                                            if (model.getStatus().equals("complete")){
                                                myComplete.add(model);
                                            }
                                            if (model.getStatus().equals("cancel")){
                                                myCancel.add(model);
                                            }
                                        }
                                        myOrderAdapter = new DisplayMyOrderAdapter(getActivity(), myOrderModels);
                                        myOrderView.setAdapter(myOrderAdapter);
                                        myOrderAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                        myOrdersBtn.setBackgroundResource(R.color.teal_200);
                        myCompleteBtn.setBackgroundResource(R.color.purple_700);
                        myCancelBtn.setBackgroundResource(R.color.purple_700);
                        myOrderLayoutExpandable.setVisibility(View.VISIBLE);
                        customerServiceCardExpand.setVisibility(View.GONE);
                        storeExpandable.setVisibility(View.GONE);
                        orderLayoutExpandable.setVisibility(View.GONE);
                    }else{
                        myOrderLayoutExpandable.setVisibility(View.GONE);
                    }
                }

            }
        });
        myOrdersBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                myOrdersBtn.setBackgroundResource(R.color.teal_200);
                myCompleteBtn.setBackgroundResource(R.color.purple_700);
                myCancelBtn.setBackgroundResource(R.color.purple_700);
                myOrderAdapter = new DisplayMyOrderAdapter(getActivity(), myOrderModels);
                myOrderView.setAdapter(myOrderAdapter);
                myOrderAdapter.notifyDataSetChanged();
            }
        });
        myCompleteBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                myOrdersBtn.setBackgroundResource(R.color.purple_700);
                myCompleteBtn.setBackgroundResource(R.color.teal_200);
                myCancelBtn.setBackgroundResource(R.color.purple_700);
                myOrderAdapter = new DisplayMyOrderAdapter(getActivity(), myComplete);
                myOrderView.setAdapter(myOrderAdapter);
                myOrderAdapter.notifyDataSetChanged();
            }
        });
        myCancelBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                myOrdersBtn.setBackgroundResource(R.color.purple_700);
                myCompleteBtn.setBackgroundResource(R.color.purple_700);
                myCancelBtn.setBackgroundResource(R.color.teal_200);
                myOrderAdapter = new DisplayMyOrderAdapter(getActivity(), myCancel);
                myOrderView.setAdapter(myOrderAdapter);
                myOrderAdapter.notifyDataSetChanged();
            }
        });

        //For add Items
        AddItemBTNSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!itemName() | !itemPrice() | !itemQty() | imageUri == null) {
                    Toast.makeText(getActivity(), "Please add an image", Toast.LENGTH_SHORT).show();
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
                                    AshMap.put("Item_Name", AddItemNameTB.getText().toString());
                                    AshMap.put("Price", AddItemPriceTB.getText().toString());
                                    AshMap.put("Qty", AddItemQtyTB.getText().toString());
                                    AshMap.put("Category", ItemCatSpin.getSelectedItem().toString());
                                    AshMap.put("ID", MainID);
                                    AshMap.put("Item_Surl", Struri);
                                    AshMap.put("ItemKey", key);
                                    AshMap.put("rate", "0");
                                    AshMap.put("ShopUID", FirebaseAuth.getInstance().getCurrentUser().getUid());

                                    FirebaseDatabase.getInstance().getReference().child("ITEMS").child(key).setValue(AshMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(getActivity(), "Added Successfully...", Toast.LENGTH_SHORT).show();
                                            AddItemNameTB.setText("");
                                            AddItemPriceTB.setText("");
                                            AddItemQtyTB.setText("");
                                            imageUri = null;
                                            Picasso.get().load(android.R.color.transparent).into(ItemImageView);
                                            storeExpandable.setVisibility(View.VISIBLE);
                                            addItemsLayout.setVisibility(View.GONE);
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

        storeCardBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(storeExpandable.getVisibility() == View.GONE){
                    storeExpandable.setVisibility(View.VISIBLE);
                    addItemsLayout.setVisibility(View.GONE);
                    customerServiceCardExpand.setVisibility(View.GONE);
                    orderLayoutExpandable.setVisibility(View.GONE);
                    myOrderLayoutExpandable.setVisibility(View.GONE);
                    //For display Items sa store
                    FirebaseDatabase.getInstance().getReference().child("ITEMS").orderByChild("ShopUID").equalTo(MainID)
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
                }else{
                    storeExpandable.setVisibility(View.GONE);
                }
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
                itemView.setAdapter(itemDispAdapter);
                itemDispAdapter.notifyDataSetChanged();
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
                itemView.setAdapter(itemDispAdapter);
                itemDispAdapter.notifyDataSetChanged();
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
                itemView.setAdapter(itemDispAdapter);
                itemDispAdapter.notifyDataSetChanged();
            }
        });
        homeAddPicBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        //Item Counter
        FirebaseDatabase.getInstance().getReference().child("ITEMS")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                store_Counter = 0;
                for(DataSnapshot ds: snapshot.getChildren()){
                    if(ds.child("ShopUID").getValue().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        store_Counter++;
                    }
                }
                StoreCounter.setText(store_Counter + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseDatabase.getInstance().getReference().child("DRIVER_SERVICE_CENT_ISSUE")
                .addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                customerServCounter = 0;
                newIssueCounter = 0;
                for(DataSnapshot ds: snapshot.getChildren()){
                    ServCentCustomerService servModel = ds.getValue(ServCentCustomerService.class);
                    if(servModel.getShopID().equals(MainID)){
                        if (servModel.getSeen().equals("new"))
                            newIssueCounter++;
                        customerServCounter++;
                    }
                }
                CustomerCounter.setText(""+customerServCounter);
                if (newIssueCounter == 0) {
                    newIssueCardView.setVisibility(View.GONE);
                }else {
                    newIssueCardView.setVisibility(View.VISIBLE);
                    newIssue.setText(""+newIssueCounter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseDatabase.getInstance().getReference().child("ORDERS")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        order_Counter = 0;
                        newOrderCounter = 0;
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            OrderModel orderModel = ds.getValue(OrderModel.class);
                            if(orderModel.getShopUID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                if (orderModel.getSeen().equals("new"))
                                    newOrderCounter++;
                                order_Counter++;
                            }
                        }
                        OrderCounter.setText(order_Counter + "");
                        if (newOrderCounter == 0) {
                            newOrderCardView.setVisibility(View.GONE);
                        }else {
                            newOrderCardView.setVisibility(View.VISIBLE);
                            newOrder.setText(""+newOrderCounter);
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        FirebaseDatabase.getInstance().getReference().child("ORDERS")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        myOrder_Counter = 0;
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            if(ds.child("ID").getValue().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                myOrder_Counter++;
                            }
                        }

                        MyOrderCounter.setText(myOrder_Counter + "");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        return homeViewF;
    }

    private boolean itemName() {
        String str = AddItemNameTB.getText().toString().trim();

        if (str.isEmpty()) {
            AddItemNameTB.setError("Field should not be empty.");
            return false;
        }
        return true;
    }

    private boolean itemPrice() {
        String str = AddItemPriceTB.getText().toString().trim();

        if (str.isEmpty()) {
            AddItemPriceTB.setError("Field should not be empty.");
            return false;
        }
        return true;
    }

    private boolean itemQty() {
        String str = AddItemQtyTB.getText().toString().trim();

        if (str.isEmpty()) {
            AddItemQtyTB.setError("Field should not be empty.");
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
            Picasso.get().load(imageUri).into(ItemImageView);
        }
    }
}