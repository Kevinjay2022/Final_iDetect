package com.example.idetect;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.idetect.Adapters.DisplayStoreItemsAdapter;
import com.example.idetect.Models.ItemsModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class FragmentServiceCenterAutoParts extends Fragment {

    RecyclerView recyclerView;
    DisplayStoreItemsAdapter displayStoreItemsAdapter;
    RelativeLayout cart_btn;
    int Counter = 0;
    String category = "fourwheels";
    TextView CartCounter;
    androidx.appcompat.widget.SearchView searchView;
    Button fourwheels, sixwheels, tools;
    Spinner sortSpinner;
    private ArrayList<ItemsModel> fourItemsModels;
    private ArrayList<ItemsModel> sixItemsModels;
    private ArrayList<ItemsModel> toolsItemsModels;
    String[] sort;
    ArrayAdapter sortAdapter;
    LinearLayout noNotif;

    public FragmentServiceCenterAutoParts(){

    }

    @SuppressLint({"ResourceAsColor", "NotifyDataSetChanged"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragServShop = inflater.inflate(R.layout.fragment_service_center_auto_parts, container, false);

        cart_btn = fragServShop.findViewById(R.id.cart_btn);
        CartCounter = fragServShop.findViewById(R.id.CartCounter);
        noNotif = fragServShop.findViewById(R.id.noNotifications);

        sortSpinner = fragServShop.findViewById(R.id.spinnerSort);
        sort = getResources().getStringArray(R.array.sort_item);
        sortAdapter = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, sort);
        sortAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        sortSpinner.setAdapter(sortAdapter);

        FirebaseDatabase.getInstance().getReference().child("ITEM_CART")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Counter = 0;
                        for(DataSnapshot ds: snapshot.getChildren()){
                            if(ds.child("ID").getValue().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                Counter++;
                            }
                            CartCounter.setText(Counter + "");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        sortByRatings();
                    case 1:
                        sortByLowToHigh();
                        break;
                    case 2:
                        sortByHighToLow();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        searchView = fragServShop.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (s!=null)
                    filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s!=null)
                    filter(s);
                return false;
            }
        });


        //For displaying the Items in grid
        recyclerView = (RecyclerView)fragServShop.findViewById(R.id.items_grid);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        fourItemsModels = new ArrayList<>();
        sixItemsModels = new ArrayList<>();
        toolsItemsModels = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("ITEMS")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()){
                            ItemsModel model = ds.getValue(ItemsModel.class);
                            if (ds.child("ShopUID").getValue().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            || ds.child("Qty").getValue().toString().equals("0")){}
                            else {
                                if (ds.child("Category").getValue().toString().equals("4 wheels")) {
                                    fourItemsModels.add(model);
                                }
                                if (ds.child("Category").getValue().toString().equals("6 wheels")) {
                                        sixItemsModels.add(model);
                                }
                                if (ds.child("Category").getValue().toString().equals("Tools")) {
                                        toolsItemsModels.add(model);
                                }
                            }

                        }
                        fourwheels.setBackgroundResource(R.color.teal_200);
                        if (fourItemsModels.size() == 0){
                            recyclerView.setVisibility(View.GONE);
                            noNotif.setVisibility(View.VISIBLE);
                        }else {
                            recyclerView.setVisibility(View.VISIBLE);
                            noNotif.setVisibility(View.GONE);
                            sortByRatings();
                            displayStoreItemsAdapter = new DisplayStoreItemsAdapter(getActivity(), fourItemsModels);
                            recyclerView.setAdapter(displayStoreItemsAdapter);
                            displayStoreItemsAdapter.notifyDataSetChanged();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        fourwheels = fragServShop.findViewById(R.id.fourWheelers);
        sixwheels = fragServShop.findViewById(R.id.sixWheelers);
        tools = fragServShop.findViewById(R.id.tools);

        fourwheels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortSpinner.setSelection(0);
                category = "fourwheels";
                fourwheels.setBackgroundResource(R.color.teal_200);
                sixwheels.setBackgroundResource(R.color.purple_700);
                tools.setBackgroundResource(R.color.purple_700);
                if (fourItemsModels.size() == 0){
                    recyclerView.setVisibility(View.GONE);
                    noNotif.setVisibility(View.VISIBLE);
                }else {
                    recyclerView.setVisibility(View.VISIBLE);
                    noNotif.setVisibility(View.GONE);
                    sortByRatings();
                    displayStoreItemsAdapter = new DisplayStoreItemsAdapter(getActivity(), fourItemsModels);
                    recyclerView.setAdapter(displayStoreItemsAdapter);
                    displayStoreItemsAdapter.notifyDataSetChanged();
                }
            }
        });
        sixwheels.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                sortSpinner.setSelection(0);
                category = "sixwheels";
                fourwheels.setBackgroundResource(R.color.purple_700);
                sixwheels.setBackgroundResource(R.color.teal_200);
                tools.setBackgroundResource(R.color.purple_700);
                if (sixItemsModels.size() == 0){
                    recyclerView.setVisibility(View.GONE);
                    noNotif.setVisibility(View.VISIBLE);
                }else {
                    recyclerView.setVisibility(View.VISIBLE);
                    noNotif.setVisibility(View.GONE);
                    sortByRatings();
                    displayStoreItemsAdapter = new DisplayStoreItemsAdapter(getActivity(), sixItemsModels);
                    recyclerView.setAdapter(displayStoreItemsAdapter);
                    displayStoreItemsAdapter.notifyDataSetChanged();
                }
            }
        });
        tools.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                sortSpinner.setSelection(0);
                category = "tools";
                fourwheels.setBackgroundResource(R.color.purple_700);
                sixwheels.setBackgroundResource(R.color.purple_700);
                tools.setBackgroundResource(R.color.teal_200);
                if (toolsItemsModels.size() == 0){
                    recyclerView.setVisibility(View.GONE);
                    noNotif.setVisibility(View.VISIBLE);
                }else {
                    recyclerView.setVisibility(View.VISIBLE);
                    noNotif.setVisibility(View.GONE);
                    sortByRatings();
                    displayStoreItemsAdapter = new DisplayStoreItemsAdapter(getActivity(), toolsItemsModels);
                    recyclerView.setAdapter(displayStoreItemsAdapter);
                    displayStoreItemsAdapter.notifyDataSetChanged();
                }
            }
        });

        cart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FragmentServiceCenterAutoPartsCart.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        return fragServShop;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void sortByHighToLow() {
        if (category.equals("tools")) {
                Collections.sort(toolsItemsModels, (l1, l2) -> {
                    float r1 = Float.parseFloat(l1.getPrice());
                    float r2 = Float.parseFloat(l2.getPrice());
                    if (r2 > r1) return 1;
                    else if(r2 < r1) return -1;
                    else return 0;
                });
            displayStoreItemsAdapter = new DisplayStoreItemsAdapter(getActivity(), toolsItemsModels);
            recyclerView.setAdapter(displayStoreItemsAdapter);
        }
        if (category.equals("fourwheels")) {
            Collections.sort(fourItemsModels, (l1, l2) -> {
                float r1 = Float.parseFloat(l1.getPrice());
                float r2 = Float.parseFloat(l2.getPrice());
                if (r2 > r1) return 1;
                else if (r2 < r1) return -1;
                else return 0;
            });
            displayStoreItemsAdapter = new DisplayStoreItemsAdapter(getActivity(), fourItemsModels);
            recyclerView.setAdapter(displayStoreItemsAdapter);
        }
        if (category.equals("sixwheels")) {
            Collections.sort(sixItemsModels, (l1, l2) -> {
                float r1 = Float.parseFloat(l1.getPrice());
                float r2 = Float.parseFloat(l2.getPrice());
                if (r2 > r1) return 1;
                else if (r2 < r1) return -1;
                else return 0;
            });
            displayStoreItemsAdapter = new DisplayStoreItemsAdapter(getActivity(), sixItemsModels);
            recyclerView.setAdapter(displayStoreItemsAdapter);
        }

    }
    @SuppressLint("NotifyDataSetChanged")
    private void sortByLowToHigh() {
        if (category.equals("tools")) {
            Collections.sort(toolsItemsModels, (l1, l2) -> {
                float r1 = Float.parseFloat(l1.getPrice());
                float r2 = Float.parseFloat(l2.getPrice());
                if (r1 > r2) return 1;
                else if(r1 < r2) return -1;
                else return 0;
            });
            displayStoreItemsAdapter = new DisplayStoreItemsAdapter(getActivity(), toolsItemsModels);
            recyclerView.setAdapter(displayStoreItemsAdapter);
        }
        if (category.equals("fourwheels")) {
            Collections.sort(fourItemsModels, (l1, l2) -> {
                float r1 = Float.parseFloat(l1.getPrice());
                float r2 = Float.parseFloat(l2.getPrice());
                if (r1 > r2) return 1;
                else if (r1 < r2) return -1;
                else return 0;
            });
            displayStoreItemsAdapter = new DisplayStoreItemsAdapter(getActivity(), fourItemsModels);
            recyclerView.setAdapter(displayStoreItemsAdapter);
        }
        if (category.equals("sixwheels")) {
            Collections.sort(sixItemsModels, (l1, l2) -> {
                float r1 = Float.parseFloat(l1.getPrice());
                float r2 = Float.parseFloat(l2.getPrice());
                if (r1 > r2) return 1;
                else if (r1 < r2) return -1;
                else return 0;
            });
            displayStoreItemsAdapter = new DisplayStoreItemsAdapter(getActivity(), sixItemsModels);
            recyclerView.setAdapter(displayStoreItemsAdapter);
        }

    }
    private void sortByRatings(){
        if (category.equals("tools")) {
        Collections.sort(toolsItemsModels, (l1, l2) -> {
            float r1 = Float.parseFloat(l1.getRate());
            float r2 = Float.parseFloat(l2.getRate());
            if (r2 > r1) return 1;
            else if(r2 < r1) return -1;
            else return 0;
        });
        displayStoreItemsAdapter = new DisplayStoreItemsAdapter(getActivity(), toolsItemsModels);
        recyclerView.setAdapter(displayStoreItemsAdapter);
    }
        if (category.equals("fourwheels")) {
            Collections.sort(fourItemsModels, (l1, l2) -> {
                float r1 = Float.parseFloat(l1.getRate());
                float r2 = Float.parseFloat(l2.getRate());
                if (r2 > r1) return 1;
                else if (r2 < r1) return -1;
                else return 0;
            });
            displayStoreItemsAdapter = new DisplayStoreItemsAdapter(getActivity(), fourItemsModels);
            recyclerView.setAdapter(displayStoreItemsAdapter);
        }
        if (category.equals("sixwheels")) {
            Collections.sort(sixItemsModels, (l1, l2) -> {
                float r1 = Float.parseFloat(l1.getRate());
                float r2 = Float.parseFloat(l2.getRate());
                if (r2 > r1) return 1;
                else if (r2 < r1) return -1;
                else return 0;
            });
            displayStoreItemsAdapter = new DisplayStoreItemsAdapter(getActivity(), sixItemsModels);
            recyclerView.setAdapter(displayStoreItemsAdapter);
        }
    }

    private void filter(String s) {
        List<ItemsModel> filtered = new ArrayList<>();
        filtered.clear();
        if (category.equals("tools")) {
            for (ItemsModel re : toolsItemsModels) {
                if (re.getItem_Name().toLowerCase().contains(s.toLowerCase()))
                    filtered.add(re);
            }
        }
        if (category.equals("fourwheels")) {
            for (ItemsModel re : fourItemsModels) {
                if (re.getItem_Name().toLowerCase().contains(s.toLowerCase()))
                    filtered.add(re);
            }
        }
        if (category.equals("sixwheels")) {
            for (ItemsModel re : sixItemsModels) {
                if (re.getItem_Name().toLowerCase().contains(s.toLowerCase()))
                    filtered.add(re);
            }
        }
        displayStoreItemsAdapter = new DisplayStoreItemsAdapter(getActivity(), filtered);
        recyclerView.setAdapter(displayStoreItemsAdapter);

    }

    @Override
    public void onResume() {
        Counter = 0;
        super.onResume();
    }
}