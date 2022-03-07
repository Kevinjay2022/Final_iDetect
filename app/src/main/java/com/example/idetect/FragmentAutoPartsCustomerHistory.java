package com.example.idetect;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;



public class FragmentAutoPartsCustomerHistory extends Fragment {

    public FragmentAutoPartsCustomerHistory(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragCustHst = inflater.inflate(R.layout.fragment_auto_parts_customer_history, container, false);


        return fragCustHst;
    }
}