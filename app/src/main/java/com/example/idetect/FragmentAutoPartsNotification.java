package com.example.idetect;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;



public class FragmentAutoPartsNotification extends Fragment {

    public FragmentAutoPartsNotification(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragAutoPartsNotification = inflater.inflate(R.layout.fragment_auto_parts_notification, container, false);


        return fragAutoPartsNotification;
    }

}